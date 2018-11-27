package nlp.floschne.thumbnailAnnotator.db.mapper;

import nlp.floschne.thumbnailAnnotator.core.domain.DomainObject;
import nlp.floschne.thumbnailAnnotator.db.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;

@ContextConfiguration(classes = MapperTestBase.SpringTestConfig.class)
@RunWith(SpringRunner.class)
public abstract class MapperTestBase<E extends Entity, D extends DomainObject> {


    @Configuration
    @ComponentScan(basePackageClasses = MapperTestBase.class, basePackages = "nlp.floschne.thumbnailAnnotator.db.mapper")
    public static class SpringTestConfig {
    }

    @Autowired
    protected ThumbnailMapper thumbnailMapper;

    @Autowired
    protected CaptionTokenMapper captionTokenMapper;

    public enum MapperType {
        THUMBNAIL_URL, CAPTION_TOKEN;
    }

    private IMapper<E, D> mapper;

    private MapperType type;

    public MapperTestBase(@NotNull MapperType type) {
        this.type = type;
    }

    @Before
    public void setMapper() {
        switch (type) {
            case THUMBNAIL_URL:
                this.mapper = (IMapper<E, D>) thumbnailMapper;
                break;
            case CAPTION_TOKEN:
                this.mapper = (IMapper<E, D>) captionTokenMapper;
                break;
        }
    }

    protected abstract E createDummyEntity();

    protected abstract D createDummyDomainObject();

    protected abstract void assertEqual(E entity, D domain);

    protected void assertListEqual(List<E> entities, List<D> domains) {
        assertEquals(entities.size(), domains.size());
        for (int i = 0; i < entities.size(); i++)
            this.assertEqual(entities.get(i), domains.get(i));
    }


    @Test
    public void mapToEntity() {
        D domain = createDummyDomainObject();
        E entity = mapper.mapToEntity(domain);
        assertEqual(entity, domain);
        assertNull(entity.getId());
    }

    @Test
    public void mapFromEntity() {
        E entity = createDummyEntity();
        D domain = mapper.mapFromEntity(entity);
        assertEqual(entity, domain);
    }

    @Test
    public void mapToEntityList() {
        List<D> domains = new ArrayList<>();
        for (int i = 0; i < 10; i++)
            domains.add(createDummyDomainObject());

        List<E> entities = mapper.mapToEntityList(domains);

        assertListEqual(entities, domains);
    }

    @Test
    public void mapFromEntityList() {
        List<E> entities = new ArrayList<>();
        for (int i = 0; i < 10; i++)
            entities.add(createDummyEntity());

        List<D> domains = mapper.mapFromEntityList(entities);
        assertListEqual(entities, domains);
    }

}
