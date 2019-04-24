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

    public FeatureVectorEntity(CaptionTokenEntity cte, ThumbnailEntity te) {
        this.labelCategory = te.getCategories().get(0).getName();

        this.captionTokenLemmata = cte.getLemmata();
        this.captionTokenPosTags = cte.getPosTags();
        this.captionTokenTokens = cte.getTokens();


        this.captionTokenUdContext = new ArrayList<>();
        for(CaptionToken.UDependency ud : cte.getUdContext())
            this.captionTokenUdContext.add(ud.toString());

        this.captionTokenSentenceContext = cte.getSentenceContext();

        this.thumbnailKeywords = te.getKeywords();
    }

    // FIXME there are often more than one category
    /*
    the label
     */
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
    /*
    TODO
        - think of representation in Python (to apply ML stuff)
            - how to access data in python -> simply connect to RedisDB ?!
     */
    private List<String> sentenceTokens;

}
