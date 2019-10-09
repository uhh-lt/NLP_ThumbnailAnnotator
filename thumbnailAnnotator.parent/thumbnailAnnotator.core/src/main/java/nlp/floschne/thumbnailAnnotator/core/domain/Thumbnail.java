package nlp.floschne.thumbnailAnnotator.core.domain;

import lombok.*;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class Thumbnail extends DomainObject {
    protected String url;

    protected String description;

    protected Long shutterstockId;

    protected List<Category> categories;

    protected List<String> keywords;

    public static Thumbnail createDummyTestingThumbnail() {
        return new Thumbnail("https://image.shutterstock.com/image-vector/lupe-magnifying-glass-barcode-serial-260nw-476181607.jpg",
                "desc",
                133437L,
                Arrays.asList(new Thumbnail.Category(3, "c"), new Thumbnail.Category(1, "d")),
                Arrays.asList("k1", "k2"));
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
