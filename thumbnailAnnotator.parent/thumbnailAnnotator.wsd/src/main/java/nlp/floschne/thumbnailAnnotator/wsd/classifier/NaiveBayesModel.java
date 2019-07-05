package nlp.floschne.thumbnailAnnotator.wsd.classifier;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import nlp.floschne.thumbnailAnnotator.wsd.featureExtractor.IFeatureVector;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class NaiveBayesModel extends IModel {

    private Map<Label, List<Object>> classFeatures;

    private Map<Label, Double> classPriors;

    private List<IFeatureVector> featureVectors;

    private boolean isTrained;

    public NaiveBayesModel() {
        this.classFeatures = new HashMap<>();
        this.classPriors = new HashMap<>();
        this.featureVectors = new ArrayList<>();

        this.isTrained = false;
    }

    public NaiveBayesModel(List<? extends IFeatureVector> featureVectors) {
        this();

        for (IFeatureVector featureVector : featureVectors)
            this.add(featureVector);
    }

    private Integer countFeatureVectorsOfClass(Label clazz) {
        AtomicInteger count = new AtomicInteger(0);
        this.featureVectors.parallelStream().forEach(featureVector -> {
            if (featureVector.getLabel().equals(clazz)) count.incrementAndGet();
        });
        return count.get();
    }

    private void updateClassPriors(Label clazz) {
        // P(clazz) = number of fv belonging to class / number of all fv
        this.classPriors.putIfAbsent(clazz, 1.0);
        for (Label c : this.classPriors.keySet())
            this.classPriors.put(c, (this.countFeatureVectorsOfClass(c) / (double) this.featureVectors.size()));
    }

    public void add(IFeatureVector featureVector) {
        Label clazz = featureVector.getLabel();

        // add vector to model
        // TODO what if its exactly the same vector?! Shouldn't we use a Set..?
        this.featureVectors.add(featureVector);

        // update the features per class (all the features that belong to the class)
        this.classFeatures.computeIfPresent(clazz, (label, stringFeatures) -> {
            List<Object> addedFeatures = new ArrayList<>(stringFeatures);
            addedFeatures.addAll(featureVector.getFeatures());
            return addedFeatures;
        });
        this.classFeatures.putIfAbsent(clazz, featureVector.getFeatures());

        // compute the class priors -> number of vectors of the class divided by total number of samples in the model
        this.updateClassPriors(clazz);

        // only if there are more than two classes in the model, the model can be treated as "trained"
        if (this.classPriors.keySet().size() > 1)
            this.isTrained = true;
    }

    public void addAll(List<IFeatureVector> featureVectors) {
        featureVectors.forEach(this::add);
    }

    public Set<Label> getClasses() {
        return this.classPriors.keySet();
    }

    public Double getClassProbability(Label clazz) {
        Double prob = this.classPriors.get(clazz);
        if (prob == null)
            return 0.0;
        return prob;
    }

    public Double computeClassConditionalProbability(Object feature, Label clazz) {
        assert classFeatures.containsKey(clazz);

        // count occurrences of feature in class
        int noOfFeatureInClass = Collections.frequency(classFeatures.get(clazz), feature);

        // total number of features of clazz
        int totalNoOfFeaturesOfClass = this.classFeatures.get(clazz).size();

        // number of unique features of clazz
        int noOfUniqueFeaturesOfClass = new HashSet<>(classFeatures.get(clazz)).size();

        /*
        with laplace smoothing
        P(feature | clazz) = (noOfFeatureInClass + 1) / totalNoOfFeaturesOfClass + noOfUniqueFeaturesOfClass
         */
        return (noOfFeatureInClass + 1.0) / (double) (totalNoOfFeaturesOfClass + noOfUniqueFeaturesOfClass);
    }

    public static NaiveBayesModel merge(NaiveBayesModel m1, NaiveBayesModel m2) {
        NaiveBayesModel merged = new NaiveBayesModel(m1.featureVectors);
        merged.addAll(m2.featureVectors);
        return merged;
    }
}
