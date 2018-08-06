package nlp.floschne.thumbnailAnnotator.web.api;

import io.swagger.annotations.Api;
import nlp.floschne.thumbnailAnnotator.core.captionTokenExtractor.CaptionTokenExtractor;
import nlp.floschne.thumbnailAnnotator.core.domain.CaptionToken;
import nlp.floschne.thumbnailAnnotator.core.domain.CrawlerResult;
import nlp.floschne.thumbnailAnnotator.core.domain.ExtractionResult;
import nlp.floschne.thumbnailAnnotator.core.domain.UserInput;
import nlp.floschne.thumbnailAnnotator.core.thumbnailCrawler.ThumbnailCrawler;
import org.apache.uima.resource.ResourceInitializationException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RestController
@RequestMapping(value = "/api/")
@Api(tags = "Thumbnail Annotator API", description = "REST API to access the functionality of the Thumbnail Annotator Service!")
public class ApiController {

    @RequestMapping(value = "/extractCaptionTokens", method = RequestMethod.POST)
    public ExtractionResult extractCaptionTokens(@RequestBody UserInput input) throws ResourceInitializationException, ExecutionException, InterruptedException {
        Future<ExtractionResult> resultFuture = CaptionTokenExtractor.getInstance().startExtractionOfCaptionTokens(input);
        return resultFuture.get();
    }


    @RequestMapping(value = "/crawlThumbnails", method = RequestMethod.POST)
    public List<CrawlerResult> crawlThumbnails(@RequestBody UserInput input) throws ResourceInitializationException, ExecutionException, InterruptedException, IOException {
        Future<ExtractionResult> extractionResultFuture = CaptionTokenExtractor.getInstance().startExtractionOfCaptionTokens(input);

        List<CaptionToken> captionTokens = extractionResultFuture.get().getCaptionTokens();
        List<Future<CrawlerResult>> crawlingResultFutures = new ArrayList<>();
        for (CaptionToken captionToken : captionTokens)
            crawlingResultFutures.add(ThumbnailCrawler.getInstance().startCrawlingThumbnails(captionToken));

        List<CrawlerResult> crawlerResults = new ArrayList<>();
        for (Future<CrawlerResult> crawlerResultFuture : crawlingResultFutures)
            crawlerResults.add(crawlerResultFuture.get());

        return crawlerResults;
    }


    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String helloWorld() {
        return "Hello from ThumbnailAnnotator REST API! Swagger-UI available under <host>:<port>/swagger-ui.html";
    }
}
