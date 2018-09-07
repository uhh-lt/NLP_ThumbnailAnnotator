package nlp.floschne.thumbnailAnnotator.db.mapper;

import javax.annotation.Generated;
import nlp.floschne.thumbnailAnnotator.core.domain.ThumbnailUrl;
import nlp.floschne.thumbnailAnnotator.db.entity.ThumbnailUrlEntity;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2018-09-07T14:38:05+0200",
    comments = "version: 1.2.0.Final, compiler: javac, environment: Java 1.8.0_181 (Oracle Corporation)"
)
@Component
public class ThumbnailUrlMapperImpl implements ThumbnailUrlMapper {

    @Override
    public ThumbnailUrlEntity map(ThumbnailUrl ulr) {
        if ( ulr == null ) {
            return null;
        }

        ThumbnailUrlEntity thumbnailUrlEntity = new ThumbnailUrlEntity();

        thumbnailUrlEntity.setUrl( ulr.getUrl() );
        if ( ulr.getPriority() != null ) {
            thumbnailUrlEntity.setPriority( ulr.getPriority() );
        }

        return thumbnailUrlEntity;
    }

    @Override
    public ThumbnailUrl map(ThumbnailUrlEntity ulrEntity) {
        if ( ulrEntity == null ) {
            return null;
        }

        ThumbnailUrl thumbnailUrl = new ThumbnailUrl();

        thumbnailUrl.setUrl( ulrEntity.getUrl() );
        thumbnailUrl.setPriority( ulrEntity.getPriority() );

        return thumbnailUrl;
    }
}
