package nlp.floschne.thumbnailAnnotator.db.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nlp.floschne.thumbnailAnnotator.core.domain.ThumbnailUrl;
import nlp.floschne.thumbnailAnnotator.core.domain.ThumbnailUrlList;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"thumbnailUrlEntities"})
@RedisHash("thumbnail_url_entity")
public class ThumbnailUrlListEntity {

    @Id
    @Indexed
    String id;

    private @Reference
    List<ThumbnailUrlEntity> thumbnailUrlEntities;

    public ThumbnailUrlListEntity(ThumbnailUrlList urls) {
        this.thumbnailUrlEntities = new ArrayList<>();
        for (ThumbnailUrl url : urls)
            this.thumbnailUrlEntities.add(new ThumbnailUrlEntity(url));
    }
}
