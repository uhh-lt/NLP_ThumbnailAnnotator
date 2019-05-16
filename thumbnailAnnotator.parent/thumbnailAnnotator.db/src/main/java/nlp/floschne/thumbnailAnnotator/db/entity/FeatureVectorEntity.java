package nlp.floschne.thumbnailAnnotator.db.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nlp.floschne.thumbnailAnnotator.core.domain.CaptionToken;
import nlp.floschne.thumbnailAnnotator.core.domain.SentenceContext;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("feature_vector")
public class FeatureVectorEntity {

    @Indexed
    private String id;

    public FeatureVectorEntity(String labelCategory, String ownerUserName, CaptionTokenEntity cte, ThumbnailEntity te) {
        this.labelCategory = labelCategory;

        this.ownerUserName = ownerUserName;

        this.captionTokenLemmata = cte.getLemmata();
        this.captionTokenPosTags = cte.getPosTags();
        this.captionTokenTokens = cte.getTokens();


        this.captionTokenUdContext = new ArrayList<>();
        for (CaptionToken.UDependency ud : cte.getUdContext())
            this.captionTokenUdContext.add(ud.toString());

        this.captionTokenSentenceContext = cte.getSentenceContext();

        this.thumbnailKeywords = te.getKeywords();
    }

    @Indexed
    private String ownerUserName;

    // FIXME there are often more than one category
    /*
    the label
     */
    @Indexed
    private String labelCategory;

    /*
    Features
     */
    private List<String> captionTokenPosTags;
    private List<String> captionTokenTokens;
    private List<String> captionTokenLemmata;

    private List<String> captionTokenUdContext;
    private SentenceContext captionTokenSentenceContext;


    private List<String> thumbnailKeywords;
}
