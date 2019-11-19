package nlp.floschne.thumbnailAnnotator.db.mapper;

import nlp.floschne.thumbnailAnnotator.core.domain.Thumbnail;
import nlp.floschne.thumbnailAnnotator.db.entity.ThumbnailEntity;

import static junit.framework.TestCase.assertEquals;

public class ThumbnailMapperTest extends MapperTestBase<ThumbnailEntity, Thumbnail> {


    public ThumbnailMapperTest() {
        super(MapperType.THUMBNAIL_URL);
    }

    @Override
    public ThumbnailEntity createDummyEntity() {
        ThumbnailEntity entity = ThumbnailEntity.createDummyTestingThumbnailEntity();
        entity.setId("id");
        return entity;
    }

    @Override
    public Thumbnail createDummyDomainObject() {
        return Thumbnail.createDummyTestingThumbnail();
    }

    @Override
    public void assertEqual(ThumbnailEntity entity, Thumbnail domain) {
        assertEquals(entity.getUrl(), domain.getUrl());
        assertEquals(entity.getDescription(), domain.getDescription());
        assertEquals(entity.getShutterstockId(), domain.getShutterstockId());
        assertEquals(entity.getCategory(), domain.getCategory());
        assertEquals(entity.getKeywords(), domain.getKeywords());

    }

}