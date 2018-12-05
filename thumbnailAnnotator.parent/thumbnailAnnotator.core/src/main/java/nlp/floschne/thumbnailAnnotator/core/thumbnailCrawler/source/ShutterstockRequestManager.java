package nlp.floschne.thumbnailAnnotator.core.thumbnailCrawler.source;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
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
import java.net.ConnectException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ShutterstockRequestManager {


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

    /**
     * KEY and SECRET from https://developers.shutterstock.com/user/me/apps
     */
    private static final String CONSUMER_KEY_1 = "a23c2-918bb-11c18-e9fa2-268dd-35f02";
    private static final String CONSUMER_SECRET_1 = "a4b74-ccd2c-a0fb2-c37ad-bface-75a3e";

    private static final String CONSUMER_KEY_2 = "98987-31f4d-0dc30-9f060-bcf36-bf729";
    private static final String CONSUMER_SECRET_2 = "ca74b-3edd8-a5e18-69871-c013d-89d37";

    private static final String CONSUMER_KEY_3 = "a2e0a-6e12d-a6f15-b6ffa-f1a00-8046a";
    private static final String CONSUMER_SECRET_3 = "8e9de-4ffd6-7a35f-6def9-ddc9f-22368";

    private static final String CONSUMER_KEY_4 = "524be-6f277-f96e0-1d3c0-1c604-dccbc";
    private static final String CONSUMER_SECRET_4 = "f3d20-44518-8b363-f5718-c4a43-ad002";

    private static final String CONSUMER_KEY_5 = "6276e-1d14b-28fa1-762b7-e3720-85d6e";
    private static final String CONSUMER_SECRET_5 = "383b2-cfe7a-03951-7ff92-91606-4f18b";

    private static final String CONSUMER_KEY_6 = "171f4-83ac3-d70d1-29424-9ac9e-344ac";
    private static final String CONSUMER_SECRET_6 = "460b0-e8d62-859bc-d4710-b6c29-43ae6";

    private static final String CONSUMER_KEY_7 = "d399a-ba0c6-0664c-a4aec-c203c-57c1b";
    private static final String CONSUMER_SECRET_7 = "f62e0-9bad8-cfe6f-bdb18-7fb7c-9ed84";

    private static final String CONSUMER_KEY_8 = "f491e-32251-27406-65be0-0b305-4b512";
    private static final String CONSUMER_SECRET_8 = "9312d-ff3e1-9bc00-f4c34-108dd-8eece";

    private static final String CONSUMER_KEY_9 = "3dd52-db536-4df8a-4ba65-a95d0-5a5cd";
    private static final String CONSUMER_SECRET_9 = "cacaf-7b347-97974-dedf9-2f4ad-99f86";

    private static final String CONSUMER_KEY_10 = "0fc88-b865c-ad0fe-81eff-12763-9bdc4";
    private static final String CONSUMER_SECRET_10 = "92d94-a150c-6db0f-49a74-819c4-557fc";

    private static final Integer HOURLY_RATE_LIMIT = 250;
    private static final Long MS_IN_HOUR = (long) 1000 * 60 * 60;

    private List<ShutterstockCredentials> credentialsList;


    private ShutterstockRequestManager() {
        this.credentialsList = new ArrayList<>();
        this.credentialsList = Collections.synchronizedList(this.credentialsList);

        this.credentialsList.add(new ShutterstockCredentials(CONSUMER_KEY_1, CONSUMER_SECRET_1));
        this.credentialsList.add(new ShutterstockCredentials(CONSUMER_KEY_2, CONSUMER_SECRET_2));
        this.credentialsList.add(new ShutterstockCredentials(CONSUMER_KEY_3, CONSUMER_SECRET_3));
        this.credentialsList.add(new ShutterstockCredentials(CONSUMER_KEY_4, CONSUMER_SECRET_4));
        this.credentialsList.add(new ShutterstockCredentials(CONSUMER_KEY_5, CONSUMER_SECRET_5));
        this.credentialsList.add(new ShutterstockCredentials(CONSUMER_KEY_6, CONSUMER_SECRET_6));
        this.credentialsList.add(new ShutterstockCredentials(CONSUMER_KEY_7, CONSUMER_SECRET_7));
        this.credentialsList.add(new ShutterstockCredentials(CONSUMER_KEY_8, CONSUMER_SECRET_8));
        this.credentialsList.add(new ShutterstockCredentials(CONSUMER_KEY_9, CONSUMER_SECRET_9));
        this.credentialsList.add(new ShutterstockCredentials(CONSUMER_KEY_10, CONSUMER_SECRET_10));

        Collections.shuffle(this.credentialsList);
    }

    private synchronized UsernamePasswordCredentials getCredentials() throws ConnectException {
        long shortestWaitingTime = Long.MAX_VALUE;
        for (ShutterstockCredentials screds : this.credentialsList) {
            UsernamePasswordCredentials creds = screds.getCredentials();
            if (creds != null)
                return creds;
            else
                shortestWaitingTime = Math.min(screds.getMilliSecondsUntilReset(), shortestWaitingTime);
        }
        throw new ConnectException("Rate Limit for all API keys exceeded! Seconds until reset: " + shortestWaitingTime);
    }

    public synchronized JsonObject makeGetRequest(String resourceUrl) throws IOException {
        // Set host
        HttpHost target = new HttpHost("api.shutterstock.com", 443, "https");

        // Set credentials (by getting the current active credentials)
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        UsernamePasswordCredentials creds = this.getCredentials();
        System.out.println("Using: " + creds);
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
