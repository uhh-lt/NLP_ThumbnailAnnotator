package nlp.floschne.thumbnailAnnotator.core.thumbnailCrawler;

import nlp.floschne.thumbnailAnnotator.core.domain.CaptionToken;
import nlp.floschne.thumbnailAnnotator.core.domain.CrawlerResult;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.Assert.*;

public class ThumbnailCrawlerTest {

    @Test
    public void crawlThumbnails() {

        List<CaptionToken.UDependency> udContext = new ArrayList<>();
        udContext.add(new CaptionToken.UDependency("amod", "small", "car"));
        CaptionToken captionToken = new CaptionToken(
                "small car",
                CaptionToken.Type.NOUN,
                Arrays.asList("JJ", "NN"),
                Arrays.asList("small", "car"),
                udContext,
                Collections.singletonList("car"));

        Future<CrawlerResult> resultFuture = null;
        try {
            resultFuture = ThumbnailCrawler.getInstance().startCrawlingThumbnails(captionToken);
        } catch (IOException e) {
            e.printStackTrace();
        }

        CrawlerResult crawlerResult = null;
        try {
            assert resultFuture != null;
            crawlerResult = resultFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        assertNotNull(crawlerResult);
        assertFalse(crawlerResult.getThumbnails().isEmpty());
        assertEquals(crawlerResult.getCaptionToken(), captionToken);
    }
}