package nlp.floschne.thumbnailAnnotator.core.thumbnailCrawler;

import nlp.floschne.thumbnailAnnotator.core.domain.CaptionToken;
import nlp.floschne.thumbnailAnnotator.core.domain.CrawlerResult;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.Assert.*;

public class ThumbnailCrawlerTest {

    @Test
    public void crawlThumbnails() throws IOException, ExecutionException, InterruptedException {
        CaptionToken captionToken = new CaptionToken("ship", CaptionToken.Type.NOUN, Collections.singletonList("NN"), Collections.singletonList("ship"));
        Future<CrawlerResult> resultFuture = ThumbnailCrawler.getInstance().startCrawlingThumbnails(captionToken);
        CrawlerResult crawlerResult = resultFuture.get();
        assertNotNull(crawlerResult);
        assertFalse(crawlerResult.getThumbnails().isEmpty());
        assertEquals(crawlerResult.getCaptionToken(), captionToken);
    }
}