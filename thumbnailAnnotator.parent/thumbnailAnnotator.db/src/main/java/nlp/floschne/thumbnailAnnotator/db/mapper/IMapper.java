package nlp.floschne.thumbnailAnnotator.db.mapper;

import nlp.floschne.thumbnailAnnotator.core.domain.DomainObject;
import nlp.floschne.thumbnailAnnotator.db.entity.Entity;
import org.mapstruct.factory.Mappers;

import java.util.List;

public interface IMapper<E extends Entity, D extends DomainObject> {

//    IMapper INSTANCE = Mappers.getMapper(IMapper.class);

    E mapToEntity(D domainObject);

    D mapFromEntity(E entity);

    List<E> mapToEntityList(List<D> domainObjectList);

    List<D> mapFromEntityList(List<E> entityList);
}
