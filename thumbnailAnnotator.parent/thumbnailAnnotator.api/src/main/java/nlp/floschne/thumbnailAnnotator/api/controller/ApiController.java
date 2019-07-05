package nlp.floschne.thumbnailAnnotator.api.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import net.sf.extjwnl.JWNLException;
import nlp.floschne.thumbnailAnnotator.api.auth.AuthenticationService;
import nlp.floschne.thumbnailAnnotator.api.dto.AccessKeyDTO;
import nlp.floschne.thumbnailAnnotator.api.dto.AuthenticatedUserInputDTO;
import nlp.floschne.thumbnailAnnotator.api.dto.UserDataDTO;
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
    public ApiController(DBService dbService, AuthenticationService dummyAuthenticationService, WSDService wsdService) throws ResourceInitializationException, JWNLException, IOException, ExecutionException, InterruptedException {
        this.startUpUIMA();
        this.dbService = dbService;
        this.dummyAuthenticationService = dummyAuthenticationService;
        this.wsdService = wsdService;
        log.info("API Controller ready!");
    }

    /**
     * Starts a simple extraction to load all the AnalysisEngines from UIMA
     *
     * @throws ResourceInitializationException
     * @throws JWNLException
     * @throws IOException
     */
    private void startUpUIMA() throws ResourceInitializationException, JWNLException, IOException, ExecutionException, InterruptedException {
        //TODO copy WordNet Stuff to /tmp/thumbnailAnnotator/WNet....
        CaptionTokenExtractor.getInstance().startExtractionOfCaptionTokens(new UserInput("Starting Application!")).get();
    }

    /**
     * This does nothing but redirecting to the Swagger-UI
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    void home(HttpServletResponse response) throws IOException {
        response.sendRedirect("./swagger-ui.html");
    }

    /**
     * Tries to register a user with a given password. Please note, that this is just a dummy implementation that will  later
     * be replaced by Oauth2 + JWT
     *
     * @return true or false when User was registered successfully or not respectively
     */
    @RequestMapping(value = "/register", method = RequestMethod.PUT)
    public boolean register(@RequestBody UserDataDTO userDataDTO) {
        if (this.dummyAuthenticationService.registerUser(userDataDTO.getUsername(), userDataDTO.getPassword())) {
            this.wsdService.createNewModel(userDataDTO.getUsername());
            return true;
        }
        return false;
    }

    /**
     * Tries to login a user with a given password. Please note, that this is just a dummy implementation that will  later
     * be replaced by Oauth2 + JWT
     *
     * @return a access key that the user needs to provide to access the API Resources
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public AccessKeyDTO login(@RequestBody UserDataDTO userDataDTO) {
        return this.dummyAuthenticationService.login(userDataDTO.getUsername(), userDataDTO.getPassword());
    }


    /**
     * Tries to logout a user with a given password. Please note, that this is just a dummy implementation that will  later
     * be replaced by Oauth2 + JWT
     *
     * @param accessKeyDTO The accessKey in form of JSON
     */
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public boolean logout(@RequestBody AccessKeyDTO accessKeyDTO) {
        if (accessKeyDTO.getAccessKey() == null || accessKeyDTO.getAccessKey().isEmpty())
            return false;
        return this.dummyAuthenticationService.logout(accessKeyDTO.getAccessKey());
    }

    private void throwIfNotLoggedIn(String accessKey) throws AuthException {
        if (!this.dummyAuthenticationService.isActive(accessKey))
            throw new AuthException("AccessKey is not authorized!");
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

        // check if the AccessKey is logged in!
        this.throwIfNotLoggedIn(authenticatedUserInputDTO.getAccessKey());

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
        for (CaptionTokenEntity cached : cachedAndUncachedCaptionTokens.getLeft())
            log.info("Using cached " + cached.toString());
        List<CaptionTokenEntity> results = new ArrayList<>(cachedAndUncachedCaptionTokens.getLeft());

        // collect the results from features
        for (Future<CaptionToken> captionTokenFuture : captionTokenFutures) {
            CaptionToken captionToken;
            try {
                // wait no longer than 120 second
                // TODO ConfigVariable
                captionToken = captionTokenFuture.get(120, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                log.error("It too long time (120s) to finish crawling of Thumbnails!");
                throw new ConnectException("It too long time (120s) to finish crawling of Thumbnails!");
            } catch (ExecutionException e) {
                throw e;
            }

            try {
                // save the results in repo
                CaptionTokenEntity result = this.dbService.saveCaptionToken(captionToken, authenticatedUserInputDTO.getAccessKey());
                log.info("Caching results for '" + result + "' for AccessKey'" + authenticatedUserInputDTO.getAccessKey() + "'");

                if (!results.contains(result))
                    results.add(result);
            } catch (Exception e) {
                throw e;
            }

        }
        return results;
    }

    /**
     * Get a single {@link CaptionTokenEntity} by it's ID
     *
     * @param captionTokenId the ID of the {@link CaptionTokenEntity}
     * @return the {@link CaptionTokenEntity} identified by the ID or null if the accessKey is not active
     */
    @RequestMapping(value = "/getCaptionTokenById", method = RequestMethod.GET)
    public CaptionTokenEntity getCaptionToken(@RequestParam("captionTokenId") String captionTokenId,
                                              @RequestParam("accessKey") String accessKey) throws IOException, AuthException {
        // check if the AccessKey is logged in!
        this.throwIfNotLoggedIn(accessKey);

        return this.dbService.findCaptionTokenEntityById(captionTokenId);
    }

    /**
     * Get a single {@link ThumbnailEntity} by it's ID
     *
     * @param thumbnailId the ID of the {@link ThumbnailEntity}
     * @return the {@link ThumbnailEntity} identified by the ID or null if the accessKey is not active
     */
    @RequestMapping(value = "/getThumbnailById", method = RequestMethod.GET)
    public ThumbnailEntity getThumbnail(@RequestParam("thumbnailId") String thumbnailId,
                                        @RequestParam("accessKey") String accessKey) throws IOException, AuthException {
        // check if the AccessKey is logged in!
        this.throwIfNotLoggedIn(accessKey);

        return this.dbService.findThumbnailEntityById(thumbnailId);
    }

    /**
     * Sets the priority of a {@link ThumbnailEntity} identified by the ID to the specified value.
     *
     * @param thumbnailId the ID of the {@link ThumbnailEntity}
     * @param priority    the new priority of the {@link ThumbnailEntity} or null if the accessKey is not active
     * @return the updated {@link ThumbnailEntity}
     */
    @RequestMapping(value = "/setThumbnailPriority", method = RequestMethod.PUT)
    public ThumbnailEntity setThumbnailPriority(@RequestParam("thumbnailId") String thumbnailId,
                                                @RequestParam("priority") Integer priority,
                                                @RequestParam("captionTokenId") String captionTokenId,
                                                @RequestParam("accessKey") String accessKey) throws IOException, AuthException {
        // check if the AccessKey is logged in!
        this.throwIfNotLoggedIn(accessKey);

        // get and update the Thumbnails priority
        ThumbnailEntity te = this.dbService.setThumbnailPriorityById(thumbnailId, priority);
        log.info("Setting priority " + priority + " for Thumbnail[" + thumbnailId + "]");

        // if the priority is set to 1 it's interpreted as a labelling by the user!
        // So we generate the corresponding FeatureVector and "train" the model with it!
        if (te.getPriority() == 1) {

            // get the CaptionToken and Thumbnail to train and extract the FeatureVectors from them
            CaptionToken ct = this.dbService.findCaptionTokenById(captionTokenId);
            Thumbnail t = this.dbService.findThumbnailById(thumbnailId);
            @SuppressWarnings("unchecked")
            List<FeatureVector> featureVectors = (List) this.wsdService.extractFeatures(ct, t);

            // get the model / user name
            String modelName = this.dbService.getUserByAccessKey(accessKey).getUsername();

            // train the user model with the FeatureVectors
            this.wsdService.trainNaiveBayesModel(featureVectors, modelName);
        }

        return te;
    }

    /**
     * Predicts a sorted list of pairs of {@link ThumbnailEntity} IDs and {@link Prediction} representing the Thumbnails that best "match" the {@link CaptionToken}.
     * The first element of the list has the highest prediction score.
     *
     * @param captionTokenId the ID of the CaptionToken
     * @return sorted list of pairs of {@link Thumbnail} and {@link Prediction} representing the Thumbnails that best "match" the {@link CaptionToken}.
     * @throws IOException
     */
    @RequestMapping(value = "/predict", method = RequestMethod.GET)
    public List<Pair<String, Prediction>> predict(@RequestParam("captionTokenId") String captionTokenId,
                                                  @RequestParam("accessKey") String accessKey) throws IOException, AuthException {
        // check if the AccessKey is logged in!
        this.throwIfNotLoggedIn(accessKey);

        // get the model / user name
        String modelName = this.dbService.getUserByAccessKey(accessKey).getUsername();

        // get the CaptionToken
        CaptionToken captionToken = this.dbService.findCaptionTokenById(captionTokenId);

        // start the prediction with the users model
        List<Pair<Thumbnail, Prediction>> preds = this.wsdService.predictCategoryWithModel(captionToken, modelName);

        // convert the Thumbnails to ThumbnailEntity IDs to save data
        List<Pair<String, Prediction>> result = new ArrayList<>();
        for (Pair<Thumbnail, Prediction> pred : preds)
            result.add(Pair.of(this.dbService.findThumbnailEntityByUrl(pred.getKey().getUrl()).getId(), pred.getValue()));

        return result;
    }

    /*
    THE FOLLOWING METHODS ARE NOT USED ANYMORE AND WILL BE REMOVED SOON!
     */

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
     * generates and stores a feature vector for every thumbnail category
     *
     * @param ownerUsername  the username of the owner of the feature vector
     * @param thumbnailId    the id of the Thumbnail the feature vector is created for
     * @param captionTokenId the id of the CaptionToken the feature vector is created for
     */
    @RequestMapping(value = "/generateFeatureVector", method = RequestMethod.PUT)
    @Deprecated
    public List<FeatureVectorEntity> generateFeatureVector(@RequestParam("ownerUsername") String ownerUsername, @RequestParam("thumbnailId") String thumbnailId, @RequestParam("captionTokenId") String captionTokenId) {
        try {
            CaptionToken ct = this.dbService.findCaptionTokenById(captionTokenId);
            Thumbnail t = this.dbService.findThumbnailById(thumbnailId);

            @SuppressWarnings("unchecked")
            List<FeatureVector> featureVectors = (List) this.wsdService.extractFeatures(ct, t);
            return this.dbService.saveFeatureVectors(ownerUsername, featureVectors);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    /**
     * @return All the {@link CaptionTokenEntity} that are saved in the Redis Cache of all users
     */
    @RequestMapping(value = "/getCachedCaptionTokens", method = RequestMethod.GET)
    @Deprecated
    public List<CaptionTokenEntity> getCachedCaptionTokens() {
        return new ArrayList<>(this.dbService.findAllCaptionTokens());
    }


    /**
     * @return All the {@link CaptionTokenEntity} of the given username that are saved in the Redis Cache
     */
    @RequestMapping(value = "/getCachedCaptionTokens/{username}", method = RequestMethod.GET)
    @Deprecated
    public List<CaptionTokenEntity> getCachedCaptionTokensOfUser(@PathVariable String username) throws IOException {
        return new ArrayList<>(this.dbService.findCaptionTokensByUsername(username));
    }

    /**
     * @return All the {@link CaptionTokenEntity} of the given username that are saved in the Redis Cache
     */
    @RequestMapping(value = "/getUsers", method = RequestMethod.GET)
    @Deprecated
    public List<UserEntity> getUsers() {
        return new ArrayList<>(this.dbService.getUsers());
    }


    /**
     * Flushes the Redis Cache for the CaptionTokens
     */
    @RequestMapping(value = "/flushCache", method = RequestMethod.DELETE)
    @Deprecated
    public void flushCache() {
        log.warn("Flushed Cache!");
        this.dbService.deleteAllCaptionTokenEntities();
    }
}
