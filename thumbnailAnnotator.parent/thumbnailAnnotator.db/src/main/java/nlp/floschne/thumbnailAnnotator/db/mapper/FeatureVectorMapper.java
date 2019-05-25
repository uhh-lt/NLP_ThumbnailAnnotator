package nlp.floschne.thumbnailAnnotator.db.mapper;


import nlp.floschne.thumbnailAnnotator.db.entity.FeatureVectorEntity;
import nlp.floschne.thumbnailAnnotator.wsd.featureExtractor.FeatureVector;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring")
@Component
public interface FeatureVectorMapper extends IMapper<FeatureVectorEntity, FeatureVector> {

    @Mapping(target = "ownerUserName", ignore = true)
    @Mapping(target = "id", ignore = true)
    default FeatureVectorEntity mapToEntity(FeatureVector domainObject) {
        FeatureVectorEntity entity = new FeatureVectorEntity();

        entity.setCaptionTokenTokens(domainObject.getCaptionTokenTokens());
        entity.setCaptionTokenLemmata(domainObject.getCaptionTokenLemmata());
        entity.setCaptionTokenPosTags(domainObject.getCaptionTokenPosTags());
        entity.setCaptionTokenSentenceContext(domainObject.getCaptionTokenSentenceContext());
        entity.setCaptionTokenUdContext(domainObject.getCaptionTokenUdContext());
        entity.setThumbnailKeywords(domainObject.getThumbnailKeywords());
        entity.setLabel(domainObject.getLabel().getValue());

        return entity;
    }

    default FeatureVector mapFromEntity(FeatureVectorEntity entity) {
        return new FeatureVector(
                entity.getLabel(),
                entity.getCaptionTokenPosTags(),
                entity.getCaptionTokenTokens(),
                entity.getCaptionTokenLemmata(),
                entity.getCaptionTokenUdContext(),
                entity.getCaptionTokenSentenceContext(),
                entity.getThumbnailKeywords()
        );
    }

    @Override
    List<FeatureVectorEntity> mapToEntityList(List<FeatureVector> domainObjectList);

    @Override
    List<FeatureVector> mapFromEntityList(List<FeatureVectorEntity> entityList);
}
