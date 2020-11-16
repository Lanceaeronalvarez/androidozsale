package au.com.dealsdirect.data.cachedresponses;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import javax.inject.Inject;

public class AppCachedResponseHelper implements CachedResponseHelper {
    // Max age, which is 7 days, in milliseconds;
    private static final long CACHE_MAX_AGE = (long) (6.048 * Math.pow(10, 8));

    private CachedResponsesManager cachedResponsesManager;

    @Inject
    public AppCachedResponseHelper() {
        cachedResponsesManager = new CachedResponsesManager();
    }

    @Override
    public <T extends CachableRequest, V extends CachableResponse> void setCachedResponse(T request, V response) {
        cachedResponsesManager.put(
                request.getCacheKey(),
                new CachedResponse(response));
    }

    @Override
    public <T extends CachableRequest, V extends CachableResponse> V getCachedResponse(T request, Class<V> responseClass) {
        return cachedResponsesManager.getResponse(request.getCacheKey(), responseClass);
    }

    @Override
    public void pruneCachedResponses() {
        cachedResponsesManager.pruneExpiredResponses();
    }

    @Override
    public <T extends CachableRequest> void pruneCachedResponse(T keyObject) {
        cachedResponsesManager.pruneExpiredResponse(keyObject.getCacheKey());
    }

    @Override
    public void storeCache() {
        // TODO: implement a better persistent cache system

    }

    @Override
    public void fetchCache() {
        // TODO: implement a better persistent cache system
    }

    private static class CachedResponsesManager {
        @SerializedName("cachedResponsesManager")
        @Expose
        private HashMap<String, CachedResponse> cachedResponses = new HashMap<>();

        private <T extends CachableResponse> T getResponse(String key, Class<T> type) {
            CachedResponse cachedResponse = cachedResponses.get(key);
            if (cachedResponse != null) {
                Object response = cachedResponse.getResponse();
                if (response.getClass().isAssignableFrom(type)) {
                    return type.cast(response);
                }
            }
            return null;
        }

        private void put(String key, CachedResponse response) {
            cachedResponses.put(key, response);
        }

        private void pruneExpiredResponses() {
            long timeNow = (new Date()).getTime();
            HashSet<String> keySet = new HashSet<>(cachedResponses.keySet());
            for (String key : keySet) {
                pruneExpiredResponse(key, timeNow);
            }
        }

        private void pruneExpiredResponse(String key) {
            pruneExpiredResponse(key, (new Date()).getTime());
        }


        private void pruneExpiredResponse(String key, long timeNow) {
            CachedResponse cachedResponse = cachedResponses.get(key);
            if (cachedResponse != null &&
                    cachedResponse.getTimestamp() + CACHE_MAX_AGE < timeNow) {
                cachedResponses.remove(key);
            }
        }
    }

    private static class CachedResponse implements Serializable {
        @SerializedName("response")
        @Expose
        private CachableResponse response;
        @SerializedName("timestamp")
        @Expose
        private long timestamp;

        <T extends CachableResponse> CachedResponse(T response) {
            this.response = response;
            timestamp = (new Date()).getTime();
        }

        public CachableResponse getResponse() {
            return response;
        }

        public long getTimestamp() {
            return timestamp;
        }

        @NonNull
        @Override
        public String toString() {
            Gson gson = new Gson();
            return gson.toJson(this);
        }
    }
}