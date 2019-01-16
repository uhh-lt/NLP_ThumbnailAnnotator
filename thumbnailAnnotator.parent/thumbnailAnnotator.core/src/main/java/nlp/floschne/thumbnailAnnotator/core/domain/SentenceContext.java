package nlp.floschne.thumbnailAnnotator.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class SentenceContext {

    private List<String> sentenceTokens;
    private List<String> sentenceLemmata;
    private Pair<Integer, Integer> captionTokenSpan;

    // TODO methods to get the n left and/or right words! (context window)

    public static SentenceContext createDummyDomainTestingSentenceContext() {
        String s = "I like my smaller car a lot .";
        List<String> tokens = Arrays.asList(s.split(" "));
        List<String> lemmata = Arrays.asList("I", "like", "my", "small", "car", "a", "lot", ".");
        Pair<Integer, Integer> captionTokenSpan = Pair.of(3, 4);
        return new SentenceContext(tokens, lemmata, captionTokenSpan);
    }

    public static SentenceContext createDummyEntityTestingSentenceContext() {
        String s = "I like my bigger ship a lot .";
        List<String> tokens = Arrays.asList(s.split(" "));
        List<String> lemmata = Arrays.asList("I", "like", "my", "big", "ship", "a", "lot", ".");
        Pair<Integer, Integer> captionTokenSpan = Pair.of(3, 4);
        return new SentenceContext(tokens, lemmata, captionTokenSpan);
    }
}
