package nlp.floschne.thumbnailAnnotator.db.repository;

import junit.framework.TestCase;
import nlp.floschne.thumbnailAnnotator.core.domain.ThumbnailUrl;
import nlp.floschne.thumbnailAnnotator.db.entity.ThumbnailUrlEntity;
import nlp.floschne.thumbnailAnnotator.db.util.RepositoryTestsBase;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class ThumbnailUrlEntityRepositoryTests extends RepositoryTestsBase {

    @Autowired
    ThumbnailUrlEntityRepository repository;

    private static ThumbnailUrlEntity createDummyThumbnailUrlEntity() {
        return new ThumbnailUrlEntity("https://image.shutterstock.com/image-photo/big-ship-parked-harbor-260nw-677257045.jpg", 1);
    }

    @Before
    public void flushRedis() {
        repository.deleteAll();
    }

    @Override
    public void whenSaving_thenAvailableOnRetrieval() {
        ThumbnailUrlEntity a = createDummyThumbnailUrlEntity();
        repository.save(a);
        final Optional<ThumbnailUrlEntity> o = repository.findById(a.getId());
        assertTrue(o.isPresent());

        ThumbnailUrlEntity b = o.get();
        TestCase.assertEquals(a.getPriority(), b.getPriority());
        TestCase.assertEquals(a.getUrl(), b.getUrl());
        TestCase.assertEquals(a.getId(), b.getId());
    }

    @Override
    public void whenSavingMultiple_thenAllShouldAvailableOnRetrieval() {

        final ThumbnailUrlEntity a = createDummyThumbnailUrlEntity();
        final ThumbnailUrlEntity b = createDummyThumbnailUrlEntity();
        final ThumbnailUrlEntity c = createDummyThumbnailUrlEntity();
        repository.save(a);
        repository.save(b);
        repository.save(c);

        List<ThumbnailUrlEntity> thumbnailUrlEntities = new ArrayList<>();
        repository.findAll().forEach(thumbnailUrlEntities::add);
        assertEquals(3, thumbnailUrlEntities.size());
    }

    @Override
    public void whenUpdating_thenAvailableOnRetrieval() {
        final ThumbnailUrlEntity a = createDummyThumbnailUrlEntity();
        repository.save(a);
        a.setPriority(3);
        repository.save(a);

        final Optional<ThumbnailUrlEntity> o = repository.findById(a.getId());
        assertTrue(o.isPresent());
        ThumbnailUrlEntity b = o.get();
        TestCase.assertEquals(a.getPriority(), b.getPriority());
        TestCase.assertEquals(a.getUrl(), b.getUrl());
        TestCase.assertEquals(a.getId(), b.getId());
    }

    @Override
    public void whenDeleting_thenNotAvailableOnRetrieval() {
        final ThumbnailUrlEntity a = createDummyThumbnailUrlEntity();
        repository.save(a);
        repository.deleteById(a.getId());
        TestCase.assertFalse(repository.findById(a.getId()).isPresent());
    }
}