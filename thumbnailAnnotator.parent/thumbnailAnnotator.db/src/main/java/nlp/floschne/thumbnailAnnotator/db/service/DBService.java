package nlp.floschne.thumbnailAnnotator.db.service;

import lombok.extern.slf4j.Slf4j;
import nlp.floschne.thumbnailAnnotator.core.domain.CaptionToken;
import nlp.floschne.thumbnailAnnotator.core.domain.CrawlerResult;
import nlp.floschne.thumbnailAnnotator.db.entity.CrawlerResultEntity;
import nlp.floschne.thumbnailAnnotator.db.entity.ThumbnailEntity;
import nlp.floschne.thumbnailAnnotator.db.mapper.CrawlerResultMapper;
import nlp.floschne.thumbnailAnnotator.db.mapper.ThumbnailMapper;
import nlp.floschne.thumbnailAnnotator.db.repository.CaptionTokenEntityRepository;
import nlp.floschne.thumbnailAnnotator.db.repository.CrawlerResultEntityRepository;
import nlp.floschne.thumbnailAnnotator.db.repository.ThumbnailEntityRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@ComponentScan(basePackages = {"nlp.floschne.thumbnailAnnotator.db"})
@Slf4j
public class DBService {

    private final CrawlerResultEntityRepository crawlerResultEntityRepository;

    private final ThumbnailEntityRepository thumbnailEntityRepository;

    private final CaptionTokenEntityRepository captionTokenEntityRepository;

    private final CrawlerResultMapper crawlerResultMapper;

    @Autowired
    public DBService(CrawlerResultEntityRepository crawlerResultEntityRepository,
                     ThumbnailEntityRepository thumbnailEntityRepository, CrawlerResultMapper crawlerResultMapper, ThumbnailMapper thumbnailMapper, CaptionTokenEntityRepository captionTokenEntityRepository) {
        this.crawlerResultEntityRepository = crawlerResultEntityRepository;
        this.thumbnailEntityRepository = thumbnailEntityRepository;
        this.crawlerResultMapper = crawlerResultMapper;
        this.captionTokenEntityRepository = captionTokenEntityRepository;

        log.info("DB Service ready!");
    }

    public void saveCrawlerResult(@NotNull CrawlerResult cre) {
        CrawlerResultEntity entity;
        if (this.crawlerResultExistsByCaptionToken(cre.getCaptionToken()))
            entity = this.crawlerResultEntityRepository.findByCaptionTokenValue(cre.getCaptionToken().getValue()).get();
        else
            entity = this.crawlerResultMapper.mapToEntity(cre);

        this.thumbnailEntityRepository.saveAll(entity.getThumbnails());
        this.captionTokenEntityRepository.save(entity.getCaptionToken());
        this.crawlerResultEntityRepository.save(entity);
    }

    public CrawlerResultEntity findCrawlerResultById(@NotNull String id) {
        CrawlerResultEntity cr = this.crawlerResultEntityRepository.findById(id).get();
        Collections.sort(cr.getThumbnails());
        return cr;
    }

    public CrawlerResultEntity findCrawlerResultByCaptionToken(@NotNull CaptionToken captionToken) {
        CrawlerResultEntity cr = this.crawlerResultEntityRepository.findByCaptionTokenValue(captionToken.getValue()).get();
        Collections.sort(cr.getThumbnails());
        return cr;
    }

    public Boolean crawlerResultExistsById(@NotNull String id) {
        return this.crawlerResultEntityRepository.existsById(id);
    }

    public Boolean crawlerResultExistsByCaptionToken(@NotNull CaptionToken captionToken) {
        return this.crawlerResultEntityRepository.findByCaptionTokenValue(captionToken.getValue()).isPresent();
    }

    public ThumbnailEntity incrementThumbnailPriorityById(@NotNull String id) {
        ThumbnailEntity thumbnailEntity = this.thumbnailEntityRepository.findById(id).get();
        thumbnailEntity.setPriority(thumbnailEntity.getPriority() + 1);
        this.thumbnailEntityRepository.save(thumbnailEntity);
        return thumbnailEntity;
    }


    public ThumbnailEntity decrementThumbnailPriorityById(@NotNull String id) {
        ThumbnailEntity thumbnailEntity = this.thumbnailEntityRepository.findById(id).get();
        thumbnailEntity.setPriority(thumbnailEntity.getPriority() - 1);
        this.thumbnailEntityRepository.save(thumbnailEntity);
        return thumbnailEntity;
    }


    public List<CrawlerResultEntity> findAllCrawlerResult() {
        return (List<CrawlerResultEntity>) this.crawlerResultEntityRepository.findAll();
    }

    public void deleteAllCrawlerResultEntities() {
        this.crawlerResultEntityRepository.deleteAll();
    }
}
