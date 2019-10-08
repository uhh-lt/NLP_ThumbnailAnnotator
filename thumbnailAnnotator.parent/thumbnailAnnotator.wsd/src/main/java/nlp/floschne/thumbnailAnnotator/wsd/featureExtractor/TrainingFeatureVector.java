package nlp.floschne.thumbnailAnnotator.wsd.featureExtractor;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import nlp.floschne.thumbnailAnnotator.core.domain.SentenceContext;
import nlp.floschne.thumbnailAnnotator.wsd.classifier.Label;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@ToString
public class TrainingFeatureVector extends FeatureVector {
    private Label label;

    private List<String> captionTokenPosTags;
    private List<String> captionTokenTokens;
    private List<String> captionTokenLemmata;

    private List<String> captionTokenUdContext;
    private SentenceContext captionTokenSentenceContext;

    //    private List<String> thumbnailDescriptionTokens; TODO include!? requires tokenization -> UIMA
    private List<String> thumbnailKeywords;

    public TrainingFeatureVector(String label, List<Object> features) {
        super();
        this.label = new Label<>(label);
        this.features = features;
    }

    public TrainingFeatureVector(String label, List<String> captionTokenPosTags, List<String> captionTokenTokens, List<String> captionTokenLemmata, List<String> captionTokenUdContext, SentenceContext captionTokenSentenceContext, List<String> thumbnailKeywords) {
        super();
        this.label = new Label<>(label);
        this.captionTokenPosTags = captionTokenPosTags;
        this.captionTokenTokens = captionTokenTokens;
        this.captionTokenLemmata = captionTokenLemmata;
        this.captionTokenUdContext = captionTokenUdContext;
        this.captionTokenSentenceContext = captionTokenSentenceContext;
        this.thumbnailKeywords = thumbnailKeywords;
        this.features = this.getAllFeatures();
    }

    public static TrainingFeatureVector createDummyTestingTrainingFeatureVector() {
        return new TrainingFeatureVector(
                "labelDummy",
                Arrays.asList("POS3", "POS4"),
                Arrays.asList("Token3", "Token4"),
                Arrays.asList("Lemma3", "Lemma4"),
                Arrays.asList("subj(a, b)", "det(c)"),
                SentenceContext.createDummyDomainTestingSentenceContext(),
                Arrays.asList("keyword3", "keyword4")
        );
    }

    private List<Object> getAllFeatures() {
        List<Object> allFeatures = new ArrayList<>();
        allFeatures.addAll(this.captionTokenPosTags);
        allFeatures.addAll(this.captionTokenTokens);
        allFeatures.addAll(this.captionTokenLemmata);

        allFeatures.addAll(this.captionTokenUdContext);
        allFeatures.addAll(this.captionTokenSentenceContext.getSLemmata());
        allFeatures.addAll(this.captionTokenSentenceContext.getSPosTags());
        allFeatures.addAll(this.captionTokenSentenceContext.getSTokens());

        allFeatures.addAll(this.thumbnailKeywords);

        return allFeatures;
    }
}
