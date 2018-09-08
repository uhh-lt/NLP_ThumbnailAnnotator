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
public class CaptionToken extends DomainObject{

    public enum Type {
        NOUN("Noun"),
        COMPOUND("Compound"),
        PERSON("Person"),
        ORGANIZATION("Organization"),
        LOCATION("Location"),
        DATE("Date");

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
    private Integer beginPosition;
    private Integer endPosition;

    private List<String> posTags;
    private List<String> tokens;
}
