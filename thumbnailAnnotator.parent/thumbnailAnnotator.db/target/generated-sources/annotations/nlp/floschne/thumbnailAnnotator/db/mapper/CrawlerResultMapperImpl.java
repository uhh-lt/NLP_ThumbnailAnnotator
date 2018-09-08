package nlp.floschne.thumbnailAnnotator.db.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import nlp.floschne.thumbnailAnnotator.core.domain.CrawlerResult;
import nlp.floschne.thumbnailAnnotator.db.entity.CrawlerResultEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2018-09-08T04:43:03+0200",
    comments = "version: 1.2.0.Final, compiler: javac, environment: Java 1.8.0_181 (Oracle Corporation)"
)
@Component
public class CrawlerResultMapperImpl implements CrawlerResultMapper {

    @Autowired
    private ThumbnailUrlMapper thumbnailUrlMapper;
    @Autowired
    private CaptionTokenMapper captionTokenMapper;

    @Override
    public CrawlerResultEntity mapToEntity(CrawlerResult crawlerResult) {
        if ( crawlerResult == null ) {
            return null;
        }

        CrawlerResultEntity crawlerResultEntity = new CrawlerResultEntity();

        crawlerResultEntity.setCaptionToken( captionTokenMapper.mapToEntity( crawlerResult.getCaptionToken() ) );
        crawlerResultEntity.setThumbnailUrls( thumbnailUrlMapper.mapToEntityList( crawlerResult.getThumbnailUrls() ) );

        crawlerResultEntity.setCaptionTokenValue( crawlerResult.getCaptionToken().getValue() );

        return crawlerResultEntity;
    }

    @Override
    public CrawlerResult mapFromEntity(CrawlerResultEntity crawlerResultEntity) {
        if ( crawlerResultEntity == null ) {
            return null;
        }

        CrawlerResult crawlerResult = new CrawlerResult();

        crawlerResult.setCaptionToken( captionTokenMapper.mapFromEntity( crawlerResultEntity.getCaptionToken() ) );
        crawlerResult.setThumbnailUrls( thumbnailUrlMapper.mapFromEntityList( crawlerResultEntity.getThumbnailUrls() ) );

        return crawlerResult;
    }

    @Override
    public List<CrawlerResultEntity> mapToEntityList(List<CrawlerResult> crawlerResults) {
        if ( crawlerResults == null ) {
            return null;
        }

        List<CrawlerResultEntity> list = new ArrayList<CrawlerResultEntity>( crawlerResults.size() );
        for ( CrawlerResult crawlerResult : crawlerResults ) {
            list.add( mapToEntity( crawlerResult ) );
        }

        return list;
    }

    @Override
    public List<CrawlerResult> mapFromEntityList(List<CrawlerResultEntity> crawlerResultEntities) {
        if ( crawlerResultEntities == null ) {
            return null;
        }

        List<CrawlerResult> list = new ArrayList<CrawlerResult>( crawlerResultEntities.size() );
        for ( CrawlerResultEntity crawlerResultEntity : crawlerResultEntities ) {
            list.add( mapFromEntity( crawlerResultEntity ) );
        }

        return list;
    }
}
