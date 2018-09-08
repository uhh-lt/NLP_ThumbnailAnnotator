package nlp.floschne.thumbnailAnnotator.db.repository;

import nlp.floschne.thumbnailAnnotator.db.entity.ThumbnailUrlEntity;
import org.jetbrains.annotations.NotNull;

import static junit.framework.TestCase.assertEquals;

public class ThumbnailUrlEntityRepositoryTest extends RepositoryTestBase<ThumbnailUrlEntity> {

    public ThumbnailUrlEntityRepositoryTest() {
        super(RepoType.THUMBNAIL_URL);
    }

    @NotNull
    @Override
    protected ThumbnailUrlEntity createDummyEntity() {
        return new ThumbnailUrlEntity("https://image.shutterstock.com/image-photo/big-ship-parked-harbor-260nw-677257045.jpg", 1);
    }

    @Override
    protected void assertEqual(ThumbnailUrlEntity a, ThumbnailUrlEntity b) {
        assertEquals(a.getPriority(), b.getPriority());
        assertEquals(a.getUrl(), b.getUrl());
        assertEquals(a.getId(), b.getId());
        assertEquals(a, b);
    }

    @Override
    protected void saveEntity(ThumbnailUrlEntity entity) {
        this.thumbnailUrlEntityRepository.save(entity);
    }
}