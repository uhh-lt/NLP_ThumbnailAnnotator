package nlp.floschne.thumbnailAnnotator.db.service;

import lombok.extern.slf4j.Slf4j;
import nlp.floschne.thumbnailAnnotator.core.domain.CaptionToken;
import nlp.floschne.thumbnailAnnotator.db.entity.CaptionTokenEntity;
import nlp.floschne.thumbnailAnnotator.db.entity.ThumbnailEntity;
import nlp.floschne.thumbnailAnnotator.db.entity.UserEntity;
import nlp.floschne.thumbnailAnnotator.db.mapper.CaptionTokenMapper;
import nlp.floschne.thumbnailAnnotator.db.repository.CaptionTokenEntityRepository;
import nlp.floschne.thumbnailAnnotator.db.repository.ThumbnailEntityRepository;
import nlp.floschne.thumbnailAnnotator.db.repository.UserEntityRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@ComponentScan(basePackages = {"nlp.floschne.thumbnailAnnotator.db"})
@Slf4j
public class DBService {

    private final ThumbnailEntityRepository thumbnailEntityRepository;

    private final CaptionTokenEntityRepository captionTokenEntityRepository;

    private final UserEntityRepository userEntityRepository;

    private final CaptionTokenMapper captionTokenMapper;

    @Autowired
    public DBService(ThumbnailEntityRepository thumbnailEntityRepository,
                     CaptionTokenEntityRepository captionTokenEntityRepository,
                     CaptionTokenMapper captionTokenMapper,
                     UserEntityRepository userEntityRepository) {
        this.thumbnailEntityRepository = thumbnailEntityRepository;
        this.captionTokenEntityRepository = captionTokenEntityRepository;
        this.userEntityRepository = userEntityRepository;
        this.captionTokenMapper = captionTokenMapper;


        log.info("DB Service ready!");
    }

    public CaptionTokenEntity saveCaptionToken(@NotNull CaptionToken ct, @NotNull String accessKey) throws IOException {
        //TODO save the caption to the user by access key!
        CaptionTokenEntity entity;
        if (this.captionTokenEntityRepository.findByValue(ct.getValue()).isPresent())
            //CaptionToken is already cached -> get the CaptionTokenEntity
            entity = this.captionTokenEntityRepository.findByValue(ct.getValue()).get();
        else
            //CaptionToken is not yet in the DB -> convert it to a CaptionTokenEntity
            entity = this.captionTokenMapper.mapToEntity(ct);

        UserEntity owner;
        if (this.userEntityRepository.findByAccessKey(accessKey).isPresent())
            owner = this.userEntityRepository.findByAccessKey(accessKey).get();
        else
            throw new IOException("Cannot find UserEntity with accessKey: " + accessKey);

        this.thumbnailEntityRepository.saveAll(entity.getThumbnails());
        this.captionTokenEntityRepository.save(entity);

        if (owner.getCaptionTokenEntities() == null)
            owner.setCaptionTokenEntities(new ArrayList<>());
        owner.getCaptionTokenEntities().add(entity);

        this.userEntityRepository.save(owner);

        return entity;
    }

    public CaptionTokenEntity findCaptionTokenById(@NotNull String id) throws IOException {
        if (this.captionTokenEntityRepository.findById(id).isPresent()) {
            CaptionTokenEntity ct = this.captionTokenEntityRepository.findById(id).get();
            Collections.sort(ct.getThumbnails());
            return ct;
        } else {
            throw new IOException("Cannot find CaptionTokenEntity with ID: " + id);
        }
    }

    public ThumbnailEntity findThumbnailById(@NotNull String id) throws IOException {
        if (this.thumbnailEntityRepository.findById(id).isPresent()) {
            ThumbnailEntity te = this.thumbnailEntityRepository.findById(id).get();
            return te;
        } else {
            throw new IOException("Cannot find ThumbnailEntity with ID: " + id);
        }
    }

    public CaptionTokenEntity findBestMatchingCaptionTokenByUDContext(@NotNull CaptionToken captionToken) {
        // first, find all CaptionTokenEntities with the CaptionToken's value
        List<CaptionTokenEntity> cts = this.captionTokenEntityRepository.findAllByValue(captionToken.getValue());

        // second, find the (best) matching CaptionToken by the UDContext
        CaptionTokenEntity best = null;
        for (CaptionTokenEntity ct : cts)
            if (ct.getUdContext().containsAll(captionToken.getUdContext()))
                best = ct;

        if (best != null)
            Collections.sort(best.getThumbnails());
        return best;
    }

    public List<CaptionTokenEntity> findCaptionTokensByUsername(@NotNull String username) throws IOException {
        UserEntity owner;
        if (this.userEntityRepository.findByUsername(username).isPresent())
            owner = this.userEntityRepository.findByUsername(username).get();
        else
            throw new IOException("Cannot find UserEntity with username: " + username);

        return owner.getCaptionTokenEntities();
    }

    public List<CaptionTokenEntity> findCaptionTokensByAccessKey(@NotNull String accessKey) throws IOException {
        UserEntity owner;
        if (this.userEntityRepository.findByAccessKey(accessKey).isPresent())
            owner = this.userEntityRepository.findByAccessKey(accessKey).get();
        else
            throw new IOException("Cannot find UserEntity with AccessKey: " + accessKey);

        return owner.getCaptionTokenEntities();
    }

    public List<CaptionTokenEntity> findCaptionTokenEntitiesOfAccessKey(@NotNull CaptionToken captionToken, @NotNull String accessKey) throws IOException {
        List<CaptionTokenEntity> all = this.findCaptionTokensByAccessKey(accessKey);
        return all.stream()
                .filter(captionTokenEntity -> captionToken.getValue().equals(captionTokenEntity.getValue()))
                .collect(Collectors.toList());
    }

    public ThumbnailEntity incrementThumbnailPriorityById(@NotNull String id) throws IOException {
        if (this.thumbnailEntityRepository.findById(id).isPresent()) {
            ThumbnailEntity thumbnailEntity = this.thumbnailEntityRepository.findById(id).get();
            thumbnailEntity.setPriority(thumbnailEntity.getPriority() + 1);
            this.thumbnailEntityRepository.save(thumbnailEntity);
            return this.thumbnailEntityRepository.findById(id).get();
        } else
            throw new IOException("Cannot find ThumbnailEntity with ID: " + id);
    }


    public ThumbnailEntity decrementThumbnailPriorityById(@NotNull String id) throws IOException {
        if (this.thumbnailEntityRepository.findById(id).isPresent()) {
            ThumbnailEntity thumbnailEntity = this.thumbnailEntityRepository.findById(id).get();
            thumbnailEntity.setPriority(thumbnailEntity.getPriority() - 1);
            this.thumbnailEntityRepository.save(thumbnailEntity);
            return this.thumbnailEntityRepository.findById(id).get();
        } else
            throw new IOException("Cannot find ThumbnailEntity with ID: " + id);
    }


    public ThumbnailEntity setThumbnailPriorityById(@NotNull String id, @NotNull Integer priority) throws IOException {
        if (this.thumbnailEntityRepository.findById(id).isPresent()) {
            ThumbnailEntity thumbnailEntity = this.thumbnailEntityRepository.findById(id).get();
            thumbnailEntity.setPriority(priority);
            this.thumbnailEntityRepository.save(thumbnailEntity);
            return this.thumbnailEntityRepository.findById(id).get();
        } else
            throw new IOException("Cannot find ThumbnailEntity with ID: " + id);
    }

    public List<CaptionTokenEntity> findAllCaptionTokens() {
        return (List<CaptionTokenEntity>) this.captionTokenEntityRepository.findAll();
    }

    public void deleteAllCaptionTokenEntities() {
        this.thumbnailEntityRepository.deleteAll();
        this.captionTokenEntityRepository.deleteAll();
    }

    public UserEntity registerUser(@NotNull String username, @NotNull String password) {
        UserEntity user = null;
        if (!this.userEntityRepository.findByUsername(username).isPresent()) {
            user = new UserEntity(username, password, UUID.randomUUID().toString(), null);
            this.userEntityRepository.save(user);
            return user;
        } else
            return this.userEntityRepository.findByUsername(username).get();
    }

    public boolean checkPassword(@NotNull String username, @NotNull String password) {
        if (!this.userEntityRepository.findByUsername(username).isPresent())
            return false;
        else return this.userEntityRepository.findByUsername(username).get().getPassword().equals(password);
    }

    public UserEntity getUserByAccessKey(@NotNull String accessKey) {
        if (!this.userEntityRepository.findByAccessKey(accessKey).isPresent())
            return this.userEntityRepository.findByAccessKey(accessKey).get();
        else return null;
    }

    public UserEntity getUserByUsername(@NotNull String username) {
        if (this.userEntityRepository.findByUsername(username).isPresent())
            return this.userEntityRepository.findByUsername(username).get();
        else return null;
    }

    public List<CaptionToken> filterUnCachedCaptionTokens(@NotNull List<CaptionToken> extractedCaptionTokens, @NotNull String accessKey) throws IOException {
        // list of CaptionTokens cached for the the User
        List<CaptionToken> cachedForUser = this.captionTokenMapper.mapFromEntityList(this.findCaptionTokensByAccessKey(accessKey));

        // TODO find more efficient way!
        Set<CaptionToken> unCached = new HashSet<>();
        for (CaptionToken ct : extractedCaptionTokens) {
            for (CaptionToken cta : cachedForUser)
                if (!ct.contextEquals(cta))
                    unCached.add(ct);
        }

        return new ArrayList<>(unCached);
    }
}
