package nlp.floschne.thumbnailAnnotator.core.thumbnailCrawler.source;

import nlp.floschne.thumbnailAnnotator.core.domain.Thumbnail;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class ShutterstockSourceTest {

    // TODO testing for all fields of a Thumbnail!

    @Test
    public void basicQueryTest() {
        String query = "water";
        IThumbnailSource shutterstockSource = new ShutterstockSource();
        List<Thumbnail> response = null;
        try {
            response = shutterstockSource.queryThumbnails(query, 20);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(response);
        assertFalse(response.isEmpty());

        System.out.println("Query: '" + query + "'");
        System.out.println("Response:");

        for (Thumbnail thumbnail : response)
            System.out.println(thumbnail.toString());

    }


    @Test
    public void multiWordQueryTest() {
        String query = "red water";
        IThumbnailSource shutterstockSource = new ShutterstockSource();
        List<Thumbnail> response = null;
        try {
            response = shutterstockSource.queryThumbnails(query, 20);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(response);
        assertFalse(response.isEmpty());

        System.out.println("Query: '" + query + "'");
        System.out.println("Response:");

        for (Thumbnail thumbnail : response)
            System.out.println(thumbnail);

    }
}
