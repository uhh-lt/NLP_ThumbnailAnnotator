package nlp.floschne.thumbnailAnnotator.db.service;

import lombok.extern.slf4j.Slf4j;
import nlp.floschne.thumbnailAnnotator.core.domain.CaptionToken;
import nlp.floschne.thumbnailAnnotator.core.domain.Thumbnail;
import nlp.floschne.thumbnailAnnotator.db.entity.CaptionTokenEntity;
import nlp.floschne.thumbnailAnnotator.db.entity.FeatureVectorEntity;
import nlp.floschne.thumbnailAnnotator.db.entity.ThumbnailEntity;
import nlp.floschne.thumbnailAnnotator.db.entity.UserEntity;
import nlp.floschne.thumbnailAnnotator.db.mapper.CaptionTokenMapper;
import nlp.floschne.thumbnailAnnotator.db.mapper.FeatureVectorMapper;
import nlp.floschne.thumbnailAnnotator.db.mapper.ThumbnailMapper;
import nlp.floschne.thumbnailAnnotator.db.repository.CaptionTokenEntityRepository;
import nlp.floschne.thumbnailAnnotator.db.repository.FeatureVectorEntityRepo;
import nlp.floschne.thumbnailAnnotator.db.repository.ThumbnailEntityRepository;
import nlp.floschne.thumbnailAnnotator.db.repository.UserEntityRepository;
import nlp.floschne.thumbnailAnnotator.wsd.featureExtractor.FeatureVector;
import org.apache.commons.lang3.tuple.Pair;
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

    private final FeatureVectorEntityRepo featureVectorEntityRepo;

    private final CaptionTokenMapper captionTokenMapper;

    private final ThumbnailMapper thumbnailMapper;

    private final FeatureVectorMapper featureVectorMapper;


    @Autowired
    public DBService(ThumbnailEntityRepository thumbnailEntityRepository,
                     CaptionTokenEntityRepository captionTokenEntityRepository,
                     CaptionTokenMapper captionTokenMapper,
                     UserEntityRepository userEntityRepository,
                     FeatureVectorEntityRepo featureVectorEntityRepo, ThumbnailMapper thumbnailMapper, FeatureVectorMapper featureVectorMapper) {
        this.thumbnailEntityRepository = thumbnailEntityRepository;
        this.captionTokenEntityRepository = captionTokenEntityRepository;
        this.userEntityRepository = userEntityRepository;
        this.captionTokenMapper = captionTokenMapper;
        this.featureVectorEntityRepo = featureVectorEntityRepo;
        this.thumbnailMapper = thumbnailMapper;
        this.featureVectorMapper = featureVectorMapper;


        log.info("DB Service ready!");
    }

    // check if already cached (if value AND sentence context match) // TODO better UDContext?!
    private CaptionTokenEntity captionTokenIsCached(@NotNull CaptionToken ct) {
        if(this.captionTokenEntityRepository.findByValue(ct.getValue()).isPresent() &&
                this.captionTokenEntityRepository.findByValue(ct.getValue()).get().getSentenceContext().equals(ct.getSentenceContext()))
            return this.captionTokenEntityRepository.findByValue(ct.getValue()).get();
        else
            return null;
    }

    public CaptionTokenEntity saveCaptionToken(@NotNull CaptionToken ct, @NotNull String accessKey) throws IOException {
        // get user
        UserEntity owner;
        if (this.userEntityRepository.findByAccessKey(accessKey).isPresent())
            owner = this.userEntityRepository.findByAccessKey(accessKey).get();
        else
            throw new IOException("Cannot find UserEntity with accessKey: " + accessKey);

        //CaptionToken is already cached -> get the CaptionTokenEntity
        CaptionTokenEntity entity = captionTokenIsCached(ct);
        if (entity == null)
            //CaptionToken is not yet in the DB -> convert it to a CaptionTokenEntity
            entity = this.captionTokenMapper.mapToEntity(ct);

        this.thumbnailEntityRepository.saveAll(entity.getThumbnails());
        this.captionTokenEntityRepository.save(entity);

        // add the CaptionTokenEntity to the user
        if (owner.getCaptionTokenEntities() == null)
            owner.setCaptionTokenEntities(new ArrayList<>());
        owner.getCaptionTokenEntities().add(entity);

        this.userEntityRepository.save(owner);

        return entity;
    }

    public CaptionTokenEntity findCaptionTokenEntityById(@NotNull String id) throws IOException {
        if (this.captionTokenEntityRepository.findById(id).isPresent()) {
            CaptionTokenEntity ct = this.captionTokenEntityRepository.findById(id).get();
            Collections.sort(ct.getThumbnails());
            return ct;
        } else {
            throw new IOException("Cannot find CaptionTokenEntity with ID: " + id);
        }
    }

    public ThumbnailEntity findThumbnailEntityById(@NotNull String id) throws IOException {
        if (this.thumbnailEntityRepository.findById(id).isPresent()) {
            return this.thumbnailEntityRepository.findById(id).get();
        } else {
            throw new IOException("Cannot find ThumbnailEntity with ID: " + id);
        }
    }

    public CaptionToken findCaptionTokenById(@NotNull String id) throws IOException {
        if (this.captionTokenEntityRepository.findById(id).isPresent()) {
            CaptionTokenEntity ct = this.captionTokenEntityRepository.findById(id).get();
            Collections.sort(ct.getThumbnails());
            return this.captionTokenMapper.mapFromEntity(ct);
        } else {
            throw new IOException("Cannot find CaptionTokenEntity with ID: " + id);
        }
    }

    public Thumbnail findThumbnailById(@NotNull String id) throws IOException {
        if (this.thumbnailEntityRepository.findById(id).isPresent()) {
            ThumbnailEntity te = this.thumbnailEntityRepository.findById(id).get();
            return this.thumbnailMapper.mapFromEntity(te);
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
            log.info("Registered new User < " + username + " | " + password + ">!");
            return user;
        } else
            return this.userEntityRepository.findByUsername(username).get();
    }

    public boolean checkPassword(@NotNull String username, @NotNull String password) {
        if (!this.userEntityRepository.findByUsername(username).isPresent())
            return false;
        else
            return this.userEntityRepository.findByUsername(username).get().getPassword().equals(password);
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

    public List<UserEntity> getUsers() {
        return (List<UserEntity>) this.userEntityRepository.findAll();
    }


    // TODO implement sufficient test!
    // TODO rewrite this ugly piece of code... (stream / filter API)
    public Pair<List<CaptionTokenEntity>, List<CaptionToken>> getCachedAndUncachedCaptionTokens(@NotNull List<CaptionToken> extractedCaptionTokens, @NotNull String accessKey) throws IOException {
        // get cached CaptionTokens from extracted CaptionTokens
        Set<CaptionTokenEntity> cached = new HashSet<>();
        for (CaptionToken ct : extractedCaptionTokens) {
            CaptionTokenEntity cte = captionTokenIsCached(ct);
            if (cte != null)
                cached.add(cte);
        }

        // remove all the cached CaptionTokens from the extracted to get the uncached CaptionTokens
        Set<CaptionToken> uncachedSet = new HashSet<>(extractedCaptionTokens);
        uncachedSet.removeAll(this.captionTokenMapper.mapFromEntityList(new ArrayList<>(cached)));

        return Pair.of(new ArrayList<>(cached), new ArrayList<>(uncachedSet));
    }

    public List<FeatureVectorEntity> saveFeatureVectors(String ownerUsername, List<FeatureVector> featureVectors) {
        List<FeatureVectorEntity> featureVectorEntities = this.featureVectorMapper.mapToEntityList(featureVectors);
        for (FeatureVectorEntity fve : featureVectorEntities)
            fve.setOwnerUserName(ownerUsername);

        return (List<FeatureVectorEntity>) this.featureVectorEntityRepo.saveAll(featureVectorEntities);
    }

    public FeatureVector findFeatureVectorById(String featureVectorId) throws IOException {
        Optional<FeatureVectorEntity> featureVector = this.featureVectorEntityRepo.findById(featureVectorId);
        if (featureVector.isPresent())
            return this.featureVectorMapper.mapFromEntity(featureVector.get());
        else
            throw new IOException("Cannot find FeatureVectorEntity with ID: " + featureVectorId);
    }

    public UserEntity findOwnerOfCaptionTokenId(String captionTokenId) throws IOException {
        CaptionTokenEntity cte = this.findCaptionTokenEntityById(captionTokenId);
        List<UserEntity> allUsers = (List<UserEntity>) this.userEntityRepository.findAll();
        for (UserEntity user : allUsers) {
            if (user.getCaptionTokenEntities().contains(cte))
                return user;
        }
        throw new IOException("Cannot find Owner of CaptionTokenEntity with ID: " + captionTokenId);
    }
}
