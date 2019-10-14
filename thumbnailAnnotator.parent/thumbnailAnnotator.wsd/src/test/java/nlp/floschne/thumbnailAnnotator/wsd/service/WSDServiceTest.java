package nlp.floschne.thumbnailAnnotator.wsd.service;

import nlp.floschne.thumbnailAnnotator.core.domain.CaptionToken;
import nlp.floschne.thumbnailAnnotator.core.domain.Thumbnail;
import nlp.floschne.thumbnailAnnotator.wsd.classifier.NaiveBayesClassifier;
import nlp.floschne.thumbnailAnnotator.wsd.classifier.NaiveBayesModel;
import nlp.floschne.thumbnailAnnotator.wsd.classifier.Prediction;
import nlp.floschne.thumbnailAnnotator.wsd.featureExtractor.BasicFeatureExtractor;
import nlp.floschne.thumbnailAnnotator.wsd.featureExtractor.TrainingFeatureVector;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertNotEquals;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {WSDService.class, BasicFeatureExtractor.class, NaiveBayesClassifier.class})
public class WSDServiceTest {

    @Autowired
    WSDService service;

    @Test
    public void contextLoadsService() {
        System.out.println("Context loaded successfully!");
    }

    @Test
    public void extractFeatures() {
        CaptionToken c = CaptionToken.createDummyTestingCaptionToken();
        Thumbnail t = Thumbnail.createDummyTestingThumbnail();

        List<TrainingFeatureVector> featureVectors = this.service.extractTrainingFeatures(c, t);

        assertFalse(featureVectors.isEmpty());
        assertEquals(featureVectors.size(), t.getCategories().size());

        for (TrainingFeatureVector f : featureVectors) {
            assertNotNull(f);

            if (!(f.getLabel().toString().equals((t.getCategories().get(0).getName())) || f.getLabel().toString().equals((t.getCategories().get(1).getName()))))
                fail("The label of the FeatureVector does not match any of the Thumbnail's categories!");
        }
    }

    @Test
    public void serializeGlobalNaiveBayesModel() throws FileNotFoundException {
        // TODO update test so that it makes sense
        Set<TrainingFeatureVector> vectors = getTestingVectors();
        this.service.trainGlobalNaiveBayesModel(vectors);

        NaiveBayesModel myModel = this.service.deserializeGlobalNaiveBayesModel();

        assertEquals(this.service.getClassifier().getModel(), myModel);

        myModel.setFeatureVectors(null);

        assertNotEquals(this.service.getClassifier().getModel(), myModel);
    }

    public Set<TrainingFeatureVector> getTestingVectors() {
        Set<TrainingFeatureVector> vectors = new HashSet<>();
        vectors.add(new TrainingFeatureVector("+", Arrays.asList("I", "loved", "the", "movie")));
        vectors.add(new TrainingFeatureVector("-", Arrays.asList("poor", "acting")));
        vectors.add(new TrainingFeatureVector("+", Arrays.asList("a", "great", "movie", "good", "movie")));
        vectors.add(new TrainingFeatureVector("-", Arrays.asList("I", "hated", "the", "movie")));
        vectors.add(new TrainingFeatureVector("+", Arrays.asList("great", "acting", "good", "movie")));
        return vectors;
    }

    @Test
    @Ignore // TODO fix this or remove it since it's not representable for the application
    public void classifyTest() throws FileNotFoundException {
        Set<TrainingFeatureVector> vectors = getTestingVectors();
        this.service.trainGlobalNaiveBayesModel(vectors);

        TrainingFeatureVector testVec = new TrainingFeatureVector("-", Arrays.asList("I", "hated", "the", "poor", "acting"));
        Prediction pred = this.service.classifyWithGlobalModel(testVec);

        assertEquals(pred.getMostProbable(), testVec.getLabel());

        testVec = new TrainingFeatureVector("+", Arrays.asList("I", "loved", "the", "great", "movie"));
        pred = this.service.classifyWithGlobalModel(testVec);

        assertEquals(pred.getMostProbable(), testVec.getLabel());
    }
}