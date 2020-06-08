package au.com.dealsdirect.data.cachedresponses;

public interface CachedResponseHelper {
    <T extends CachableRequest, V extends CachableResponse> void setCachedResponse(T request, V response);

    <T extends CachableRequest, V extends CachableResponse> V getCachedResponse(T request, Class<V> responseClass);

    void pruneCachedResponses();

    <T extends CachableRequest> void pruneCachedResponse(T request);

    void storeCache();

    void fetchCache();
}
