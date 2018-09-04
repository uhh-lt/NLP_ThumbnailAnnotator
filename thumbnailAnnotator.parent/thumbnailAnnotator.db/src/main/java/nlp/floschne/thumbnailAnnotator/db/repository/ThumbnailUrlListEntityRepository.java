package nlp.floschne.thumbnailAnnotator.db.repository;

import nlp.floschne.thumbnailAnnotator.db.entity.ThumbnailUrlListEntity;
import org.springframework.data.repository.CrudRepository;

public interface ThumbnailUrlListEntityRepository extends CrudRepository<ThumbnailUrlListEntity, String> {
}
