package nlp.floschne.thumbnailAnnotator.db.entity;


import lombok.*;
import nlp.floschne.thumbnailAnnotator.core.domain.CaptionToken;
import nlp.floschne.thumbnailAnnotator.core.domain.SentenceContext;
import org.springframework.data.annotation.Reference;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Data
@ToString(callSuper = true)
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

    private SentenceContext sentenceContext;

    private List<String> biGrams;
    private List<String> triGrams;

    private String fullSentence;

    public List<String> getPosTags() {
        return posTags != null ? posTags : new ArrayList<>();
    }

    public List<String> getTokens() {
        return tokens != null ? tokens : new ArrayList<>();
    }

    public List<String> getLemmata() {
        return lemmata != null ? lemmata : new ArrayList<>();
    }

    public List<String> getBiGrams() {
        return biGrams != null ? biGrams : new ArrayList<>();
    }

    public List<String> getTriGrams() {
        return triGrams != null ? triGrams : new ArrayList<>();
    }

    public static CaptionTokenEntity createDummyTestingCaptionTokenEntity() {
        return new CaptionTokenEntity(
                "bigger ship",
                "COMPOUND",
                Arrays.asList("JJ", "NN"),
                Arrays.asList("bigger", "ship"),
                Collections.singletonList(new CaptionToken.UDependency("amod", "bigger", "ship")),
                Collections.singletonList("A ship is a vehicle that swims on the water."),
                Collections.singletonList(ThumbnailEntity.createDummyTestingThumbnailEntity()),
                Arrays.asList("big", "ship"),
                SentenceContext.createDummyEntityTestingSentenceContext(),
                Collections.singletonList("bigger ship"),
                null,
                "bigger ship"
        );
    }
}
