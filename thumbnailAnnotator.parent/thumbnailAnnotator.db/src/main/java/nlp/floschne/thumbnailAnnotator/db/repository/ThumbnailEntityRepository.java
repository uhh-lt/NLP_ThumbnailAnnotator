package nlp.floschne.thumbnailAnnotator.db.repository;

import nlp.floschne.thumbnailAnnotator.db.entity.ThumbnailEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ThumbnailEntityRepository extends CrudRepository<ThumbnailEntity, String> {
    Optional<ThumbnailEntity> findByUrlAndPriority(String url, Integer priority);
    Optional<ThumbnailEntity> findByUrl(String url);
}
