package nlp.floschne.thumbnailAnnotator.wsd.service;

import lombok.extern.slf4j.Slf4j;
import nlp.floschne.thumbnailAnnotator.core.domain.CaptionToken;
import nlp.floschne.thumbnailAnnotator.core.domain.Thumbnail;
import nlp.floschne.thumbnailAnnotator.wsd.featureExtractor.FeatureVector;
import nlp.floschne.thumbnailAnnotator.wsd.featureExtractor.IFeatureExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class WSDService {

    private final IFeatureExtractor featureExtractor;

    @Autowired
    public WSDService(IFeatureExtractor featureExtractor) {
        this.featureExtractor = featureExtractor;

        log.info("WSD Service ready!");
    }

    public List<FeatureVector> extractFeatures(CaptionToken ct, Thumbnail t) {
        List<FeatureVector> featureVectors = new ArrayList<>();
        for (Thumbnail.Category c : t.getCategories())
            featureVectors.add(this.featureExtractor.extractFeatures(ct, t, c.getName()));

        return featureVectors;
    }
}
