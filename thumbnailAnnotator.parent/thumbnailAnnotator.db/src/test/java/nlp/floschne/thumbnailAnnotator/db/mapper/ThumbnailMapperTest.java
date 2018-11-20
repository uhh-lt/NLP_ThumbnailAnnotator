package nlp.floschne.thumbnailAnnotator.db.mapper;

import nlp.floschne.thumbnailAnnotator.core.domain.Thumbnail;
import nlp.floschne.thumbnailAnnotator.db.entity.ThumbnailEntity;

import java.util.Arrays;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;

public class ThumbnailMapperTest extends MapperTestBase<ThumbnailEntity, Thumbnail> {


    public ThumbnailMapperTest() {
        super(MapperType.THUMBNAIL_URL);
    }

    @Override
    public ThumbnailEntity createDummyEntity() {
        ThumbnailEntity entity = new ThumbnailEntity(
                "https://image.shutterstock.com/image-photo/big-ship-parked-harbor-260nw-677257045.jpg",
                1,
                "desc1",
                13337L,
                Arrays.asList(new Thumbnail.Category(1, "a"), new Thumbnail.Category(2, "b")),
                Arrays.asList("k1", "k2"));
        entity.setId("id");
        return entity;
    }

    @Override
    public Thumbnail createDummyDomainObject() {
        return new Thumbnail("https://image.shutterstock.com/image-vector/lupe-magnifying-glass-barcode-serial-260nw-476181607.jpg",
                2,
                "desc2",
                133437L,
                Arrays.asList(new Thumbnail.Category(3, "c"), new Thumbnail.Category(4, "d")),
                Arrays.asList("k3", "k4"));
    }

    @Override
    public void assertEqual(ThumbnailEntity entity, Thumbnail domain) {
        assertEquals(entity.getPriority(), domain.getPriority());
        assertEquals(entity.getUrl(), domain.getUrl());
        assertEquals(entity.getDescription(), domain.getDescription());
        assertEquals(entity.getShutterstockId(), domain.getShutterstockId());
        assertEquals(entity.getCategories(), domain.getCategories());
        assertEquals(entity.getKeywords(), domain.getKeywords());

    }

}