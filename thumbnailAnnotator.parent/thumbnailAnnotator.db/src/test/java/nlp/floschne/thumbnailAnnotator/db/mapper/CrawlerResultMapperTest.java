package nlp.floschne.thumbnailAnnotator.db.mapper;

import nlp.floschne.thumbnailAnnotator.core.domain.CaptionToken;
import nlp.floschne.thumbnailAnnotator.core.domain.CrawlerResult;
import nlp.floschne.thumbnailAnnotator.core.domain.Thumbnail;
import nlp.floschne.thumbnailAnnotator.db.entity.CaptionTokenEntity;
import nlp.floschne.thumbnailAnnotator.db.entity.CrawlerResultEntity;
import nlp.floschne.thumbnailAnnotator.db.entity.ThumbnailEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class CrawlerResultMapperTest extends MapperTestBase<CrawlerResultEntity, CrawlerResult> {


    public CrawlerResultMapperTest() {
        super(MapperType.CRAWLER_RESULT);
    }

    @Override
    public CrawlerResultEntity createDummyEntity() {
        CaptionTokenEntity captionTokenEntity = new CaptionTokenEntity("big ship", "COMPOUND", Arrays.asList("JJ", "NN"), Arrays.asList("big", "ship"));

        List<ThumbnailEntity> urls = new ArrayList<>();

        ThumbnailEntity entity = new ThumbnailEntity("https://image.shutterstock.com/image-photo/big-ship-parked-harbor-260nw-677257045.jpg", 1);
        entity.setId("https://image.shutterstock.com/image-photo/big-ship-parked-harbor-260nw-677257045.jpg");

        urls.add(entity);
        entity = new ThumbnailEntity("https://image.shutterstock.com/image-vector/lupe-magnifying-glass-barcode-serial-260nw-476181607.jpg", 2);
        entity.setId("https://image.shutterstock.com/image-vector/lupe-magnifying-glass-barcode-serial-260nw-476181607.jpg");

        urls.add(entity);

        return new CrawlerResultEntity(captionTokenEntity.getValue(), captionTokenEntity, urls);
    }

    @Override
    public CrawlerResult createDummyDomainObject() {
        CaptionToken captionToken = new CaptionToken("car", CaptionToken.Type.NOUN, Collections.singletonList("NN"), Collections.singletonList("car"));

        List<Thumbnail> urls = new ArrayList<>();

        urls.add(new Thumbnail("https://image.shutterstock.com/image-vector/lupe-magnifying-glass-barcode-serial-260nw-476181607.jpg", 1));
        urls.add(new Thumbnail("https://image.shutterstock.com/image-photo/big-ship-parked-harbor-260nw-677257045.jpg", 2));

        return new CrawlerResult(captionToken, urls);
    }

    @Override
    public void assertEqual(CrawlerResultEntity entity, CrawlerResult domain) {
        assertEquals(entity.getCaptionTokenValue(), domain.getCaptionToken().getValue());
        assertEquals(captionTokenMapper.mapFromEntity(entity.getCaptionToken()), domain.getCaptionToken());
        assertEquals(thumbnailMapper.mapFromEntityList(entity.getThumbnails()), domain.getThumbnails());

        assertEquals(entity.getCaptionToken(), captionTokenMapper.mapToEntity(domain.getCaptionToken()));
        assertEquals(entity.getThumbnails(), thumbnailMapper.mapToEntityList(domain.getThumbnails()));
    }
}