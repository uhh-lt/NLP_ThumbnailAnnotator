package nlp.floschne.thumbnailAnnotator.db.mapper;

import javax.annotation.Generated;
import nlp.floschne.thumbnailAnnotator.core.domain.CrawlerResult;
import nlp.floschne.thumbnailAnnotator.db.entity.CrawlerResultEntity;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2018-09-07T14:38:06+0200",
    comments = "version: 1.2.0.Final, compiler: javac, environment: Java 1.8.0_181 (Oracle Corporation)"
)
@Component
public class CrawlerResultMapperImpl implements CrawlerResultMapper {

    @Override
    public CrawlerResultEntity map(CrawlerResult result) {
        if ( result == null ) {
            return null;
        }

        CrawlerResultEntity crawlerResultEntity = new CrawlerResultEntity();

        crawlerResultEntity.setCaptionToken( result.getCaptionToken() );

        return crawlerResultEntity;
    }

    @Override
    public CrawlerResult map(CrawlerResultEntity resultEntity) {
        if ( resultEntity == null ) {
            return null;
        }

        CrawlerResult crawlerResult = new CrawlerResult();

        crawlerResult.setCaptionToken( resultEntity.getCaptionToken() );

        return crawlerResult;
    }
}
