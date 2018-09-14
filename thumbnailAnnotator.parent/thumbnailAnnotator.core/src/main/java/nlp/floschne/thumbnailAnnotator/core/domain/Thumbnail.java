package nlp.floschne.thumbnailAnnotator.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Thumbnail extends DomainObject implements Comparable<Thumbnail> {
    protected String url;

    protected Integer priority;

    @Override
    public int compareTo(Thumbnail o) {
        if (o == null || o.priority == null)
            return this.priority;
        return o.priority - this.priority;
    }
}
