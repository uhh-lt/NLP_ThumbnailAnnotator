package nlp.floschne.thumbnailAnnotator.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CrawlerResult {
    private CaptionToken captionToken;
    private List<ThumbnailUrl> thumbnailURLs;
}
