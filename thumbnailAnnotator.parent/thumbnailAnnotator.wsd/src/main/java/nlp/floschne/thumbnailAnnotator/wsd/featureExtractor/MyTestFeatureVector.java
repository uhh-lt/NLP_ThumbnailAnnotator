package nlp.floschne.thumbnailAnnotator.wsd.featureExtractor;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nlp.floschne.thumbnailAnnotator.wsd.classifier.Label;

import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MyTestFeatureVector extends IFeatureVector {
    public MyTestFeatureVector(String label, List<Object> words) {
        super(new Label<>(label), words);
    }
}

