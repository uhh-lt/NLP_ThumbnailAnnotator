package nlp.floschne.thumbnailAnnotator.wsd.featureExtractor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nlp.floschne.thumbnailAnnotator.core.domain.DomainObject;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public abstract class IFeatureVector extends DomainObject {
    protected String label;
}
