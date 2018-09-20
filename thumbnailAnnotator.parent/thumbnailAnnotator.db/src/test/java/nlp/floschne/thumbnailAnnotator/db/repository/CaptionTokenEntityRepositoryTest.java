package nlp.floschne.thumbnailAnnotator.db.repository;

import nlp.floschne.thumbnailAnnotator.core.domain.UDependency;
import nlp.floschne.thumbnailAnnotator.db.entity.CaptionTokenEntity;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class CaptionTokenEntityRepositoryTest extends RepositoryTestBase<CaptionTokenEntity> {

    public CaptionTokenEntityRepositoryTest() {
        super(RepoType.CAPTION_TOKEN);
    }

    @NotNull
    @Override
    protected CaptionTokenEntity createDummyEntity() {
        List<UDependency> udContext = new ArrayList<>();
        udContext.add(new UDependency("amod", "big", "ship"));
        CaptionTokenEntity entity = new CaptionTokenEntity("big ship", "COMPOUND", Arrays.asList("JJ", "NN"), Arrays.asList("big", "ship"), udContext);
        return entity;
    }

    @Override
    protected void assertEqual(CaptionTokenEntity a, CaptionTokenEntity b) {
        assertEquals(a.getType(), a.getType());
        assertEquals(a.getValue(), a.getValue());
        assertEquals(a.getId(), b.getId());
        assertEquals(a.getPosTags(), b.getPosTags());
        assertEquals(a.getTokens(), b.getTokens());
        assertEquals(a, b);
    }

    @Override
    protected void saveEntity(CaptionTokenEntity entity) {
        this.repo.save(entity);
    }

    @Test
    public void findByValue() {
        CaptionTokenEntity a = createDummyEntity();
        this.saveEntity(a);
        final Optional<CaptionTokenEntity> o = this.captionTokenEntityRepository.findByValue(a.getValue());
        assertTrue(o.isPresent());

        CaptionTokenEntity b = o.get();
        assertEqual(a, b);
    }
}