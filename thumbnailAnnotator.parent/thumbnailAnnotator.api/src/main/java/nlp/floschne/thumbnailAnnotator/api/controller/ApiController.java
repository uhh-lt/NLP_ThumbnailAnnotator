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
import nlp.floschne.thumbnailAnnotator.db.entity.ThumbnailEntity;
import nlp.floschne.thumbnailAnnotator.db.service.DBService;
import nlp.floschne.thumbnailAnnotator.wsd.classifier.Prediction;
import nlp.floschne.thumbnailAnnotator.wsd.service.WSDService;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.uima.resource.ResourceInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.security.auth.message.AuthException;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

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
    public boolean logout(@RequestBody AccessKeyDTO accessKeyDTO) throws FileNotFoundException {
        if (accessKeyDTO.getAccessKey() == null || accessKeyDTO.getAccessKey().isEmpty())
            return false;

        // merge the model of the user into the global model (if set in settings.properties)
        String modelName = this.dbService.getUserByAccessKey(accessKeyDTO.getAccessKey()).getUsername();
        this.wsdService.mergeModelIntoGlobalModel(modelName);

        return this.dummyAuthenticationService.logout(accessKeyDTO.getAccessKey());
    }

    /**
     * This is the main method of the API it extracts {@link CaptionToken} from a {@link UserInput}, crawls the {@link Thumbnail} for the
     * {@link CaptionToken} and returns the List of extracted {@link CaptionToken}.
     * It also caches the {@link CaptionTokenEntity} and returns the  {@link CaptionTokenEntity} that are already cached.
     *
     * @param authenticatedUserInputDTO the AuthUserInputDTO in form of JSON
     * @return a List of  {@link CaptionTokenEntity} for each CaptionToken that was extracted from the UserInput or null if the accessKey is not active
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


    // TODO
    //  make this POST
    //  error handling if anything goes wrong
    @RequestMapping(value = "/trainModel", method = RequestMethod.POST)
    public void trainModel(@RequestParam("thumbnailId") String thumbnailId,
                           @RequestParam("captionTokenId") String captionTokenId,
                           @RequestParam("accessKey") String accessKey) throws IOException, AuthException {
        // check if the AccessKey is logged in!
        this.throwIfNotLoggedIn(accessKey);

        // get the CaptionToken and Thumbnail to train and extract the FeatureVectors from them
        Thumbnail t = this.dbService.findThumbnailById(thumbnailId);
        CaptionToken ct = this.dbService.findCaptionTokenById(captionTokenId);

        // generate the corresponding FeatureVector and "train" the model with it!

        // get the model / user name
        String modelName = this.dbService.getUserByAccessKey(accessKey).getUsername();

        // train the user model
        this.wsdService.trainNaiveBayesModel(ct, t, modelName);
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
     * Crawls new {@link ThumbnailEntity}s for a {@link CaptionTokenEntity}
     *
     * @param captionTokenId the ID of the {@link CaptionTokenEntity}
     * @return the {@link CaptionTokenEntity} identified by the ID or null if the accessKey is not active
     */
    @RequestMapping(value = "/crawlNewThumbnails", method = RequestMethod.GET)
    public CaptionTokenEntity crawlNewThumbnails(@RequestParam("captionTokenId") String captionTokenId,
                                                 @RequestParam("accessKey") String accessKey) throws IOException, AuthException, ExecutionException {
        // check if the AccessKey is logged in!
        this.throwIfNotLoggedIn(accessKey);

        CaptionToken ct = this.dbService.findCaptionTokenById(captionTokenId);

        ct = crawlThumbnailsForCaptionToken(ct);

        CaptionTokenEntity result = this.dbService.updateThumbnailsOfCaptionTokenEntity(captionTokenId, ct.getThumbnails());

        log.info("Updating Thumbnails for '" + result + "' for AccessKey'" + accessKey + "'");
        return result;
    }

    private CaptionToken crawlThumbnailsForCaptionToken(CaptionToken ct) throws ExecutionException, ConnectException {
        List<Long> beforeIds = ct.getThumbnails().stream().map(Thumbnail::getShutterstockId).collect(Collectors.toList());
        Future<CaptionToken> f = ThumbnailCrawler.getInstance().startCrawlingThumbnails(ct);
        try {
            // wait no longer than 120 second
            // TODO ConfigVariable
            ct = f.get(120, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            log.error("It too long time (120s) to finish crawling of Thumbnails!");
            throw new ConnectException("It too long time (120s) to finish crawling of Thumbnails!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<Long> afterIds = ct.getThumbnails().stream().map(Thumbnail::getShutterstockId).collect(Collectors.toList());

        if (afterIds.containsAll(beforeIds))
            log.warn("Cannot crawl new Thumbnails for '" + ct.toString() + "!'\n This happens most probably due to Shutterstock Licences.");

        return ct;
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
     * Predicts a URL of a Thumbnail and {@link Prediction} from a given {@link CaptionToken}.
     *
     * @param captionTokenId the ID of the CaptionToken
     * @return pair of URL of Thumbnail and {@link Prediction}.
     */
    @RequestMapping(value = "/predict", method = RequestMethod.GET)
    public Pair<String, Prediction> predict(@RequestParam("captionTokenId") String captionTokenId,
                                            @RequestParam("accessKey") String accessKey) throws IOException, AuthException {
        // check if the AccessKey is logged in!
        this.throwIfNotLoggedIn(accessKey);

        log.info("Starting prediction for CaptionToken with id <" + captionTokenId + "> with AccessKey <" + accessKey + ">");

        // get the model / user name
        String modelName = this.dbService.getUserByAccessKey(accessKey).getUsername();

        // get the CaptionToken
        CaptionToken captionToken = this.dbService.findCaptionTokenById(captionTokenId);

        // predict with the users model
        Prediction pred = this.wsdService.classifyWithModel(captionToken, modelName);
        if (pred == null)
            return null;

        // search for Thumbnail with predicted category
        Thumbnail t = ThumbnailCrawler.getInstance().findThumbnailWithCategory(captionToken, pred.getMostProbable().toString());

        // convert the Thumbnail to ThumbnailEntity ID to save data
        return Pair.of(t != null ? t.getUrl() : "", pred);
    }


    private void throwIfNotLoggedIn(String accessKey) throws AuthException {
        if (!this.dummyAuthenticationService.isActive(accessKey))
            throw new AuthException("AccessKey is not authorized!");
    }

    /**
     * Starts a simple extraction to load all the AnalysisEngines from UIMA
     */
    private void startUpUIMA() throws ResourceInitializationException, JWNLException, IOException, ExecutionException, InterruptedException {
        //TODO copy WordNet Stuff to /tmp/thumbnailAnnotator/WNet....
        CaptionTokenExtractor.getInstance().startExtractionOfCaptionTokens(new UserInput("Starting Application!")).get();
    }

}
