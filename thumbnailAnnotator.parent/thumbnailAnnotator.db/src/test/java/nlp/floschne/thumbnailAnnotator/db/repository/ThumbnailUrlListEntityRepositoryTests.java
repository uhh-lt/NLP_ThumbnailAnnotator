package nlp.floschne.thumbnailAnnotator.db.repository;

import junit.framework.TestCase;
import nlp.floschne.thumbnailAnnotator.core.domain.ThumbnailUrl;
import nlp.floschne.thumbnailAnnotator.core.domain.ThumbnailUrlList;
import nlp.floschne.thumbnailAnnotator.db.entity.ThumbnailUrlListEntity;
import nlp.floschne.thumbnailAnnotator.db.util.RepositoryTestsBase;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class ThumbnailUrlListEntityRepositoryTests extends RepositoryTestsBase {

    @Autowired
    private ThumbnailUrlListEntityRepository thumbnailUrlListEntityRepository;

    @Autowired
    private ThumbnailUrlEntityRepository thumbnailUrlEntityRepository;


    private ThumbnailUrlListEntity createDummyThumbnailUrlListEntity() {
        ThumbnailUrlList urls = new ThumbnailUrlList();
        urls.add(new ThumbnailUrl("https://image.shutterstock.com/image-photo/big-ship-parked-harbor-260nw-677257045.jpg", 1));
        urls.add(new ThumbnailUrl("https://image.shutterstock.com/image-photo/tugboats-assisting-container-cargo-ship-harbor-260nw-326359325.jpg", 2));
        urls.add(new ThumbnailUrl("https://image.shutterstock.com/image-photo/tugboats-assisting-container-cargo-ship-harbor-260nw-326359325.jpg", 3));
        urls.add(new ThumbnailUrl("https://image.shutterstock.com/image-photo/tugboats-assisting-container-cargo-ship-harbor-260nw-326359325.jpg", 1));

        return new ThumbnailUrlListEntity(urls);
    }

    @Before
    public void flushRedis() {
        thumbnailUrlListEntityRepository.deleteAll();
        thumbnailUrlEntityRepository.deleteAll();
    }


    private void saveThumbnailUrlListEntityRecursivly(ThumbnailUrlListEntity tule) {
        thumbnailUrlEntityRepository.saveAll(tule.getThumbnailUrlEntities());
        thumbnailUrlListEntityRepository.save(tule);
    }

    @Override
    public void whenSaving_thenAvailableOnRetrieval() {
        ThumbnailUrlListEntity a = createDummyThumbnailUrlListEntity();
        saveThumbnailUrlListEntityRecursivly(a);
        final Optional<ThumbnailUrlListEntity> o = thumbnailUrlListEntityRepository.findById(a.getId());
        assertTrue(o.isPresent());

        ThumbnailUrlListEntity b = o.get();
        TestCase.assertEquals(a.getThumbnailUrlEntities(), b.getThumbnailUrlEntities());
        TestCase.assertEquals(a.getId(), b.getId());
    }

    @Override
    public void whenSavingMultiple_thenAllShouldAvailableOnRetrieval() {

        final ThumbnailUrlListEntity a = createDummyThumbnailUrlListEntity();
        final ThumbnailUrlListEntity b = createDummyThumbnailUrlListEntity();
        final ThumbnailUrlListEntity c = createDummyThumbnailUrlListEntity();
        saveThumbnailUrlListEntityRecursivly(a);
        saveThumbnailUrlListEntityRecursivly(b);
        saveThumbnailUrlListEntityRecursivly(c);

        List<ThumbnailUrlListEntity> thumbnailUrlListEntities = new ArrayList<>();
        thumbnailUrlListEntityRepository.findAll().forEach(thumbnailUrlListEntities::add);
        assertEquals(3, thumbnailUrlListEntities.size());
    }

    @Override
    public void whenUpdating_thenAvailableOnRetrieval() {
        final ThumbnailUrlListEntity a = createDummyThumbnailUrlListEntity();
        saveThumbnailUrlListEntityRecursivly(a);
        a.getThumbnailUrlEntities().remove(0);
        saveThumbnailUrlListEntityRecursivly(a);

        final Optional<ThumbnailUrlListEntity> o = thumbnailUrlListEntityRepository.findById(a.getId());
        assertTrue(o.isPresent());
        ThumbnailUrlListEntity b = o.get();
        TestCase.assertEquals(a.getThumbnailUrlEntities(), b.getThumbnailUrlEntities());
        TestCase.assertEquals(a.getId(), b.getId());
    }

    @Override
    public void whenDeleting_thenNotAvailableOnRetrieval() {
        final ThumbnailUrlListEntity a = createDummyThumbnailUrlListEntity();
        saveThumbnailUrlListEntityRecursivly(a);
        thumbnailUrlListEntityRepository.deleteById(a.getId());
        TestCase.assertFalse(thumbnailUrlListEntityRepository.findById(a.getId()).isPresent());
    }
}