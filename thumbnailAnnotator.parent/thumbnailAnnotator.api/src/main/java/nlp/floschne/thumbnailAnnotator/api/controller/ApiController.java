package nlp.floschne.thumbnailAnnotator.api.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import net.sf.extjwnl.JWNLException;
import nlp.floschne.thumbnailAnnotator.api.auth.AuthenticationService;
import nlp.floschne.thumbnailAnnotator.api.dto.AccessKeyDTO;
import nlp.floschne.thumbnailAnnotator.api.dto.AuthenticatedUserInputDTO;
import nlp.floschne.thumbnailAnnotator.api.dto.LoginDataDTO;
import nlp.floschne.thumbnailAnnotator.core.captionTokenExtractor.CaptionTokenExtractor;
import nlp.floschne.thumbnailAnnotator.core.domain.CaptionToken;
import nlp.floschne.thumbnailAnnotator.core.domain.ExtractorResult;
import nlp.floschne.thumbnailAnnotator.core.domain.Thumbnail;
import nlp.floschne.thumbnailAnnotator.core.domain.UserInput;
import nlp.floschne.thumbnailAnnotator.core.thumbnailCrawler.ThumbnailCrawler;
import nlp.floschne.thumbnailAnnotator.db.entity.CaptionTokenEntity;
import nlp.floschne.thumbnailAnnotator.db.entity.FeatureVectorEntity;
import nlp.floschne.thumbnailAnnotator.db.entity.ThumbnailEntity;
import nlp.floschne.thumbnailAnnotator.db.entity.UserEntity;
import nlp.floschne.thumbnailAnnotator.db.service.DBService;
import nlp.floschne.thumbnailAnnotator.wsd.classifier.Prediction;
import nlp.floschne.thumbnailAnnotator.wsd.featureExtractor.FeatureVector;
import nlp.floschne.thumbnailAnnotator.wsd.service.WSDService;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.uima.resource.ResourceInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.message.AuthException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RestController
@CrossOrigin
@RequestMapping(value = "/")
@Api(tags = "Thumbnail Annotator API", description = "REST API to access the functionality of the Thumbnail Annotator Service!")
@Slf4j
public class ApiController {


    private final DBService dbService;

    private final AuthenticationService dummyAuthenticationService;

    private final WSDService wsdService;

    @Autowired
    public ApiController(DBService dbService, AuthenticationService dummyAuthenticationService, WSDService wsdService) {
        this.dbService = dbService;
        this.dummyAuthenticationService = dummyAuthenticationService;
        this.wsdService = wsdService;
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
     * Tries to login a user with a given password. Please note, that this is just a dummy implementation that will  later
     * be replaced by Oauth2 + JWT
     *
     * @return a access key that the user needs to provide to access the API Resources
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public AccessKeyDTO login(@RequestBody LoginDataDTO loginDataDTO) throws ResourceInitializationException, ExecutionException, InterruptedException, IOException, JWNLException {
        return this.dummyAuthenticationService.login(loginDataDTO.getUsername(), loginDataDTO.getPassword());
    }


    /**
     * Tries to logout a user with a given password. Please note, that this is just a dummy implementation that will  later
     * be replaced by Oauth2 + JWT
     *
     * @param accessKeyDTO The accessKey in form of JSON
     */
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public boolean logout(@RequestBody AccessKeyDTO accessKeyDTO) throws ResourceInitializationException, ExecutionException, InterruptedException, IOException, JWNLException {
        return this.dummyAuthenticationService.logout(accessKeyDTO.getAccessKey());
    }

    /**
     * Extracts the CaptionTokens in the {@link UserInput}
     *
     * @param authenticatedUserInputDTO the AuthUserInputDTO in form of JSON
     * @return the {@link ExtractorResult} for the given {@link UserInput}
     * @throws ResourceInitializationException
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws IOException
     * @throws JWNLException
     */
    @Deprecated
    @RequestMapping(value = "/extractCaptionTokens", method = RequestMethod.POST)
    public ExtractorResult extractCaptionTokens(@RequestBody AuthenticatedUserInputDTO authenticatedUserInputDTO) throws ResourceInitializationException, ExecutionException, InterruptedException, IOException, JWNLException, AuthException {
        if (this.dummyAuthenticationService.isActive(authenticatedUserInputDTO.getAccessKey())) {
            Future<ExtractorResult> resultFuture = CaptionTokenExtractor.getInstance().startExtractionOfCaptionTokens(authenticatedUserInputDTO.getUserInput());
            return resultFuture.get();
        } else throw new AuthException("AccessKey is not authorized!");
        //TODO use entities!
    }

    /**
     * This is the main method of the API it extracts {@link CaptionToken} from a {@link UserInput}, crawls the {@link Thumbnail} for the
     * {@link CaptionToken} and returns the List of extracted {@link CaptionToken}.
     * It also caches the {@link CaptionTokenEntity} and returns the  {@link CaptionTokenEntity} that are already cached.
     *
     * @param authenticatedUserInputDTO the AuthUserInputDTO in form of JSON
     * @return a List of  {@link CaptionTokenEntity} for each CaptionToken that was extracted from the UserInput or null if the accessKey is not active
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws IOException
     * @throws ResourceInitializationException
     * @throws JWNLException
     */
    @RequestMapping(value = "/crawlThumbnails", method = RequestMethod.POST)
    public List<CaptionTokenEntity> crawlThumbnails(@RequestBody AuthenticatedUserInputDTO authenticatedUserInputDTO) throws ExecutionException, InterruptedException, IOException, ResourceInitializationException, JWNLException, AuthException {
        if (this.dummyAuthenticationService.isActive(authenticatedUserInputDTO.getAccessKey())) {
            if (authenticatedUserInputDTO.getUserInput().getValue().isEmpty())
                throw new InputMismatchException("Must input at least a Token!");

            log.info("Crawling Thumbnails for " + authenticatedUserInputDTO.toString());

            // extract the CaptionTokens from UserInput
            Future<ExtractorResult> extractionResultFuture = CaptionTokenExtractor.getInstance().startExtractionOfCaptionTokens(authenticatedUserInputDTO.getUserInput());
            List<CaptionToken> extractedCaptionTokens = extractionResultFuture.get().getCaptionTokens();


            // get the cached and uncached CaptionTokens
            Pair<List<CaptionTokenEntity>, List<CaptionToken>> cachedAndUncachedCaptionTokens = this.dbService.getCachedAndUncachedCaptionTokens(extractedCaptionTokens, authenticatedUserInputDTO.getAccessKey());


            // for every un-cached CaptionToken start crawling for Thumbnails!
            List<Future<CaptionToken>> captionTokenFutures = new ArrayList<>();
            List<CaptionToken> unCachedCaptionTokens = cachedAndUncachedCaptionTokens.getRight();
            for (CaptionToken captionToken : unCachedCaptionTokens) {
                try {
                    captionTokenFutures.add(ThumbnailCrawler.getInstance().startCrawlingThumbnails(captionToken));
                } catch (Exception e) {
                    throw e;
                }
            }


            // create the list of resulting CaptionTokenEntities
            List<CaptionTokenEntity> results = new ArrayList<>(cachedAndUncachedCaptionTokens.getLeft());

            // collect the results from features
            for (Future<CaptionToken> captionTokenFuture : captionTokenFutures) {
                CaptionToken captionToken;
                try {
                    // wait no longer than 120 second
                    // TODO ConfigVariable
                    captionToken = captionTokenFuture.get(120, TimeUnit.SECONDS);
                } catch (TimeoutException e) {
                    throw new ConnectException("It too long time (120s) to finish crawling of Thumbnails!");
                } catch (ExecutionException e) {
                    throw e;
                }

                try {
                    // save the results in repo
                    log.info("Caching results for '" + captionToken + "' for AccessKey'" + authenticatedUserInputDTO.getAccessKey() + "'");
                    CaptionTokenEntity result = this.dbService.saveCaptionToken(captionToken, authenticatedUserInputDTO.getAccessKey());

                    if (!results.contains(result))
                        results.add(result);
                } catch (Exception e) {
                    throw e;
                }

            }
            return results;
        } else throw new AuthException("AccessKey is not authorized!");
    }

    /**
     * Get a single {@link CaptionTokenEntity} by it's ID
     *
     * @param id the ID of the {@link CaptionTokenEntity}
     * @return the {@link CaptionTokenEntity} identified by the ID or null if the accessKey is not active
     */
    @RequestMapping(value = "/getCaptionToken/{id}", method = RequestMethod.GET)
    public CaptionTokenEntity getCaptionToken(@PathVariable String id) throws IOException {
        return this.dbService.findCaptionTokenEntityById(id);
    }

    /**
     * Get a single {@link ThumbnailEntity} by it's ID
     *
     * @param id the ID of the {@link ThumbnailEntity}
     * @return the {@link ThumbnailEntity} identified by the ID or null if the accessKey is not active
     */
    @RequestMapping(value = "/getThumbnail/{id}", method = RequestMethod.GET)
    public ThumbnailEntity getThumbnail(@PathVariable String id) throws IOException {
        return this.dbService.findThumbnailEntityById(id);
    }

    /**
     * Increments the priority of a {@link ThumbnailEntity} identified by the ID.
     *
     * @param id the ID of the {@link ThumbnailEntity}
     * @return the updated {@link ThumbnailEntity} or null if the accessKey is not active
     */
    @Deprecated
    @RequestMapping(value = "/incrementThumbnailPriority/{id}", method = RequestMethod.PUT)
    public ThumbnailEntity incrementThumbnailPriority(@PathVariable String id) throws IOException {
        return this.dbService.incrementThumbnailPriorityById(id);
    }

    /**
     * Decrements the priority of a {@link ThumbnailEntity} identified by the ID.
     *
     * @param id the ID of the {@link ThumbnailEntity}
     * @return the updated {@link ThumbnailEntity} or null if the accessKey is not active
     */
    @Deprecated
    @RequestMapping(value = "/decrementThumbnailPriority/{id}", method = RequestMethod.PUT)
    public ThumbnailEntity decrementThumbnailPriority(@PathVariable String id) throws IOException {
        return this.dbService.decrementThumbnailPriorityById(id);
    }

    /**
     * Sets the priority of a {@link ThumbnailEntity} identified by the ID to the specified value.
     *
     * @param thumbnailId       the ID of the {@link ThumbnailEntity}
     * @param priority the new priority of the {@link ThumbnailEntity} or null if the accessKey is not active
     * @return the updated {@link ThumbnailEntity}
     */
    @RequestMapping(value = "/setThumbnailPriority", method = RequestMethod.PUT)
    public ThumbnailEntity setThumbnailPriority(@RequestParam("thumbnailId") String thumbnailId, @RequestParam("priority") Integer priority, @RequestParam("captionTokenId") String captionTokenId) throws IOException {
        ThumbnailEntity te = this.dbService.setThumbnailPriorityById(thumbnailId, priority);
        log.info("Setting priority " + priority + " for Thumbnail["+thumbnailId+"]");

        // if the priority is set to 1 it's interpreted as a labelling by the user!
        // So we generate the corresponding FeatureVector and "train" the model with it!
        if (te.getPriority() == 1) {
            CaptionToken ct = this.dbService.findCaptionTokenById(captionTokenId);
            Thumbnail t = this.dbService.findThumbnailById(thumbnailId);

            @SuppressWarnings("unchecked")
            List<FeatureVector> featureVectors = (List) this.wsdService.extractFeatures(ct, t);

            this.wsdService.trainNaiveBayesModel(featureVectors);
            this.wsdService.serializeNaiveBayesModel();

            this.dbService.saveFeatureVectors(this.dbService.findOwnerOfCaptionTokenId(captionTokenId).getUsername(), featureVectors);
        }

        return te;
    }

    /**
     * generates and stores a feature vector for every thumbnail category
     *
     * @param ownerUsername  the username of the owner of the feature vector
     * @param thumbnailId    the id of the Thumbnail the feature vector is created for
     * @param captionTokenId the id of the CaptionToken the feature vector is created for
     */
    @RequestMapping(value = "/generateFeatureVector", method = RequestMethod.PUT)
    public void generateFeatureVector(@RequestParam("ownerUsername") String ownerUsername, @RequestParam("thumbnailId") String thumbnailId, @RequestParam("captionTokenId") String captionTokenId) {
        try {
            CaptionToken ct = this.dbService.findCaptionTokenById(captionTokenId);
            Thumbnail t = this.dbService.findThumbnailById(thumbnailId);

            @SuppressWarnings("unchecked")
            List<FeatureVector> featureVectors = (List) this.wsdService.extractFeatures(ct, t);
            this.dbService.saveFeatureVectors(ownerUsername, featureVectors);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * @return All the {@link CaptionTokenEntity} that are saved in the Redis Cache of all users
     */
    @RequestMapping(value = "/getCachedCaptionTokens", method = RequestMethod.GET)
    public List<CaptionTokenEntity> getCachedCaptionTokens() {
        return new ArrayList<>(this.dbService.findAllCaptionTokens());
    }


    /**
     * @return All the {@link CaptionTokenEntity} of the given username that are saved in the Redis Cache
     */
    @RequestMapping(value = "/getCachedCaptionTokens/{username}", method = RequestMethod.GET)
    public List<CaptionTokenEntity> getCachedCaptionTokensOfUser(@PathVariable String username) throws IOException {
        return new ArrayList<>(this.dbService.findCaptionTokensByUsername(username));
    }

    /**
     * @return All the {@link CaptionTokenEntity} of the given username that are saved in the Redis Cache
     */
    @RequestMapping(value = "/getUsers", method = RequestMethod.GET)
    public List<UserEntity> getUsers() {
        return new ArrayList<>(this.dbService.getUsers());
    }


    /**
     * Flushes the Redis Cache for the CaptionTokens
     */
    @RequestMapping(value = "/flushCache", method = RequestMethod.DELETE)
    public void flushCache() {
        log.warn("Flushed Cache!");
        this.dbService.deleteAllCaptionTokenEntities();
    }
}
