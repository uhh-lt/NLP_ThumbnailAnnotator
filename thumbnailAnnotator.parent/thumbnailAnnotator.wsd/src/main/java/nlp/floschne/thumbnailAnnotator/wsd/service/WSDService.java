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
    private static final String MODEL_BASE_DIR = "/tmp/thumbnailAnnotator/models/";
    private static final String MODEL_SUFFIX = ".model";
    private static final String GLOBAL_MODEL = "globalModel";

    private final Kryo kryo;

    private final IFeatureExtractor featureExtractor;

    private final IClassifier classifier;

    private final Map<String, IModel> models;

    @Autowired
    public WSDService(IFeatureExtractor featureExtractor, IClassifier classifier) throws FileNotFoundException {
        this.kryo = initKryo();
        this.featureExtractor = featureExtractor;
        this.classifier = classifier;

        this.models = new HashMap<>();
        this.loadGlobalModel();

        log.info("WSD Service ready!");
    }


    private String getModelPath(String modelName) {
        return MODEL_BASE_DIR + modelName + MODEL_SUFFIX;
    }

    private synchronized void setClassifierModel(String modelName) throws FileNotFoundException {
        this.classifier.setModel(this.loadModel(modelName));
    }

    // TODO this needs to get moved to classifier or better IModel...
    // TODO initialize with some basic training data
    private IModel loadGlobalModel() throws FileNotFoundException {
        return this.loadModel(GLOBAL_MODEL);
    }

    private synchronized IModel loadModel(String name) throws FileNotFoundException {
        // if in memory return it
        if (this.models.containsKey(name))
            return this.models.get(name);

        // otherwise try to deserialize or create new
        try {
            this.models.put(name, this.deserializeNaiveBayesModel(getModelPath(name)));
        } catch (FileNotFoundException e) {
            log.warn("Cannot load Model from " + getModelPath(name) + "! Creating new empty model!");
            this.models.put(name, new NaiveBayesModel());
            this.serializeNaiveBayesModel(name);
        }
        return this.models.get(name);
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

    public synchronized void trainGlobalNaiveBayesModel(List<? extends IFeatureVector> featureVectors) throws FileNotFoundException {
        this.trainNaiveBayesModel(featureVectors, GLOBAL_MODEL);
    }

    public synchronized void trainNaiveBayesModel(List<? extends IFeatureVector> featureVectors, String modelName) throws FileNotFoundException {
        StringBuilder sb = new StringBuilder("Training Naive Bayes Model " + modelName + " with ");
        sb.append(featureVectors.size()).append(" FeatureVector(s) for class(es): ");
        for (IFeatureVector f : featureVectors)
            sb.append(f.getLabel().getValue().toString()).append(", ");
        sb.delete(sb.length() - 2, sb.length() - 1);
        log.info(sb.toString());

        this.setClassifierModel(modelName);
        this.classifier.train(featureVectors);
        this.serializeNaiveBayesModel(modelName);
    }

    public synchronized Prediction classifyWithGlobalModel(IFeatureVector featureVector) throws FileNotFoundException {
        this.setClassifierModel(GLOBAL_MODEL);
        return this.classifier.classify(featureVector);
    }

    public synchronized Prediction classifyWithModel(IFeatureVector featureVector, String userName) throws FileNotFoundException {
        this.classifier.setModel(loadModel(userName));
        return this.classifier.classify(featureVector);
    }

    private void serializeGlobalNaiveBayesModel() throws FileNotFoundException {
        this.serializeNaiveBayesModel(GLOBAL_MODEL);
    }

    private void serializeNaiveBayesModel(String modelName) throws FileNotFoundException {
        String path = getModelPath(modelName);
        IModel model = this.models.get(modelName);
        if (model == null)
            throw new RuntimeException("Cannot find model " + modelName);

        log.info("Serializing Naive Bayes Model " + path);
        File file = new File(path);
        file.getParentFile().mkdirs();

        Output out = new Output(new FileOutputStream(file, false));
        this.kryo.writeClassAndObject(out, model);
        out.close();
    }

    synchronized NaiveBayesModel deserializeGlobalNaiveBayesModel() throws FileNotFoundException {
        return this.deserializeNaiveBayesModel(GLOBAL_MODEL);
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

    public synchronized List<Pair<Thumbnail, Prediction>> predictCategoryWithGlobalModel(CaptionToken captionToken) throws FileNotFoundException {
        return this.predictCategoryWithModel(captionToken, GLOBAL_MODEL);
    }

    /**
     * @param captionToken
     * @return a sorted list of pairs of thumbnail and prediction. the first item is the thumbnail with the best prediction
     */
    public synchronized List<Pair<Thumbnail, Prediction>> predictCategoryWithModel(CaptionToken captionToken, String model) throws FileNotFoundException {
        // generate FeatureVectors for every thumbnail
        Map<Thumbnail, List<IFeatureVector>> thumbnailIFeatureVectorsMap = new HashMap<>();
        for (Thumbnail t : captionToken.getThumbnails())
            thumbnailIFeatureVectorsMap.put(t, this.extractFeatures(captionToken, t));


        // classify the FeatureVectors
        List<Pair<Thumbnail, Prediction>> thumbnailPredictions = new ArrayList<>();
        for (Map.Entry<Thumbnail, List<IFeatureVector>> e : thumbnailIFeatureVectorsMap.entrySet())
            for (IFeatureVector featureVector : e.getValue()) {
                Prediction p = this.classifyWithModel(featureVector, model);
                if (!p.equals(Prediction.getZeroPrediction()))
                    thumbnailPredictions.add(Pair.of(e.getKey(), p));
            }

        // sort by highest prediction descending
        if(!thumbnailPredictions.isEmpty()) {
            thumbnailPredictions.sort(Comparator.comparing(Pair::getValue));
            Collections.reverse(thumbnailPredictions);
        }

        return thumbnailPredictions;
    }
}
