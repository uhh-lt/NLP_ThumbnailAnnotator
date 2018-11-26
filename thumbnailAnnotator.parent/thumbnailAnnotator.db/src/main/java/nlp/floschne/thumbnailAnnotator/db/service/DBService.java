package nlp.floschne.thumbnailAnnotator.db.service;

import lombok.extern.slf4j.Slf4j;
import nlp.floschne.thumbnailAnnotator.core.domain.CaptionToken;
import nlp.floschne.thumbnailAnnotator.db.entity.CaptionTokenEntity;
import nlp.floschne.thumbnailAnnotator.db.entity.ThumbnailEntity;
import nlp.floschne.thumbnailAnnotator.db.mapper.CaptionTokenMapper;
import nlp.floschne.thumbnailAnnotator.db.repository.CaptionTokenEntityRepository;
import nlp.floschne.thumbnailAnnotator.db.repository.ThumbnailEntityRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Service
@ComponentScan(basePackages = {"nlp.floschne.thumbnailAnnotator.db"})
@Slf4j
public class DBService {

    private final ThumbnailEntityRepository thumbnailEntityRepository;

    private final CaptionTokenEntityRepository captionTokenEntityRepository;

    private final CaptionTokenMapper captionTokenMapper;

    @Autowired
    public DBService(ThumbnailEntityRepository thumbnailEntityRepository,
                     CaptionTokenEntityRepository captionTokenEntityRepository,
                     CaptionTokenMapper captionTokenMapper) {
        this.thumbnailEntityRepository = thumbnailEntityRepository;
        this.captionTokenEntityRepository = captionTokenEntityRepository;
        this.captionTokenMapper = captionTokenMapper;

        log.info("DB Service ready!");
    }

    public CaptionTokenEntity saveCaptionToken(@NotNull CaptionToken ct) {
        CaptionTokenEntity entity;
        if (this.captionTokenEntityRepository.findByValue(ct.getValue()).isPresent())
            entity = this.captionTokenEntityRepository.findByValue(ct.getValue()).get();
        else
            entity = this.captionTokenMapper.mapToEntity(ct);

        this.thumbnailEntityRepository.saveAll(entity.getThumbnails());
        this.captionTokenEntityRepository.save(entity);

        return entity;
    }

    public CaptionTokenEntity findCaptionTokenById(@NotNull String id) throws IOException {
        if (this.captionTokenEntityRepository.findById(id).isPresent()) {
            CaptionTokenEntity ct = this.captionTokenEntityRepository.findById(id).get();
            Collections.sort(ct.getThumbnails());
            return ct;
        } else {
            throw new IOException("Cannot find CaptionToken with ID: " + id);
        }
    }

    public CaptionTokenEntity findBestMatchingCaptionTokenByUDContext(@NotNull CaptionToken captionToken) {
        // first, find all CrawlerResultEntities with the CaptionToken's value
        List<CaptionTokenEntity> cts = this.captionTokenEntityRepository.findAllByValue(captionToken.getValue());

        // second, find the (best) matching CrawlerResultEntity by the CaptionToken's UDContext
        CaptionTokenEntity best = null;
        for (CaptionTokenEntity ct : cts)
            if (ct.getUdContext().containsAll(captionToken.getUdContext()))
                best = ct;

        if (best != null)
            Collections.sort(best.getThumbnails());
        return best;
    }

    public ThumbnailEntity incrementThumbnailPriorityById(@NotNull String id) throws IOException {
        if (this.thumbnailEntityRepository.findById(id).isPresent()) {
            ThumbnailEntity thumbnailEntity = this.thumbnailEntityRepository.findById(id).get();
            thumbnailEntity.setPriority(thumbnailEntity.getPriority() + 1);
            this.thumbnailEntityRepository.save(thumbnailEntity);
            return thumbnailEntity;
        } else
            throw new IOException("Cannot find ThumbnailEntity with ID: " + id);
    }


    public ThumbnailEntity decrementThumbnailPriorityById(@NotNull String id) throws IOException {
        if (this.thumbnailEntityRepository.findById(id).isPresent()) {
            ThumbnailEntity thumbnailEntity = this.thumbnailEntityRepository.findById(id).get();
            thumbnailEntity.setPriority(thumbnailEntity.getPriority() - 1);
            this.thumbnailEntityRepository.save(thumbnailEntity);
            return thumbnailEntity;
        } else
            throw new IOException("Cannot find ThumbnailEntity with ID: " + id);
    }


    public List<CaptionTokenEntity> findAllCaptionTokens() {
        return (List<CaptionTokenEntity>) this.captionTokenEntityRepository.findAll();
    }

    public void deleteAllCrawlerResultEntities() {
        this.captionTokenEntityRepository.deleteAll();
    }

}
