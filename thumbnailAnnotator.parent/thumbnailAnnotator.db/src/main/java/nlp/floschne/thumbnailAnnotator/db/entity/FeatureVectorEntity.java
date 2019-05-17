package nlp.floschne.thumbnailAnnotator.db.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nlp.floschne.thumbnailAnnotator.core.domain.SentenceContext;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.Arrays;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("feature_vector")
public class FeatureVectorEntity extends Entity {

    @Indexed
    private String ownerUserName;

    // FIXME there are often more than one category
    /*
    the label
     */
    @Indexed
    private String label;

    /*
    Features
     */
    private List<String> captionTokenPosTags;
    private List<String> captionTokenTokens;
    private List<String> captionTokenLemmata;

    private List<String> captionTokenUdContext;
    private SentenceContext captionTokenSentenceContext;


    private List<String> thumbnailKeywords;

    public static FeatureVectorEntity createDummyTestingFeatureVectorEntity() {
        return new FeatureVectorEntity(
                "DummyUser",
                "DummyLabel",
                Arrays.asList("POS1", "POS2"),
                Arrays.asList("Token1", "Token2"),
                Arrays.asList("Lemma1", "Lemma2"),
                Arrays.asList("Lemma1", "Lemma2"),
                SentenceContext.createDummyEntityTestingSentenceContext(),
                Arrays.asList("keyword1", "keyword2")
        );
    }
}
