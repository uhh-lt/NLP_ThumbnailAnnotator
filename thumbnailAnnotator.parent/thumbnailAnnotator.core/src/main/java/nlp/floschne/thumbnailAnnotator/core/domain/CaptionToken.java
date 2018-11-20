package nlp.floschne.thumbnailAnnotator.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
}
