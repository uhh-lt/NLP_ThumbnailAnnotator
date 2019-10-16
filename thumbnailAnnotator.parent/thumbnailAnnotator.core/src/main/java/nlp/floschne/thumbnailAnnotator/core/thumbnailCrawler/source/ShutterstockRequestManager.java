package nlp.floschne.thumbnailAnnotator.core.thumbnailCrawler.source;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class ShutterstockRequestManager {

    /*
    A lot of stuff in this class is now obsolete since we have now a higher rate limit and most probably
    will only need one single API key. This class was designed to rotate multiple keys to cheat
    higher rate limits...
     */

    private static ShutterstockRequestManager instance;

    public static ShutterstockRequestManager getInstance() {
        if (instance == null)
            instance = new ShutterstockRequestManager();
        return instance;
    }

    private List<UsernamePasswordCredentials> credentialsList;

    private AtomicInteger currentCredentialsIndex;

    private static final String KEY_FILE = "shutterstock_api_keys.json";

    private ShutterstockRequestManager() {
        this.credentialsList = new ArrayList<>();
        this.credentialsList = Collections.synchronizedList(this.credentialsList);

        // load the keys from the KEY_FILE
        try {
            InputStream keyInputStream = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResourceAsStream(KEY_FILE));
            InputStreamReader inputStreamReader = new InputStreamReader(keyInputStream, StandardCharsets.UTF_8);
            JsonArray keys = new GsonBuilder().create().fromJson(inputStreamReader, JsonArray.class);
            for (JsonElement key : keys)
                this.credentialsList.add(new UsernamePasswordCredentials(((JsonObject) key).get("key").getAsString(), ((JsonObject) key).get("secret").getAsString()));

            this.currentCredentialsIndex = new AtomicInteger(0);
        } catch (NullPointerException e) {
            log.error("Cannot parse key file '" + KEY_FILE + "' from resources!");
            throw new RuntimeException(e);
        }
    }

    private synchronized Integer getAndIncrementCurrentCredentialsIndex() {
        Integer tmp = this.currentCredentialsIndex.get();
        this.currentCredentialsIndex.set((this.currentCredentialsIndex.get() + 1) % this.credentialsList.size());
        return tmp;
    }

    private UsernamePasswordCredentials getCredentials() throws ConnectException {
        int i = 0;
        while (i < this.credentialsList.size()) { // try it for at max. the number of available keys
            UsernamePasswordCredentials creds = this.credentialsList.get(this.getAndIncrementCurrentCredentialsIndex());
            if (creds != null)
                return creds;
            i++;
        }
        throw new ConnectException("Rate Limit for all API keys exceeded!");
    }

    // package private by intention
    JsonObject makeGetRequest(String resourceUrl) throws IOException {
        // Set host
        HttpHost target = new HttpHost("api.shutterstock.com", 443, "https");

        // Set credentials (by getting the current active credentials)
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        UsernamePasswordCredentials creds = this.getCredentials();

        log.info("Using Shutterstock Credentials: " + creds.getUserName());
        credsProvider.setCredentials(new AuthScope(target.getHostName(), target.getPort()), creds);

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

            // execute the call
            HttpGet httpget = new HttpGet(resourceUrl);
            try (CloseableHttpResponse response = httpclient.execute(target, httpget, localContext)) {
                JsonObject jsonResponse = new GsonBuilder().create().fromJson(EntityUtils.toString(response.getEntity()), JsonObject.class);
                if (jsonResponse == null) {
                    throw new ConnectException("Got no response from Thumbnail Source!");
                } else if (jsonResponse.get("message") != null) {
                    throw new ConnectException("Error returned from Thumbnail Source!:" + jsonResponse.get("message"));
                }
                return jsonResponse;
            } catch (Exception e) {
                throw new ConnectException("There was an error connecting to the Thumbnail Source! " + e.getMessage());
            }
        }
    }
}
