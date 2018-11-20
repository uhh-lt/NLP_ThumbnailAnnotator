package nlp.floschne.thumbnailAnnotator.db.repository;

import nlp.floschne.thumbnailAnnotator.core.domain.Thumbnail;
import nlp.floschne.thumbnailAnnotator.db.entity.ThumbnailEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static junit.framework.TestCase.assertEquals;

public class ThumbnailEntityRepositoryTest extends RepositoryTestBase<ThumbnailEntity> {

    public ThumbnailEntityRepositoryTest() {
        super(RepoType.THUMBNAIL_URL);
    }

    @NotNull
    @Override
    protected ThumbnailEntity createDummyEntity() {
        return new ThumbnailEntity(
                "https://image.shutterstock.com/image-photo/big-ship-parked-harbor-260nw-677257045.jpg",
                1,
                "desc1",
                13337L,
                Arrays.asList(new Thumbnail.Category(3, "c"), new Thumbnail.Category(4, "d")),
                Arrays.asList("k3", "k4"));
    }

    @Override
    protected void assertEqual(ThumbnailEntity a, ThumbnailEntity b) {
        assertEquals(a.getPriority(), b.getPriority());
        assertEquals(a.getUrl(), b.getUrl());
        assertEquals(a.getId(), b.getId());

        assertEquals(a, b);
    }

    @Override
    protected void saveEntity(ThumbnailEntity entity) {
        this.thumbnailEntityRepository.save(entity);
    }
}