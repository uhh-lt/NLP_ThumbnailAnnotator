package nlp.floschne.thumbnailAnnotator.db.mapper;

import nlp.floschne.thumbnailAnnotator.core.domain.CrawlerResult;
import nlp.floschne.thumbnailAnnotator.db.entity.CrawlerResultEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(uses = {ThumbnailMapper.class, CaptionTokenMapper.class}, componentModel = "spring")
@Component
public interface CrawlerResultMapper extends IMapper<CrawlerResultEntity, CrawlerResult> {

    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "captionTokenValue", expression = "java(crawlerResult.getCaptionToken().getValue())")
    CrawlerResultEntity mapToEntity(CrawlerResult crawlerResult);

    @Override
    CrawlerResult mapFromEntity(CrawlerResultEntity crawlerResultEntity);

    @Override
    List<CrawlerResultEntity> mapToEntityList(List<CrawlerResult> crawlerResults);

    @Override
    List<CrawlerResult> mapFromEntityList(List<CrawlerResultEntity> crawlerResultEntities);

}

