package nlp.floschne.thumbnailAnnotator.core.thumbnailCrawler.source;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nlp.floschne.thumbnailAnnotator.core.domain.Thumbnail;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private static final String CATEGORIES_FILE = "shutterstock_categories.json";

    private static final List<Integer> DISRUPTIVE_CATEGORY_IDS = Collections.unmodifiableList(Arrays.asList(
            3, 8, 12, 19, 20, 21, 22, 23, 24, 25, 26
    ));

    private static final List<Integer> INFORMATIVE_CATEGORY_IDS = Collections.unmodifiableList(Arrays.asList(
            1, 2, 4, 5, 6, 7, 9, 10, 11, 13, 14, 15, 16, 17, 18, 27
    ));

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

    private Map<Integer, String> shutterstockCategories;
    private Map<String, Integer> shutterstockCategoriesReverse;

    public ShutterstockSource() {
        this.sortBy = SortBy.RANDOM;
        this.per_page = 40;

        // get shutterstock categories from json file
        shutterstockCategories = new HashMap<>();
        shutterstockCategoriesReverse = new HashMap<>();
        InputStream keyInputStream = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResourceAsStream(CATEGORIES_FILE));
        InputStreamReader inputStreamReader = new InputStreamReader(keyInputStream, StandardCharsets.UTF_8);
        JsonParser parser = new JsonParser();
        JsonArray root = parser.parse(inputStreamReader).getAsJsonArray();
        for (JsonElement cat : root) {
            Integer id = cat.getAsJsonObject().get("id").getAsInt();
            String name = cat.getAsJsonObject().get("name").getAsString();
            shutterstockCategories.put(id, name);
            shutterstockCategoriesReverse.put(name, id);
        }

        DISRUPTIVE_CATEGORY_IDS.forEach(id -> shutterstockCategories.remove(id));
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
            if (result.size() == limit)
                break;
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

                // TODO sure get(0)?!
                Thumbnail.Category category = this.extractCategoriesFromJsonResponse(obj.getAsJsonObject()).get(0);

                List<String> keywords = this.extractKeywordsFromJsonResponse(obj.getAsJsonObject());

                result.add(new Thumbnail(url, desc, id, category, keywords));
            }
        }
        return result;
    }


    private Thumbnail.Category extractCategoryFromJsonResponse(JsonObject response, String categoryId) {
        for (JsonElement obj : response.getAsJsonArray("categories")) {
            if (obj != null) {
                // remove quotes
                String idStr = Objects.requireNonNull(getElementByPath(obj.getAsJsonObject(), "id")).toString();
                if (!idStr.equalsIgnoreCase(categoryId))
                    continue;

                int catId = Integer.parseInt(idStr.substring(1, idStr.length() - 1));

                String name = Objects.requireNonNull(getElementByPath(obj.getAsJsonObject(), "name")).toString();
                // remove quotes
                name = name.substring(1, name.length() - 1);

                return new Thumbnail.Category(catId, name);
            }
        }
        // TODO can we actually reach this code??
        return null;
    }

    @Deprecated
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

    @Deprecated
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
    public List<Thumbnail> queryThumbnails(String queryParameter, Integer limitPerCategory) throws IOException {
        List<Thumbnail> thumbnails = new ArrayList<>();
        shutterstockCategories.entrySet().parallelStream().forEach(entry -> {
            try {
                String cat = entry.getValue();
                List<Thumbnail> thumbs = this.queryThumbnails(queryParameter, limitPerCategory, cat);
                thumbs.removeIf(thumbnail -> !thumbnail.getCategory().getName().equals(cat));
                thumbnails.addAll(thumbs);
            } catch (IOException e) {
                // TODO
                e.printStackTrace();
            }
        });

        return thumbnails;
    }

    @Override
    public List<Thumbnail> queryThumbnails(String queryParameter, Integer limitPerCategory, String category) throws IOException {
        String searchImagesApiUrl = generateSearchImagesApiUrl(queryParameter, category);

        JsonObject searchImagesResponse = ShutterstockRequestManager.getInstance().makeGetRequest(searchImagesApiUrl);
        if (searchImagesResponse == null)
            throw new ConnectException("Got no response from Thumbnail Source!");

        return createThumbnailsFromJsonResponse(searchImagesResponse, limitPerCategory);
    }
}
