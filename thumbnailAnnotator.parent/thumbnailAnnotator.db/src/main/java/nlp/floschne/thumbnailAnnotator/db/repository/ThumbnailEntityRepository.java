package nlp.floschne.thumbnailAnnotator.db.repository;

import nlp.floschne.thumbnailAnnotator.db.entity.ThumbnailEntity;
import org.springframework.data.repository.CrudRepository;

public interface ThumbnailEntityRepository extends CrudRepository<ThumbnailEntity, String> {
}
