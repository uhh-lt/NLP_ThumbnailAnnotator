package nlp.floschne.thumbnailAnnotator.wsd.classifier;

import lombok.Data;
import nlp.floschne.thumbnailAnnotator.wsd.featureExtractor.IFeatureVector;

import java.util.List;

@Data
public abstract class IClassifier {
    protected IModel model;

    public abstract Prediction classify(IFeatureVector featureVector);

    public abstract void train(List<? extends IFeatureVector> featureVectors);
}
