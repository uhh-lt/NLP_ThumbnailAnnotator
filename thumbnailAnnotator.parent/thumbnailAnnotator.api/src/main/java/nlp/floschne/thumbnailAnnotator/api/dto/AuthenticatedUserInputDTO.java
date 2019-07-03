package nlp.floschne.thumbnailAnnotator.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import nlp.floschne.thumbnailAnnotator.core.domain.UserInput;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AuthenticatedUserInputDTO {
    private String accessKey;
    private UserInput userInput;
}
