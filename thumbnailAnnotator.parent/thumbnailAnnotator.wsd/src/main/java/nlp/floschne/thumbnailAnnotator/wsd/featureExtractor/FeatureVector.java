package nlp.floschne.thumbnailAnnotator.wsd.featureExtractor;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nlp.floschne.thumbnailAnnotator.core.domain.DomainObject;
import nlp.floschne.thumbnailAnnotator.wsd.classifier.Label;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class FeatureVector implements Iterable<Object> {
    protected List<Object> features;

    protected FeatureVector() {
        this.features = new ArrayList<>();
    }

    protected FeatureVector(List<?>... features) {
        this();
        for(List<?> featureList : features)
            this.features.addAll(featureList);
    }

    @NotNull
    @Override
    public Iterator<Object> iterator() {
        return features.iterator();
    }
}
