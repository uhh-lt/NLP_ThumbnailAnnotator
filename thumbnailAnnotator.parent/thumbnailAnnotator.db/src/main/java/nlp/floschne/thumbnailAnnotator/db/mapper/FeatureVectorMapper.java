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

    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerUserName", ignore = true)
    FeatureVectorEntity mapToEntity(FeatureVector domainObject);

    @Override
    FeatureVector mapFromEntity(FeatureVectorEntity entity);

    @Override
    List<FeatureVectorEntity> mapToEntityList(List<FeatureVector> domainObjectList);

    @Override
    List<FeatureVector> mapFromEntityList(List<FeatureVectorEntity> entityList);
}
