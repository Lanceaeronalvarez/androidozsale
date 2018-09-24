package au.com.dealsdirect.service.event;

/**
 * Created by smartwave on 31/07/2018.
 */

public interface ActionTrackerInterface {

    void startCheckoutEvent();

    void addToCartEvent(String source, int attempts);

    void CCScan(ActionTracker.EventProgress eventProgress);

    void CVAppLaunch(double milliseconds);

    void CVSaleBanners(double milliseconds);

    void CVItemList(double milliseconds);

    void CVItemDetails(double milliseconds);

    void CVOrderTrack(String source);

    void purchase(String paymentOption, boolean isNewUser, boolean result);

    void signUp(String method, boolean result);

    void login(String method, boolean result);

    void share(String method, String source);

    void addToCartJourneyViewCart();

    void addToCartJourneyViewProductCategory();

    void addToCartJourney(String type);

    void checkoutJourney(String type);

    void clicksOrdersEvent(String source);

    void clicksEvent(String type, int itemArrPos);
}
