package nlp.floschne.thumbnailAnnotator.api.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import net.sf.extjwnl.JWNLException;
import nlp.floschne.thumbnailAnnotator.core.captionTokenExtractor.CaptionTokenExtractor;
import nlp.floschne.thumbnailAnnotator.core.domain.CaptionToken;
import nlp.floschne.thumbnailAnnotator.core.domain.ExtractorResult;
import nlp.floschne.thumbnailAnnotator.core.domain.Thumbnail;
import nlp.floschne.thumbnailAnnotator.core.domain.UserInput;
import nlp.floschne.thumbnailAnnotator.core.thumbnailCrawler.ThumbnailCrawler;
import nlp.floschne.thumbnailAnnotator.db.entity.CaptionTokenEntity;
import nlp.floschne.thumbnailAnnotator.db.entity.ThumbnailEntity;
import nlp.floschne.thumbnailAnnotator.db.service.DBService;
import org.apache.uima.resource.ResourceInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@SuppressWarnings("ALL")
@RestController
@CrossOrigin
@RequestMapping(value = "/")
@Api(tags = "Thumbnail Annotator API", description = "REST API to access the functionality of the Thumbnail Annotator Service!")
@Slf4j
public class ApiController {


    private final DBService dbService;

    @Autowired
    public ApiController(DBService dbService) {
        this.dbService = dbService;
        log.info("API Controller ready!");
    }

    /**
     * This does nothing but redirecting to the Swagger-UI
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    void home(HttpServletResponse response) throws IOException {
        response.sendRedirect("./swagger-ui.html");
    }


    /**
     * Extracts the CaptionTokens in the {@link UserInput}
     *
     * @param input the UserInput in form of JSON
     * @return the {@link ExtractorResult} for the given {@link UserInput}
     * @throws ResourceInitializationException
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws IOException
     * @throws JWNLException
     */
    @RequestMapping(value = "/extractCaptionTokens", method = RequestMethod.POST)
    public ExtractorResult extractCaptionTokens(@RequestBody UserInput input) throws ResourceInitializationException, ExecutionException, InterruptedException, IOException, JWNLException {
        Future<ExtractorResult> resultFuture = CaptionTokenExtractor.getInstance().startExtractionOfCaptionTokens(input);
        return resultFuture.get();
        //TODO use entities!
    }

    /**
     * This is the main method of the API it extracts {@link CaptionToken} from a {@link UserInput}, crawls the {@link Thumbnail} for the
     * {@link CaptionToken} and returns the List of extracted {@link CaptionToken}.
     * It also caches the {@link CaptionTokenEntity} and returns the  {@link CaptionTokenEntity} that are already cached.
     *
     * @param input The UserInput in form of JSON
     * @return a List of  {@link CaptionTokenEntity} for each CaptionToken that was extracted from the UserInput
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws IOException
     * @throws ResourceInitializationException
     * @throws JWNLException
     */
    @RequestMapping(value = "/crawlThumbnails", method = RequestMethod.POST)
    public List<CaptionTokenEntity> crawlThumbnails(@RequestBody UserInput input) throws ExecutionException, InterruptedException, IOException, ResourceInitializationException, JWNLException {
        if (input.getValue().isEmpty())
            throw new InputMismatchException("Must input at least a Token!");

        // extract the CaptionTokens from UserInput
        Future<ExtractorResult> extractionResultFuture = CaptionTokenExtractor.getInstance().startExtractionOfCaptionTokens(input);
        List<CaptionToken> captionTokens = extractionResultFuture.get().getCaptionTokens();

        // list of CaptionTokenEntities that are already cached or get cached and will be returned
        List<CaptionTokenEntity> captionTokenEntities = new ArrayList<>();

        List<Future<CaptionToken>> captionTokenFutures = new ArrayList<>();

        // for every extracted CaptionToken
        for (CaptionToken captionToken : captionTokens) {
            // check if CaptionTokenEntity that matches the captionToken is cached in repo and skip new crawling if so
            CaptionTokenEntity result = this.dbService.findBestMatchingCaptionTokenByUDContext(captionToken);
            if (result != null) {
                log.info("Using cached results for '" + captionToken + "'");
                if (!captionTokenEntities.contains(result))
                    captionTokenEntities.add(result);
                continue;
            }

            // no cached CaptionTokenEntity for the CaptionToken -> start crawling
            try {
                captionTokenFutures.add(ThumbnailCrawler.getInstance().startCrawlingThumbnails(captionToken));
            } catch (Exception e) {
                throw e;
            }
        }


        // get the CaptionTokens from the Futures, cache them and add them to the final results
        for (Future<CaptionToken> captionTokenFuture : captionTokenFutures) {
            CaptionToken captionToken = null;
            try {
                // wait no longer than 5 second
                // TODO ConfigVariable
                captionToken = captionTokenFuture.get(10, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                throw new ConnectException("It too long time (10s) to finish crawling of Thumbnails!");
            } catch (ExecutionException e) {
                throw e;
            }

            try {
                // save the results in repo
                log.info("Caching results for '" + captionToken + "'");
                CaptionTokenEntity result = this.dbService.saveCaptionToken(captionToken);

                if (!captionTokenEntities.contains(result))
                    captionTokenEntities.add(result);
            } catch (Exception e) {
                throw e;
            }

        }

        return captionTokenEntities;
    }

    /**
     * Get a single {@link CaptionTokenEntity} by it's ID
     *
     * @param id the ID of the {@link CaptionTokenEntity}
     * @return the {@link CaptionTokenEntity} identified by the ID
     */
    @RequestMapping(value = "/getCrawlerResult/{id}", method = RequestMethod.GET)
    public CaptionTokenEntity getCaptionToken(@PathVariable String id) throws IOException {
        return this.dbService.findCaptionTokenById(id);
    }

    /**
     * Increments the Priority of a {@link ThumbnailEntity} identified by the ID.
     *
     * @param id the ID of the {@link ThumbnailEntity}
     * @return the {@link ThumbnailEntity}
     */
    @RequestMapping(value = "/incrementThumbnailPriority/{id}", method = RequestMethod.PUT)
    public ThumbnailEntity incrementThumbnailPriority(@PathVariable String id) throws IOException {
        return this.dbService.incrementThumbnailPriorityById(id);
    }

    /**
     * Decrements the Priority of a {@link ThumbnailEntity} identified by the ID.
     *
     * @param id the ID of the {@link ThumbnailEntity}
     * @return the {@link ThumbnailEntity}
     */
    @RequestMapping(value = "/decrementThumbnailPriority/{id}", method = RequestMethod.PUT)
    public ThumbnailEntity decrementThumbnailPriority(@PathVariable String id) throws IOException {
        return this.dbService.decrementThumbnailPriorityById(id);
    }

    /**
     * @return All the {@link CaptionTokenEntity} that are saved in the Redis Cache
     */
    @RequestMapping(value = "/getCachedCaptionTokens", method = RequestMethod.GET)
    public List<CaptionTokenEntity> getCachedCrawlerResults() {
        return new ArrayList<>(this.dbService.findAllCaptionTokens());
    }

    /**
     * Flushes the Redis Cache
     */
    @RequestMapping(value = "/flushCache", method = RequestMethod.DELETE)
    public void flushCache() {
        log.warn("Flushed Cache!");
        this.dbService.deleteAllCrawlerResultEntities();
    }
}
