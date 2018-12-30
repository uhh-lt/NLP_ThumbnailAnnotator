package nlp.floschne.thumbnailAnnotator.db.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nlp.floschne.thumbnailAnnotator.core.domain.CaptionToken;
import org.springframework.data.annotation.Reference;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("caption_token_entity")
public class CaptionTokenEntity extends Entity {

    @Indexed
    private String value;
    private String type;

    private List<String> posTags;
    private List<String> tokens;
    private List<CaptionToken.UDependency> udContext;
    private List<String> wordNetSenses;

    @Reference
    private List<ThumbnailEntity> thumbnails;

    private List<String> lemmata;

    public static CaptionTokenEntity createDummyTestingCaptionTokenEnitity() {
        return new CaptionTokenEntity(
                "bigger ship",
                "COMPOUND",
                Arrays.asList("JJ", "NN"),
                Arrays.asList("bigger", "ship"),
                Collections.singletonList(new CaptionToken.UDependency("amod", "bigger", "ship")),
                Collections.singletonList("ship"),
                Collections.singletonList(ThumbnailEntity.createDummyTestingThumbnailEntity()),
                Arrays.asList("big", "ship"));
    }
}
