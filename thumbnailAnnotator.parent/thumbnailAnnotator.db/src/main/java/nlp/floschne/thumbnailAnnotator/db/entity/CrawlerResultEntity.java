package nlp.floschne.thumbnailAnnotator.db.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nlp.floschne.thumbnailAnnotator.core.domain.CaptionToken;
import nlp.floschne.thumbnailAnnotator.core.domain.CrawlerResult;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Data
@EqualsAndHashCode
@RedisHash("crawler_result_entity")
@NoArgsConstructor
@AllArgsConstructor
public class CrawlerResultEntity {

    @TimeToLive
    private final static long TIME_TO_LIVE = 60 * 60 * 24;

    @Id
    private String captionTokenValue;

    private CaptionToken captionToken;

    @Reference
    private ThumbnailUrlListEntity thumbnailUrlList;

    public CrawlerResultEntity(@NotNull CrawlerResult crawlerResult) {
        this.captionTokenValue = crawlerResult.getCaptionToken().getValue();
        this.captionToken = crawlerResult.getCaptionToken();
        this.thumbnailUrlList = new ThumbnailUrlListEntity(crawlerResult.getThumbnailURLs());
    }
}
