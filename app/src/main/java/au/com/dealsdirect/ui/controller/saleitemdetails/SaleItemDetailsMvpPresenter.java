package au.com.dealsdirect.ui.controller.saleitemdetails;

import au.com.dealsdirect.data.network.model.ourpaydata.OurpayDataResponse;
import au.com.dealsdirect.data.network.model.saleitemdetails.AddToCartRequest;
import au.com.dealsdirect.data.network.model.saleitemdetails.SaleItemDetails;
import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * Created by smartwave on 08/06/2017.
 */

public interface SaleItemDetailsMvpPresenter<V extends MvpView> extends MvpPresenter<V> {

    //    void loadProductDetails(GetPublicItemDetailsRequest publicItemDetailsRequest, GetPublicSaleDetailsRequest publicSaleDetailsRequest);
    void loadProductDetails(String saleId, String seoIdentifierId);

    void loadOurpayData(SaleItemDetails value);

    void loadAfterpayData(Double price);

    void loadPromoInfo(String skuId);

    void addToCart(AddToCartRequest requestValues);

    boolean isAuthorized();

    void generateOurpay(SaleItemDetails value, OurpayDataResponse ourpayDataResponse);

    void callGetBasketItemsQuantity();

    String getPersonalisationErrorText();

    void getDynamicDiscount(String skuId);

    String getAfterpayLightboxImgUrl();

    String getAfterpayTermsLink();

    int wishlistCount();

    boolean isProductInWishlist(String productId);

    void addProductToWishlist(String productId, String seoIdentifier, String masterProductId, WishlistDelayedCallback delayedCallback);

    void removeProductFromWishlist(String productId, WishlistDelayedCallback delayedCallback);

    void loadRecommendedItems();

    void loadRecentlyViewedItems();

    public interface WishlistDelayedCallback {
        void performDelayedAction();
    }

    void loadYouMayAlsoLike(String skuId);

    void addToRecentlyViewedItems(String productId, String masterSkuId);

    void loadDefaultPostcode();

    void setDefaultPostcode(String postcode);

    void loadPreviewShippingPrice(String postcode, String skuid, float price, int weight, int width, int height, Integer operation);

    String getBuyboxTemplateTextTitle();

    String getBuyboxTemplateTextSellerTemplate();

    String getBuyboxTemplateTextButtonText();

    void loadLeaderboardBanner();

    void getPricingInfoText(String seoIdentifier, String saleId);
}
