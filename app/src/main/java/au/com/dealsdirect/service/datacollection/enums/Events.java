package au.com.dealsdirect.service.datacollection.enums;


/**
 * Created by MTC on 2/19/19.
 */

public enum Events {

    PurchaseEvent("PurchaseEvent"),
    AddedToCartEvent("AddedToCartEvent"),
    CompleteRegistration("CompleteRegistration"),
    ViewedContent("ViewedContent"),
    AddPaymentInfo("AddPaymentInfo"),
    InitiateCheckout("InitiateCheckout"),
    CCScan("CCScan"),
    CVAppLaunch("CVAppLaunch"),
    CVSaleBanners("CVSaleBanners"),
    CVItemList("CVItemList"),
    CVItemDetails("CVItemDetails"),
    CVOrderTrack("CVOrderTrack"),
    SignUp("SignUp"),
    Login("Login"),
    Share("Share"),
    addToCartJourneyViewCart("addToCartJourneyViewCart"),
    addToCartJourneyViewProductCategory("addToCartJourneyViewProductCategory"),
    addToCartJourney("addToCartJourney"),
    checkoutJourney("checkoutJourney"),
    clicksOrdersEvent("clicksOrdersEvent"),
    clicksEvent("clicksEvent"),
    SearchEvent("searchEvent"),
    ProductViewEvent("ProductViewEvent"),
    EventUser("EventUser"),
    SaleEvent("SaleEvent"),
    RemoveFromCart("RemoveFromCart"),
    FailedTransaction("FailedTransaction"),
    ToggleColumn("ToggleColumn"),
    ProductListGridViewPreference("ProductListGridViewPreference"),
    WishlistEvent("WishlistEvent"),
    WishlistAddToCartEvent("WishlistAddToCartEvent"),
    WishlistPaymentSuccessEvent("WishlistPaymentSuccessEvent"),
    YouMayAlsoLikeEvent("YouMayAlsoLikeEvent"),
    RecentlyViewed("RecentlyViewedClickEvent"),
    RecommendationClickEvent("RecommendationClickEvent"),
    BannerClickEvent("BannerClickEvent"),
    SponsoredBannerClickEvent("SponsoredBannerClickEvent"),
    RegularBannerClickEvent("RegularBannerClickEvent"),
    FeatureUsageEvent("FeatureUsageEvent");

    private String value;

    Events(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

}