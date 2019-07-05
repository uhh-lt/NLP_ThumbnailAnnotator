package nlp.floschne.thumbnailAnnotator.wsd.classifier;

import nlp.floschne.thumbnailAnnotator.wsd.featureExtractor.FeatureVector;
import nlp.floschne.thumbnailAnnotator.wsd.featureExtractor.IFeatureVector;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NaiveBayesClassifier extends IClassifier {

    @Override
    public Prediction classify(IFeatureVector featureVector) {
        assert this.model instanceof NaiveBayesModel;
        NaiveBayesModel myModel = (NaiveBayesModel) this.model;
        if(!myModel.isTrained())
            return Prediction.getZeroPrediction(); // FIXME better throw an exception!?

        Double maxProb = Double.MIN_VALUE;
        Prediction pred = new Prediction();

        Double prob;
        for (Label clazz : myModel.getClasses()) {
            // prior probability
            prob = myModel.getClassProbability(clazz);
            for (Object feature : featureVector)
                // class conditional probabilities
                prob *= myModel.computeClassConditionalProbability(feature, clazz);

            if (prob >= maxProb) {
                maxProb = prob;
                pred.setPred(clazz);
                pred.setProb(maxProb);
            }
            pred.addClass(clazz, prob);
        }
        return pred;
    }

    @Override
    public void train(List<? extends IFeatureVector> featureVectors) {
        assert featureVectors != null && !featureVectors.isEmpty();
        assert featureVectors.get(0) instanceof FeatureVector;
        ((NaiveBayesModel) this.model).addAll((List)featureVectors);
    }
}
