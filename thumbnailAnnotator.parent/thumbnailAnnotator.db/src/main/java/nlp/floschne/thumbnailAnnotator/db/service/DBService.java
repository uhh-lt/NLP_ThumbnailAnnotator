package nlp.floschne.thumbnailAnnotator.db.service;

import lombok.extern.slf4j.Slf4j;
import nlp.floschne.thumbnailAnnotator.core.domain.CaptionToken;
import nlp.floschne.thumbnailAnnotator.core.domain.Thumbnail;
import nlp.floschne.thumbnailAnnotator.db.entity.CaptionTokenEntity;
import nlp.floschne.thumbnailAnnotator.db.entity.ThumbnailEntity;
import nlp.floschne.thumbnailAnnotator.db.entity.UserEntity;
import nlp.floschne.thumbnailAnnotator.db.mapper.CaptionTokenMapper;
import nlp.floschne.thumbnailAnnotator.db.mapper.ThumbnailMapper;
import nlp.floschne.thumbnailAnnotator.db.repository.CaptionTokenEntityRepository;
import nlp.floschne.thumbnailAnnotator.db.repository.ThumbnailEntityRepository;
import nlp.floschne.thumbnailAnnotator.db.repository.UserEntityRepository;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@ComponentScan(basePackages = {"nlp.floschne.thumbnailAnnotator.db"})
@Slf4j
public class DBService {

    private final ThumbnailEntityRepository thumbnailEntityRepository;

    private final CaptionTokenEntityRepository captionTokenEntityRepository;

    private final UserEntityRepository userEntityRepository;

    private final CaptionTokenMapper captionTokenMapper;

    private final ThumbnailMapper thumbnailMapper;


    @Autowired
    public DBService(ThumbnailEntityRepository thumbnailEntityRepository,
                     CaptionTokenEntityRepository captionTokenEntityRepository,
                     CaptionTokenMapper captionTokenMapper,
                     UserEntityRepository userEntityRepository,
                     ThumbnailMapper thumbnailMapper) {
        this.thumbnailEntityRepository = thumbnailEntityRepository;
        this.captionTokenEntityRepository = captionTokenEntityRepository;
        this.userEntityRepository = userEntityRepository;
        this.captionTokenMapper = captionTokenMapper;
        this.thumbnailMapper = thumbnailMapper;


        log.info("DB Service ready!");
    }

    // check if already cached (if value AND sentence context match) // TODO better UDContext?!
    private CaptionTokenEntity captionTokenIsCached(@NotNull CaptionToken ct) {
        // trivial case: if there is no cte with the same value it's obviously not cached
        if (!this.captionTokenEntityRepository.findByValue(ct.getValue()).isPresent())
            return null;

        // non-trivial case: since there is a problem with indexing by SentenceContext, we have to check manually..
        List<CaptionTokenEntity> cteWithSameValue = this.captionTokenEntityRepository.findAllByValue(ct.getValue());
        if (cteWithSameValue.size() >= 1) {
            for (CaptionTokenEntity cte : cteWithSameValue)
                if (cte.getSentenceContext().equals(ct.getSentenceContext()))
//                if (cte.getUdContext().equals(ct.getUdContext()))
                    return cte;
        }
        return null;
    }

    public CaptionTokenEntity updateThumbnailsOfCaptionTokenEntity(@NotNull String id, List<Thumbnail> thumbnails) throws IOException {
        CaptionTokenEntity cte = this.findCaptionTokenEntityById(id);
        // remove old ones from cache since they're not needed anymore
        this.thumbnailEntityRepository.deleteAll(cte.getThumbnails());

        List<ThumbnailEntity> newThumbnails = this.thumbnailMapper.mapToEntityList(thumbnails);
        List<Long> beforeIds = cte.getThumbnails().stream().map(ThumbnailEntity::getShutterstockId).collect(Collectors.toList());
        cte.setThumbnails(newThumbnails);
        this.saveCaptionTokenEntity(cte);
        List<Long> afterIds = cte.getThumbnails().stream().map(ThumbnailEntity::getShutterstockId).collect(Collectors.toList());

        // TODO .equals() does not work.. (why?!)
        if (!afterIds.containsAll(beforeIds) && afterIds.size() == beforeIds.size())
            throw new IOException("There was a problem when updating Thumbnails of CaptionTokenEntity with ID <" + id + ">");

        return cte;
    }

    public CaptionTokenEntity saveCaptionToken(@NotNull CaptionToken ct, @NotNull String accessKey) throws IOException {
        UserEntity owner = getUserEntity(accessKey);

        //CaptionToken is already cached -> get the CaptionTokenEntity
        CaptionTokenEntity entity = captionTokenIsCached(ct);
        if (entity == null)
            //CaptionToken is not yet in the DB -> convert it to a CaptionTokenEntity
            entity = this.captionTokenMapper.mapToEntity(ct);

        this.saveCaptionTokenEntity(entity);

        // add the CaptionTokenEntity to the user
        if (owner.getCaptionTokenEntities() == null)
            owner.setCaptionTokenEntities(new ArrayList<>());
        owner.getCaptionTokenEntities().add(entity);

        this.userEntityRepository.save(owner);

        return entity;
    }

    private void saveCaptionTokenEntity(CaptionTokenEntity entity) {
        this.thumbnailEntityRepository.saveAll(entity.getThumbnails());
        this.captionTokenEntityRepository.save(entity);
    }

    @NotNull
    private UserEntity getUserEntity(@NotNull String accessKey) throws IOException {
        // get user
        UserEntity owner;
        if (this.userEntityRepository.findByAccessKey(accessKey).isPresent())
            owner = this.userEntityRepository.findByAccessKey(accessKey).get();
        else
            throw new IOException("Cannot find UserEntity with accessKey: " + accessKey);
        return owner;
    }

    public CaptionTokenEntity findCaptionTokenEntityById(@NotNull String id) throws IOException {
        if (this.captionTokenEntityRepository.findById(id).isPresent())
            return this.captionTokenEntityRepository.findById(id).get();
        else
            throw new IOException("Cannot find CaptionTokenEntity with ID: " + id);
    }

    public ThumbnailEntity findThumbnailEntityById(@NotNull String id) throws IOException {
        if (this.thumbnailEntityRepository.findById(id).isPresent())
            return this.thumbnailEntityRepository.findById(id).get();
        else
            throw new IOException("Cannot find ThumbnailEntity with ID: " + id);
    }

    public CaptionToken findCaptionTokenById(@NotNull String id) throws IOException {
        if (this.captionTokenEntityRepository.findById(id).isPresent()) {
            CaptionTokenEntity ct = this.captionTokenEntityRepository.findById(id).get();
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

    public UserEntity registerUser(@NotNull String username, @NotNull String password) {
        UserEntity user = null;
        if (!this.userEntityRepository.findByUsername(username).isPresent()) {
            user = new UserEntity(username, password, UUID.randomUUID().toString(), null);
            this.userEntityRepository.save(user);
        }
        return user;
    }

    public boolean checkPassword(@NotNull String username, @NotNull String password) {
        if (!this.userEntityRepository.findByUsername(username).isPresent())
            return false;
        else
            return this.userEntityRepository.findByUsername(username).get().getPassword().equals(password);
    }

    public UserEntity getUserByAccessKey(@NotNull String accessKey) {
        if (this.userEntityRepository.findByAccessKey(accessKey).isPresent())
            return this.userEntityRepository.findByAccessKey(accessKey).get();
        return null;
    }

    public UserEntity getUserByUsername(@NotNull String username) {
        if (this.userEntityRepository.findByUsername(username).isPresent())
            return this.userEntityRepository.findByUsername(username).get();
        else return null;
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
}
