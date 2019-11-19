package nlp.floschne.thumbnailAnnotator.wsd.classifier;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nlp.floschne.thumbnailAnnotator.wsd.featureExtractor.FeatureVector;
import nlp.floschne.thumbnailAnnotator.wsd.featureExtractor.TrainingFeatureVector;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = false)
@Component
@Data
public class NaiveBayesClassifier extends IClassifier {

    private Boolean useLogits;
    private Boolean normalize;
    private Integer maxNumberOfInfluentialFeatures;

    public NaiveBayesClassifier() {
        this.useLogits = true;
        this.normalize = true;
        this.maxNumberOfInfluentialFeatures = 10;
    }

    @Override
    public Prediction classify(FeatureVector featureVector) {
        assert this.model instanceof NaiveBayesModel;
        NaiveBayesModel myModel = (NaiveBayesModel) this.model;
        if (!myModel.isTrained())
            return null;

        Double maxProb = Double.MIN_VALUE;
        Prediction pred = new Prediction();

        Double prob;
        Double prior;
        // TODO do we really want logits and normalization?!
        for (Label clazz : myModel.getClasses()) {
            // prior probability
            prior = myModel.getClassPrior(clazz);
            if (useLogits)
                prior = Math.log(prior);
            prob = prior;

            // TODO most of the times features occur more than once in a FV..
            //  is the formula correct ?! or do we have to weight the unique features
            //  think about better features like bi or trigrams..
            //  checkout TF&IDF
            for (Object feature : featureVector) {
                // class conditional probabilities
                double classConditional = myModel.computeClassConditionalProbability(feature, clazz, useLogits);
                if (useLogits)
                    prob += classConditional;
                else
                    prob *= classConditional;
            }

            // TODO We're multiplying by the number of features of the clazz in order to
            //  add a penalty to clazzes with a lot of features! add this to documentation!
//            prob *= myModel.getClassFeatures().get(clazz).size();
//            int n_max = myModel.getMaxFeatureNumberOfClasses();
//            int n_c = myModel.getClassFeatures().get(clazz).size();

            if (useLogits) {
//                prob += -Math.log(1.0 / n_max) * (n_max - n_c);
                prob = Math.exp(prob);
            } else {
            }
//                prob *= Math.exp(Math.log(1.0 / n_max) * (n_max - n_c));

            if (prob >= maxProb) {
                maxProb = prob;
                pred.setMostProbable(clazz);
                pred.setHighestProbability(maxProb);
            }
            List<Pair<Object, Double>> mostInfluentialFeatures = getMostInfluentialFeatures(clazz, featureVector);
            pred.addClass(clazz, prob, mostInfluentialFeatures, prior);
        }
        if (normalize)
            pred.normalize();
        return pred;
    }

    private List<Pair<Object, Double>> getMostInfluentialFeatures(Label clazz, FeatureVector featureVector) {
        assert this.model instanceof NaiveBayesModel;
        NaiveBayesModel myModel = (NaiveBayesModel) this.model;

        List<Pair<Object, Double>> mostInfluentialFeatures = new ArrayList<>();
        for (Object feature : featureVector) {
            double classConditional = myModel.computeClassConditionalProbability(feature, clazz, useLogits);
            mostInfluentialFeatures.add(Pair.of(feature, classConditional));
        }

        // sort the list
        // TODO understand logits!!
        //  remove 1 counts!
        if (useLogits)
            mostInfluentialFeatures.sort(Comparator.comparing(Pair::getRight));
        else
            mostInfluentialFeatures.sort((o1, o2) -> o2.getRight().compareTo(o1.getRight()));

        return mostInfluentialFeatures.subList(0, mostInfluentialFeatures.size() <= maxNumberOfInfluentialFeatures ? mostInfluentialFeatures.size() - 1 : maxNumberOfInfluentialFeatures);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void train(Set<? extends FeatureVector> featureVectors) {
        assert featureVectors != null && !featureVectors.isEmpty();
        assert !featureVectors.contains(null);
        ((NaiveBayesModel) this.model).addAll((Set<TrainingFeatureVector>) featureVectors);
    }
}
