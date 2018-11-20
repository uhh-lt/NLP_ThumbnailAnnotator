package nlp.floschne.thumbnailAnnotator.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Thumbnail extends DomainObject implements Comparable<Thumbnail> {
    protected String url;

    protected Integer priority;

    protected String description;

    protected Long shutterstockId;

    protected List<Category> categories;

    protected List<String> keywords;

    @Override
    public int compareTo(Thumbnail o) {
        if (o == null || o.priority == null)
            return this.priority;
        return o.priority - this.priority;
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class Category {
        private Integer id;
        private String name;
    }
}
