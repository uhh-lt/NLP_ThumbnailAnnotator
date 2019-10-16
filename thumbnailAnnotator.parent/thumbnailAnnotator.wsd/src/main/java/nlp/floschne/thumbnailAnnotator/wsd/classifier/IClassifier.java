package nlp.floschne.thumbnailAnnotator.wsd.classifier;

import lombok.Data;
import nlp.floschne.thumbnailAnnotator.wsd.featureExtractor.FeatureVector;

import java.util.Set;

@Data
public abstract class IClassifier {
    protected IModel model;

    public abstract Prediction classify(FeatureVector featureVector);

    public abstract void train(Set<? extends FeatureVector> featureVectors);
}
