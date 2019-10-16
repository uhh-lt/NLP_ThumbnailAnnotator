package nlp.floschne.thumbnailAnnotator.api.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDataDTO {
    private String username;
    private String password;
}
