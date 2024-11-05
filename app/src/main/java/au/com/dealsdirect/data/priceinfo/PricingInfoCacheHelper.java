package au.com.dealsdirect.data.priceinfo;

public interface PricingInfoCacheHelper {
    void cacheRrpText(String id, String rrpText);

    String getCachedRrpText(String id);

    void cachePricingText(String id, String pricingText);

    String getCachedPricingText(String id);

    void cacheTotalPercentOff(String id, Double totalPercentOff);

    Double getCachedTotalPercentOff(String id);

    void cacheOriginalPrice(String id, Double originalPrice);

    Double getCachedOriginalPrice(String id);

}
