package nlp.floschne.thumbnailAnnotator.wsd.featureExtractor;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import nlp.floschne.thumbnailAnnotator.core.domain.SentenceContext;
import nlp.floschne.thumbnailAnnotator.wsd.classifier.Label;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@ToString
public class TrainingFeatureVector extends FeatureVector {
    private Label label;

    public TrainingFeatureVector(String label, List<?>... features) {
        super(features);
        this.label = new Label<>(label);
    }
}
