package nlp.floschne.thumbnailAnnotator.db.repository;

import nlp.floschne.thumbnailAnnotator.db.entity.CaptionTokenEntity;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.Arrays;
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
        return new CaptionTokenEntity("big ship", "COMPOUND", Arrays.asList("JJ", "NN"), Arrays.asList("big", "ship"));
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