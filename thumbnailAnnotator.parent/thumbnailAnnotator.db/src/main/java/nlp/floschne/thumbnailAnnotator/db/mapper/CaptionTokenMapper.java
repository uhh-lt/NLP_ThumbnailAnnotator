package nlp.floschne.thumbnailAnnotator.db.mapper;

import nlp.floschne.thumbnailAnnotator.core.domain.CaptionToken;
import nlp.floschne.thumbnailAnnotator.db.entity.CaptionTokenEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring")
@Component
public interface CaptionTokenMapper extends IMapper<CaptionTokenEntity, CaptionToken> {

    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fullSentence", ignore = true)
    CaptionTokenEntity mapToEntity(CaptionToken domainObject);

    @Override
    CaptionToken mapFromEntity(CaptionTokenEntity entity);

    @Override
    List<CaptionTokenEntity> mapToEntityList(List<CaptionToken> domainObjectList);

    @Override
    List<CaptionToken> mapFromEntityList(List<CaptionTokenEntity> entityList);
}
