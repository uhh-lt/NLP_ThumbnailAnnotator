package nlp.floschne.thumbnailAnnotator.core.thumbnailCrawler.source;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nlp.floschne.thumbnailAnnotator.core.domain.Thumbnail;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
public class ShutterstockSource implements IThumbnailSource {

    public enum SortBy {
        RELEVANCE("relevance"),
        RANDOM("random"),
        NEWEST("newest"),
        POPULAR("popular");

        private final String value;

        SortBy(String s) {
            this.value = s;
        }

        @Override
        public String toString() {
            return this.value;
        }
    }

    private static final String IMAGE_SEARCH_RESOURCE_URL = "https://api.shutterstock.com/v2/images/search";
    private static final String IMAGE_DETAILS_RESOURCE_URL = "https://api.shutterstock.com/v2/images/";
    private static final String IMAGE_TYPES = "&image_type[]=photo&image_type[]=illustration&image_type[]=vector";
    private static final String IMAGE_LICENCES = "&license[]=commercial&license[]=editorial&license[]=enhanced&license[]=sensitive";
    private static final String IMAGE_QUERY_PARAMETER = "?query=";
    private static final String SORT_PARAMETER = "&sort=";
    private static final String PER_PAGE_PARAMETER = "&per_page=";

    private static final String OPERATOR_AND = "AND";
    private static final String OPERATOR_OR = "OR";
    private static final String OPERATOR_NOT = "NOT";

    private SortBy sortBy;
    private Integer per_page;

    public ShutterstockSource() {
        this.sortBy = SortBy.RELEVANCE;
        this.per_page = 20;
    }

    private String generateSearchImagesApiUrl(String queryParameter) throws UnsupportedEncodingException {
        return IMAGE_SEARCH_RESOURCE_URL +
                IMAGE_TYPES +
                IMAGE_LICENCES +
                IMAGE_QUERY_PARAMETER + URLEncoder.encode(queryParameter, "UTF-8") +
                SORT_PARAMETER + sortBy.toString() +
                PER_PAGE_PARAMETER + per_page.toString();
    }


    private String generateImageDetailsApiUrl(Long imageId) throws UnsupportedEncodingException {
        return IMAGE_DETAILS_RESOURCE_URL + URLEncoder.encode(imageId.toString(), "UTF-8");
    }

    private JsonElement getElementByPath(JsonObject obj, String path) {
        String[] seg = path.split("\\.");
        for (String element : seg) {
            if (obj != null) {
                JsonElement ele = obj.get(element);
                if (!ele.isJsonObject())
                    return ele;
                else
                    obj = ele.getAsJsonObject();
            } else {
                return null;
            }
        }
        return obj;
    }

    private List<Thumbnail> createThumbnailsFromJsonResponse(@NotNull JsonObject response, Integer limit) {
        List<Thumbnail> result = new ArrayList<>();
        for (JsonElement obj : response.getAsJsonArray("data")) {
            if (obj != null) {

                String idStr = Objects.requireNonNull(getElementByPath(obj.getAsJsonObject(), "id")).toString();
                // remove quotes
                Long id = Long.parseLong(idStr.substring(1, idStr.length() - 1));

                String url = Objects.requireNonNull(getElementByPath(obj.getAsJsonObject(), "assets.large_thumb.url")).toString();
                // remove quotes
                url = url.substring(1, url.length() - 1);

                String desc = Objects.requireNonNull(getElementByPath(obj.getAsJsonObject(), "description")).toString();
                // remove quotes
                desc = desc.substring(1, desc.length() - 1);


                result.add(new Thumbnail(url, 0, desc, id, null, null));
                if (result.size() == limit)
                    break;
            }
        }
        return result;
    }

    private List<Thumbnail.Category> extractCategoriesFromJsonResponse(JsonObject response) {
        List<Thumbnail.Category> categories = new ArrayList<>();
        for (JsonElement obj : response.getAsJsonArray("categories")) {
            if (obj != null) {
                // remove quotes
                String idStr = Objects.requireNonNull(getElementByPath(obj.getAsJsonObject(), "id")).toString();
                int catId = Integer.parseInt(idStr.substring(1, idStr.length() - 1));

                String name = Objects.requireNonNull(getElementByPath(obj.getAsJsonObject(), "name")).toString();
                // remove quotes
                name = name.substring(1, name.length() - 1);

                categories.add(new Thumbnail.Category(catId, name));
            }
        }
        return categories;
    }

    private List<String> extractKeywordsFromJsonResponse(JsonObject response) {
        List<String> keywords = new ArrayList<>();
        for (JsonElement obj : response.getAsJsonArray("keywords")) {
            if (obj != null) {
                String keyString = obj.toString();
                keywords.add(keyString.substring(1, keyString.length() - 1));
            }
        }
        return keywords;
    }

    private void setThumbnailDetails(Thumbnail t) throws IOException {
        String imageDetailsApiUrl = generateImageDetailsApiUrl(t.getShutterstockId());
        JsonObject imageDetailsResponse = ShutterstockRequestManager.getInstance().makeGetRequest(imageDetailsApiUrl);

        List<Thumbnail.Category> categories = extractCategoriesFromJsonResponse(imageDetailsResponse);
        List<String> keywords = extractKeywordsFromJsonResponse(imageDetailsResponse);

        t.setCategories(categories);
        t.setKeywords(keywords);
    }

    @Override
    public List<Thumbnail> queryThumbnails(String queryParameter, Integer limit) throws IOException {
        String searchImagesApiUrl = generateSearchImagesApiUrl(queryParameter);

        JsonObject searchImagesResponse = ShutterstockRequestManager.getInstance().makeGetRequest(searchImagesApiUrl);
        if (searchImagesResponse == null)
            throw new ConnectException("Got no response from Thumbnail Source!");

        List<Thumbnail> thumbnails = createThumbnailsFromJsonResponse(searchImagesResponse, limit);

        for (Thumbnail t : thumbnails)
            setThumbnailDetails(t);

        return thumbnails;
    }

}
