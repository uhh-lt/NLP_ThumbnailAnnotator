package nlp.floschne.thumbnailAnnotator.api.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import nlp.floschne.thumbnailAnnotator.api.service.DomainSevice;
import nlp.floschne.thumbnailAnnotator.core.captionTokenExtractor.CaptionTokenExtractor;
import nlp.floschne.thumbnailAnnotator.core.domain.CaptionToken;
import nlp.floschne.thumbnailAnnotator.core.domain.CrawlerResult;
import nlp.floschne.thumbnailAnnotator.core.domain.ExtractionResult;
import nlp.floschne.thumbnailAnnotator.core.domain.UserInput;
import nlp.floschne.thumbnailAnnotator.core.thumbnailCrawler.ThumbnailCrawler;
import nlp.floschne.thumbnailAnnotator.db.entity.CrawlerResultEntity;
import nlp.floschne.thumbnailAnnotator.db.service.DBService;
import org.apache.uima.resource.ResourceInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RestController
@CrossOrigin
@RequestMapping(value = "/api/")
@Api(tags = "Thumbnail Annotator API", description = "REST API to access the functionality of the Thumbnail Annotator Service!")
@Slf4j
public class ApiController {


    private final DBService dbService;
    private final DomainSevice domainSevice;

    @Autowired
    public ApiController(DBService dbService, DomainSevice domainSevice) {
        this.dbService = dbService;
        this.domainSevice = domainSevice;
        log.info("API Controller ready!");
    }

    @RequestMapping(value = "/extractCaptionTokens", method = RequestMethod.POST)
    public ExtractionResult extractCaptionTokens(@RequestBody UserInput input) throws ResourceInitializationException, ExecutionException, InterruptedException {
        Future<ExtractionResult> resultFuture = CaptionTokenExtractor.getInstance().startExtractionOfCaptionTokens(input);
        return resultFuture.get();
        //TODO use entities!
    }


    @RequestMapping(value = "/crawlThumbnails", method = RequestMethod.POST)
    public Set<CrawlerResultEntity> crawlThumbnails(@RequestBody UserInput input) throws ResourceInitializationException, ExecutionException, InterruptedException, IOException {
        Future<ExtractionResult> extractionResultFuture = CaptionTokenExtractor.getInstance().startExtractionOfCaptionTokens(input);

        List<CaptionToken> captionTokens = extractionResultFuture.get().getCaptionTokens();
        List<Future<nlp.floschne.thumbnailAnnotator.core.domain.CrawlerResult>> crawlingResultFutures = new ArrayList<>();
        Set<CrawlerResultEntity> crawlerResults = new HashSet<>();
        for (CaptionToken captionToken : captionTokens) {
            // check if captionToken is cached in repo and skip new crawling if so
            if (this.dbService.crawlerResultExistsByCaptionToken(captionToken)) {
                log.info("Using cached results for '" + captionToken.getValue() + "'");
                crawlerResults.add(this.dbService.findCrawlerResultByCaptionToken(captionToken));
                continue;
            }
            crawlingResultFutures.add(ThumbnailCrawler.getInstance().startCrawlingThumbnails(captionToken));
        }


        for (Future<nlp.floschne.thumbnailAnnotator.core.domain.CrawlerResult> crawlerResultFuture : crawlingResultFutures) {
            CrawlerResult crawlerResult = crawlerResultFuture.get();
            // save the results in repo
            log.info("Caching results for '" + crawlerResult.getCaptionToken().getValue() + "'");
            this.dbService.saveCrawlerResult(crawlerResult);
            crawlerResults.add(this.dbService.findCrawlerResultByCaptionToken(crawlerResult.getCaptionToken()));
        }
        return crawlerResults;
    }

    @RequestMapping(value = "/incrementThumbnailPriority/{id}", method = RequestMethod.PUT)
    public void incrementThumbnailPriority(@PathVariable String id) {
        this.dbService.incrementThumbnailPriorityById(id);
    }

    @RequestMapping(value = "/decrementThumbnailPriority/{id}", method = RequestMethod.PUT)
    public void decrementThumbnailPriority(@PathVariable String id) {
        this.dbService.decrementThumbnailPriorityById(id);
    }

    @RequestMapping(value = "/getCachedCrawlerResults", method = RequestMethod.GET)
    public List<CrawlerResultEntity> getCachedCrawlerResults() {
        return new ArrayList<>(this.dbService.findAllCrawlerResult());
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
