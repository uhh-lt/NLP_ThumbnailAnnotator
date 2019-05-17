package nlp.floschne.thumbnailAnnotator.wsd.featureExtractor;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import nlp.floschne.thumbnailAnnotator.core.domain.SentenceContext;

import java.util.Arrays;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@ToString
public class FeatureVector extends IFeatureVector {

    private List<String> captionTokenPosTags;
    private List<String> captionTokenTokens;
    private List<String> captionTokenLemmata;

    private List<String> captionTokenUdContext;
    private SentenceContext captionTokenSentenceContext;

    //    private List<String> thumbnailDescriptionTokens; TODO include!? requires tokenization -> UIMA
    private List<String> thumbnailKeywords;

    public FeatureVector(String label, List<String> captionTokenPosTags, List<String> captionTokenTokens, List<String> captionTokenLemmata, List<String> captionTokenUdContext, SentenceContext captionTokenSentenceContext, List<String> thumbnailKeywords) {
        super(label);
        this.captionTokenPosTags = captionTokenPosTags;
        this.captionTokenTokens = captionTokenTokens;
        this.captionTokenLemmata = captionTokenLemmata;
        this.captionTokenUdContext = captionTokenUdContext;
        this.captionTokenSentenceContext = captionTokenSentenceContext;
        this.thumbnailKeywords = thumbnailKeywords;
    }

    public static FeatureVector createDummyTestingFeatureVector() {
        return new FeatureVector(
                "labelDummy",
                Arrays.asList("POS3", "POS4"),
                Arrays.asList("Token3", "Token4"),
                Arrays.asList("Lemma3", "Lemma4"),
                Arrays.asList("Lemma3", "Lemma4"),
                SentenceContext.createDummyDomainTestingSentenceContext(),
                Arrays.asList("keyword3", "keyword4")
        );
    }
}
