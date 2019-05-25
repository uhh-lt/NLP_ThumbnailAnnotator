package nlp.floschne.thumbnailAnnotator.wsd.classifier;

import nlp.floschne.thumbnailAnnotator.wsd.featureExtractor.IFeatureVector;

import java.util.List;

public abstract class IClassifier {
    public abstract Prediction classify(IModel model, IFeatureVector featureVector);

    public abstract IModel train(List<? extends IFeatureVector> featureVectors);
}
