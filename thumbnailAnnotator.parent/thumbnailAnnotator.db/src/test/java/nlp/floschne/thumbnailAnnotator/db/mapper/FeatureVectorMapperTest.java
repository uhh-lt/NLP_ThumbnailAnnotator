package nlp.floschne.thumbnailAnnotator.db.mapper;

import nlp.floschne.thumbnailAnnotator.db.entity.FeatureVectorEntity;
import nlp.floschne.thumbnailAnnotator.wsd.featureExtractor.FeatureVector;

import static junit.framework.TestCase.assertEquals;

public class FeatureVectorMapperTest extends MapperTestBase<FeatureVectorEntity, FeatureVector> {


    public FeatureVectorMapperTest() {
        super(MapperType.FEATURE_VECTOR);
    }

    @Override
    public FeatureVectorEntity createDummyEntity() {
        FeatureVectorEntity entity = FeatureVectorEntity.createDummyTestingFeatureVectorEntity();
        entity.setId("id");
        return entity;
    }

    @Override
    public FeatureVector createDummyDomainObject() {
        return FeatureVector.createDummyTestingFeatureVector();
    }

    @Override
    public void assertEqual(FeatureVectorEntity entity, FeatureVector domain) {
        assertEquals(entity.getLabel(), domain.getLabel().getValue());

        assertEquals(entity.getCaptionTokenTokens(), domain.getCaptionTokenTokens());
        assertEquals(entity.getCaptionTokenLemmata(), domain.getCaptionTokenLemmata());
        assertEquals(entity.getCaptionTokenPosTags(), domain.getCaptionTokenPosTags());

        assertEquals(entity.getCaptionTokenUdContext(), domain.getCaptionTokenUdContext());
        assertEquals(entity.getCaptionTokenSentenceContext(), domain.getCaptionTokenSentenceContext());

        assertEquals(entity.getThumbnailKeywords(), domain.getThumbnailKeywords());
    }
}
