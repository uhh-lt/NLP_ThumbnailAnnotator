package nlp.floschne.thumbnailAnnotator.wsd.classifier;

import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@ToString
public class Prediction implements Comparable<Prediction> {
    private Label mostProbable;
    private Double highestProbability;
    private Map<Label, Double> classProbabilities;
    private Map<Label, List<Pair<Object, Double>>> influentialFeatures;

    public Prediction() {
        this.classProbabilities = new LinkedHashMap<>();
        this.influentialFeatures = new HashMap<>();
    }

    public void addClass(Label l, Double p, List<Pair<Object, Double>> mostInfluentialFeatures) {
        this.classProbabilities.put(l, p);
        this.influentialFeatures.put(l, mostInfluentialFeatures);
        this.sortByValueDesc();
    }

    // NOT idempotent! don't call this twice!!
    public void normalize() {
        Double factor = this.calcNormalizationFactor();
        classProbabilities.replaceAll((label, prob) -> prob / factor);
        this.highestProbability /= factor;
    }

    private Double calcNormalizationFactor() {
        Double normFactor = 0.0;
        for (Double p : this.classProbabilities.values())
            normFactor += p;
        return normFactor;
    }

    @Override
    public int compareTo(@NotNull Prediction o) {
        return this.highestProbability.compareTo(o.highestProbability);
    }

    private void sortByValueDesc() {
        this.classProbabilities = this.classProbabilities.entrySet()
                .stream()
                .sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }
}
