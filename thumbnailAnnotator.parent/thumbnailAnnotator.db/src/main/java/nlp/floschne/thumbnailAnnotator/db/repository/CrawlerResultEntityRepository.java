package nlp.floschne.thumbnailAnnotator.db.repository;


import nlp.floschne.thumbnailAnnotator.db.entity.CrawlerResultEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CrawlerResultEntityRepository extends CrudRepository<CrawlerResultEntity, String> {
    Optional<CrawlerResultEntity> findByCaptionTokenValue(String captionTokenValue);

    List<CrawlerResultEntity> findAllByCaptionTokenValue(String captionTokenValue);
}
