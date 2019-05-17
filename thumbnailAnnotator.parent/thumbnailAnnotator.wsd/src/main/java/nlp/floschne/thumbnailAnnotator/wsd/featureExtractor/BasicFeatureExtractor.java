package nlp.floschne.thumbnailAnnotator.wsd.featureExtractor;

import nlp.floschne.thumbnailAnnotator.core.domain.CaptionToken;
import nlp.floschne.thumbnailAnnotator.core.domain.Thumbnail;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BasicFeatureExtractor implements IFeatureExtractor {

    @Override
    public FeatureVector extractFeatures(CaptionToken ct, Thumbnail t, String label) {

        List<String> captionTokenUdContext = new ArrayList<>();
        for (CaptionToken.UDependency ud : ct.getUdContext())
            captionTokenUdContext.add(ud.toString());

        return new FeatureVector(
                label,
                ct.getPosTags(),
                ct.getTokens(),
                ct.getLemmata(),
                captionTokenUdContext,
                ct.getSentenceContext(),
                t.getKeywords()
        );
    }

}
