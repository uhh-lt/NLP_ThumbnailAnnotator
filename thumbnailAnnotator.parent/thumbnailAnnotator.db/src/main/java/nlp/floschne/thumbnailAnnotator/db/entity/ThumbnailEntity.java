package nlp.floschne.thumbnailAnnotator.db.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import nlp.floschne.thumbnailAnnotator.core.domain.Thumbnail;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.Arrays;
import java.util.List;

@Data
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@RedisHash("thumbnail_entity")
public class ThumbnailEntity extends Entity {

    private String url;

    private String description;

    private Long shutterstockId;

    private List<Thumbnail.Category> categories;

    private List<String> keywords;

    public static ThumbnailEntity createDummyTestingThumbnailEntity() {
        return new ThumbnailEntity(
                "https://image.shutterstock.com/image-photo/big-ship-parked-harbor-260nw-677257045.jpg",
                "desc1",
                13337L,
                Arrays.asList(new Thumbnail.Category(1, "a"), new Thumbnail.Category(2, "b")),
                Arrays.asList("k1", "k2"));
    }
}
