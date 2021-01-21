package au.com.dealsdirect.service.datacollection.registerservices;
/*
 * Created by CodeineBot on 8/9/17.
 */

import android.os.Build;
import android.util.Log;

import com.androidnetworking.error.ANError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.inject.Inject;

import au.com.dealsdirect.BuildConfig;
import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.events.BannerClickEventRequest;
import au.com.dealsdirect.data.network.model.events.CategoryRequest;
import au.com.dealsdirect.data.network.model.events.DeliveryPriceViewEventRequest;
import au.com.dealsdirect.data.network.model.events.FeatureUsageEventRequest;
import au.com.dealsdirect.data.network.model.events.FrontEndInfo;
import au.com.dealsdirect.data.network.model.events.ProductViewRequest;
import au.com.dealsdirect.data.network.model.events.RecentlyViewedEventRequest;
import au.com.dealsdirect.data.network.model.events.RecommendationEventRequest;
import au.com.dealsdirect.data.network.model.events.SaleEventRequest;
import au.com.dealsdirect.data.network.model.events.SearchEventRequest;
import au.com.dealsdirect.data.network.model.events.CommonCheckoutRequest;
import au.com.dealsdirect.data.network.model.events.VisitorInfo;
import au.com.dealsdirect.data.network.model.events.WishlistEventRequest;
import au.com.dealsdirect.data.network.model.events.YouMayAlsoLikeEventRequest;
import au.com.dealsdirect.service.datacollection.core.DataCollectionService;
import au.com.dealsdirect.service.datacollection.core.DataCollector;
import au.com.dealsdirect.service.datacollection.core.LoggingService;
import au.com.dealsdirect.service.datacollection.enums.Events;
import au.com.dealsdirect.service.datacollection.events.BannerClickEvent;
import au.com.dealsdirect.service.datacollection.events.DeliveryPriceViewEvent;
import au.com.dealsdirect.service.datacollection.events.EventUser;
import au.com.dealsdirect.service.datacollection.events.FeatureUsageEvent;
import au.com.dealsdirect.service.datacollection.events.InitiateCheckOutEvent;
import au.com.dealsdirect.service.datacollection.events.ItemDetailsDataEvent;
import au.com.dealsdirect.service.datacollection.events.ItemListDataEvent;
import au.com.dealsdirect.service.datacollection.events.RecentlyViewedDataEvent;
import au.com.dealsdirect.service.datacollection.events.RecommendationDataEvent;
import au.com.dealsdirect.service.datacollection.events.SaleBannersDataEvent;
import au.com.dealsdirect.service.datacollection.events.SearchDataEvent;
import au.com.dealsdirect.service.datacollection.events.WishlistDataEvent;
import au.com.dealsdirect.service.datacollection.events.YouMayAlsoLikeClickEvent;
import au.com.dealsdirect.service.event.FrontEndType;
import au.com.dealsdirect.service.event.GenieEventServiceInterface;
import au.com.dealsdirect.service.event.RegionType;
import au.com.dealsdirect.utils.CookieUtils;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import okhttp3.Cookie;

public class GenieEventService implements GenieEventServiceInterface, DataCollectionService {

    public static final String TAG = "GenieEventService";
    private static DataManager mDataManager;
    private static SchedulerProvider mSchedulerProvider;
    private static CompositeDisposable mCompositeDisposable;

    private static GenieEventService instance;

    public static GenieEventService getInstance() {
        if (instance == null) {
            instance = new GenieEventService();
        }
        return instance;
    }

    public static String getServiceKey() {
        return "genie";
    }


    @Inject
    public GenieEventService(DataManager dataManager,
                             SchedulerProvider schedulerProvider,
                             CompositeDisposable compositeDisposable) {
        mDataManager = dataManager;
        mSchedulerProvider = schedulerProvider;
        mCompositeDisposable = compositeDisposable;
    }


    private GenieEventService() {
        registerGenieEvents();
    }

    public static void registerGenieEvents() {

        //register event user
        DataCollector.EventRegistry.register(generateEventKey(Events.EventUser, getServiceKey()), Events.EventUser,
                new LoggingService.LogEventUser(new EventUser() {
                    @Override
                    public void LogDataEvents(HashMap<String, Object> parameters) {
                        callEventUser();
                    }
                }));

        //register search event
        DataCollector.EventRegistry.register(generateEventKey(Events.SearchEvent, getServiceKey()), Events.SearchEvent,
                new LoggingService.LogSearchEvent(new SearchDataEvent() {
                    @Override
                    public void LogDataEvents(HashMap<String, Object> parameters) {
                        callSearchEvent((SearchEventRequest) parameters.get(DataCollector.EventParameters.SEARCH_EVENT_REQUEST));
                    }
                }));

        //register product view
        DataCollector.EventRegistry.register(generateEventKey(Events.CVItemDetails, getServiceKey()), Events.CVItemDetails,
                new LoggingService.LogItemDetails(new ItemDetailsDataEvent() {
                    @Override
                    public void LogDataEvents(HashMap<String, Object> parameters) {
                        callProductViewEvent((ProductViewRequest) parameters.get(DataCollector.EventParameters.PRODUCT_VIEW_REQUEST));
                    }
                }));

        //register item list
        DataCollector.EventRegistry.register(generateEventKey(Events.CVItemList, getServiceKey()), Events.CVItemList,
                new LoggingService.LogItemList(new ItemListDataEvent() {
                    @Override
                    public void LogDataEvents(HashMap<String, Object> parameters) {
                        callCategoryEvent((CategoryRequest) parameters.get(DataCollector.EventParameters.CATEGORY_REQUEST));
                    }
                }));

        //register sale event
        DataCollector.EventRegistry.register(generateEventKey(Events.SaleEvent, getServiceKey()), Events.SaleEvent,
                new LoggingService.LogSaleEvent(new SaleBannersDataEvent() {
                    @Override
                    public void LogDataEvents(HashMap<String, Object> parameters) {
                        callSaleEvent((SaleEventRequest) parameters.get(DataCollector.EventParameters.SALE_EVENT_REQUEST));
                    }
                }));

        //register wishlist event
        DataCollector.EventRegistry.register(generateEventKey(Events.WishlistEvent, getServiceKey()), Events.WishlistEvent,
                new LoggingService.LogWishlistDataEvent(new WishlistDataEvent() {
                    @Override
                    public void LogDataEvents(HashMap<String, Object> parameters) {
                        callWishlistEvent((WishlistEventRequest) parameters.get(DataCollector.EventParameters.WISHLIST_EVENT_REQUEST));
                    }
                }));

        //register start checkout event
        DataCollector.EventRegistry.register(generateEventKey(Events.InitiateCheckout, getServiceKey()), Events.InitiateCheckout,
                new LoggingService.LogInitiateCheckout(new InitiateCheckOutEvent() {
                    @Override
                    public void LogDataEvents(HashMap<String, Object> parameters) {
                        callStartCheckoutEvent((CommonCheckoutRequest) parameters.get(DataCollector.EventParameters.START_CHECKOUT_REQUEST));
                    }
                }));

        //register you may also like event
        DataCollector.EventRegistry.register(generateEventKey(Events.YouMayAlsoLikeEvent, getServiceKey()), Events.YouMayAlsoLikeEvent,
                new LoggingService.LogYouMayAlsoLikeEvent(new YouMayAlsoLikeClickEvent() {
                    @Override
                    public void LogDataEvents(HashMap<String, Object> parameters) {
                        callYouMayAlsoLikeEvent((YouMayAlsoLikeEventRequest) parameters.get(DataCollector.EventParameters.YOU_MAY_ALSO_LIKE_REQUEST));
                    }
                }));

        // register banner click event
        DataCollector.EventRegistry.register(generateEventKey(Events.BannerClickEvent, getServiceKey()), Events.BannerClickEvent,
                new LoggingService.LogBannerClickEvent(new BannerClickEvent() {
                    @Override
                    public void LogDataEvents(HashMap<String, Object> parameters) {
                        if (parameters.containsKey(DataCollector.EventParameters.BANNER_CLICK_REQUEST)) {
                            callBannerClickEvent((BannerClickEventRequest) parameters.get(DataCollector.EventParameters.BANNER_CLICK_REQUEST));
                        }
                    }
                }));

        //register recommendation click event
        DataCollector.EventRegistry.register(generateEventKey(Events.RecommendationClickEvent, getServiceKey()), Events.RecommendationClickEvent,
                new LoggingService.LogRecommendationClickEvent(new RecommendationDataEvent() {
                    @Override
                    public void LogDataEvents(HashMap<String, Object> parameters) {
                        callRecommendationEvent((RecommendationEventRequest) parameters.get(DataCollector.EventParameters.RECOMMENDATION_EVENT_REQUEST));
                    }
                }));

        //register recently viewed event
        DataCollector.EventRegistry.register(generateEventKey(Events.RecentlyViewed, getServiceKey()), Events.RecentlyViewed,
                new LoggingService.LogRecentlyViewedEvent(new RecentlyViewedDataEvent() {
                    @Override
                    public void LogDataEvents(HashMap<String, Object> parameters) {
                        callRecentlyViewedEvent((RecentlyViewedEventRequest) parameters.get(DataCollector.EventParameters.RECENTLY_VIEWED_REQUEST));
                    }
                }));

        //register feature usage event
        DataCollector.EventRegistry.register(generateEventKey(Events.FeatureUsageEvent, getServiceKey()), Events.FeatureUsageEvent,
                new LoggingService.LogFeatureUsageEvent(new FeatureUsageEvent() {
                    @Override
                    public void LogDataEvents(HashMap<String, Object> parameters) {
                        callFeatureUsageEvent((FeatureUsageEventRequest) parameters.get(DataCollector.EventParameters.FEATURE_EVENT_REQUEST));
                    }
                }));

        DataCollector.EventRegistry.register(generateEventKey(Events.DeliveryPriceViewEvent, getServiceKey()), Events.DeliveryPriceViewEvent,
                new LoggingService.LogDeliveryPriceViewEvent(new DeliveryPriceViewEvent() {
                    @Override
                    public void LogDataEvents(HashMap<String, Object> parameters) {
                        super.LogDataEvents(parameters);
                        callDeliveryPriceViewEvent((DeliveryPriceViewEventRequest) parameters.get(DataCollector.EventParameters.DELIVERY_PRICE_VIEW_EVENT_REQUEST));
                    }
                }));

        //register common checkout event
        DataCollector.EventRegistry.register(generateEventKey(Events.CommonCheckoutEvent, getServiceKey()), Events.CommonCheckoutEvent,
                new LoggingService.LogInitiateCheckout(new InitiateCheckOutEvent() {
                    @Override
                    public void LogDataEvents(HashMap<String, Object> parameters) {
                        callStartCheckoutEvent((CommonCheckoutRequest) parameters.get(DataCollector.EventParameters.COMMON_CHECKOUT_REQUEST));
                    }
                }));

    }

    private static String generateEventKey(Events events, String service) {
        return events + "." + service;
    }

    private static void callSearchEvent(SearchEventRequest request) {

        request.setFrontEndInfo(includeFrontEndInfo());
        request.setVisitorInfo(includeVisitorInfo());

        getCompositeDisposable().add(getDataManager()
                .callSearchEvent(request)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(response -> {

                    Log.d(TAG, response);

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        // handle load accounts error here
                        if (throwable instanceof ANError) {
                            ANError anError = (ANError) throwable;
                            Log.d("Error", String.valueOf(anError.getErrorBody()));
                        }
                    }
                }));
    }

    private static void callProductViewEvent(ProductViewRequest request) {
        request.setFrontEndInfo(includeFrontEndInfo());
        request.setVisitorInfo(includeVisitorInfo());

        getCompositeDisposable().add(getDataManager()
                .callProductViewEvent(request)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(response -> {

                    Log.d(TAG, response);

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        // handle load accounts error here
                        if (throwable instanceof ANError) {
                            ANError anError = (ANError) throwable;
                            Log.d("Error", String.valueOf(anError.getErrorBody()));
                        }
                    }
                }));
    }

    private static void callCategoryEvent(CategoryRequest request) {
        request.setFrontEndInfo(includeFrontEndInfo());
        request.setVisitorInfo(includeVisitorInfo());

        getCompositeDisposable().add(getDataManager()
                .callCategoryEvent(request)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(response -> {

                    Log.d(TAG, response);

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        // handle load accounts error here
                        if (throwable instanceof ANError) {
                            ANError anError = (ANError) throwable;
                            Log.d("Error", String.valueOf(anError.getErrorBody()));
                        }
                    }
                }));

    }

    private static void callSaleEvent(SaleEventRequest request) {
        request.setFrontEndInfo(includeFrontEndInfo());
        request.setVisitorInfo(includeVisitorInfo());

        getCompositeDisposable().add(getDataManager()
                .callSaleEvent(request)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(response -> {

                    Log.d(TAG, response);

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        // handle load accounts error here
                        if (throwable instanceof ANError) {
                            ANError anError = (ANError) throwable;
                            Log.d("Error", String.valueOf(anError.getErrorBody()));
                        }
                    }
                }));
    }

    private static void callEventUser() {
        getCompositeDisposable().add(getDataManager()
                .callEventUser()
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(responseValue -> {

                    //Save user Id to data manager
                    if (!responseValue.isEmpty()) {
                        getDataManager().setEventUserId(responseValue.replace("\"", ""));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        // handle load accounts error here
                        if (throwable instanceof ANError) {
                            ANError anError = (ANError) throwable;
                            Log.d("Error", String.valueOf(anError.getErrorBody()));
                        }
                    }
                }));
    }

    private static void callWishlistEvent(WishlistEventRequest request) {
        request.setFrontEndInfo(includeFrontEndInfo());
        request.setVisitorInfo(includeVisitorInfo());

        getCompositeDisposable().add(getDataManager()
                .callWishlistEvent(request)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(responseValue -> {

                    //Save user Id to data manager
                    if (!responseValue.isEmpty()) {
                        getDataManager().setEventUserId(responseValue.replace("\"", ""));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        // handle load accounts error here
                        if (throwable instanceof ANError) {
                            ANError anError = (ANError) throwable;
                            Log.d("Error", String.valueOf(anError.getErrorBody()));
                        }
                    }
                }));
    }

    private static void callStartCheckoutEvent(CommonCheckoutRequest request) {
        request.setFrontEndInfo(includeFrontEndInfo());
        request.setVisitorInfo(includeVisitorInfo());

        getCompositeDisposable().add(getDataManager()
                .callStartCheckoutEvent(request)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(response -> {

                    Log.d(TAG, response);

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        // handle load accounts error here
                        if (throwable instanceof ANError) {
                            ANError anError = (ANError) throwable;
                            Log.d("Error", String.valueOf(anError.getErrorBody()));
                        }
                    }
                }));
    }

    private static void callYouMayAlsoLikeEvent(YouMayAlsoLikeEventRequest request) {
        request.setFrontEndInfo(includeFrontEndInfo());
        request.setVisitorInfo(includeVisitorInfo());

        getCompositeDisposable().add(getDataManager()
                .callYouMayAlsoLikeEvent(request)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(response -> {

                    Log.d(TAG, response);

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        // handle load accounts error here
                        if (throwable instanceof ANError) {
                            ANError anError = (ANError) throwable;
                            Log.d("Error", String.valueOf(anError.getErrorBody()));
                        }
                    }
                }));
    }

    private static void callBannerClickEvent(BannerClickEventRequest request) {
        request.setFrontEndInfo(includeFrontEndInfo());
        request.setVisitorInfo(includeVisitorInfo());

        getCompositeDisposable().add(getDataManager()
                .callBannerClickEvent(request)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(response -> {

                    Log.d(TAG, response);

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        // handle load accounts error here
                        if (throwable instanceof ANError) {
                            ANError anError = (ANError) throwable;
                            Log.d("Error", String.valueOf(anError.getErrorBody()));
                        }
                    }
                }));
    }

    private static void callRecommendationEvent(RecommendationEventRequest request) {
        request.setFrontEndInfo(includeFrontEndInfo());
        request.setVisitorInfo(includeVisitorInfo());

        getCompositeDisposable().add(getDataManager()
                .callRecommendationClickEvent(request)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(response -> {

                    Log.d(TAG, response);

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        // handle load accounts error here
                        if (throwable instanceof ANError) {
                            ANError anError = (ANError) throwable;
                            Log.d("Error", String.valueOf(anError.getErrorBody()));
                        }
                    }
                }));
    }

    private static void callRecentlyViewedEvent(RecentlyViewedEventRequest request) {
        request.setFrontEndInfo(includeFrontEndInfo());
        request.setVisitorInfo(includeVisitorInfo());

        getCompositeDisposable().add(getDataManager()
                .callRecentlyViewedEvent(request)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(response -> {

                    Log.d(TAG, response);

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        // handle load accounts error here
                        if (throwable instanceof ANError) {
                            ANError anError = (ANError) throwable;
                            Log.d("Error", String.valueOf(anError.getErrorBody()));
                        }
                    }
                }));
    }

    private static void callFeatureUsageEvent(FeatureUsageEventRequest request) {
        request.setFrontEndInfo(includeFrontEndInfo());
        request.setVisitorInfo(includeVisitorInfo());

        getCompositeDisposable().add(getDataManager()
                .callFeatureUsageEvent(request)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(response -> {

                    Log.d(TAG, response);

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        // handle load accounts error here
                        if (throwable instanceof ANError) {
                            ANError anError = (ANError) throwable;
                            Log.d("Error", String.valueOf(anError.getErrorBody()));
                        }
                    }
                }));
    }

    private static void callDeliveryPriceViewEvent(DeliveryPriceViewEventRequest request) {
        request.setFrontEndInfo(includeFrontEndInfo());
        request.setVisitorInfo(includeVisitorInfo());

        getCompositeDisposable().add(getDataManager()
                .callDeliveryPriceViewEvent(request)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(response -> {

                    Log.d(TAG, response);

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        // handle load accounts error here
                        if (throwable instanceof ANError) {
                            ANError anError = (ANError) throwable;
                            Log.d("Error", String.valueOf(anError.getErrorBody()));
                        }
                    }
                }));
    }

    private static FrontEndInfo includeFrontEndInfo() {
        FrontEndInfo frontEndInfo = new FrontEndInfo();

        if (getDataManager().isTablet()) {
            frontEndInfo.setFrontEnd(FrontEndType.TABLET);
        } else {
            frontEndInfo.setFrontEnd(FrontEndType.PHONE);
        }

        frontEndInfo.setOsVersion(Build.VERSION.RELEASE);
        frontEndInfo.setUiVersion(BuildConfig.VERSION_NAME);

        return frontEndInfo;
    }

    private static VisitorInfo includeVisitorInfo() {

        VisitorInfo visitorInfo = new VisitorInfo();
        visitorInfo.setVisitorId(getVisitorId());
        visitorInfo.getUserCohorts().addAll(getCohorts());
        visitorInfo.setUserGroup(getUserGroup());
        visitorInfo.setCompany(getDataManager().getCountryId());
        visitorInfo.setRegion(RegionType.getType(getDataManager().getCountryId()));
        visitorInfo.setUserId(getDataManager().getEventUserId());

        return visitorInfo;
    }

    private static String getVisitorId() {
        String visitorId = "";
        for (Iterator<Cookie> it = CookieUtils.getInstance().getCookieIterator(); it.hasNext(); ) {
            Cookie cookie = it.next();
            if (cookie.name().equalsIgnoreCase("v")) {
                visitorId = cookie.value();
                break;
            }
        }

        return visitorId;
    }

    private static ArrayList<String> getCohorts() {
        ArrayList<String> cohorts = new ArrayList<>();
        for (Iterator<Cookie> it = CookieUtils.getInstance().getCookieIterator(); it.hasNext(); ) {
            Cookie cookie = it.next();
            if (cookie.name().contains("ut")) {
                String ut[] = cookie.value().split("&");
                for (String anUt : ut) {
                    if (ut.length > 1) {
                        cohorts.add(anUt.split("=")[1]);
                    }
                }
                break;
            }
        }
        return cohorts;
    }

    private static String getUserGroup() {
        String userGroup = "";
        for (Iterator<Cookie> it = CookieUtils.getInstance().getCookieIterator(); it.hasNext(); ) {
            Cookie cookie = it.next();
            if (cookie.name().equalsIgnoreCase("us")) {
                String[] subCookies = cookie.value().split("&");
                if (subCookies.length > 0) {
                    String value;
                    // only take first subcookie
                    String[] subCookieParts = subCookies[0].split("=");
                    if (subCookieParts.length > 1) {
                        // when equal sign exists
                        value = subCookieParts[1];
                    } else if (subCookieParts.length == 1) {
                        // when value is not actually subcookies
                        value = subCookieParts[0];
                    } else {
                        value = "";
                    }

                    if (!value.isEmpty()) {
                        char firstCharacter = value.charAt(0);
                        userGroup = String.valueOf(firstCharacter);
                    }
                }
                break;
            }
        }

        return userGroup;
    }

    public static DataManager getDataManager() {
        return mDataManager;
    }

    public static SchedulerProvider getSchedulerProvider() {
        return mSchedulerProvider;
    }

    public static CompositeDisposable getCompositeDisposable() {
        return mCompositeDisposable;
    }

    @Override
    public boolean hasEvent(String eventKey) {
        return DataCollector.EventRegistry.hasEvent(eventKey + "." + getServiceKey());
    }

    @Override
    public void logEvent(String eventKey, HashMap<String, Object> parameters) {
        DataCollector.EventRegistry.logData(eventKey + "." + getServiceKey(), parameters);
    }
}
