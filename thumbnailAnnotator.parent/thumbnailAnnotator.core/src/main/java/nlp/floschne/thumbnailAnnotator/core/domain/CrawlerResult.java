package nlp.floschne.thumbnailAnnotator.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CrawlerResult {
    private CaptionToken captionToken;
    private List<String> thumbnailURLs;
}
