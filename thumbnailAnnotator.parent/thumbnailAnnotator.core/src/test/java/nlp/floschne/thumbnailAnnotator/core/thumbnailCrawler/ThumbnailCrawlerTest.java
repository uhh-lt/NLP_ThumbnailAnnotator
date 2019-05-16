package nlp.floschne.thumbnailAnnotator.core.thumbnailCrawler;

import nlp.floschne.thumbnailAnnotator.core.domain.CaptionToken;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.Assert.*;

public class ThumbnailCrawlerTest {

    @Test
    public void crawlThumbnails() {
        CaptionToken captionToken = CaptionToken.createDummyTestingCaptionToken();

        Future<CaptionToken> resultFuture = null;
        resultFuture = ThumbnailCrawler.getInstance().startCrawlingThumbnails(captionToken);

        CaptionToken ct = null;
        try {
            assert resultFuture != null;
            ct = resultFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        assertNotNull(ct);
        assertFalse(ct.getThumbnails().isEmpty());
        assertEquals(ct, captionToken);
    }

}