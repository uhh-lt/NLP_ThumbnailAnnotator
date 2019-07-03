package nlp.floschne.thumbnailAnnotator.wsd.service;

import nlp.floschne.thumbnailAnnotator.core.domain.CaptionToken;
import nlp.floschne.thumbnailAnnotator.core.domain.Thumbnail;
import nlp.floschne.thumbnailAnnotator.wsd.classifier.NaiveBayesClassifier;
import nlp.floschne.thumbnailAnnotator.wsd.classifier.NaiveBayesModel;
import nlp.floschne.thumbnailAnnotator.wsd.classifier.Prediction;
import nlp.floschne.thumbnailAnnotator.wsd.featureExtractor.BasicFeatureExtractor;
import nlp.floschne.thumbnailAnnotator.wsd.featureExtractor.FeatureVector;
import nlp.floschne.thumbnailAnnotator.wsd.featureExtractor.IFeatureVector;
import nlp.floschne.thumbnailAnnotator.wsd.featureExtractor.MyTestFeatureVector;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.*;
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

        List<IFeatureVector> featureVectors = this.service.extractFeatures(c, t);

        assertFalse(featureVectors.isEmpty());
        assertEquals(featureVectors.size(), t.getCategories().size());

        for (IFeatureVector f : featureVectors) {
            assertTrue(f instanceof FeatureVector);
            assertEquals(((FeatureVector) f).getCaptionTokenLemmata(), c.getLemmata());
            assertEquals(((FeatureVector) f).getCaptionTokenPosTags(), c.getPosTags());
            assertEquals(((FeatureVector) f).getCaptionTokenUdContext(), Arrays.asList(c.getUdContext().stream().map(CaptionToken.UDependency::toString).toArray(String[]::new)));
            assertEquals(((FeatureVector) f).getCaptionTokenSentenceContext(), c.getSentenceContext());
            assertEquals(((FeatureVector) f).getCaptionTokenTokens(), c.getTokens());
            assertEquals(((FeatureVector) f).getThumbnailKeywords(), t.getKeywords());

            if (!(f.getLabel().equals((t.getCategories().get(0).getName())) || f.getLabel().equals((t.getCategories().get(1).getName()))))
                fail("The label of the FeatureVector does not match any of the Thumbnail's categories!");
        }
    }

    @Test
    public void serializeNaiveBayesModel() throws FileNotFoundException {
        String modelPath = "/tmp/akk/myModel.bin";

        List<MyTestFeatureVector> vectors = getTestingVectors();
        this.service.trainNaiveBayesModel(vectors);

        this.service.serializeNaiveBayesModel();

        NaiveBayesModel myModel = this.service.deserializeNaiveBayesModel(modelPath);

        assertEquals(this.service.getClassifier().getModel(), myModel);

        myModel.setFeatureVectors(null);

        assertNotEquals(this.service.getClassifier().getModel(), myModel);
    }

    public List<MyTestFeatureVector> getTestingVectors() {
        List<MyTestFeatureVector> vectors = new ArrayList<>();
        vectors.add(new MyTestFeatureVector("+", Arrays.asList("I", "loved", "the", "movie")));
        vectors.add(new MyTestFeatureVector("-", Arrays.asList("poor", "acting")));
        vectors.add(new MyTestFeatureVector("+", Arrays.asList("a", "great", "movie", "good", "movie")));
        vectors.add(new MyTestFeatureVector("-", Arrays.asList("I", "hated", "the", "movie")));
        vectors.add(new MyTestFeatureVector("+", Arrays.asList("great", "acting", "good", "movie")));
        return vectors;
    }

    @Test
    public void classifyTest() {
        List<MyTestFeatureVector> vectors = getTestingVectors();
        this.service.trainNaiveBayesModel(vectors);

        MyTestFeatureVector testVec = new MyTestFeatureVector("-", Arrays.asList("I", "hated", "the", "poor", "acting"));
        Prediction pred = this.service.classify(testVec);

        assertEquals(pred.getPred(), testVec.getLabel());

        testVec = new MyTestFeatureVector("+", Arrays.asList("I", "loved", "the", "great", "movie"));
        pred = this.service.classify(testVec);

        assertEquals(pred.getPred(), testVec.getLabel());
    }
}