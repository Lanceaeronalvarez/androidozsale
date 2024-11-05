package au.com.dealsdirect.data.priceinfo;

import androidx.annotation.NonNull;

import au.com.dealsdirect.data.network.model.saleitemdetails.SaleItemDetails;
import au.com.dealsdirect.data.network.model.saleitems.SaleItemProduct;

public class PricingInfoLoaderHelper {
    @NonNull
    private final PricingInfoCacheHelper pricingInfoCacheHelper;
    @NonNull
    private final SaleItemProductLoader loader;

    public PricingInfoLoaderHelper(@NonNull SaleItemProductLoader loader,
                                   @NonNull PricingInfoCacheHelper pricingInfoCacheHelper) {
        this.loader = loader;
        this.pricingInfoCacheHelper = pricingInfoCacheHelper;
    }

    public void cacheItem(SaleItemDetails item, String saleId) {
        final String seoIdentifier = item.getSeoIdentifier();
        final String id = getCacheKey(seoIdentifier, saleId);
        pricingInfoCacheHelper.cacheRrpText(id, item.getRrpText());
        pricingInfoCacheHelper.cachePricingText(id, item.getPricing());
        pricingInfoCacheHelper.cacheTotalPercentOff(id, item.getTotalPercentOff());
        pricingInfoCacheHelper.cacheOriginalPrice(
                id,
                item.getOriginalPrice() != null ? item.getOriginalPrice().getValue() : null);
    }

    public void getPricingInfo(String seoIdentifier,
                               String saleId,
                               PricingInfoReceiver receiver) {
        if (seoIdentifier == null || receiver == null) {
            return;
        }

        final String id = getCacheKey(seoIdentifier, saleId);

        final String rrpText = pricingInfoCacheHelper.getCachedRrpText(id);

        if (rrpText == null) {
            loader.load(seoIdentifier, saleId, loadedItem -> {
                cacheItem(loadedItem, saleId);
                receiver.receive(
                        loadedItem.getRrpText(),
                        loadedItem.getTotalPercentOff(),
                        loadedItem.getOriginalPrice() != null ? loadedItem.getOriginalPrice().getValue() : null,
                        getPricingInfoText(loadedItem, saleId));
            });
            return;
        }

        final String pricingText = pricingInfoCacheHelper.getCachedPricingText(id);
        final Double totalPercentOff = pricingInfoCacheHelper.getCachedTotalPercentOff(id);
        final Double originalPrice = pricingInfoCacheHelper.getCachedOriginalPrice(id);
        receiver.receive(
                rrpText,
                totalPercentOff,
                originalPrice,
                getPricingInfoText(rrpText, pricingText));
    }

    private String getPricingInfoText(SaleItemProduct item, String saleId) {
        final String seoIdentifier = ((SaleItemDetails) item).getSeoIdentifier();
        final String id = getCacheKey(seoIdentifier, saleId);
        final String rrpText = pricingInfoCacheHelper.getCachedRrpText(id);
        final String pricingText = pricingInfoCacheHelper.getCachedPricingText(id);
        return getPricingInfoText(rrpText, pricingText);
    }

    private String getPricingInfoText(String rrpText, String pricingText) {
        String pricingInfoText = rrpText;
        if (pricingInfoText != null && !pricingInfoText.isEmpty()) {
            pricingInfoText += "<br/><br/>";
        }
        if (pricingInfoText == null) {
            pricingInfoText = pricingText;
        } else {
            pricingInfoText += pricingText;
        }
        return pricingInfoText;
    }

    private String getCacheKey(String seoIdentifier, String saleId) {
        return seoIdentifier + "::" + (saleId != null ? saleId : "NOSALEID");
    }

    public interface PricingInfoReceiver {
        void receive(String rrpText, Double totalPercentOff, Double originalPrice, String combinedPricingInfoText);
    }

    public interface SaleItemProductLoader {
        void load(String seoIdentifier, String saleId, SaleItemProductReceiver receiver);
    }

    public interface SaleItemProductReceiver {
        void receive(SaleItemDetails item);
    }
}
