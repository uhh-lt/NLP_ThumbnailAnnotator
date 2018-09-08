package nlp.floschne.thumbnailAnnotator.db.repository;

import nlp.floschne.thumbnailAnnotator.db.entity.CrawlerResultEntity;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class CrawlerResultEntityRepositoryTest extends RepositoryTestBase {

    @Test
    public void findByCaptionTokenValue() {

        final CrawlerResultEntity a = createDummyCrawlerResultEntity();
        saveCrawlerResultEntity(a);

        final Optional<CrawlerResultEntity> o = this.crawlerResultEntityRepository.findByCaptionTokenValue(a.getCaptionTokenValue());
        assertTrue(o.isPresent());

        CrawlerResultEntity b = o.get();
        assertEquals(a.getCaptionTokenValue(), b.getCaptionTokenValue());
        assertEquals(a.getCaptionToken(), b.getCaptionToken());
        assertEquals(a.getThumbnailUrls(), b.getThumbnailUrls());
    }

    @Test
    public void existsByCaptionTokenValue() {

        final CrawlerResultEntity a = createDummyCrawlerResultEntity();
        saveCrawlerResultEntity(a);

        assertTrue(this.crawlerResultEntityRepository.existsById(a.getId()));
    }

    @Override
    public void whenSaving_thenAvailableOnRetrieval() {
        final CrawlerResultEntity a = createDummyCrawlerResultEntity();
        saveCrawlerResultEntity(a);

        final Optional<CrawlerResultEntity> o = this.crawlerResultEntityRepository.findById(a.getId());
        assertTrue(o.isPresent());

        CrawlerResultEntity b = o.get();
        assertEquals(a.getCaptionTokenValue(), b.getCaptionTokenValue());
        assertEquals(a.getCaptionToken(), b.getCaptionToken());
        assertEquals(a.getThumbnailUrls(), b.getThumbnailUrls());
    }


    @Override
    public void whenUpdating_thenAvailableOnRetrieval() {
        final CrawlerResultEntity a = createDummyCrawlerResultEntity();
        saveCrawlerResultEntity(a);

        Optional<CrawlerResultEntity> o = this.crawlerResultEntityRepository.findByCaptionTokenValue(a.getCaptionTokenValue());
        assertTrue(o.isPresent());

        a.setCaptionTokenValue("updated");
        saveCrawlerResultEntity(a);

        o = this.crawlerResultEntityRepository.findByCaptionTokenValue(a.getCaptionTokenValue());
        assertTrue(o.isPresent());
        CrawlerResultEntity b = o.get();
        assertEquals(a.getCaptionTokenValue(), b.getCaptionTokenValue());
        assertEquals(a.getCaptionToken(), b.getCaptionToken());
        assertEquals(a.getThumbnailUrls(), b.getThumbnailUrls());
    }


    //    @Ignore
    @Override
    // not working when running with mvn test .. in IntelliJ everything works fine (maybe due to parallel execution of tests with surefire)
    public void whenSavingMultiple_thenAllShouldAvailableOnRetrieval() {
        final CrawlerResultEntity a = createDummyCrawlerResultEntity();
        final CrawlerResultEntity b = createDummyCrawlerResultEntity();
        b.setCaptionTokenValue("b");
        final CrawlerResultEntity c = createDummyCrawlerResultEntity();
        c.setCaptionTokenValue("c");

        saveCrawlerResultEntity(a);
        saveCrawlerResultEntity(b);
        saveCrawlerResultEntity(c);

        List<CrawlerResultEntity> crawlerResultEntities = new ArrayList<>();
        this.crawlerResultEntityRepository.findAll().forEach(crawlerResultEntities::add);
        assertEquals(3, crawlerResultEntities.size());
    }

    @Override
    public void whenDeleting_thenNotAvailableOnRetrieval() {
        final CrawlerResultEntity a = createDummyCrawlerResultEntity();
        saveCrawlerResultEntity(a);
        assertTrue(this.crawlerResultEntityRepository.findById(a.getId()).isPresent());

        this.crawlerResultEntityRepository.deleteById(a.getId());
        assertFalse(this.crawlerResultEntityRepository.findById(a.getId()).isPresent());
    }

}