package nlp.floschne.thumbnailAnnotator.db.mapper;

import nlp.floschne.thumbnailAnnotator.core.domain.ThumbnailUrl;
import nlp.floschne.thumbnailAnnotator.db.entity.ThumbnailUrlEntity;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;

public class ThumbnailUrlMapperTest extends MapperTestBase<ThumbnailUrlEntity, ThumbnailUrl> {


    public ThumbnailUrlMapperTest() {
        super(MapperType.THUMBNAIL_URL);
    }

    @Override
    public ThumbnailUrlEntity createDummyEntity() {
        ThumbnailUrlEntity entity = new ThumbnailUrlEntity("https://image.shutterstock.com/image-photo/big-ship-parked-harbor-260nw-677257045.jpg", 1);
        entity.setId("id");
        return entity;
    }

    @Override
    public ThumbnailUrl createDummyDomainObject() {
        return new ThumbnailUrl("https://image.shutterstock.com/image-vector/lupe-magnifying-glass-barcode-serial-260nw-476181607.jpg", 2);
    }

    @Override
    public void assertEqual(ThumbnailUrlEntity entity, ThumbnailUrl domain) {
        assertEquals(entity.getPriority(), domain.getPriority());
        assertEquals(entity.getUrl(), domain.getUrl());
    }

}