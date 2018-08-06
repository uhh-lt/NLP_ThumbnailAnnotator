package nlp.floschne.thumbnailAnnotator.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

/**
 * The result of an extraction of CaptionTokens given an UserInput.
 * I.e. the list of CaptionTokens that were extracted from an UserInput
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExtractionResult {

    private List<CaptionToken> captionTokens;
    private UserInput userInput;

    public boolean addCaptionToken(CaptionToken captionToken) {
        if (this.captionTokens == null)
            this.captionTokens = new ArrayList<>();
        return this.captionTokens.add(captionToken);
    }

}
