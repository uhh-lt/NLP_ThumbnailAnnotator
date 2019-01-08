package nlp.floschne.thumbnailAnnotator.db.repository;

import nlp.floschne.thumbnailAnnotator.db.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserEntityRepository extends CrudRepository<UserEntity, String> {

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByAccessKey(String accessKey);
}
