package nlp.floschne.thumbnailAnnotator.core.captionTokenExtractor;

import nlp.floschne.thumbnailAnnotator.core.domain.CaptionToken;
import org.junit.Test;

import java.util.Collections;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

public class CaptionTokenTests {

    @Test
    public void contextEquals() {

        CaptionToken c1 = CaptionToken.createDummyTestingCaptionToken();
        CaptionToken c2 = CaptionToken.createDummyTestingCaptionToken();

        assertTrue(c1.contextEquals(c2));
        assertTrue(c2.contextEquals(c1));

        c1.setUdContext(Collections.emptyList());

        assertFalse(c1.contextEquals(c2));
        assertFalse(c2.contextEquals(c1));

        c2.setUdContext(Collections.emptyList());

        assertTrue(c1.contextEquals(c2));
        assertTrue(c2.contextEquals(c1));
    }
}
