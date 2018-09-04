package nlp.floschne.thumbnailAnnotator.db.util;

import nlp.floschne.thumbnailAnnotator.db.RedisConfig;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {RedisConfig.class, EmbeddedRedisServer.class})
public abstract class RepositoryTestsBase {

    /**
     * We need to have a Redis server instance available. <br />
     * 1) Start/Stop an embedded instance or reuse an already running local installation <br />
     * 2) Ignore tests if startup failed and no server running locally.
     */
    public static @ClassRule
    RuleChain rules = RuleChain
            .outerRule(EmbeddedRedisServer.runningAt(6379).suppressExceptions())
            .around(RequiresRedisServer.onLocalhost().atLeast("3.2"));

    @Test
    public abstract void whenSaving_thenAvailableOnRetrieval();

    @Test
    public abstract void whenSavingMultiple_thenAllShouldAvailableOnRetrieval();

    @Test
    public abstract void whenUpdating_thenAvailableOnRetrieval();

    @Test
    public abstract void whenDeleting_thenNotAvailableOnRetrieval();
}
