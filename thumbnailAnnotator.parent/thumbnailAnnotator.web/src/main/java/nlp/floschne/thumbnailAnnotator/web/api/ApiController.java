package nlp.floschne.thumbnailAnnotator.web.api;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import nlp.floschne.thumbnailAnnotator.core.captionTokenExtractor.CaptionTokenExtractor;
import nlp.floschne.thumbnailAnnotator.core.domain.CaptionToken;
import nlp.floschne.thumbnailAnnotator.core.domain.CrawlerResult;
import nlp.floschne.thumbnailAnnotator.core.domain.ExtractionResult;
import nlp.floschne.thumbnailAnnotator.core.domain.UserInput;
import nlp.floschne.thumbnailAnnotator.core.thumbnailCrawler.ThumbnailCrawler;
import nlp.floschne.thumbnailAnnotator.db.entity.CrawlerResultEntity;
import org.apache.uima.resource.ResourceInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
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
@Slf4j
public class ApiController {


    private final DBService dbService;

    @Autowired
    public ApiController(DBService dbService) {
        this.dbService = dbService;
        log.info("API Controller instantiated!");
    }

    @RequestMapping(value = "/extractCaptionTokens", method = RequestMethod.POST)
    public ExtractionResult extractCaptionTokens(@RequestBody UserInput input) throws ResourceInitializationException, ExecutionException, InterruptedException {
        Future<ExtractionResult> resultFuture = CaptionTokenExtractor.getInstance().startExtractionOfCaptionTokens(input);
        return resultFuture.get();
    }


    @RequestMapping(value = "/crawlThumbnails", method = RequestMethod.POST)
    public List<CrawlerResultEntity> crawlThumbnails(@RequestBody UserInput input) throws ResourceInitializationException, ExecutionException, InterruptedException, IOException {
        Future<ExtractionResult> extractionResultFuture = CaptionTokenExtractor.getInstance().startExtractionOfCaptionTokens(input);

        List<CaptionToken> captionTokens = extractionResultFuture.get().getCaptionTokens();
        List<Future<CrawlerResult>> crawlingResultFutures = new ArrayList<>();
        List<CrawlerResultEntity> crawlerResults = new ArrayList<>();
        for (CaptionToken captionToken : captionTokens) {
            // check if captionToken is cached in repo and skip new crawling if so
            if (this.dbService.crawlerResultEntityExistsById(captionToken.getValue())) {
                log.info("Using cached results for '" + captionToken.getValue() + "'");
                crawlerResults.add(this.dbService.findCrawlerResultEntityById(captionToken.getValue()).get());
                continue;
            }
            crawlingResultFutures.add(ThumbnailCrawler.getInstance().startCrawlingThumbnails(captionToken));
        }


        for (Future<CrawlerResult> crawlerResultFuture : crawlingResultFutures) {
            CrawlerResult crawlerResult = crawlerResultFuture.get();
            CrawlerResultEntity crawlerResultEntity = new CrawlerResultEntity(crawlerResult);
            crawlerResults.add(crawlerResultEntity);
            // save the results in repo
            this.dbService.saveCrawlerResultEntity(crawlerResultEntity);
            log.info("Cached results for '" + crawlerResultEntity.getCaptionTokenValue() + "'");
        }
        return crawlerResults;
    }


    @RequestMapping(value = "/getCachedCrawlerResults", method = RequestMethod.GET)
    public List<CrawlerResultEntity> getCachedCrawlerResults() {
        List<CrawlerResultEntity> crawlerResults = new ArrayList<>();
        this.dbService.findAllCrawlerResultEntities().forEach(crawlerResults::add);
        return crawlerResults;
    }

    @RequestMapping(value = "/flushCache", method = RequestMethod.DELETE)
    public void flushCache() {
        log.warn("Flushed Cache!");
        this.dbService.deleteAllCrawlerResultEntities();
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String helloWorld() {
        return "Hello from ThumbnailAnnotator REST API! Swagger-UI available under <host>:<port>/swagger-ui.html";
    }
}
