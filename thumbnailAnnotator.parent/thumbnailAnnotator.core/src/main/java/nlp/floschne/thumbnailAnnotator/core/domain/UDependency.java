package nlp.floschne.thumbnailAnnotator.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UDependency extends DomainObject {

    String type;
    String governor;
    String dependent;

    @Override
    public String toString() {
        return this.type + "(" + this.governor + "," + this.dependent + ")";
    }
}
