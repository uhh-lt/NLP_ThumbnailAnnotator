package nlp.floschne.thumbnailAnnotator.db.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nlp.floschne.thumbnailAnnotator.core.domain.ThumbnailUrl;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@RedisHash("thumbnail_url_entity")
public class ThumbnailUrlEntity implements Comparable<ThumbnailUrlEntity> {

    @Id
    @Indexed
    private String id;

    @Indexed
    private String url;

    private int priority;

    public ThumbnailUrlEntity(ThumbnailUrl url) {
        this.url = url.getUrl();
        this.priority = url.getPriority();
    }

    @Override
    public int compareTo(@NotNull ThumbnailUrlEntity o) {
        return o.priority - this.priority;
    }
}
