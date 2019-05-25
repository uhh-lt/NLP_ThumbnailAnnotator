package nlp.floschne.thumbnailAnnotator.wsd.featureExtractor;

import nlp.floschne.thumbnailAnnotator.core.domain.CaptionToken;
import nlp.floschne.thumbnailAnnotator.core.domain.Thumbnail;


public interface IFeatureExtractor {
    IFeatureVector extractFeatures(CaptionToken ct, Thumbnail t, String label);
}
