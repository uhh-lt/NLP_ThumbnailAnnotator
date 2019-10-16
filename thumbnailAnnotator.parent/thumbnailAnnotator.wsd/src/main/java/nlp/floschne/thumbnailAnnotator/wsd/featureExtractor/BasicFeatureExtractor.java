package nlp.floschne.thumbnailAnnotator.wsd.featureExtractor;

import nlp.floschne.thumbnailAnnotator.core.domain.CaptionToken;
import nlp.floschne.thumbnailAnnotator.core.domain.Thumbnail;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BasicFeatureExtractor implements IFeatureExtractor {

    @Override
    public TrainingFeatureVector extractTrainingFeatures(CaptionToken ct, Thumbnail t, String label) {

        List<String> captionTokenUdContext = getUDContextFeatures(ct);

        return new TrainingFeatureVector(
                label,
                captionTokenUdContext,
                ct.getSentenceContext().getSLemmata(),
                ct.getSentenceContext().getSTokens(),
                ct.getBiGrams(),
                ct.getTriGrams(),
                t.getKeywords()
        );
    }

    @NotNull
    private List<String> getUDContextFeatures(CaptionToken ct) {
        List<String> captionTokenUdContext = new ArrayList<>();
        for (CaptionToken.UDependency ud : ct.getUdContext())
            captionTokenUdContext.add(ud.toString());
        return captionTokenUdContext;
    }

    @Override
    public FeatureVector extractFeatures(CaptionToken ct) {
        List<String> captionTokenUdContext = getUDContextFeatures(ct);
        return new FeatureVector(
                captionTokenUdContext,
                ct.getSentenceContext().getSLemmata(),
                ct.getBiGrams(),
                ct.getTriGrams());
    }
}
