package nlp.floschne.thumbnailAnnotator.db.repository;

import nlp.floschne.thumbnailAnnotator.db.entity.FeatureVectorEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface FeatureVectorEntityRepo extends CrudRepository<FeatureVectorEntity, String> {
    Optional<FeatureVectorEntity> findAllByOwnerUserName(String ownerUserName);
    Optional<FeatureVectorEntity> findAllByLabelCategory(String labelCategory);
}
