package nlp.floschne.thumbnailAnnotator.web.api;

import nlp.floschne.thumbnailAnnotator.db.entity.CrawlerResultEntity;
import nlp.floschne.thumbnailAnnotator.db.entity.ThumbnailUrlEntity;
import nlp.floschne.thumbnailAnnotator.db.entity.ThumbnailUrlListEntity;
import nlp.floschne.thumbnailAnnotator.db.repository.CrawlerResultEntityRepository;
import nlp.floschne.thumbnailAnnotator.db.repository.ThumbnailUrlEntityRepository;
import nlp.floschne.thumbnailAnnotator.db.repository.ThumbnailUrlListEntityRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@ComponentScan(basePackages = {"nlp.floschne.thumbnailAnnotator.db"})
public class DBService {

    private final CrawlerResultEntityRepository crawlerResultEntityRepository;

    private final ThumbnailUrlListEntityRepository thumbnailUrlListEntityRepository;

    private final ThumbnailUrlEntityRepository thumbnailUrlEntityRepository;

    @Autowired
    public DBService(CrawlerResultEntityRepository crawlerResultEntityRepository,
                     ThumbnailUrlListEntityRepository thumbnailUrlListEntityRepository,
                     ThumbnailUrlEntityRepository thumbnailUrlEntityRepository) {
        this.crawlerResultEntityRepository = crawlerResultEntityRepository;
        this.thumbnailUrlListEntityRepository = thumbnailUrlListEntityRepository;
        this.thumbnailUrlEntityRepository = thumbnailUrlEntityRepository;
    }

    public void saveCrawlerResultEntity(@NotNull CrawlerResultEntity cre) {
        this.thumbnailUrlEntityRepository.saveAll(cre.getThumbnailUrlList().getThumbnailUrlEntities());
        this.thumbnailUrlListEntityRepository.save(cre.getThumbnailUrlList());
        this.crawlerResultEntityRepository.save(cre);
    }

    public void saveThumbnailUrlEntity(@NotNull ThumbnailUrlEntity tue) {
        this.thumbnailUrlEntityRepository.save(tue);
    }

    public void saveThumbnailUrlListEntity(@NotNull ThumbnailUrlListEntity tule) {
        this.thumbnailUrlListEntityRepository.save(tule);
    }

    public void sortThumbnailUrlListEntityById(@NotNull String id) {
        if (this.thumbnailUrlListEntityRepository.existsById(id)) {
            ThumbnailUrlListEntity thumbnailUrlListEntity = this.thumbnailUrlListEntityRepository.findById(id).get();
            Collections.sort(thumbnailUrlListEntity.getThumbnailUrlEntities());
            this.saveThumbnailUrlListEntity(thumbnailUrlListEntity);
        }
    }

    public Optional<CrawlerResultEntity> findCrawlerResultEntityById(String id) {
        return this.crawlerResultEntityRepository.findById(id);
    }

    public Optional<ThumbnailUrlEntity> findThumbnailUrlEntityById(String id) {
        return this.thumbnailUrlEntityRepository.findById(id);
    }

    public Boolean crawlerResultEntityExistsById(@NotNull String id) {
        return this.crawlerResultEntityRepository.existsById(id);
    }

    public Iterable<CrawlerResultEntity> findAllCrawlerResultEntities() {
        return this.crawlerResultEntityRepository.findAll();
    }

    public void deleteAllCrawlerResultEntities() {
        this.crawlerResultEntityRepository.deleteAll();
    }
}
