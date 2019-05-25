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
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public abstract class IFeatureVector extends DomainObject implements Iterable<Object> {
    protected Label label;
    protected List<Object> features;

    protected IFeatureVector(Label label) {
        this.label = label;
        this.features = new ArrayList<>();
    }

    protected IFeatureVector(Label label, List<Object> features) {
        this.label = label;
        this.features = features;
    }

    @NotNull
    @Override
    public Iterator<Object> iterator() {
        return features.iterator();
    }
}
