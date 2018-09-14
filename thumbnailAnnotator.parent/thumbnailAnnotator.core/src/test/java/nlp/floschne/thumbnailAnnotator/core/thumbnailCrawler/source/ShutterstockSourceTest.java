package nlp.floschne.thumbnailAnnotator.core.thumbnailCrawler.source;

import nlp.floschne.thumbnailAnnotator.core.domain.Thumbnail;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class ShutterstockSourceTest {

    @Test
    public void basicQueryTest() throws IOException {
        String query = "water";
        IThumbnailSource shutterstockSource = new ShutterstockSource();
        List<Thumbnail> response = shutterstockSource.queryThumbnails(query, 20);
        assertNotNull(response);
        assertFalse(response.isEmpty());

        System.out.println("Query: '" + query + "'");
        System.out.println("Response:");

        for (Thumbnail url : response)
            System.out.println(url.getUrl());

    }


    @Test
    public void multiWordQueryTest() throws IOException {
        String query = "red water";
        IThumbnailSource shutterstockSource = new ShutterstockSource();
        List<Thumbnail> response = shutterstockSource.queryThumbnails(query, 20);
        assertNotNull(response);
        assertFalse(response.isEmpty());

        System.out.println("Query: '" + query + "'");
        System.out.println("Response:");

        for (Thumbnail url : response)
            System.out.println(url.getUrl());

    }
}
