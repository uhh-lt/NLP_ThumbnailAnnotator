package nlp.floschne.thumbnailAnnotator.wsd.service;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import lombok.extern.slf4j.Slf4j;
import nlp.floschne.thumbnailAnnotator.core.domain.CaptionToken;
import nlp.floschne.thumbnailAnnotator.core.domain.SentenceContext;
import nlp.floschne.thumbnailAnnotator.core.domain.Thumbnail;
import nlp.floschne.thumbnailAnnotator.wsd.classifier.IClassifier;
import nlp.floschne.thumbnailAnnotator.wsd.classifier.Label;
import nlp.floschne.thumbnailAnnotator.wsd.classifier.NaiveBayesModel;
import nlp.floschne.thumbnailAnnotator.wsd.classifier.Prediction;
import nlp.floschne.thumbnailAnnotator.wsd.featureExtractor.FeatureVector;
import nlp.floschne.thumbnailAnnotator.wsd.featureExtractor.IFeatureExtractor;
import nlp.floschne.thumbnailAnnotator.wsd.featureExtractor.IFeatureVector;
import nlp.floschne.thumbnailAnnotator.wsd.featureExtractor.MyTestFeatureVector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Service
@Slf4j
public class WSDService {
    private final Kryo kryo;

    private final IFeatureExtractor featureExtractor;

    private final IClassifier classifier;

    @Autowired
    public WSDService(IFeatureExtractor featureExtractor, IClassifier classifier) {
        this.featureExtractor = featureExtractor;
        this.classifier = classifier;
        this.kryo = initKryo();

        log.info("WSD Service ready!");
    }

    private Kryo initKryo() {
        Kryo kryo = new Kryo();
        kryo.register(ArrayList.class);
        kryo.register(java.util.Arrays.asList().getClass());
        kryo.register(HashSet.class);
        kryo.register(HashMap.class);
        kryo.register(Object.class);
        kryo.register(SentenceContext.class);
        kryo.register(Label.class);
        kryo.register(FeatureVector.class);
        kryo.register(MyTestFeatureVector.class);
        kryo.register(NaiveBayesModel.class);

        return kryo;
    }

    public List<IFeatureVector> extractFeatures(CaptionToken ct, Thumbnail t) {
        List<IFeatureVector> featureVectors = new ArrayList<>();
        for (Thumbnail.Category c : t.getCategories())
            featureVectors.add(this.featureExtractor.extractFeatures(ct, t, c.getName()));

        return featureVectors;
    }

    public NaiveBayesModel trainNaiveBayesModel(List<? extends IFeatureVector> featureVectors) {
        return (NaiveBayesModel) this.classifier.train(featureVectors);
    }

    public Prediction classify(NaiveBayesModel model, IFeatureVector featureVector) {
        return this.classifier.classify(model, featureVector);
    }

    public void serializeNaiveBayesModel(NaiveBayesModel model, String path) throws FileNotFoundException {
        File file = new File(path);
        file.getParentFile().mkdirs();

        Output out = new Output(new FileOutputStream(file, false));
        this.kryo.writeClassAndObject(out, model);
        out.close();
    }

    public NaiveBayesModel deserializeNaiveBayesModel(String path) throws FileNotFoundException {
        Input input = new Input(new FileInputStream(path));
        Object model = this.kryo.readClassAndObject(input);
        input.close();

        assert model instanceof NaiveBayesModel;
        return (NaiveBayesModel) model;
    }
}
