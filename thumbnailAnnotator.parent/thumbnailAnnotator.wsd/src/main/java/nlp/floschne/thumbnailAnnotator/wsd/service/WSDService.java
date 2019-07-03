package nlp.floschne.thumbnailAnnotator.wsd.service;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import lombok.extern.slf4j.Slf4j;
import nlp.floschne.thumbnailAnnotator.core.domain.CaptionToken;
import nlp.floschne.thumbnailAnnotator.core.domain.SentenceContext;
import nlp.floschne.thumbnailAnnotator.core.domain.Thumbnail;
import nlp.floschne.thumbnailAnnotator.wsd.classifier.*;
import nlp.floschne.thumbnailAnnotator.wsd.featureExtractor.FeatureVector;
import nlp.floschne.thumbnailAnnotator.wsd.featureExtractor.IFeatureExtractor;
import nlp.floschne.thumbnailAnnotator.wsd.featureExtractor.IFeatureVector;
import nlp.floschne.thumbnailAnnotator.wsd.featureExtractor.MyTestFeatureVector;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.*;

@Service
@Slf4j
public class WSDService {
    private static final String DEFAULT_MODEL = "/tmp/thumbnailAnnotator/models/defaultModel.bin";

    private final Kryo kryo;

    private final IFeatureExtractor featureExtractor;

    private final IClassifier classifier;

    @Autowired
    public WSDService(IFeatureExtractor featureExtractor, IClassifier classifier) {
        this.kryo = initKryo();
        this.featureExtractor = featureExtractor;
        this.classifier = classifier;
        this.classifier.setModel(initDefaultModel());

        log.info("WSD Service ready!");
    }

    private IModel initDefaultModel() {
        try {
            return this.deserializeNaiveBayesModel(DEFAULT_MODEL);
        } catch (FileNotFoundException e) {
            return new NaiveBayesModel();
        }
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

    public void trainNaiveBayesModel(List<? extends IFeatureVector> featureVectors) {
        StringBuilder sb = new StringBuilder("Training Naive Bayes Model with ");
        sb.append(featureVectors.size()).append(" FeatureVector(s) for class(es): ");
        for (IFeatureVector f : featureVectors)
            sb.append(f.getLabel().getValue().toString()).append(", ");
        sb.delete(sb.length() - 2, sb.length() - 1);
        log.info(sb.toString());
        this.classifier.train(featureVectors);
    }

    public Prediction classify(IFeatureVector featureVector) {
        return this.classifier.classify(featureVector);
    }

    public void serializeNaiveBayesModel() throws FileNotFoundException {
        assert this.classifier.getModel() instanceof NaiveBayesModel;
        this.serializeNaiveBayesModel((NaiveBayesModel) this.classifier.getModel(), DEFAULT_MODEL);
    }

    public void serializeNaiveBayesModel(NaiveBayesModel model, String path) throws FileNotFoundException {
        log.info("Serializing Naive Bayes Model to " + path);
        File file = new File(path);
        file.getParentFile().mkdirs();

        Output out = new Output(new FileOutputStream(file, false));
        this.kryo.writeClassAndObject(out, model);
        out.close();
    }

    public NaiveBayesModel deserializeNaiveBayesModel() throws FileNotFoundException {
        return this.deserializeNaiveBayesModel(DEFAULT_MODEL);
    }

    public NaiveBayesModel deserializeNaiveBayesModel(String path) throws FileNotFoundException {
        log.info("Deserializing Naive Bayes Model from " + path);
        Input input = new Input(new FileInputStream(path));
        Object model = this.kryo.readClassAndObject(input);
        input.close();

        assert model instanceof NaiveBayesModel;
        return (NaiveBayesModel) model;
    }
}
