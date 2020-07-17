package au.com.dealsdirect.data.cachedresponses;

import androidx.annotation.NonNull;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mysale.genie.utility.Prefs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.inject.Inject;

public class AppCachedResponseHelper implements CachedResponseHelper {
    private static final String CACHED_RESPONSES = "CACHED_RESPONSES";

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
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(CachableResponse.class, new CachableResponseSerializer())
                .create();
        String json = gson.toJson(cachedResponsesManager);
        byte[] compressed = compress(json);
        String string = new String(compressed, Charsets.ISO_8859_1);
        Prefs.putString(CACHED_RESPONSES, string);
    }

    @Override
    public void fetchCache() {
        String source = Prefs.getString(CACHED_RESPONSES, "");
        if (!source.isEmpty()) {
            Gson gson = new GsonBuilder()
                    .serializeNulls()
                    .registerTypeAdapter(CachableResponse.class, new CachableResponseSerializer())
                    .create();
            cachedResponsesManager = gson.fromJson(
                    decompress(source.getBytes(Charsets.ISO_8859_1)),
                    CachedResponsesManager.class);
            if (cachedResponsesManager == null) {
                cachedResponsesManager = new CachedResponsesManager();
            }
        }
    }

    private static String decompress(byte[] compressed) {
        if (compressed == null || compressed.length == 0) {
            return null;
        }
        try {
            final int BUFFER_SIZE = 32;
            ByteArrayInputStream is = new ByteArrayInputStream(compressed);
            GZIPInputStream gis = new GZIPInputStream(is, BUFFER_SIZE);
            StringBuilder string = new StringBuilder();
            byte[] data = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = gis.read(data)) != -1) {
                string.append(new String(data, 0, bytesRead));
            }
            gis.close();
            is.close();
            return string.toString();
        } catch (IOException ex) {
            return null;
        }
    }

    private static byte[] compress(String json) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream(json.length());
            GZIPOutputStream gzip = new GZIPOutputStream(bos);
            gzip.write(json.getBytes());
            gzip.close();
            byte[] compressed = bos.toByteArray();
            bos.close();
            return compressed;
        } catch (IOException exception) {
            return new byte[]{};
        }
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

    private static class CachableResponseSerializer implements JsonSerializer<CachableResponse>, JsonDeserializer<CachableResponse> {
        private static final String CLASS_META_KEY = "clz";

        @Override
        public CachableResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Class<?> clz;
            CachableResponse cachableResponse;
            JsonObject object = json.getAsJsonObject();
            if (object.has(CLASS_META_KEY)) {
                String className = object.get(CLASS_META_KEY).getAsString();
                try {
                    clz = Class.forName(className);
                } catch (Exception e) {
                    clz = CachableResponse.class;
                }
                cachableResponse = context.deserialize(json, clz);
            } else {
                cachableResponse = context.deserialize(json, typeOfT);
            }
            return cachableResponse;
        }

        @Override
        public JsonElement serialize(CachableResponse src, Type typeOfSrc, JsonSerializationContext context) {
            JsonElement element = null;
            if (src == null) {
                return element;
            }
            try {
                String className = src.getClass().getName();
                element = context.serialize(src, Class.forName(className));
            } catch (Exception e) {
                throw new IllegalArgumentException("Unspecified class serializer for " + src.getClass().getName());
            }
            element.getAsJsonObject().addProperty(CLASS_META_KEY, src.getClass().getCanonicalName());
            return element;
        }
    }
}