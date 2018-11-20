package nlp.floschne.thumbnailAnnotator.db.mapper;

import nlp.floschne.thumbnailAnnotator.core.domain.CaptionToken;
import nlp.floschne.thumbnailAnnotator.core.domain.CrawlerResult;
import nlp.floschne.thumbnailAnnotator.core.domain.Thumbnail;
import nlp.floschne.thumbnailAnnotator.core.domain.UDependency;
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
        List<UDependency> udContext = new ArrayList<>();
        udContext.add(new UDependency("amod", "big", "ship"));
        CaptionTokenEntity captionTokenEntity = new CaptionTokenEntity(
                "big ship",
                "COMPOUND",
                Arrays.asList("JJ", "NN"),
                Arrays.asList("big", "ship"),
                udContext,
                Collections.singletonList("ship"));

        List<ThumbnailEntity> thumbnails = new ArrayList<>();

        ThumbnailEntity entity = new ThumbnailEntity(
                "https://image.shutterstock.com/image-photo/big-ship-parked-harbor-260nw-677257045.jpg",
                1,
                "desc1",
                13337L,
                Arrays.asList(new Thumbnail.Category(1, "a"), new Thumbnail.Category(2, "b")),
                Arrays.asList("k1", "k2"));
        entity.setId("https://image.shutterstock.com/image-photo/big-ship-parked-harbor-260nw-677257045.jpg");
        thumbnails.add(entity);

        entity = new ThumbnailEntity(
                "https://image.shutterstock.com/image-vector/lupe-magnifying-glass-barcode-serial-260nw-476181607.jpg",
                2,
                "desc2",
                133437L,
                Arrays.asList(new Thumbnail.Category(3, "c"), new Thumbnail.Category(4, "d")),
                Arrays.asList("k3", "k4"));
        entity.setId("https://image.shutterstock.com/image-vector/lupe-magnifying-glass-barcode-serial-260nw-476181607.jpg");
        thumbnails.add(entity);

        return new CrawlerResultEntity(captionTokenEntity.getValue(), captionTokenEntity, thumbnails);
    }

    @Override
    public CrawlerResult createDummyDomainObject() {
        List<UDependency> udContext = new ArrayList<>();
        udContext.add(new UDependency("amod", "small", "car"));
        CaptionToken captionToken = new CaptionToken(
                "small car",
                CaptionToken.Type.NOUN,
                Arrays.asList("JJ", "NN"),
                Arrays.asList("small", "car"),
                udContext,
                Collections.singletonList("car"));

        List<Thumbnail> thumbnails = new ArrayList<>();

        Thumbnail domain = new Thumbnail(
                "https://image.shutterstock.com/image-photo/big-ship-parked-harbor-260nw-677257045.jpg",
                1,
                "desc1",
                13337L,
                Arrays.asList(new Thumbnail.Category(1, "a"), new Thumbnail.Category(2, "b")),
                Arrays.asList("k1", "k2"));
        thumbnails.add(domain);

        domain = new Thumbnail(
                "https://image.shutterstock.com/image-photo/big-ship-parked-harbor-260nw-677257045.jpg",
                2,
                "desc2",
                133437L,
                Arrays.asList(new Thumbnail.Category(3, "c"), new Thumbnail.Category(4, "d")),
                Arrays.asList("k3", "k4"));
        thumbnails.add(domain);

        return new CrawlerResult(captionToken, thumbnails);
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