package nlp.floschne.thumbnailAnnotator.db.mapper;

import nlp.floschne.thumbnailAnnotator.core.domain.CaptionToken;
import nlp.floschne.thumbnailAnnotator.db.entity.CaptionTokenEntity;

import java.util.Arrays;
import java.util.Collections;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class CaptionTokenMapperTest extends MapperTestBase<CaptionTokenEntity, CaptionToken> {

    public CaptionTokenMapperTest() {
        super(MapperType.CAPTION_TOKEN);
    }

    @Override
    public CaptionTokenEntity createDummyEntity() {
        CaptionTokenEntity entity = new CaptionTokenEntity("big ship", "COMPOUND", 0, 7, Arrays.asList("JJ", "NN"), Arrays.asList("big", "ship"));
        entity.setId("id");
        return entity;
    }

    @Override
    public CaptionToken createDummyDomainObject() {
        return new CaptionToken("car", CaptionToken.Type.NOUN, 0, 4, Collections.singletonList("NN"), Collections.singletonList("car"));
    }

    @Override
    public void assertEqual(CaptionTokenEntity entity, CaptionToken domain) {
        assertEquals(entity.getValue(), domain.getValue());
        assertEquals(entity.getTokens(), domain.getTokens());
        assertEquals(entity.getPosTags(), domain.getPosTags());
        assertEquals(entity.getEndPosition(), domain.getEndPosition());
        assertEquals(entity.getBeginPosition(), domain.getBeginPosition());
        assertTrue(entity.getType().equalsIgnoreCase(domain.getType().toString()));
    }
}