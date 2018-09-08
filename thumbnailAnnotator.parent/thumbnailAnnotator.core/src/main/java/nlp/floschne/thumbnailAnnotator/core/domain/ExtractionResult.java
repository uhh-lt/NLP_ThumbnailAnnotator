package nlp.floschne.thumbnailAnnotator.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * The result of an extraction of CaptionTokens given an UserInput.
 * I.e. the list of CaptionTokens that were extracted from an UserInput
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ExtractionResult extends DomainObject {

    private List<CaptionToken> captionTokens;
    private UserInput userInput;

    public void addCaptionToken(CaptionToken captionToken) {
        if (this.captionTokens == null)
            this.captionTokens = new ArrayList<>();
        this.captionTokens.add(captionToken);
    }

}
