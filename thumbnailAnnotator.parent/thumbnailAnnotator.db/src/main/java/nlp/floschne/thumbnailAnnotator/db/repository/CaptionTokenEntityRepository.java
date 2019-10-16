package nlp.floschne.thumbnailAnnotator.db.repository;

import nlp.floschne.thumbnailAnnotator.core.domain.SentenceContext;
import nlp.floschne.thumbnailAnnotator.db.entity.CaptionTokenEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CaptionTokenEntityRepository extends CrudRepository<CaptionTokenEntity, String> {
    Optional<CaptionTokenEntity> findByValue(String value);

    List<CaptionTokenEntity> findAllByValue(String captionTokenValue);

    Optional<CaptionTokenEntity> findBySentenceContext(SentenceContext s);
}
