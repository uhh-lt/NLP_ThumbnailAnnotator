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
import java.util.stream.Collectors;

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
    private static final String IMAGE_TYPES = "?image_type[]=photo&image_type[]=illustration&image_type[]=vector";
    private static final String IMAGE_LICENCES = "&license[]=commercial&license[]=editorial&license[]=enhanced&license[]=sensitive";
    private static final String IMAGE_QUERY_PARAMETER = "&query=";
    private static final String SORT_PARAMETER = "&sort=";
    private static final String PAGE_PARAMETER = "&page=1";
    private static final String PER_PAGE_PARAMETER = "&per_page=";
    private static final String CATEGORY = "&category=";
    private static final String VIEW = "&view=full";

    private static final String OPERATOR_AND = "AND";
    private static final String OPERATOR_OR = "OR";
    private static final String OPERATOR_NOT = "NOT";

    private SortBy sortBy;
    private Integer per_page;

    public ShutterstockSource() {
        this.sortBy = SortBy.RANDOM;
        this.per_page = 40;
    }

    private String generateSearchImagesApiUrl(String queryParameter, String category) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder(IMAGE_SEARCH_RESOURCE_URL);
        sb.append(IMAGE_TYPES);
        sb.append(IMAGE_LICENCES);
        sb.append(IMAGE_QUERY_PARAMETER).append(URLEncoder.encode(queryParameter, "UTF-8"));
        if (category != null && !category.isEmpty())
            sb.append(CATEGORY).append(URLEncoder.encode(category, "UTF-8"));
        sb.append(SORT_PARAMETER).append(URLEncoder.encode(sortBy.toString(), "UTF-8"));
        sb.append(PAGE_PARAMETER);
        sb.append(PER_PAGE_PARAMETER).append(URLEncoder.encode(per_page.toString(), "UTF-8"));
        sb.append(VIEW);
        return sb.toString();
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

                List<Thumbnail.Category> categories = this.extractCategoriesFromJsonResponse(obj.getAsJsonObject());

                List<String> keywords = this.extractKeywordsFromJsonResponse(obj.getAsJsonObject());

                result.add(new Thumbnail(url, 0, desc, id, categories, keywords));
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
        categories = removeDisruptiveCategories(categories);
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

    private List<Thumbnail.Category> removeDisruptiveCategories(List<Thumbnail.Category> categories) {
        // see shutterstock API for category names instead of ID's
        return categories.stream().filter(category -> {
            switch (category.getId()) {
                case 3:
                case 8:
                case 12:
                case 19:
                case 20:
                case 21:
                case 22:
                case 23:
                case 24:
                case 25:
                case 26:
                    return false;
                default:
                    return true;
            }
        }).collect(Collectors.toList());
    }

    @Override
    public List<Thumbnail> queryThumbnails(String queryParameter, Integer limit) throws IOException {
        return this.queryThumbnails(queryParameter, limit, null);
    }

    @Override
    public List<Thumbnail> queryThumbnails(String queryParameter, Integer limit, String category) throws IOException {
        String searchImagesApiUrl = generateSearchImagesApiUrl(queryParameter, category);

        JsonObject searchImagesResponse = ShutterstockRequestManager.getInstance().makeGetRequest(searchImagesApiUrl);
        if (searchImagesResponse == null)
            throw new ConnectException("Got no response from Thumbnail Source!");

        List<Thumbnail> thumbnails = createThumbnailsFromJsonResponse(searchImagesResponse, limit);

        // remove thumbnails with no categories
        thumbnails = thumbnails.stream().filter(thumbnail -> !thumbnail.getCategories().isEmpty()).collect(Collectors.toList());
        // TODO search new thumbnails that replace the removed ones

        return thumbnails;
    }
}
