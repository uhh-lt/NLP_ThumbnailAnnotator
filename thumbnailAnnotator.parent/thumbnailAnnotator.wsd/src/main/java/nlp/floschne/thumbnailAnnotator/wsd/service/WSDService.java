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
import nlp.floschne.thumbnailAnnotator.wsd.classifier.NaiveBayesClassifier;
import nlp.floschne.thumbnailAnnotator.wsd.classifier.NaiveBayesModel;
import nlp.floschne.thumbnailAnnotator.wsd.classifier.Prediction;
import nlp.floschne.thumbnailAnnotator.wsd.featureExtractor.FeatureVector;
import nlp.floschne.thumbnailAnnotator.wsd.featureExtractor.IFeatureExtractor;
import nlp.floschne.thumbnailAnnotator.wsd.featureExtractor.TrainingFeatureVector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

@Service
@Slf4j
public class WSDService {
    private final Kryo kryo;

    private final IFeatureExtractor featureExtractor;

    private final IClassifier classifier;

    private final Map<String, NaiveBayesModel> models;

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

        this.modelBaseDirectory = this.properties.getProperty("wsd.model.baseDirectory");
        this.modelBaseDirectory = this.modelBaseDirectory.replaceFirst("^~", System.getProperty("user.home"));
        this.globalModelName = this.properties.getProperty("wsd.model.global.name");
        this.modelSuffix = this.properties.getProperty("wsd.model.suffix");

        ((NaiveBayesClassifier) classifier).setNormalize(Boolean.parseBoolean(this.properties.getProperty("wsd.classifier.normalize", "true")));
        ((NaiveBayesClassifier) classifier).setUseLogits(Boolean.parseBoolean(this.properties.getProperty("wsd.classifier.logits", "true")));
        ((NaiveBayesClassifier) classifier).setNumberOfInfluentialFeatures(Integer.parseInt(this.properties.getProperty("wsd.classifier.numberOfInfluentialFeatures", "10")));

        this.loadGlobalModel();
        log.info("WSD Service ready!");
    }

    private Properties loadProperties() throws IOException {
        String settingsPath = "settings.properties";

        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(settingsPath);

        Properties props = new Properties();
        props.load(is);

        log.info("Loaded WSD Module properties");
        return props;
    }

    public void mergeModelIntoGlobalModel(String modelName) throws FileNotFoundException {
        if (Boolean.parseBoolean(this.properties.getProperty("wsd.model.global.mergeUserModelOnLogout", "false"))) {
            NaiveBayesModel global = this.loadGlobalModel();
            global.mergeWith(this.loadModel(modelName));
            log.info("Merged '" + modelName + this.modelSuffix + "' into '" + this.globalModelName + this.modelSuffix + "'");
            this.serializeGlobalNaiveBayesModel();
        }
    }

    @PreDestroy
    private void preDestroyCallback() throws FileNotFoundException {
        // merge all active models into global
        if (Boolean.parseBoolean(this.properties.getProperty("wsd.model.global.mergeAllOnExit", "true"))) {
            NaiveBayesModel global = this.loadGlobalModel();
            for (String modelName : this.models.keySet()) {
                if (!modelName.equals(this.globalModelName)) {
                    global.mergeWith(this.loadModel(modelName));
                    log.info("Merged '" + modelName + this.modelSuffix + "' into '" + this.globalModelName + this.modelSuffix + "'");
                }
            }
        }

        // serialize all active models
        if (Boolean.parseBoolean(this.properties.getProperty("wsd.model.global.saveOnExit", "true")))
            for (String modelName : this.models.keySet())
                this.serializeNaiveBayesModel(modelName);

        // TODO think about doing this for all models in the folder and not only the active ones

        log.info("WSD Service shutdown!");
    }


    private String getModelPath(String modelName) {
        return this.modelBaseDirectory + modelName + this.modelSuffix;
    }

    private synchronized void setClassifierModel(String modelName) throws FileNotFoundException {
        this.classifier.setModel(this.loadModel(modelName));
    }

    // TODO this needs to get moved to classifier or better IModel...
    // TODO initialize with some basic training data
    private NaiveBayesModel loadGlobalModel() throws FileNotFoundException {
        return this.loadModel(this.globalModelName);
    }

    private synchronized NaiveBayesModel loadModel(String name) throws FileNotFoundException {
        // if in memory return it
        if (this.models.containsKey(name))
            return this.models.get(name);

        // otherwise try to deserialize or create new
        try {
            this.models.put(name, this.deserializeNaiveBayesModel(name));
            return this.models.get(name);
        } catch (FileNotFoundException e) {
            log.warn("Cannot load Model from " + getModelPath(name) + "!");
            return this.createNewModel(name);
        }
    }

    public NaiveBayesModel createNewModel(String name) {
        log.warn("Creating new model " + getModelPath(name) + "!");
        this.models.put(name, new NaiveBayesModel());
        NaiveBayesModel newModel = this.models.get(name);

        if (Boolean.parseBoolean(this.properties.getProperty("wsd.model.user.initWithGlobalModel", "true"))) {
            log.warn("Initializing new model with global model!");
            try {
                assert newModel != null;
                newModel.mergeWith(this.loadGlobalModel());
            } catch (FileNotFoundException e) {
                log.warn("Cannot load global model from " + getModelPath(name) + "! Creating new empty model!");
            }
        }

        this.serializeNaiveBayesModel(name);
        return newModel;
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

    public synchronized void trainGlobalNaiveBayesModel(Set<? extends TrainingFeatureVector> featureVectors) throws FileNotFoundException {
        this.trainNaiveBayesModel(featureVectors, this.globalModelName);
    }

    public synchronized void trainNaiveBayesModel(CaptionToken ct, Thumbnail t, String modelName) throws FileNotFoundException {
        List<TrainingFeatureVector> trainingFeatureVectors = this.extractTrainingFeatures(ct, t);

        this.trainNaiveBayesModel(new HashSet<>(trainingFeatureVectors), modelName);
    }

    private synchronized void trainNaiveBayesModel(Set<? extends TrainingFeatureVector> featureVectors, String modelName) throws FileNotFoundException {
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

    public synchronized Prediction classifyWithModel(CaptionToken ct, String modelName) throws FileNotFoundException {
        FeatureVector featureVector = this.extractFeatures(ct);

        return this.classifyWithModel(featureVector, modelName);
    }

    // package private by intention
    synchronized Prediction classifyWithGlobalModel(FeatureVector featureVector) throws FileNotFoundException {
        this.setClassifierModel(this.globalModelName);
        return this.classifier.classify(featureVector);
    }

    public synchronized Prediction classifyWithGlobalModel(CaptionToken ct) throws FileNotFoundException {
        FeatureVector featureVector = this.extractFeatures(ct);

        return this.classifyWithGlobalModel(featureVector);
    }

    private synchronized Prediction classifyWithModel(FeatureVector featureVector, String modelName) throws FileNotFoundException {
        this.classifier.setModel(loadModel(modelName));
        return this.classifier.classify(featureVector);
    }

    private void serializeNaiveBayesModel(String modelName) {
        String path = getModelPath(modelName);
        NaiveBayesModel model = this.models.get(modelName);
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

    public synchronized void serializeGlobalNaiveBayesModel() {
        this.serializeNaiveBayesModel(this.globalModelName);
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
