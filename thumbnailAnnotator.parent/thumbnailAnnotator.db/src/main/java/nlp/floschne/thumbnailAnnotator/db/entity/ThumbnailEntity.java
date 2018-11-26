package nlp.floschne.thumbnailAnnotator.db.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nlp.floschne.thumbnailAnnotator.core.domain.Thumbnail;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.Arrays;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@RedisHash("thumbnail_url_entity")
public class ThumbnailEntity extends Entity implements Comparable<ThumbnailEntity> {
    @Indexed
    private String url;

    private Integer priority;

    private String description;

    @Indexed
    protected Long shutterstockId;

    protected List<Thumbnail.Category> categories;

    protected List<String> keywords;

    @Override
    public int compareTo(ThumbnailEntity o) {
        if (o == null || o.priority == null)
            return this.priority;
        return o.priority - this.priority;
    }

    public static ThumbnailEntity createDummyTestingThumbnailEntity() {
        return new ThumbnailEntity(
                "https://image.shutterstock.com/image-photo/big-ship-parked-harbor-260nw-677257045.jpg",
                1,
                "desc1",
                13337L,
                Arrays.asList(new Thumbnail.Category(1, "a"), new Thumbnail.Category(2, "b")),
                Arrays.asList("k1", "k2"));
    }
}
