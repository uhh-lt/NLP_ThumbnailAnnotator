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
    private Label pred;
    private Double prob;
    private Map<Label, Double> classProbabilities;

    public Prediction() {
        this.classProbabilities = new LinkedHashMap<>();
    }

    public void addClass(Label l, Double p) {
        this.classProbabilities.put(l, p);
        this.sortByValueDesc();
    }

    public static Prediction getZeroPrediction() {
        Prediction p = new Prediction();
        p.prob = 0.0;
        p.pred = new Label<>("NO_PREDICTION");
        return p;
    }

    @Override
    public int compareTo(@NotNull Prediction o) {
        return this.prob.compareTo(o.prob);
    }

    private void sortByValueDesc() {
        this.classProbabilities = this.classProbabilities.entrySet()
                .stream()
                .sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }
}
