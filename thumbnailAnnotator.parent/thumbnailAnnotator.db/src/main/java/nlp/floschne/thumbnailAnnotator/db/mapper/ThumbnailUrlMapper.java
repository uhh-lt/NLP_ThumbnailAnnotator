package nlp.floschne.thumbnailAnnotator.db.mapper;

import nlp.floschne.thumbnailAnnotator.core.domain.ThumbnailUrl;
import nlp.floschne.thumbnailAnnotator.db.entity.ThumbnailUrlEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring")
@Component
public interface ThumbnailUrlMapper extends IMapper<ThumbnailUrlEntity, ThumbnailUrl> {

    @Override
    @Mapping(target = "id", ignore = true)
    ThumbnailUrlEntity mapToEntity(ThumbnailUrl ulr);

    @Override
    ThumbnailUrl mapFromEntity(ThumbnailUrlEntity ulrEntity);

    @Override
    List<ThumbnailUrlEntity> mapToEntityList(List<ThumbnailUrl> urlList);

    @Override
    List<ThumbnailUrl> mapFromEntityList(List<ThumbnailUrlEntity> urlEntityList);
}
