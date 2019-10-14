package nlp.floschne.thumbnailAnnotator.wsd.service;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import lombok.extern.slf4j.Slf4j;
import nlp.floschne.thumbnailAnnotator.core.domain.CaptionToken;
import nlp.floschne.thumbnailAnnotator.core.domain.SentenceContext;
import nlp.floschne.thumbnailAnnotator.core.domain.Thumbnail;
import nlp.floschne.thumbnailAnnotator.wsd.classifier.IClassifier;
import nlp.floschne.thumbnailAnnotator.wsd.classifier.IModel;
import nlp.floschne.thumbnailAnnotator.wsd.classifier.Label;
import nlp.floschne.thumbnailAnnotator.wsd.classifier.NaiveBayesModel;
import nlp.floschne.thumbnailAnnotator.wsd.classifier.Prediction;
import nlp.floschne.thumbnailAnnotator.wsd.featureExtractor.FeatureVector;
import nlp.floschne.thumbnailAnnotator.wsd.featureExtractor.IFeatureExtractor;
import nlp.floschne.thumbnailAnnotator.wsd.featureExtractor.TrainingFeatureVector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Service
@Slf4j
public class WSDService {
    private final Kryo kryo;

    private final IFeatureExtractor featureExtractor;

    private final IClassifier classifier;

    private final Map<String, IModel> models;

    private Properties properties;

    private String modelBaseDirectory;
    private String globalModelName;
    private String modelSuffix;

    @Autowired
    public WSDService(IFeatureExtractor featureExtractor, IClassifier classifier) throws IOException {
        this.kryo = initKryo();
        this.featureExtractor = featureExtractor;
        this.classifier = classifier;

        this.models = new HashMap<>();

        this.properties = this.loadProperties();
        this.modelBaseDirectory = this.properties.getProperty("model.baseDirectory");
        this.globalModelName = this.properties.getProperty("model.global.name");
        this.modelSuffix = this.properties.getProperty("model.suffix");

        this.loadGlobalModel();
        log.info("WSD Service ready!");
    }

    private Properties loadProperties() throws IOException {
        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        String settingsPath = rootPath + "settings.properties";

        Properties props = new Properties();
        props.load(new FileInputStream(settingsPath));

        log.info("Loaded WSD Module properties");
        return props;
    }


    private String getModelPath(String modelName) {
        return this.modelBaseDirectory + modelName + this.modelSuffix;
    }

    private synchronized void setClassifierModel(String modelName) throws FileNotFoundException {
        this.classifier.setModel(this.loadModel(modelName));
    }

    // TODO this needs to get moved to classifier or better IModel...
    // TODO initialize with some basic training data
    private IModel loadGlobalModel() throws FileNotFoundException {
        return this.loadModel(this.globalModelName);
    }

    private synchronized IModel loadModel(String name) {
        // if in memory return it
        if (this.models.containsKey(name))
            return this.models.get(name);

        // otherwise try to deserialize or create new
        try {
            this.models.put(name, this.deserializeNaiveBayesModel(name));
        } catch (FileNotFoundException e) {
            log.warn("Cannot load Model from " + getModelPath(name) + "! Creating new empty model!");
            this.createNewModel(name);
        }
        return this.models.get(name);
    }

    public void createNewModel(String name) {
        this.models.put(name, new NaiveBayesModel());
        this.serializeNaiveBayesModel(name);
    }

    private Kryo initKryo() {
        Kryo kryo = new Kryo();
        kryo.register(List.class);
        kryo.register(ArrayList.class);
        kryo.register(java.util.Arrays.asList().getClass());
        kryo.register(HashSet.class);
        kryo.register(HashMap.class);
        kryo.register(Object.class);
        kryo.register(SentenceContext.class);
        kryo.register(Label.class);
        kryo.register(TrainingFeatureVector.class);
        kryo.register(NaiveBayesModel.class);

        return kryo;
    }

    // package private by intention
    List<TrainingFeatureVector> extractTrainingFeatures(CaptionToken ct, Thumbnail t) {
        List<TrainingFeatureVector> trainingFeatureVectors = new ArrayList<>();
        for (Thumbnail.Category c : t.getCategories())
            trainingFeatureVectors.add(this.featureExtractor.extractTrainingFeatures(ct, t, c.getName()));

        return trainingFeatureVectors;
    }


    // package private by intention
    FeatureVector extractFeatures(CaptionToken ct) {
        return this.featureExtractor.extractFeatures(ct);
    }

    public synchronized void trainGlobalNaiveBayesModel(List<? extends TrainingFeatureVector> featureVectors) throws FileNotFoundException {
        this.trainNaiveBayesModel(featureVectors, this.globalModelName);
    }

    public synchronized void trainNaiveBayesModel(CaptionToken ct, Thumbnail t, String modelName) throws FileNotFoundException {
        List<TrainingFeatureVector> trainingFeatureVectors = this.extractTrainingFeatures(ct, t);

        this.trainNaiveBayesModel(trainingFeatureVectors, modelName);
    }

    private synchronized void trainNaiveBayesModel(List<? extends TrainingFeatureVector> featureVectors, String modelName) throws FileNotFoundException {
        StringBuilder sb = new StringBuilder("Training Naive Bayes Model " + this.getModelPath(modelName) + " with ");
        sb.append(featureVectors.size()).append(" FeatureVector(s) for class(es): ");
        for (TrainingFeatureVector tfv : featureVectors)
            sb.append(tfv.getLabel().getValue().toString()).append(", ");
        sb.delete(sb.length() - 2, sb.length() - 1);
        log.info(sb.toString());

        this.setClassifierModel(modelName);
        this.classifier.train(featureVectors);
        this.serializeNaiveBayesModel(modelName);
    }

    public synchronized Prediction classifyWithModel(CaptionToken ct, String modelName) {
        FeatureVector featureVector = this.extractFeatures(ct);

        return this.classifyWithModel(featureVector, modelName);
    }

    // package private by intention
    synchronized Prediction classifyWithGlobalModel(FeatureVector featureVector) throws FileNotFoundException {
        this.setClassifierModel(this.globalModelName);
        return this.classifier.classify(featureVector);
    }

    private synchronized Prediction classifyWithModel(FeatureVector featureVector, String modelName) {
        this.classifier.setModel(loadModel(modelName));
        return this.classifier.classify(featureVector);
    }

    private void serializeGlobalNaiveBayesModel() {
        this.serializeNaiveBayesModel(this.globalModelName);
    }

    private void serializeNaiveBayesModel(String modelName) {
        String path = getModelPath(modelName);
        IModel model = this.models.get(modelName);
        if (model == null)
            throw new RuntimeException("Cannot find model " + modelName);

        log.info("Serializing Naive Bayes Model " + path);
        try {
            Output out;
            File file = new File(path);
            file.getParentFile().mkdirs();

            out = new Output(new FileOutputStream(file, false));
            this.kryo.writeClassAndObject(out, model);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            log.error("Cannot serialize model: " + path);
        }
    }

    public synchronized NaiveBayesModel deserializeGlobalNaiveBayesModel() throws FileNotFoundException {
        return this.deserializeNaiveBayesModel(this.globalModelName);
    }

    private synchronized NaiveBayesModel deserializeNaiveBayesModel(String modelName) throws FileNotFoundException {
        String path = getModelPath(modelName);
        log.info("Deserializing Naive Bayes Model from " + path);
        Input input = new Input(new FileInputStream(path));
        Object model = this.kryo.readClassAndObject(input);
        input.close();

        assert model instanceof NaiveBayesModel;
        return (NaiveBayesModel) model;
    }

    public final IClassifier getClassifier() {
        return this.classifier;
    }
}
