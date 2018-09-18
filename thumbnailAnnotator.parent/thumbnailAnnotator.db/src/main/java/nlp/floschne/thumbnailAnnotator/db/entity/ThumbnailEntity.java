package nlp.floschne.thumbnailAnnotator.db.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@RedisHash("thumbnail_url_entity")
public class ThumbnailEntity extends Entity implements Comparable<ThumbnailEntity> {
    @Indexed
    private String url;

    private Integer priority;


    @Override
    public int compareTo(ThumbnailEntity o) {
        if (o == null || o.priority == null)
            return this.priority;
        return o.priority - this.priority;
    }
}
