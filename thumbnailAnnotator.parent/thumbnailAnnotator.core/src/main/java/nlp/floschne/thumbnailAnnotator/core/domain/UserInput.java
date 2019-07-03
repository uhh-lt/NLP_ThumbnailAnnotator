package nlp.floschne.thumbnailAnnotator.core.domain;

import lombok.*;

/**
 * The input text of a User (i.e. just a wrapper of a String)
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserInput extends DomainObject {
    @NonNull
    private String value;
}
