package nlp.floschne.thumbnailAnnotator.core.thumbnailCrawler.source;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * KEY and SECRET from https://developers.shutterstock.com/user/me/apps
     */
    private static final String CONSUMER_KEY = "a23c2-918bb-11c18-e9fa2-268dd-35f02";
    private static final String CONSUMER_SECRET = "a4b74-ccd2c-a0fb2-c37ad-bface-75a3e";

    private static final String IMAGE_SEARCH_RESOURCE_URL = "https://api.shutterstock.com/v2/images/search";
    private static final String QUERY_PARAMETER = "?query=";
    private static final String SORT_PARAMETER = "&sort=";
    private static final String PER_PAGE_PARAMETER = "&per_page=";

    private static final String OPERATOR_AND = "AND";
    private static final String OPERATOR_OR = "OR";
    private static final String OPERATOR_NOT = "NOT";

    private SortBy sortBy;
    private Integer per_page;

    public ShutterstockSource() {
        this.sortBy = SortBy.RELEVANCE;
        this.per_page = 5;
    }

    private String generateApiCall(String queryParameter) throws UnsupportedEncodingException {
        return IMAGE_SEARCH_RESOURCE_URL +
                QUERY_PARAMETER + URLEncoder.encode(queryParameter, "UTF-8") +
                SORT_PARAMETER + sortBy.toString() +
                PER_PAGE_PARAMETER + per_page.toString();
    }

    private List<String> extractURLsFromJsonResponse(JsonObject response, Integer limit) {
        List<String> result = new ArrayList<>();
        for (JsonElement obj : response.getAsJsonArray("data")) {
            if (obj != null) {
                String url = getElementByPath(obj.getAsJsonObject(), "assets.huge_thumb.url").toString();
                // remove "
                url = url.substring(1, url.length() - 1);
                result.add(url);
                if (result.size() == limit)
                    break;
            }
        }
        return result;
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


    @Override
    public List<String> queryThumbnailURLs(String queryParameter, Integer limit) throws IOException {
        String apiCall = generateApiCall(queryParameter);

        // Set host & credentials
        HttpHost target = new HttpHost("api.shutterstock.com", 443, "https");
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(target.getHostName(), target.getPort()),
                new UsernamePasswordCredentials(CONSUMER_KEY, CONSUMER_SECRET));

        // Create model and get and then execute
        try (CloseableHttpClient httpclient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build()) {
            // Create AuthCache instance
            AuthCache authCache = new BasicAuthCache();

            // Generate BASIC scheme object and add it to the local auth cache
            BasicScheme basicAuth = new BasicScheme();
            authCache.put(target, basicAuth);

            // Add AuthCache to the execution context
            HttpClientContext localContext = HttpClientContext.create();
            localContext.setAuthCache(authCache);

            HttpGet httpget = new HttpGet(apiCall);
            try (CloseableHttpResponse response = httpclient.execute(target, httpget, localContext)) {
                JsonObject jsonResponse = new GsonBuilder().create().fromJson(EntityUtils.toString(response.getEntity()), JsonObject.class);
                return extractURLsFromJsonResponse(jsonResponse, limit);
            }
        }
    }
}
