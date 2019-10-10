package nlp.floschne.thumbnailAnnotator.wsd.classifier;

import nlp.floschne.thumbnailAnnotator.wsd.featureExtractor.FeatureVector;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class NaiveBayesClassifier extends IClassifier {

    private static final Boolean LOGITS = true;
    private static final Boolean NORMALIZE = true;
    private static final Integer NUMBER_OF_INFLUENTIAL_FEATURES = 10;

    @Override
    public Prediction classify(FeatureVector featureVector) {
        assert this.model instanceof NaiveBayesModel;
        NaiveBayesModel myModel = (NaiveBayesModel) this.model;
        if (!myModel.isTrained())
            return null;

        Double maxProb = Double.MIN_VALUE;
        Prediction pred = new Prediction();

        Double prob;
        // TODO do we really want logits and normalization?!
        for (Label clazz : myModel.getClasses()) {
            // prior probability
            prob = myModel.getClassPrior(clazz);
            if (LOGITS)
                prob = -Math.log(prob);

            // TODO most of the times features occur more than once in a FV..
            //  is the formula correct ?! or do we have to weight the unique features
            //  think about better features like bi or trigrams..
            //  checkout TF&IDF
            for (Object feature : featureVector) {
                // class conditional probabilities
                double classConditional = myModel.computeClassConditionalProbability(feature, clazz);
                if (LOGITS)
                    prob += -Math.log(classConditional);
                else
                    prob *= classConditional;
            }

            // TODO We're multiplying by the number of features of the clazz in order to
            //  add a penalty to clazzes with a lot of features! add this to documentation!
//            prob *= myModel.getClassFeatures().get(clazz).size();
//            int n_max = myModel.getMaxFeatureNumberOfClasses();
//            int n_c = myModel.getClassFeatures().get(clazz).size();

            if (LOGITS) {
//                prob += -Math.log(1.0 / n_max) * (n_max - n_c);
                prob = Math.exp(-prob);
            } else {
            }
//                prob *= Math.exp(Math.log(1.0 / n_max) * (n_max - n_c));

            if (prob >= maxProb) {
                maxProb = prob;
                pred.setMostProbable(clazz);
                pred.setHighestProbability(maxProb);
            }
            List<Pair<Object, Double>> mostInfluentialFeatures = getMostInfluentialFeatures(clazz, featureVector);
            pred.addClass(clazz, prob, mostInfluentialFeatures);
        }
        if (NORMALIZE)
            pred.normalize();
        return pred;
    }

    private List<Pair<Object, Double>> getMostInfluentialFeatures(Label clazz, FeatureVector featureVector) {
        assert this.model instanceof NaiveBayesModel;
        NaiveBayesModel myModel = (NaiveBayesModel) this.model;

        List<Pair<Object, Double>> mostInfluentialFeatures = new ArrayList<>();
        for (Object feature : featureVector) {
            double classConditional = myModel.computeClassConditionalProbability(feature, clazz);
            if (LOGITS)
                classConditional = -Math.log(classConditional);

            mostInfluentialFeatures.add(Pair.of(feature, classConditional));
        }

        // sort the list
        // TODO understand logits!!
        //  remove 1 counts!
        if (LOGITS)
            mostInfluentialFeatures.sort(Comparator.comparing(Pair::getRight));
        else
            mostInfluentialFeatures.sort((o1, o2) -> o2.getRight().compareTo(o1.getRight()));

        return mostInfluentialFeatures.subList(0, mostInfluentialFeatures.size() <= NUMBER_OF_INFLUENTIAL_FEATURES ? mostInfluentialFeatures.size() - 1 : NUMBER_OF_INFLUENTIAL_FEATURES);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void train(List<? extends FeatureVector> featureVectors) {
        assert featureVectors != null && !featureVectors.isEmpty();
        assert featureVectors.get(0) != null;
        ((NaiveBayesModel) this.model).addAll((List) featureVectors);
    }
}
