package nlp.floschne.thumbnailAnnotator.db.service;

import nlp.floschne.thumbnailAnnotator.core.domain.CaptionToken;
import nlp.floschne.thumbnailAnnotator.core.domain.Thumbnail;
import nlp.floschne.thumbnailAnnotator.db.entity.FeatureVectorEntity;
import nlp.floschne.thumbnailAnnotator.wsd.classifier.NaiveBayesClassifier;
import nlp.floschne.thumbnailAnnotator.wsd.featureExtractor.BasicFeatureExtractor;
import nlp.floschne.thumbnailAnnotator.wsd.featureExtractor.FeatureVector;
import nlp.floschne.thumbnailAnnotator.wsd.service.WSDService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertNotEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
        WSDService.class,
        BasicFeatureExtractor.class,
        NaiveBayesClassifier.class,
        DBService.class})
public class DBServiceTest {


    @Autowired
    WSDService wsdService;

    @Autowired
    DBService dbService;

    @Test
    public void contextLoadsTest() {
        System.out.println("Context loaded successfully!");
    }

    @Test
    public void saveCrawlerResult() {
    }

    @Test
    public void findCrawlerResultById() {
    }

    @Test
    public void findCrawlerResultByCaptionToken() {
    }

    @Test
    public void crawlerResultExistsById() {
    }

    @Test
    public void crawlerResultExistsByCaptionToken() {
    }

    @Test
    public void incrementThumbnailPriorityById() {
    }

    @Test
    public void decrementThumbnailPriorityById() {
    }

    @Test
    public void findAllCrawlerResult() {
    }

    @Test
    public void deleteAllCrawlerResultEntities() {
    }

    @Test
    public void saveCaptionToken() {
    }

    @Test
    public void findCaptionTokenEntityById() {
    }

    @Test
    public void findThumbnailEntityById() {
    }

    @Test
    public void findCaptionTokenById() {
    }

    @Test
    public void findThumbnailById() {
    }

    @Test
    public void findBestMatchingCaptionTokenByUDContext() {
    }

    @Test
    public void findCaptionTokensByUsername() {
    }

    @Test
    public void findCaptionTokensByAccessKey() {
    }

    @Test
    public void findCaptionTokenEntitiesOfAccessKey() {
    }

    @Test
    public void setThumbnailPriorityById() {
    }

    @Test
    public void findAllCaptionTokens() {
    }

    @Test
    public void deleteAllCaptionTokenEntities() {
    }

    @Test
    public void registerUser() {
    }

    @Test
    public void checkPassword() {
    }

    @Test
    public void getUserByAccessKey() {
    }

    @Test
    public void getUserByUsername() {
    }

    @Test
    public void getUsers() {
    }

    @Test
    public void getCachedAndUncachedCaptionTokens() {
    }

    @Test
    public void saveFeatureVectors() {
        CaptionToken c = CaptionToken.createDummyTestingCaptionToken();
        Thumbnail t = Thumbnail.createDummyTestingThumbnail();

        @SuppressWarnings("unchecked")
        List<FeatureVector> featureVectors = (List) this.wsdService.extractFeatures(c, t);

        List<FeatureVectorEntity> featureVectorEntities = this.dbService.saveFeatureVectors("dummy", featureVectors);

        assertEquals(featureVectors.size(), featureVectorEntities.size());

        featureVectorEntities.stream().forEach(fve -> {
            assertEquals("dummy", fve.getOwnerUserName());
            assertNotNull(fve.getId());
            assertNotEquals("", fve.getId());
        });
    }
}