package nlp.floschne.thumbnailAnnotator.db.mapper;

import nlp.floschne.thumbnailAnnotator.core.domain.CaptionToken;
import nlp.floschne.thumbnailAnnotator.db.entity.CaptionTokenEntity;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class CaptionTokenMapperTest extends MapperTestBase<CaptionTokenEntity, CaptionToken> {

    public CaptionTokenMapperTest() {
        super(MapperType.CAPTION_TOKEN);
    }

    @Override
    public CaptionTokenEntity createDummyEntity() {
        CaptionTokenEntity entity = CaptionTokenEntity.createDummyTestingCaptionTokenEntity();
        entity.setId("id");
        return entity;
    }

    @Override
    public CaptionToken createDummyDomainObject() {
        return CaptionToken.createDummyTestingCaptionToken();
    }

    @Override
    public void assertEqual(CaptionTokenEntity entity, CaptionToken domain) {
        assertEquals(entity.getValue(), domain.getValue());
        assertEquals(entity.getTokens(), domain.getTokens());
        assertEquals(entity.getPosTags(), domain.getPosTags());
        assertEquals(entity.getLemmata(), domain.getLemmata());
        assertEquals(entity.getUdContext(), domain.getUdContext());
        assertTrue(entity.getType().equalsIgnoreCase(domain.getType().toString()));
    }
}