package nlp.floschne.thumbnailAnnotator.db.repository;

import nlp.floschne.thumbnailAnnotator.db.entity.FeatureVectorEntity;
import org.springframework.data.repository.CrudRepository;

public interface FeatureVectorEntityRepo extends CrudRepository<FeatureVectorEntity, String> {
}
