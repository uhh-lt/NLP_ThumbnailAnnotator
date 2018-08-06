package nlp.floschne.thumbnailAnnotator.db.repository;

import junit.framework.TestCase;
import nlp.floschne.thumbnailAnnotator.db.Depiction;
import nlp.floschne.thumbnailAnnotator.db.DepictionRepository;
import nlp.floschne.thumbnailAnnotator.db.RedisConfig;
import nlp.floschne.thumbnailAnnotator.db.util.EmbeddedRedisServer;
import nlp.floschne.thumbnailAnnotator.db.util.RequiresRedisServer;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {RedisConfig.class, EmbeddedRedisServer.class})
public class DepictionRepositoryTests {

    @Autowired
    private DepictionRepository repository;


    /**
     * We need to have a Redis server instance available. <br />
     * 1) Start/Stop an embedded instance or reuse an already running local installation <br />
     * 2) Ignore tests if startup failed and no server running locally.
     */
    public static @ClassRule
    RuleChain rules = RuleChain
            .outerRule(EmbeddedRedisServer.runningAt(6379).suppressExceptions())
            .around(RequiresRedisServer.onLocalhost().atLeast("3.2"));

    private static Depiction createRandomDummyDepiction() {
        return new Depiction(UUID.randomUUID().toString(), Collections.singletonList(UUID.randomUUID().toString()));
    }

    @Before
    public void deleteAllDepictions() {
        repository.deleteAll();
    }

    @Test
    public void whenSavingDepiction_thenAvailableOnRetrieval() throws Exception {
        final Depiction a = createRandomDummyDepiction();
        repository.save(a);

        final Optional<Depiction> o = repository.findById(a.getCaptionTokenValue());
        assertTrue(o.isPresent());

        Depiction b = o.get();
        TestCase.assertEquals(a.getCaptionTokenValue(), b.getCaptionTokenValue());
        TestCase.assertEquals(a.getThumbnailURLs(), b.getThumbnailURLs());
    }


    @Test
    public void whenUpdatingDepiction_thenAvailableOnRetrieval() {
        final Depiction a = createRandomDummyDepiction();
        repository.save(a);
        a.setThumbnailURLs(Collections.singletonList("www.bing.com"));
        repository.save(a);

        final Optional<Depiction> o = repository.findById(a.getCaptionTokenValue());
        assertTrue(o.isPresent());
        Depiction b = o.get();
        TestCase.assertEquals(a.getCaptionTokenValue(), b.getCaptionTokenValue());
        TestCase.assertNotSame(a.getThumbnailURLs(), b.getThumbnailURLs());
    }


    @Test
    @Ignore // not working when running with mvn test .. in IntelliJ everything works fine (maybe due to parallel execution of tests with surefire)
    public void whenSavingDepictions_thenAllShouldAvailableOnRetrieval() {
        final Depiction a = createRandomDummyDepiction();
        final Depiction b = createRandomDummyDepiction();
        final Depiction c = createRandomDummyDepiction();
        repository.save(a);
        repository.save(b);
        repository.save(c);

        List<Depiction> depictions = new ArrayList<>();
        repository.findAll().forEach(depictions::add);
        assertEquals(3, depictions.size());
    }

    @Test
    public void whenDeletingDepiction_thenNotAvailableOnRetrieval() {
        final Depiction a = createRandomDummyDepiction();
        repository.save(a);
        repository.deleteById(a.getCaptionTokenValue());
        TestCase.assertFalse(repository.findById(a.getCaptionTokenValue()).isPresent());
    }

}