package au.com.dealsdirect.service.event;

import au.com.dealsdirect.service.datacollection.core.DataCollector;
import au.com.dealsdirect.service.datacollection.registerservices.ActionTracker;

/**
 * Created by smartwave on 31/07/2018.
 */

public interface ActionTrackerInterface {

    static void startCheckoutEvent(){};

    static void addToCartEvent(String source, int attempts){};

    static void CCScan(DataCollector.EventParameters.EventProgress eventProgress){};

    static void CVAppLaunch(double milliseconds){};

    static void CVSaleBanners(double milliseconds){};

    static void CVItemList(double milliseconds){};

    static void CVItemDetails(double milliseconds){};

    static void CVOrderTrack(String source){};

    static void purchase(String paymentOption, boolean isNewUser, boolean result){};

    static void signUp(String method, boolean result){};

    static void login(String method, boolean result){};

    static void share(String method, String source){};

    static void addToCartJourneyViewCart(){};

    static void addToCartJourneyViewProductCategory(){};

    static void addToCartJourney(String type){};

    static void checkoutJourney(String type){};

    static void clicksOrdersEvent(String source){};

    static void clicksEvent(String type, int itemArrPos){};
}
