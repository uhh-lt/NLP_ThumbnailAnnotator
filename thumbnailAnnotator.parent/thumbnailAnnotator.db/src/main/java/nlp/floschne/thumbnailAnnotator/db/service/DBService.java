package nlp.floschne.thumbnailAnnotator.db.service;

import lombok.extern.slf4j.Slf4j;
import nlp.floschne.thumbnailAnnotator.core.domain.CaptionToken;
import nlp.floschne.thumbnailAnnotator.core.domain.CrawlerResult;
import nlp.floschne.thumbnailAnnotator.db.entity.CrawlerResultEntity;
import nlp.floschne.thumbnailAnnotator.db.entity.ThumbnailUrlEntity;
import nlp.floschne.thumbnailAnnotator.db.mapper.CrawlerResultMapper;
import nlp.floschne.thumbnailAnnotator.db.mapper.ThumbnailUrlMapper;
import nlp.floschne.thumbnailAnnotator.db.repository.CaptionTokenEntityRepository;
import nlp.floschne.thumbnailAnnotator.db.repository.CrawlerResultEntityRepository;
import nlp.floschne.thumbnailAnnotator.db.repository.ThumbnailUrlEntityRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@ComponentScan(basePackages = {"nlp.floschne.thumbnailAnnotator.db"})
@Slf4j
public class DBService {

    private final CrawlerResultEntityRepository crawlerResultEntityRepository;

    private final ThumbnailUrlEntityRepository thumbnailUrlEntityRepository;

    private final CaptionTokenEntityRepository captionTokenEntityRepository;

    private final CrawlerResultMapper crawlerResultMapper;

    @Autowired
    public DBService(CrawlerResultEntityRepository crawlerResultEntityRepository,
                     ThumbnailUrlEntityRepository thumbnailUrlEntityRepository, CrawlerResultMapper crawlerResultMapper, ThumbnailUrlMapper thumbnailUrlMapper, CaptionTokenEntityRepository captionTokenEntityRepository) {
        this.crawlerResultEntityRepository = crawlerResultEntityRepository;
        this.thumbnailUrlEntityRepository = thumbnailUrlEntityRepository;
        this.crawlerResultMapper = crawlerResultMapper;
        this.captionTokenEntityRepository = captionTokenEntityRepository;
    }

    public void saveCrawlerResult(@NotNull CrawlerResult cre) {
        CrawlerResultEntity entity;
        if (this.crawlerResultExistsByCaptionToken(cre.getCaptionToken()))
            entity = this.crawlerResultEntityRepository.findByCaptionTokenValue(cre.getCaptionToken().getValue()).get();
        else
            entity = this.crawlerResultMapper.mapToEntity(cre);

        this.thumbnailUrlEntityRepository.saveAll(entity.getThumbnailUrls());
        this.captionTokenEntityRepository.save(entity.getCaptionToken());
        this.crawlerResultEntityRepository.save(entity);
    }

    public CrawlerResult findCrawlerResultById(@NotNull String id) {
        return this.crawlerResultMapper.mapFromEntity(this.crawlerResultEntityRepository.findById(id).get());
    }

    public CrawlerResult findCrawlerResultByCaptionToken(@NotNull CaptionToken captionToken) {
        return this.crawlerResultMapper.mapFromEntity(this.crawlerResultEntityRepository.findByCaptionTokenValue(captionToken.getValue()).get());
    }

    public Boolean crawlerResultExistsById(@NotNull String id) {
        return this.crawlerResultEntityRepository.existsById(id);
    }

    public Boolean crawlerResultExistsByCaptionToken(@NotNull CaptionToken captionToken) {
        return this.crawlerResultEntityRepository.findByCaptionTokenValue(captionToken.getValue()).isPresent();
    }


    public void incrementThumbnailUrlPriorityById(@NotNull String id) {
        ThumbnailUrlEntity thumbnailUrlEntity = this.thumbnailUrlEntityRepository.findById(id).get();
        thumbnailUrlEntity.setPriority(thumbnailUrlEntity.getPriority() + 1);
        this.thumbnailUrlEntityRepository.save(thumbnailUrlEntity);
    }


    public void decrementThumbnailUrlPriorityById(@NotNull String id) {
        ThumbnailUrlEntity thumbnailUrlEntity = this.thumbnailUrlEntityRepository.findById(id).get();
        thumbnailUrlEntity.setPriority(thumbnailUrlEntity.getPriority() - 1);
        this.thumbnailUrlEntityRepository.save(thumbnailUrlEntity);
    }


    public List<CrawlerResult> findAllCrawlerResult() {
        return this.crawlerResultMapper.mapFromEntityList((List<CrawlerResultEntity>) this.crawlerResultEntityRepository.findAll());
    }

    public void deleteAllCrawlerResultEntities() {
        this.crawlerResultEntityRepository.deleteAll();
    }
}
