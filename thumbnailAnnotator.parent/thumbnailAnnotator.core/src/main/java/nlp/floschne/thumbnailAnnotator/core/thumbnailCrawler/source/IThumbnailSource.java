package nlp.floschne.thumbnailAnnotator.core.thumbnailCrawler.source;

import nlp.floschne.thumbnailAnnotator.core.domain.ThumbnailUrl;

import java.io.IOException;
import java.util.List;

/**
 * Base Interface for ThumbnailSources (i.e. Sources / Services / APIs to get Thumbnails from)
 */
public interface IThumbnailSource {

    /**
     * Get a list of URLs (as Strings) to Thumbnails based on a String query
     *
     * @param queryParameter the query parameter of the Thumbnail Query
     * @param limit          the max number of URLs returned
     * @return a list of URLs (as Strings) to Thumbnails
     * @throws IOException if there was an error while querying
     */
    List<ThumbnailUrl> queryThumbnailURLs(String queryParameter, Integer limit) throws IOException;
}
