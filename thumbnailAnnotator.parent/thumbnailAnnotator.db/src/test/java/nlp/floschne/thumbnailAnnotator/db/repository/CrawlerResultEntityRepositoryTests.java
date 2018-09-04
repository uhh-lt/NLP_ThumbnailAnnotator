package nlp.floschne.thumbnailAnnotator.db.repository;

import junit.framework.TestCase;
import nlp.floschne.thumbnailAnnotator.core.domain.CaptionToken;
import nlp.floschne.thumbnailAnnotator.core.domain.CrawlerResult;
import nlp.floschne.thumbnailAnnotator.core.domain.ThumbnailUrl;
import nlp.floschne.thumbnailAnnotator.core.domain.ThumbnailUrlList;
import nlp.floschne.thumbnailAnnotator.db.entity.CrawlerResultEntity;
import nlp.floschne.thumbnailAnnotator.db.util.RepositoryTestsBase;
import org.junit.Before;
import org.junit.Ignore;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class CrawlerResultEntityRepositoryTests extends RepositoryTestsBase {

    private static CrawlerResultEntity createDummyCrawlerResultEntity() {
        CaptionToken captionToken = new CaptionToken("big ship", CaptionToken.Type.COMPOUND, 0, 4, Arrays.asList("JJ", "NN"), Arrays.asList("big", "ship"));
        ThumbnailUrlList urls = new ThumbnailUrlList();
        urls.add(new ThumbnailUrl("https://image.shutterstock.com/image-photo/big-ship-parked-harbor-260nw-677257045.jpg", 1));
        urls.add(new ThumbnailUrl("https://image.shutterstock.com/image-photo/tugboats-assisting-container-cargo-ship-harbor-260nw-326359325.jpg", 2));
        CrawlerResult crawlerResult = new CrawlerResult(captionToken, urls);

        return new CrawlerResultEntity(crawlerResult);
    }

    @Autowired
    private CrawlerResultEntityRepository crawlerResultEntityRepository;

    @Autowired
    private ThumbnailUrlListEntityRepository thumbnailUrlListEntityRepository;

    @Autowired
    private ThumbnailUrlEntityRepository thumbnailUrlEntityRepository;

    @Before
    public void flushRepository() {
        crawlerResultEntityRepository.deleteAll();
        thumbnailUrlListEntityRepository.deleteAll();
        thumbnailUrlEntityRepository.deleteAll();
    }


    private void saveCrawlerResultEntityRecursivly(CrawlerResultEntity cre) {
        thumbnailUrlEntityRepository.saveAll(cre.getThumbnailUrlList().getThumbnailUrlEntities());
        thumbnailUrlListEntityRepository.save(cre.getThumbnailUrlList());
        crawlerResultEntityRepository.save(cre);
    }

    public void whenSaving_thenAvailableOnRetrieval() {
        final CrawlerResultEntity a = createDummyCrawlerResultEntity();
        saveCrawlerResultEntityRecursivly(a);

        final Optional<CrawlerResultEntity> o = crawlerResultEntityRepository.findById(a.getCaptionTokenValue());
        assertTrue(o.isPresent());

        CrawlerResultEntity b = o.get();
        TestCase.assertEquals(a.getCaptionTokenValue(), b.getCaptionTokenValue());
        TestCase.assertEquals(a.getCaptionToken(), b.getCaptionToken());
        TestCase.assertEquals(a.getThumbnailUrlList(), b.getThumbnailUrlList());
    }


    @Override
    public void whenUpdating_thenAvailableOnRetrieval() {
        final CrawlerResultEntity a = createDummyCrawlerResultEntity();
        saveCrawlerResultEntityRecursivly(a);
        a.setCaptionTokenValue("updated");
        saveCrawlerResultEntityRecursivly(a);

        final Optional<CrawlerResultEntity> o = crawlerResultEntityRepository.findById(a.getCaptionTokenValue());
        assertTrue(o.isPresent());
        CrawlerResultEntity b = o.get();
        TestCase.assertEquals(a.getCaptionTokenValue(), b.getCaptionTokenValue());
        TestCase.assertEquals(a.getCaptionToken(), b.getCaptionToken());
        TestCase.assertEquals(a.getThumbnailUrlList(), b.getThumbnailUrlList());
    }


    @Ignore
    @Override
    // not working when running with mvn test .. in IntelliJ everything works fine (maybe due to parallel execution of tests with surefire)
    public void whenSavingMultiple_thenAllShouldAvailableOnRetrieval() {
        final CrawlerResultEntity a = createDummyCrawlerResultEntity();
        final CrawlerResultEntity b = createDummyCrawlerResultEntity();
        b.setCaptionTokenValue("b");
        final CrawlerResultEntity c = createDummyCrawlerResultEntity();
        c.setCaptionTokenValue("c");

        saveCrawlerResultEntityRecursivly(a);
        saveCrawlerResultEntityRecursivly(b);
        saveCrawlerResultEntityRecursivly(c);

        List<CrawlerResultEntity> crawlerResultEntities = new ArrayList<>();
        crawlerResultEntityRepository.findAll().forEach(crawlerResultEntities::add);
        assertEquals(3, crawlerResultEntities.size());
    }

    @Override
    public void whenDeleting_thenNotAvailableOnRetrieval() {
        final CrawlerResultEntity a = createDummyCrawlerResultEntity();
        saveCrawlerResultEntityRecursivly(a);
        crawlerResultEntityRepository.deleteById(a.getCaptionTokenValue());
        TestCase.assertFalse(crawlerResultEntityRepository.findById(a.getCaptionTokenValue()).isPresent());
    }

}