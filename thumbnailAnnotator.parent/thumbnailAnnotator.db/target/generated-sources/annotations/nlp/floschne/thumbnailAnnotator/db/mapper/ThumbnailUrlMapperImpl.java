package nlp.floschne.thumbnailAnnotator.db.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import nlp.floschne.thumbnailAnnotator.core.domain.ThumbnailUrl;
import nlp.floschne.thumbnailAnnotator.db.entity.ThumbnailUrlEntity;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2018-09-08T04:43:03+0200",
    comments = "version: 1.2.0.Final, compiler: javac, environment: Java 1.8.0_181 (Oracle Corporation)"
)
@Component
public class ThumbnailUrlMapperImpl implements ThumbnailUrlMapper {

    @Override
    public ThumbnailUrlEntity mapToEntity(ThumbnailUrl ulr) {
        if ( ulr == null ) {
            return null;
        }

        ThumbnailUrlEntity thumbnailUrlEntity = new ThumbnailUrlEntity();

        thumbnailUrlEntity.setUrl( ulr.getUrl() );
        thumbnailUrlEntity.setPriority( ulr.getPriority() );

        return thumbnailUrlEntity;
    }

    @Override
    public ThumbnailUrl mapFromEntity(ThumbnailUrlEntity ulrEntity) {
        if ( ulrEntity == null ) {
            return null;
        }

        ThumbnailUrl thumbnailUrl = new ThumbnailUrl();

        thumbnailUrl.setUrl( ulrEntity.getUrl() );
        thumbnailUrl.setPriority( ulrEntity.getPriority() );

        return thumbnailUrl;
    }

    @Override
    public List<ThumbnailUrlEntity> mapToEntityList(List<ThumbnailUrl> urlList) {
        if ( urlList == null ) {
            return null;
        }

        List<ThumbnailUrlEntity> list = new ArrayList<ThumbnailUrlEntity>( urlList.size() );
        for ( ThumbnailUrl thumbnailUrl : urlList ) {
            list.add( mapToEntity( thumbnailUrl ) );
        }

        return list;
    }

    @Override
    public List<ThumbnailUrl> mapFromEntityList(List<ThumbnailUrlEntity> urlEntityList) {
        if ( urlEntityList == null ) {
            return null;
        }

        List<ThumbnailUrl> list = new ArrayList<ThumbnailUrl>( urlEntityList.size() );
        for ( ThumbnailUrlEntity thumbnailUrlEntity : urlEntityList ) {
            list.add( mapFromEntity( thumbnailUrlEntity ) );
        }

        return list;
    }
}
