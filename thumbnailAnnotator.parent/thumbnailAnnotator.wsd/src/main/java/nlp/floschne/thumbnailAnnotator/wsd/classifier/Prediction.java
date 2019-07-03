package nlp.floschne.thumbnailAnnotator.wsd.classifier;

import lombok.Data;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Data
@ToString
public class Prediction implements Comparable<Prediction> {
    private Label pred;
    private Double prob;
    private Map<Label, Double> classProbabilities;

    public Prediction() {
        this.classProbabilities = new HashMap<>();
    }

    public void addClass(Label l, Double p) {
        this.classProbabilities.put(l, p);
    }

    @Override
    public int compareTo(@NotNull Prediction o) {
        return this.prob.compareTo(o.prob);
    }
}
