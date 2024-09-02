package au.com.dealsdirect.data.priceinfo;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

public class AppPricingInfoCacheHelper implements PricingInfoCacheHelper {

    private final Map<String, String> rrpTextCache = new HashMap<>();
    private final Map<String, String> pricingTextCache = new HashMap<>();
    private final Map<String, Double> totalPercentOffCache = new HashMap<>();
    private final Map<String, Double> originalPriceCache = new HashMap<>();

    @Inject
    public AppPricingInfoCacheHelper() {
    }

    @Override
    public void cacheRrpText(String id, String rrpText) {
        rrpTextCache.put(id, rrpText);
    }

    @Override
    public String getCachedRrpText(String id) {
        return rrpTextCache.get(id);
    }

    @Override
    public void cachePricingText(String id, String pricingText) {
        pricingTextCache.put(id, pricingText);
    }

    @Override
    public String getCachedPricingText(String id) {
        return pricingTextCache.get(id);
    }

    @Override
    public void cacheTotalPercentOff(String id, Double totalPercentOff) {
        totalPercentOffCache.put(id, totalPercentOff);
    }

    @Override
    public Double getCachedTotalPercentOff(String id) {
        return totalPercentOffCache.get(id);
    }

    @Override
    public void cacheOriginalPrice(String id, Double originalPrice) {
        originalPriceCache.put(id, originalPrice);
    }

    @Override
    public Double getCachedOriginalPrice(String id) {
        return originalPriceCache.get(id);
    }
}
