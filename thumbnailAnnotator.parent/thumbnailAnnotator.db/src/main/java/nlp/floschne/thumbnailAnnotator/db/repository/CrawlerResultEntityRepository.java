package nlp.floschne.thumbnailAnnotator.db.repository;


import nlp.floschne.thumbnailAnnotator.db.entity.CrawlerResultEntity;
import org.springframework.data.repository.CrudRepository;

public interface CrawlerResultEntityRepository extends CrudRepository<CrawlerResultEntity, String> {
}
