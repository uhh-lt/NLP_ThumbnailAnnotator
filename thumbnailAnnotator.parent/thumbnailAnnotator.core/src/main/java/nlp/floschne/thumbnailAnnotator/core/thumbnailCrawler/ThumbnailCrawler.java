package nlp.floschne.thumbnailAnnotator.core.thumbnailCrawler;


import nlp.floschne.thumbnailAnnotator.core.domain.CaptionToken;
import nlp.floschne.thumbnailAnnotator.core.domain.Thumbnail;
import nlp.floschne.thumbnailAnnotator.core.thumbnailCrawler.source.IThumbnailSource;
import nlp.floschne.thumbnailAnnotator.core.thumbnailCrawler.source.ShutterstockSource;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Crawls for Thumbnails that match to a list of CaptionTokens
 */
public class ThumbnailCrawler {
    private static final Integer MAX_PARALLEL_THREADS = 50;

    /**
     * Agent that crawls Thumbnails for a {@link CaptionToken}
     */
    private class CrawlerAgent implements Callable<CaptionToken> {
        /**
         * The {@link CaptionToken}  that is processed by this {@link CrawlerAgent}
         */
        private CaptionToken captionToken;
        private int limitPerCategory;

        /**
         * @param captionToken the input {@link CaptionToken}
         */
        // package private by intention
        CrawlerAgent(CaptionToken captionToken) {
            this.captionToken = captionToken;
            this.limitPerCategory = 2;
        }

        /**
         * The crawling happens here!
         */
        @Override
        public CaptionToken call() throws IOException {
            List<Thumbnail> thumbnails = null;
            try {
                thumbnails = thumbnailSource.queryThumbnails(this.captionToken.getValue(), limitPerCategory);
            } catch (IOException e) {
                e.printStackTrace();
                throw new IOException("There went something wrong while crawling Thumbnails!\n" + e.getMessage());
            }
            assert thumbnails != null;
            // remove tokens if the search was too specific, so that there where to less results
            // TODO remove this since it can produce errors if there is only one token
            if (thumbnails.size() < limitPerCategory / 2) {
                try {
                    thumbnails.addAll(thumbnailSource.queryThumbnails(this.captionToken.getTokens().get(this.captionToken.getTokens().size() - 1), limitPerCategory));
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new IOException("There went something wrong while crawling Thumbnails!");
                }
            }
            this.captionToken.setThumbnails(thumbnails);

            return this.captionToken;
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

    public Future<CaptionToken> startCrawlingThumbnails(CaptionToken captionToken) {
        return this.threadPool.submit(new CrawlerAgent(captionToken));
    }

    public Thumbnail findThumbnailWithCategory(CaptionToken captionToken, String category) throws IOException {
        // TODO implement checks and validate input and output
        List<Thumbnail> thumbnails = this.thumbnailSource.queryThumbnails(captionToken.getValue(), 1, category);
        if (thumbnails == null || thumbnails.isEmpty())
            return null;
        return thumbnails.get(0);
    }

}