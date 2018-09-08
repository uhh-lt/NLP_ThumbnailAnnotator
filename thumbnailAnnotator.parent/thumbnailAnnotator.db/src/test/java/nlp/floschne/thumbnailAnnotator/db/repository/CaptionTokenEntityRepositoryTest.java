package nlp.floschne.thumbnailAnnotator.db.repository;

import nlp.floschne.thumbnailAnnotator.db.entity.CaptionTokenEntity;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.*;

public class CaptionTokenEntityRepositoryTest extends RepositoryTestBase {

    private void assertEqual(CaptionTokenEntity a, CaptionTokenEntity b) {
        assertEquals(a.getType(), a.getType());
        assertEquals(a.getValue(), a.getValue());
        assertEquals(a.getId(), b.getId());
        assertEquals(a.getBeginPosition(), b.getBeginPosition());
        assertEquals(a.getEndPosition(), b.getEndPosition());
        assertEquals(a.getPosTags(), b.getPosTags());
        assertEquals(a.getTokens(), b.getTokens());
        assertEquals(a, b);
    }

    @Test
    public void findByValue() {
        CaptionTokenEntity a = createDummyCaptionTokenEntity();
        this.captionTokenEntityRepository.save(a);
        final Optional<CaptionTokenEntity> o = this.captionTokenEntityRepository.findByValue(a.getValue());
        assertTrue(o.isPresent());

        CaptionTokenEntity b = o.get();
        assertEqual(a, b);
    }


    @Override
    public void whenSaving_thenAvailableOnRetrieval() {
        CaptionTokenEntity a = createDummyCaptionTokenEntity();
        this.captionTokenEntityRepository.save(a);
        final Optional<CaptionTokenEntity> o = this.captionTokenEntityRepository.findById(a.getId());
        assertTrue(o.isPresent());

        CaptionTokenEntity b = o.get();
        assertEqual(a, b);

    }

    @Override
    public void whenSavingMultiple_thenAllShouldAvailableOnRetrieval() {
        CaptionTokenEntity a = createDummyCaptionTokenEntity();
        CaptionTokenEntity b = createDummyCaptionTokenEntity();
        CaptionTokenEntity c = createDummyCaptionTokenEntity();
        this.captionTokenEntityRepository.save(a);
        this.captionTokenEntityRepository.save(b);
        this.captionTokenEntityRepository.save(c);

        List<CaptionTokenEntity> captionTokenEntities = new ArrayList<>();
        this.captionTokenEntityRepository.findAll().forEach(captionTokenEntities::add);
        assertEquals(3, captionTokenEntities.size());

    }

    @Override
    public void whenUpdating_thenAvailableOnRetrieval() {

        CaptionTokenEntity a = createDummyCaptionTokenEntity();
        this.captionTokenEntityRepository.save(a);
        Optional<CaptionTokenEntity> o = this.captionTokenEntityRepository.findById(a.getId());
        assertTrue(o.isPresent());

        a.setValue("updated");
        this.captionTokenEntityRepository.save(a);
        o = this.captionTokenEntityRepository.findById(a.getId());
        assertTrue(o.isPresent());

        CaptionTokenEntity b = o.get();
        assertEqual(a, b);
    }

    @Override
    public void whenDeleting_thenNotAvailableOnRetrieval() {

        CaptionTokenEntity a = createDummyCaptionTokenEntity();
        this.captionTokenEntityRepository.save(a);
        assertTrue(this.captionTokenEntityRepository.findById(a.getId()).isPresent());

        this.captionTokenEntityRepository.deleteById(a.getId());
        assertFalse(this.captionTokenEntityRepository.findById(a.getId()).isPresent());
    }
}