package nlp.floschne.thumbnailAnnotator.wsd.featureExtractor;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = false)
public class FeatureVector implements Iterable<Object> {
    protected Set<Object> features;

    protected FeatureVector() {
        this.features = new HashSet<>();
    }

    protected FeatureVector(List<?>... features) {
        this();
        for (List<?> featureList : features)
            this.features.addAll(featureList);
    }

    @NotNull
    @Override
    public Iterator<Object> iterator() {
        return features.iterator();
    }
}
