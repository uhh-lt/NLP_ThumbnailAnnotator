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

    private class ShutterstockCredentials {
        private final UsernamePasswordCredentials credentials;
        private final AtomicInteger usageCounter;
        private final AtomicBoolean watchRuns;

        private final Timer resetTimer;
        private final StopWatch hourWatch;

        private final Object monitor = new Object();

        ShutterstockCredentials(String consumerKey, String consumerSecret) {
            this.credentials = new UsernamePasswordCredentials(consumerKey, consumerSecret);

            this.usageCounter = new AtomicInteger(0);
            this.watchRuns = new AtomicBoolean(false);

            this.resetTimer = new Timer(true);
            this.hourWatch = new StopWatch();
        }

        public UsernamePasswordCredentials getCredentials() {
            synchronized (this.monitor) {

                // start the reset timer if it hasn't started yet
                if (this.watchRuns.compareAndSet(false, true)) {
                    this.hourWatch.start();
                    // reset usage counter after 1 hour every hour
                    this.resetTimer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            usageCounter.set(0);
                            hourWatch.reset();
                            hourWatch.start();
                        }
                    }, MS_IN_HOUR, MS_IN_HOUR);
                }

                // return null if the number of request exceeds the rate limit
                if (this.usageCounter.get() >= HOURLY_RATE_LIMIT)
                    return null;
                else {
                    this.usageCounter.incrementAndGet();
                    return this.credentials;
                }
            }
        }

        public Long getMilliSecondsUntilReset() {
            return MS_IN_HOUR - this.hourWatch.getTime();
        }
    }

    private static ShutterstockRequestManager instance;

    public static ShutterstockRequestManager getInstance() {
        if (instance == null)
            instance = new ShutterstockRequestManager();
        return instance;
    }


    private static final Integer HOURLY_RATE_LIMIT = 60 * 1000;
    private static final Long MS_IN_HOUR = (long) 1000 * 60 * 60;

    private List<ShutterstockCredentials> credentialsList;

    private AtomicInteger currentCredentialsIndex;

    private static final Boolean USE_KEYS_EQUALLY = true;
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
                this.credentialsList.add(new ShutterstockCredentials(((JsonObject) key).get("key").getAsString(), ((JsonObject) key).get("secret").getAsString()));

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

    private synchronized UsernamePasswordCredentials getCredentials() throws ConnectException {
        long shortestWaitingTime = Long.MAX_VALUE;
        if (USE_KEYS_EQUALLY) {
            int i = 0;
            while (i < this.credentialsList.size()) { // try it for at max. the number of available keys
                ShutterstockCredentials screds = this.credentialsList.get(this.getAndIncrementCurrentCredentialsIndex());
                UsernamePasswordCredentials creds = screds.getCredentials();
                if (creds != null)
                    return creds;
                else {
                    shortestWaitingTime = Math.min(screds.getMilliSecondsUntilReset(), shortestWaitingTime);
                }
            }
        } else {
            for (ShutterstockCredentials screds : this.credentialsList) {
                UsernamePasswordCredentials creds = screds.getCredentials();
                if (creds != null)
                    return creds;
                else
                    shortestWaitingTime = Math.min(screds.getMilliSecondsUntilReset(), shortestWaitingTime);
            }
        }
        throw new ConnectException("Rate Limit for all API keys exceeded! Seconds until reset: " + shortestWaitingTime);
    }

    public synchronized JsonObject makeGetRequest(String resourceUrl) throws IOException {
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
