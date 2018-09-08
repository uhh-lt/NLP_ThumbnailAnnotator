package nlp.floschne.thumbnailAnnotator.db.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import nlp.floschne.thumbnailAnnotator.core.domain.CaptionToken;
import nlp.floschne.thumbnailAnnotator.core.domain.CaptionToken.Type;
import nlp.floschne.thumbnailAnnotator.db.entity.CaptionTokenEntity;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2018-09-08T04:43:03+0200",
    comments = "version: 1.2.0.Final, compiler: javac, environment: Java 1.8.0_181 (Oracle Corporation)"
)
@Component
public class CaptionTokenMapperImpl implements CaptionTokenMapper {

    @Override
    public CaptionTokenEntity mapToEntity(CaptionToken domainObject) {
        if ( domainObject == null ) {
            return null;
        }

        CaptionTokenEntity captionTokenEntity = new CaptionTokenEntity();

        captionTokenEntity.setValue( domainObject.getValue() );
        if ( domainObject.getType() != null ) {
            captionTokenEntity.setType( domainObject.getType().name() );
        }
        captionTokenEntity.setBeginPosition( domainObject.getBeginPosition() );
        captionTokenEntity.setEndPosition( domainObject.getEndPosition() );
        List<String> list = domainObject.getPosTags();
        if ( list != null ) {
            captionTokenEntity.setPosTags( new ArrayList<String>( list ) );
        }
        else {
            captionTokenEntity.setPosTags( null );
        }
        List<String> list1 = domainObject.getTokens();
        if ( list1 != null ) {
            captionTokenEntity.setTokens( new ArrayList<String>( list1 ) );
        }
        else {
            captionTokenEntity.setTokens( null );
        }

        return captionTokenEntity;
    }

    @Override
    public CaptionToken mapFromEntity(CaptionTokenEntity entity) {
        if ( entity == null ) {
            return null;
        }

        CaptionToken captionToken = new CaptionToken();

        captionToken.setValue( entity.getValue() );
        if ( entity.getType() != null ) {
            captionToken.setType( Enum.valueOf( Type.class, entity.getType() ) );
        }
        captionToken.setBeginPosition( entity.getBeginPosition() );
        captionToken.setEndPosition( entity.getEndPosition() );
        List<String> list = entity.getPosTags();
        if ( list != null ) {
            captionToken.setPosTags( new ArrayList<String>( list ) );
        }
        else {
            captionToken.setPosTags( null );
        }
        List<String> list1 = entity.getTokens();
        if ( list1 != null ) {
            captionToken.setTokens( new ArrayList<String>( list1 ) );
        }
        else {
            captionToken.setTokens( null );
        }

        return captionToken;
    }

    @Override
    public List<CaptionTokenEntity> mapToEntityList(List<CaptionToken> domainObjectList) {
        if ( domainObjectList == null ) {
            return null;
        }

        List<CaptionTokenEntity> list = new ArrayList<CaptionTokenEntity>( domainObjectList.size() );
        for ( CaptionToken captionToken : domainObjectList ) {
            list.add( mapToEntity( captionToken ) );
        }

        return list;
    }

    @Override
    public List<CaptionToken> mapFromEntityList(List<CaptionTokenEntity> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<CaptionToken> list = new ArrayList<CaptionToken>( entityList.size() );
        for ( CaptionTokenEntity captionTokenEntity : entityList ) {
            list.add( mapFromEntity( captionTokenEntity ) );
        }

        return list;
    }
}
