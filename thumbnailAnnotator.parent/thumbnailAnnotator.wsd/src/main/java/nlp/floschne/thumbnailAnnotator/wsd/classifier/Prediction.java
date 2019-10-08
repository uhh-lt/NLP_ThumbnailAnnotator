package nlp.floschne.thumbnailAnnotator.wsd.classifier;

import lombok.Data;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@ToString
public class Prediction implements Comparable<Prediction> {
    private Label mostProbable;
    private Double highestProbability;
    private Map<Label, Double> classProbabilities;

    public Prediction() {
        this.classProbabilities = new LinkedHashMap<>();
    }

    public void addClass(Label l, Double p) {
        this.classProbabilities.put(l, p);
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
        for(Double p : this.classProbabilities.values())
            normFactor += p;
        return normFactor;
    }

    public static Prediction getZeroPrediction() {
        Prediction p = new Prediction();
        p.highestProbability = 0.0;
        p.mostProbable = new Label<>("NO_PREDICTION");
        return p;
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
