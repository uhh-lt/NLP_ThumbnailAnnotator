package nlp.floschne.thumbnailAnnotator.core.thumbnailCrawler.source;

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
        List<String> response = shutterstockSource.queryThumbnailURLs(query, 20);
        assertNotNull(response);
        assertFalse(response.isEmpty());

        System.out.println("Query: '" + query + "'");
        System.out.println("Response:");

        for (String url : response)
            System.out.println(url);

    }


    @Test
    public void multiWordQueryTest() throws IOException {
        String query = "red water";
        IThumbnailSource shutterstockSource = new ShutterstockSource();
        List<String> response = shutterstockSource.queryThumbnailURLs(query, 20);
        assertNotNull(response);
        assertFalse(response.isEmpty());

        System.out.println("Query: '" + query + "'");
        System.out.println("Response:");

        for (String url : response)
            System.out.println(url);

    }
}
