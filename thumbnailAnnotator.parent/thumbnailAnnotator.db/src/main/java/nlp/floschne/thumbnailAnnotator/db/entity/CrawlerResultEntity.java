package nlp.floschne.thumbnailAnnotator.db.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nlp.floschne.thumbnailAnnotator.core.domain.CaptionToken;
import org.springframework.data.annotation.Reference;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("crawler_result_entity")
public class CrawlerResultEntity extends Entity {
    @Indexed
    private String captionTokenValue;

    private CaptionToken captionToken;

    @Reference
    private List<ThumbnailUrlEntity> thumbnailUrlList;
}
