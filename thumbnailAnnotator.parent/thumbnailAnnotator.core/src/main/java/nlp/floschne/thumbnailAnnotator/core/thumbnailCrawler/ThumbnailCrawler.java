package nlp.floschne.thumbnailAnnotator.core.thumbnailCrawler;


import nlp.floschne.thumbnailAnnotator.core.domain.CaptionToken;
import nlp.floschne.thumbnailAnnotator.core.domain.CrawlerResult;
import nlp.floschne.thumbnailAnnotator.core.domain.Thumbnail;
import nlp.floschne.thumbnailAnnotator.core.thumbnailCrawler.source.IThumbnailSource;
import nlp.floschne.thumbnailAnnotator.core.thumbnailCrawler.source.ShutterstockSource;

import java.io.IOException;
import java.net.ConnectException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Crawls for Thumbnails that match to a list of CaptionTokens
 */
public class ThumbnailCrawler {
    private static final Integer MAX_PARALLEL_THREADS = 16;

    /**
     * Agent that crawls Thumbnails for a {@link CaptionToken}
     */
    private class CrawlerAgent implements Callable<CrawlerResult> {
        /**
         * The {@link CaptionToken}  that is processed by this {@link CrawlerAgent}
         */
        private CaptionToken captionToken;
        private int limit;

        /**
         * @param captionToken the input {@link CaptionToken}
         */
        // package private by intention
        CrawlerAgent(CaptionToken captionToken) {
            this.captionToken = captionToken;
            this.limit = 10;
        }

        /**
         * The crawling happens here!
         *
         * @return the result wrapped in a {@link CrawlerResult}
         */
        @Override
        public CrawlerResult call() throws IOException {
            // TODO replace dummy implementation

            List<Thumbnail> thumbnails = thumbnailSource.queryThumbnails(this.captionToken.getValue(), limit);
            if (thumbnails.size() < limit / 2) {
                thumbnails.addAll(thumbnailSource.queryThumbnails(this.captionToken.getTokens().get(this.captionToken.getTokens().size() - 1), limit));
            }
            return new CrawlerResult(this.captionToken, thumbnails);
        }
    }

    /**
     * the managed thread threadPool
     */
    private final ExecutorService threadPool;

    private IThumbnailSource thumbnailSource;

    private static ThumbnailCrawler singleton;

    private ThumbnailCrawler() {
        // default is ShutterstockSource
        this.thumbnailSource = new ShutterstockSource();
        this.threadPool = Executors.newFixedThreadPool(MAX_PARALLEL_THREADS);
    }

    public static ThumbnailCrawler getInstance() {
        if (singleton == null)
            singleton = new ThumbnailCrawler();
        return singleton;
    }

    public Future<CrawlerResult> startCrawlingThumbnails(CaptionToken captionToken) throws IOException {
        try {
            return this.threadPool.submit(new CrawlerAgent(captionToken));
        } catch (Exception e) {
            throw new ConnectException("There was an error while Crawling for Thumbnails!");
        }
    }

}