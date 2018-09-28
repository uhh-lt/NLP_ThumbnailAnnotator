package nlp.floschne.thumbnailAnnotator.db.mapper;

import nlp.floschne.thumbnailAnnotator.core.domain.CaptionToken;
import nlp.floschne.thumbnailAnnotator.core.domain.UDependency;
import nlp.floschne.thumbnailAnnotator.db.entity.CaptionTokenEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class CaptionTokenMapperTest extends MapperTestBase<CaptionTokenEntity, CaptionToken> {

    public CaptionTokenMapperTest() {
        super(MapperType.CAPTION_TOKEN);
    }

    @Override
    public CaptionTokenEntity createDummyEntity() {
        List<UDependency> udContext = new ArrayList<>();
        udContext.add(new UDependency("amod", "big", "ship"));
        CaptionTokenEntity entity = new CaptionTokenEntity("big ship", "COMPOUND", Arrays.asList("JJ", "NN"), Arrays.asList("big", "ship"), udContext, null);
        entity.setId("id");
        return entity;
    }

    @Override
    public CaptionToken createDummyDomainObject() {
        List<UDependency> udContext = new ArrayList<>();
        udContext.add(new UDependency("amod", "small", "car"));
        return new CaptionToken("small car", CaptionToken.Type.NOUN, Arrays.asList("JJ", "NN"), Arrays.asList("small", "car"), udContext, null);
    }

    @Override
    public void assertEqual(CaptionTokenEntity entity, CaptionToken domain) {
        assertEquals(entity.getValue(), domain.getValue());
        assertEquals(entity.getTokens(), domain.getTokens());
        assertEquals(entity.getPosTags(), domain.getPosTags());
        assertEquals(entity.getUdContext(), domain.getUdContext());
        assertTrue(entity.getType().equalsIgnoreCase(domain.getType().toString()));
    }
}