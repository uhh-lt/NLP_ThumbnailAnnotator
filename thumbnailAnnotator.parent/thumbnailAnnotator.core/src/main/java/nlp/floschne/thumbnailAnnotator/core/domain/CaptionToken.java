package nlp.floschne.thumbnailAnnotator.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CaptionToken extends DomainObject {

    public enum Type {
        NOUN("Noun"),
        COMPOUND("Compound"),
        PERSON("Person"),
        ORGANIZATION("Organization"),
        LOCATION("Location");

        private final String type;

        Type(final String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return type;
        }
    }

    private String value;
    private Type type;

    private List<String> posTags;
    private List<String> tokens;
    private List<UDependency> udContext;
    private List<String> wordNetSenses;

    private List<Thumbnail> thumbnails;

    // TODO Sentence context
    // TODO BoW left, right context

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class UDependency extends DomainObject {

        String type;
        String governor;
        String dependent;

        @Override
        public String toString() {
            return this.type + "(" + this.governor + "," + this.dependent + ")";
        }
    }

    public boolean contextEquals(CaptionToken other) {
        return this == other || (this.value.equals(other.value) &&
                this.type.equals(other.type) &&
                this.posTags.equals(other.posTags) &&
                this.tokens.equals(other.tokens) &&
                this.udContext.equals(other.udContext));
    }

    public static CaptionToken createDummyTestingCaptionToken() {
        return new CaptionToken(
                "small car",
                CaptionToken.Type.NOUN,
                Arrays.asList("JJ", "NN"),
                Arrays.asList("small", "car"),
                Collections.singletonList(new UDependency("amod", "small", "car")),
                Collections.singletonList("car"),
                Collections.singletonList(Thumbnail.createDummyTestingThumbnail()));
    }
}
