package nlp.floschne.thumbnailAnnotator.wsd.classifier;

import nlp.floschne.thumbnailAnnotator.wsd.featureExtractor.FeatureVector;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NaiveBayesClassifier extends IClassifier {

    private static final Boolean LOGITS = true;
    private static final Boolean NORMALIZE = true;

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

            // TODO add a penalty for the size of the feature vector... (aka sample)
            //  or somehow normalize all feature vectors to maximum feature vec size?!
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
            int n_max = myModel.getMaxFeatureNumberOfClasses();
            int n_c = myModel.getClassFeatures().get(clazz).size();

            if (LOGITS) {
//                prob += -Math.log(1.0 / n_max) * (n_max - n_c);
                prob = Math.exp(-prob);
            }
            else {}
//                prob *= Math.exp(Math.log(1.0 / n_max) * (n_max - n_c));

            if (prob >= maxProb) {
                maxProb = prob;
                pred.setMostProbable(clazz);
                pred.setHighestProbability(maxProb);
            }
            pred.addClass(clazz, prob);
        }
        if (NORMALIZE)
            pred.normalize();
        return pred;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void train(List<? extends FeatureVector> featureVectors) {
        assert featureVectors != null && !featureVectors.isEmpty();
        assert featureVectors.get(0) != null;
        ((NaiveBayesModel) this.model).addAll((List) featureVectors);
    }
}
