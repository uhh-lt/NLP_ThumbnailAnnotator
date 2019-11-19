package nlp.floschne.thumbnailAnnotator.wsd.classifier;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import nlp.floschne.thumbnailAnnotator.wsd.featureExtractor.TrainingFeatureVector;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class NaiveBayesModel extends IModel {

    private Map<Label, List<Object>> classFeatures;

    private Map<Label, Double> classPriors;

    private Set<TrainingFeatureVector> featureVectors;

    private boolean isTrained;

    public NaiveBayesModel() {
        this.classFeatures = new HashMap<>();
        this.classPriors = new HashMap<>();
        this.featureVectors = new HashSet<>();

        this.isTrained = false;
    }

    public NaiveBayesModel(Set<? extends TrainingFeatureVector> featureVectors) {
        this();

        for (TrainingFeatureVector featureVector : featureVectors)
            this.add(featureVector);
    }

    /**
     * Counts the number of feature vectors that belong to the given class
     * @param clazz the given class
     * @return the number of feature vectors that belong to the given class
     */
    private Integer countFeatureVectorsOfClass(Label clazz) {
        AtomicInteger count = new AtomicInteger(0);
        this.featureVectors.parallelStream().forEach(featureVector -> {
            if (featureVector.getLabel().equals(clazz)) count.incrementAndGet();
        });
        return count.get();
    }

    /**
     * Updates the class prior P(clazz) of the given class.
     * @param clazz the given class
     */
    private void updateClassPriors(Label clazz) {
        // P(clazz) = number of fv belonging to class / number of all samples
        this.classPriors.putIfAbsent(clazz, 1.0);
        this.classPriors.replaceAll((c, v) -> (this.countFeatureVectorsOfClass(c) / (double) this.featureVectors.size()));
    }

    public void add(TrainingFeatureVector featureVector) {
        Label clazz = featureVector.getLabel();

        // add vector to model
        this.featureVectors.add(featureVector);

        // update the features per class (all the features that belong to the class)
        this.classFeatures.computeIfPresent(clazz, (label, stringFeatures) -> {
            List<Object> addedFeatures = new ArrayList<>(stringFeatures);
            addedFeatures.addAll(featureVector.getFeatures());
            return addedFeatures;
        });
        this.classFeatures.putIfAbsent(clazz, new ArrayList<>(featureVector.getFeatures()));

        // compute the class priors -> number of vectors of the class divided by total number of samples in the model
        this.updateClassPriors(clazz);

        // only if there are more than two classes in the model, the model can be treated as "trained"
        if (this.classPriors.keySet().size() > 1)
            this.isTrained = true;
    }

    public void addAll(Set<TrainingFeatureVector> featureVectors) {
        featureVectors.forEach(this::add);
    }

    public Set<Label> getClasses() {
        return this.classPriors.keySet();
    }

    public Double getClassPrior(Label clazz) {
        Double prob = this.classPriors.get(clazz);
        if (prob == null)
            return 0.0;
        return prob;
    }

    public Double computeClassConditionalProbability(Object feature, Label clazz, Boolean useLogits) {
        assert classFeatures.containsKey(clazz);

        // count of co-occurrences of feature_i for the clazz_k
        int noOfFeatureInClass = Collections.frequency(classFeatures.get(clazz), feature);

        // sum of counts of co-occurrences of all features for class_k
        int totalNoOfFeaturesInClazz = this.classFeatures.get(clazz).size();

        /*
        with laplace smoothing
        P(feature | clazz) = (noOfFeatureInClazz + eps) / totalNumberOfFeaturesInClazz
         */
        if(useLogits)
            return Math.log(noOfFeatureInClass + 1e-7) - Math.log(totalNoOfFeaturesInClazz);
        else
            return (noOfFeatureInClass + 1e-7) / ((double) totalNoOfFeaturesInClazz);
    }

    public static NaiveBayesModel merge(NaiveBayesModel m1, NaiveBayesModel m2) {
        NaiveBayesModel merged = new NaiveBayesModel(m1.featureVectors);
        merged.addAll(m2.featureVectors);
        return merged;
    }

    public void mergeWith(NaiveBayesModel other) {
        this.addAll(other.getFeatureVectors());
    }

    private int getMaxFeatureNumberOfClasses() {
        int max = Integer.MIN_VALUE;
        for (Label clazz : this.classFeatures.keySet())
            max = Math.max(max, this.classFeatures.get(clazz).size());
        return max;
    }
}
