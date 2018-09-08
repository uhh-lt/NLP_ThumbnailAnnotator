package nlp.floschne.thumbnailAnnotator.db.repository;

import junit.framework.TestCase;
import nlp.floschne.thumbnailAnnotator.db.entity.ThumbnailUrlEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class ThumbnailUrlEntityRepositoryTest extends RepositoryTestBase {

    @Override
    public void whenSaving_thenAvailableOnRetrieval() {
        ThumbnailUrlEntity a = createDummyThumbnailUrlEntity(1);
        this.thumbnailUrlEntityRepository.save(a);
        final Optional<ThumbnailUrlEntity> o = this.thumbnailUrlEntityRepository.findById(a.getId());
        assertTrue(o.isPresent());

        ThumbnailUrlEntity b = o.get();
        TestCase.assertEquals(a.getPriority(), b.getPriority());
        TestCase.assertEquals(a.getUrl(), b.getUrl());
        TestCase.assertEquals(a.getId(), b.getId());
    }

    @Override
    public void whenSavingMultiple_thenAllShouldAvailableOnRetrieval() {

        final ThumbnailUrlEntity a = createDummyThumbnailUrlEntity(1);
        final ThumbnailUrlEntity b = createDummyThumbnailUrlEntity(2);
        final ThumbnailUrlEntity c = createDummyThumbnailUrlEntity(3);
        this.thumbnailUrlEntityRepository.save(a);
        this.thumbnailUrlEntityRepository.save(b);
        this.thumbnailUrlEntityRepository.save(c);

        List<ThumbnailUrlEntity> thumbnailUrlEntities = new ArrayList<>();
        this.thumbnailUrlEntityRepository.findAll().forEach(thumbnailUrlEntities::add);
        assertEquals(3, thumbnailUrlEntities.size());
    }

    @Override
    public void whenUpdating_thenAvailableOnRetrieval() {
        final ThumbnailUrlEntity a = createDummyThumbnailUrlEntity(1);
        this.thumbnailUrlEntityRepository.save(a);

        Optional<ThumbnailUrlEntity> o = this.thumbnailUrlEntityRepository.findById(a.getId());
        assertTrue(o.isPresent());

        a.setPriority(3);
        this.thumbnailUrlEntityRepository.save(a);

        o = this.thumbnailUrlEntityRepository.findById(a.getId());
        assertTrue(o.isPresent());
        ThumbnailUrlEntity b = o.get();
        TestCase.assertEquals(a.getPriority(), b.getPriority());
        TestCase.assertEquals(a.getUrl(), b.getUrl());
        TestCase.assertEquals(a.getId(), b.getId());
    }

    @Override
    public void whenDeleting_thenNotAvailableOnRetrieval() {
        final ThumbnailUrlEntity a = createDummyThumbnailUrlEntity(1);
        this.thumbnailUrlEntityRepository.save(a);
        TestCase.assertTrue(this.thumbnailUrlEntityRepository.findById(a.getId()).isPresent());
        this.thumbnailUrlEntityRepository.deleteById(a.getId());
        TestCase.assertFalse(this.thumbnailUrlEntityRepository.findById(a.getId()).isPresent());
    }
}