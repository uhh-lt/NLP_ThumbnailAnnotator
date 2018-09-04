package nlp.floschne.thumbnailAnnotator.db.repository;

import nlp.floschne.thumbnailAnnotator.db.entity.ThumbnailUrlEntity;
import org.springframework.data.repository.CrudRepository;

public interface ThumbnailUrlEntityRepository extends CrudRepository<ThumbnailUrlEntity, String> {
}
