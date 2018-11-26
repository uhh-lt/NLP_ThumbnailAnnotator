package nlp.floschne.thumbnailAnnotator.db.repository;

import nlp.floschne.thumbnailAnnotator.db.RedisConfig;
import nlp.floschne.thumbnailAnnotator.db.entity.Entity;
import nlp.floschne.thumbnailAnnotator.db.util.EmbeddedRedisServer;
import nlp.floschne.thumbnailAnnotator.db.util.RequiresRedisServer;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {RedisConfig.class, EmbeddedRedisServer.class})
public abstract class RepositoryTestBase<E extends Entity> {

    /**
     * We need to have a Redis server instance available. <br />
     * 1) Start/Stop an embedded instance or reuse an already running local installation <br />
     * 2) Ignore tests if startup failed and no server running locally.
     */
    public static @ClassRule
    RuleChain rules = RuleChain
            .outerRule(EmbeddedRedisServer.runningAt(6379).suppressExceptions())
            .around(RequiresRedisServer.onLocalhost().atLeast("3.2"));

    @Autowired
    protected ThumbnailEntityRepository thumbnailEntityRepository;

    @Autowired
    protected CaptionTokenEntityRepository captionTokenEntityRepository;


    public enum RepoType {
        THUMBNAIL_URL, CAPTION_TOKEN;
    }

    protected CrudRepository<E, String> repo;

    private RepoType type;

    public RepositoryTestBase(@NotNull RepoType type) {
        this.type = type;
    }

    private void setRepo() {
        switch (type) {
            case THUMBNAIL_URL:
                this.repo = (CrudRepository<E, String>) thumbnailEntityRepository;
                break;
            case CAPTION_TOKEN:
                this.repo = (CrudRepository<E, String>) captionTokenEntityRepository;
                break;
        }
    }

    @Before
    public void flushRepository() {
        this.setRepo();
        this.thumbnailEntityRepository.deleteAll();
        this.captionTokenEntityRepository.deleteAll();
    }

    @NotNull
    protected abstract E createDummyEntity();

    protected abstract void assertEqual(E a, E b);

    protected abstract void saveEntity(E entity);

    @Test
    public void whenSaving_thenAvailableOnRetrieval() {
        E a = createDummyEntity();
        this.saveEntity(a);
        final Optional<E> o = this.repo.findById(a.getId());
        assertTrue(o.isPresent());

        E b = o.get();
        this.assertEqual(a, b);
    }

    @Test
    public void whenSavingMultiple_thenAllShouldAvailableOnRetrieval() {

        final E a = createDummyEntity();
        final E b = createDummyEntity();
        final E c = createDummyEntity();

        this.saveEntity(a);
        this.saveEntity(b);
        this.saveEntity(c);

        List<E> saved = new ArrayList<>();
        saved.add(a);
        saved.add(b);
        saved.add(c);

        List<E> entities = new ArrayList<>();
        this.repo.findAll().forEach(entities::add);
        entities.containsAll(saved);
    }

    @Test
    public void whenUpdating_thenAvailableOnRetrieval() {
        final E a = createDummyEntity();
        this.saveEntity(a);

        Optional<E> o = this.repo.findById(a.getId());
        assertTrue(o.isPresent());

        a.setId("updated");
        this.saveEntity(a);

        o = this.repo.findById(a.getId());
        assertTrue(o.isPresent());
        E b = o.get();
        this.assertEqual(a, b);
    }

    @Test
    public void whenDeleting_thenNotAvailableOnRetrieval() {
        final E a = createDummyEntity();
        this.saveEntity(a);
        assertTrue(this.repo.findById(a.getId()).isPresent());
        this.repo.deleteById(a.getId());
        assertFalse(this.repo.findById(a.getId()).isPresent());
    }
}
