package nlp.floschne.thumbnailAnnotator.wsd.service;

import nlp.floschne.thumbnailAnnotator.core.domain.CaptionToken;
import nlp.floschne.thumbnailAnnotator.core.domain.Thumbnail;
import nlp.floschne.thumbnailAnnotator.wsd.featureExtractor.BasicFeatureExtractor;
import nlp.floschne.thumbnailAnnotator.wsd.featureExtractor.FeatureVector;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.*;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {WSDService.class, BasicFeatureExtractor.class})
public class WSDServiceTest {

    @Autowired
    WSDService service;

    @Test
    public void contextLoadsService() {
        System.out.println("Context loaded successfully!");
    }

    @Test
    public void extractFeatures() {
        CaptionToken c = CaptionToken.createDummyTestingCaptionToken();
        Thumbnail t = Thumbnail.createDummyTestingThumbnail();

        List<FeatureVector> featureVectors = this.service.extractFeatures(c, t);

        assertFalse(featureVectors.isEmpty());
        assertEquals(featureVectors.size(), t.getCategories().size());

        for (FeatureVector f : featureVectors) {
            assertEquals(f.getCaptionTokenLemmata(), c.getLemmata());
            assertEquals(f.getCaptionTokenPosTags(), c.getPosTags());
            assertEquals(f.getCaptionTokenUdContext(), Arrays.asList(c.getUdContext().stream().map(CaptionToken.UDependency::toString).toArray(String[]::new)));
            assertEquals(f.getCaptionTokenSentenceContext(), c.getSentenceContext());
            assertEquals(f.getCaptionTokenTokens(), c.getTokens());
            assertEquals(f.getThumbnailKeywords(), t.getKeywords());

            if (!(f.getLabel().equals((t.getCategories().get(0).getName())) || f.getLabel().equals((t.getCategories().get(1).getName()))))
                fail("The label of the FeatureVector does not match any of the Thumbnail's categories!");
        }
    }

}