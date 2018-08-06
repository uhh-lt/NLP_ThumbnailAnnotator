package nlp.floschne.thumbnailAnnotator.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * The input text of a User (i.e. just a wrapper of a String)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInput {
    @NonNull
    private String value;
}
