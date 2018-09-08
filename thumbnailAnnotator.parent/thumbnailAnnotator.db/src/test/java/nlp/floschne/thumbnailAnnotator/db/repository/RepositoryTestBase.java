package nlp.floschne.thumbnailAnnotator.db.repository;

import nlp.floschne.thumbnailAnnotator.db.RedisConfig;
import nlp.floschne.thumbnailAnnotator.db.entity.CaptionTokenEntity;
import nlp.floschne.thumbnailAnnotator.db.entity.CrawlerResultEntity;
import nlp.floschne.thumbnailAnnotator.db.entity.ThumbnailUrlEntity;
import nlp.floschne.thumbnailAnnotator.db.repository.CaptionTokenEntityRepository;
import nlp.floschne.thumbnailAnnotator.db.repository.CrawlerResultEntityRepository;
import nlp.floschne.thumbnailAnnotator.db.repository.ThumbnailUrlEntityRepository;
import nlp.floschne.thumbnailAnnotator.db.util.EmbeddedRedisServer;
import nlp.floschne.thumbnailAnnotator.db.util.RequiresRedisServer;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {RedisConfig.class, EmbeddedRedisServer.class})
public abstract class RepositoryTestBase {

    /**
     * We need to have a Redis server instance available. <br />
     * 1) Start/Stop an embedded instance or reuse an already running local installation <br />
     * 2) Ignore tests if startup failed and no server running locally.
     */
    public static @ClassRule
    RuleChain rules = RuleChain
            .outerRule(EmbeddedRedisServer.runningAt(6379).suppressExceptions())
            .around(RequiresRedisServer.onLocalhost().atLeast("3.2"));


    @Autowired
    protected CrawlerResultEntityRepository crawlerResultEntityRepository;

    @Autowired
    protected ThumbnailUrlEntityRepository thumbnailUrlEntityRepository;

    @Autowired
    protected CaptionTokenEntityRepository captionTokenEntityRepository;

    @Before
    public void flushRepository() {
        this.crawlerResultEntityRepository.deleteAll();
        this.thumbnailUrlEntityRepository.deleteAll();
        this.captionTokenEntityRepository.deleteAll();
    }


    @NotNull
    protected static CrawlerResultEntity createDummyCrawlerResultEntity() {
        CaptionTokenEntity captionTokenEntity = createDummyCaptionTokenEntity();

        List<ThumbnailUrlEntity> urls = new ArrayList<>();

        urls.add(createDummyThumbnailUrlEntity(1));
        urls.add(createDummyThumbnailUrlEntity(2));

        return new CrawlerResultEntity(captionTokenEntity.getValue(), captionTokenEntity, urls);
    }

    @NotNull
    protected static ThumbnailUrlEntity createDummyThumbnailUrlEntity(Integer i) {
        return new ThumbnailUrlEntity("https://image.shutterstock.com/image-photo/big-ship-parked-harbor-260nw-677257045.jpg", i);
    }

    @NotNull
    protected static CaptionTokenEntity createDummyCaptionTokenEntity() {
        return new CaptionTokenEntity("big ship", "COMPOUND", 0, 4, Arrays.asList("JJ", "NN"), Arrays.asList("big", "ship"));
    }

    protected void saveCrawlerResultEntity(CrawlerResultEntity cre) {
        this.thumbnailUrlEntityRepository.saveAll(cre.getThumbnailUrls());
        this.captionTokenEntityRepository.save(cre.getCaptionToken());
        this.crawlerResultEntityRepository.save(cre);
    }

    @Test
    public abstract void whenSaving_thenAvailableOnRetrieval();

    @Test
    public abstract void whenSavingMultiple_thenAllShouldAvailableOnRetrieval();

    @Test
    public abstract void whenUpdating_thenAvailableOnRetrieval();

    @Test
    public abstract void whenDeleting_thenNotAvailableOnRetrieval();
}
