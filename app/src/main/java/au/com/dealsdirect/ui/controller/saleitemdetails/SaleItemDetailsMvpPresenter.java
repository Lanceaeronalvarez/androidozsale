package au.com.dealsdirect.ui.controller.saleitemdetails;

import au.com.dealsdirect.data.network.model.ourpaydata.OurpayDataResponse;
import au.com.dealsdirect.data.network.model.saleitemdetails.AddToCartRequest;
import au.com.dealsdirect.data.network.model.saleitemdetails.GetSaleItemDetailsResponse;
import au.com.dealsdirect.ui.base.BaseActivity;
import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * Created by smartwave on 08/06/2017.
 */

public interface SaleItemDetailsMvpPresenter<V extends MvpView> extends MvpPresenter<V>{

//    void loadProductDetails(GetPublicItemDetailsRequest publicItemDetailsRequest, GetPublicSaleDetailsRequest publicSaleDetailsRequest);
    void loadSaleItemDetails(String saleId, String seoIdentifierId);

    void loadOurpayData(GetSaleItemDetailsResponse value);

    void loadAfterpayData(Double price);

    void loadPromoInfo(String skuId);

    void addToCart(AddToCartRequest requestValues);

    boolean isAuthorized();

    void generateOurpay(GetSaleItemDetailsResponse value, OurpayDataResponse ourpayDataResponse);

    void callGetBasketItemsQuantity();

    String getPersonalisationErrorText();

    void getDynamicDiscount(String skuId);

    String getAfterpayLightboxImgUrl();

    String getAfterpayTermsLink();

    int wishlistCount();

    boolean isProductInWishlist(String productId);

    void addProductToWishlist(String productId, String seoIdentifier, String masterProductId);

    void removeProductFromWishlist(String productId);
}
