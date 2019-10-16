package nlp.floschne.thumbnailAnnotator.db.mapper;

import nlp.floschne.thumbnailAnnotator.core.domain.Thumbnail;
import nlp.floschne.thumbnailAnnotator.db.entity.ThumbnailEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring")
@Component
public interface ThumbnailMapper extends IMapper<ThumbnailEntity, Thumbnail> {

    @Override
    @Mapping(target = "id", ignore = true)
    ThumbnailEntity mapToEntity(Thumbnail thumbnail);

    @Override
    Thumbnail mapFromEntity(ThumbnailEntity thumbnailEntity);

    @Override
    List<ThumbnailEntity> mapToEntityList(List<Thumbnail> thumbnails);

    @Override
    List<Thumbnail> mapFromEntityList(List<ThumbnailEntity> thumbnailEntities);
}
