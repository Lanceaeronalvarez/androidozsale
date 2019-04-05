package au.com.dealsdirect.service.datacollection.registerservices;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.HashMap;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.service.datacollection.core.DataCollectionService;
import au.com.dealsdirect.service.datacollection.core.DataCollector;
import au.com.dealsdirect.service.datacollection.core.LoggingService;
import au.com.dealsdirect.service.datacollection.enums.Events;
import au.com.dealsdirect.service.datacollection.events.AddToCartJourneyViewCartEvent;
import au.com.dealsdirect.service.datacollection.events.AddToCartJourneyViewProductCategoryEvent;
import au.com.dealsdirect.service.datacollection.events.AddedToCartEvent;
import au.com.dealsdirect.service.datacollection.events.CCScanEvent;
import au.com.dealsdirect.service.datacollection.events.CVAppLaunchEvent;
import au.com.dealsdirect.service.datacollection.events.CheckOutJourneyEvent;
import au.com.dealsdirect.service.datacollection.events.ClickEvent;
import au.com.dealsdirect.service.datacollection.events.InitiateCheckOutEvent;
import au.com.dealsdirect.service.datacollection.events.ItemDetailsDataEvent;
import au.com.dealsdirect.service.datacollection.events.ItemListDataEvent;
import au.com.dealsdirect.service.datacollection.events.LoginDataEvent;
import au.com.dealsdirect.service.datacollection.events.PurchaseDataEvent;
import au.com.dealsdirect.service.datacollection.events.RegistrationDataEvent;
import au.com.dealsdirect.service.datacollection.events.SaleBannersDataEvent;
import au.com.dealsdirect.service.datacollection.events.ShareDataEvent;
import au.com.dealsdirect.service.datacollection.events.TrackOrderDataEvent;
import au.com.dealsdirect.service.event.FirebaseEventServiceInterface;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

import static au.com.dealsdirect.service.datacollection.core.DataCollector.EventParameters.ClickType.PHONE;
import static au.com.dealsdirect.service.datacollection.core.DataCollector.EventParameters.ClickType.TABLET;
import static au.com.dealsdirect.service.datacollection.core.DataCollector.EventParameters.CustomAttributeTypes.TABLET_TYPE;
import static au.com.dealsdirect.service.datacollection.core.DataCollector.EventParameters.CustomAttributeTypes.PHONE_TYPE;


/**
 * Created by MTC on 3/8/19.
 */

public class FirebaseAnalyticsService implements FirebaseEventServiceInterface, DataCollectionService {

    private static FirebaseAnalytics firebaseAnalytics;
    private static Bundle bundle;
    private static DataManager mDataManager;
    private static SchedulerProvider mSchedulerProvider;
    private static CompositeDisposable mCompositeDisposable;
    static String type;
    static String finalType;

    private static FirebaseAnalyticsService instance;
    public static FirebaseAnalyticsService getInstance() {
        if (instance == null) {
            instance = new FirebaseAnalyticsService();
        }
        return instance;
    }

    public static String getServiceKey() {
        return "firebase";
    }

    @Inject
    public FirebaseAnalyticsService(DataManager dataManager,
                             SchedulerProvider schedulerProvider,
                             CompositeDisposable compositeDisposable) {
        mDataManager = dataManager;
        mSchedulerProvider = schedulerProvider;
        mCompositeDisposable = compositeDisposable;
    }

    private FirebaseAnalyticsService (){
        registerFirebaseEvents();
    }

    public static void registerFirebaseEvents() {
        bundle = new Bundle();

        //register open app
        DataCollector.EventRegistry.register(generateEventKey(Events.CVAppLaunch, getServiceKey()), Events.CVAppLaunch,
                new LoggingService.LogCVAppLaunch(new CVAppLaunchEvent() {
                    @Override
                    public void LogDataEvents(HashMap<String, Object> parameters) {
                        bundle.putDouble(DataCollector.EventParameters.MILLISECONDS,
                                (Double) parameters.get(DataCollector.EventParameters.MILLISECONDS));
                        appOpen((Context) parameters.get(DataCollector.EventParameters.APP_CONTEXT),bundle);
                    }
                }));

        //register start checkout
        DataCollector.EventRegistry.register(generateEventKey(Events.StartCheckout, getServiceKey()), Events.StartCheckout,
                new LoggingService.LogInitiateCheckout(new InitiateCheckOutEvent() {
                    @Override
                    public void LogDataEvents(HashMap<String, Object> parameters) {
                        startCheckout((Context) parameters.get(DataCollector.EventParameters.APP_CONTEXT), bundle,
                                String.valueOf(parameters.get(DataCollector.EventParameters.SCREEN_NAME)));
                    }
                }));

        //register share
        DataCollector.EventRegistry.register(generateEventKey(Events.Share, getServiceKey()), Events.Share,
                new LoggingService.LogShare(new ShareDataEvent() {
                    @Override
                    public void LogDataEvents(HashMap<String, Object> parameters) {
                        bundle.putString(FirebaseAnalytics.Param.SOURCE,
                                String.valueOf(parameters.get(DataCollector.EventParameters.SOURCE)));
                        bundle.putString(FirebaseAnalytics.Param.MEDIUM,
                                String.valueOf(parameters.get(DataCollector.EventParameters.METHOD)));
                        shareEvent((Context) parameters.get(DataCollector.EventParameters.APP_CONTEXT), bundle,
                                String.valueOf(parameters.get(DataCollector.EventParameters.SCREEN_NAME)));
                    }
                }));

        //register item list
        DataCollector.EventRegistry.register(generateEventKey(Events.CVItemList, getServiceKey()), Events.CVItemList,
                new LoggingService.LogItemList(new ItemListDataEvent() {
                    @Override
                    public void LogDataEvents(HashMap<String, Object> parameters) {
                        bundle.putString(FirebaseAnalytics.Param.ITEM_LIST,
                                String.valueOf(parameters.get(DataCollector.EventParameters.ITEM_LIST)));
                        itemList((Context) parameters.get(DataCollector.EventParameters.APP_CONTEXT),
                                bundle,
                                String.valueOf(parameters.get(DataCollector.EventParameters.SCREEN_NAME)));
                    }
                }));

        //register item details
        DataCollector.EventRegistry.register(generateEventKey(Events.CVItemDetails, getServiceKey()), Events.CVItemDetails,
                new LoggingService.LogItemDetails(new ItemDetailsDataEvent() {
                    @Override
                    public void LogDataEvents(HashMap<String, Object> parameters) {
                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID,
                                String.valueOf(parameters.get(DataCollector.EventParameters.ITEM_ID)));
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME,
                                String.valueOf(parameters.get(DataCollector.EventParameters.ITEM_NAME)));
                        bundle.putString(FirebaseAnalytics.Param.ITEM_BRAND,
                                String.valueOf(parameters.get(DataCollector.EventParameters.ITEM_BRAND)));
                        itemDetails((Context) parameters.get(DataCollector.EventParameters.APP_CONTEXT), bundle,
                                String.valueOf(parameters.get(DataCollector.EventParameters.SCREEN_NAME)));
                    }
                }));

        // register sign up
        DataCollector.EventRegistry.register(generateEventKey(Events.SignUp, getServiceKey()), Events.SignUp,
                new LoggingService.LogRegistration(new RegistrationDataEvent() {
                    @Override
                    public void LogDataEvents(HashMap<String, Object> parameters) {
                        bundle.putString(FirebaseAnalytics.Param.METHOD,
                                String.valueOf(parameters.get(DataCollector.EventParameters.METHOD)));
                        signUp((Context) parameters.get(DataCollector.EventParameters.APP_CONTEXT), bundle,
                                String.valueOf(parameters.get(DataCollector.EventParameters.SCREEN_NAME)));
                    }
                }));

        //register login
        DataCollector.EventRegistry.register(generateEventKey(Events.Login, getServiceKey()), Events.Login,
                new LoggingService.LogLogin(new LoginDataEvent() {
                    @Override
                    public void LogDataEvents(HashMap<String, Object> parameters){

                        boolean result = (Boolean) parameters.get(DataCollector.EventParameters.RESULT);
                        bundle.putString(result ? DataCollector.EventParameters.LoginType.SUCCESSFUL_LOGIN :
                                        DataCollector.EventParameters.LoginType.GAVE_UP_LOGIN,
                                            String.valueOf(parameters.get(DataCollector.EventParameters.METHOD)));
                        login((Context) parameters.get(DataCollector.EventParameters.APP_CONTEXT), bundle,
                                String.valueOf(parameters.get(DataCollector.EventParameters.SCREEN_NAME)));
                    }
                }));

        //register add to cart
        DataCollector.EventRegistry.register(generateEventKey(Events.AddedToCartEvent, getServiceKey()), Events.AddedToCartEvent,
                new LoggingService.LogAddedToCart(new AddedToCartEvent() {
                    @Override
                    public void LogDataEvents(HashMap<String, Object> parameters){
                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID,
                                String.valueOf(parameters.get(DataCollector.EventParameters.ITEM_ID)));
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME,
                                String.valueOf(parameters.get(DataCollector.EventParameters.ITEM_NAME)));
                        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY,
                                String.valueOf(parameters.get(DataCollector.EventParameters.ITEM_CATEGORY)));
                        addToCart((Context) parameters.get(DataCollector.EventParameters.APP_CONTEXT), bundle,
                                String.valueOf(parameters.get(DataCollector.EventParameters.SCREEN_NAME)));
                    }
                }));

        //register ccscan
        DataCollector.EventRegistry.register(generateEventKey(Events.CCScan, getServiceKey()), Events.CCScan,
                new LoggingService.LogCCScan(new CCScanEvent() {
                    @Override
                    public void LogDataEvents(HashMap<String, Object> parameters) {
                        DataCollector.EventParameters.EventProgress eventProgress =
                                (DataCollector.EventParameters.EventProgress) parameters.get(DataCollector.EventParameters.EVENT_PROGRESS);
                        bundle.putString(DataCollector.EventParameters.CustomAttributeTypes.TYPE.getValue(), String.valueOf(eventProgress.getValue()));
                        ccScan((Context) parameters.get(DataCollector.EventParameters.APP_CONTEXT), bundle,
                                String.valueOf(parameters.get(DataCollector.EventParameters.SCREEN_NAME)));
                    }
                }));

        //register checkout journey event
        DataCollector.EventRegistry.register(generateEventKey(Events.checkoutJourney, getServiceKey()), Events.checkoutJourney,
                new LoggingService.LogCheckoutJourney(new CheckOutJourneyEvent() {
                    @Override
                    public void LogDataEvents(HashMap<String, Object> parameters) {

                        checkoutJourney((Context) parameters.get(DataCollector.EventParameters.APP_CONTEXT),
                                String.valueOf(parameters.get(DataCollector.EventParameters.TYPE)),
                                String.valueOf(parameters.get(DataCollector.EventParameters.SCREEN_NAME)));

                    }
                }));

        //register purchase
        DataCollector.EventRegistry.register(generateEventKey(Events.PurchaseEvent, getServiceKey()), Events.PurchaseEvent,
                new LoggingService.LogPurchase(new PurchaseDataEvent() {
                    @Override
                    public void LogDataEvents(HashMap<String, Object> parameters) {

                        bundle.putString(DataCollector.EventParameters.CustomAttributeTypes.PAYMENT_OPTION.getValue(),
                                String.valueOf(parameters.get(DataCollector.EventParameters.PAYMENT_METHOD_TYPE)));
                        bundle.putString(DataCollector.EventParameters.CustomAttributeTypes.NEW_USER.getValue(),
                                String.valueOf(parameters.get(DataCollector.EventParameters.IS_NEW_USER)));
                        purchase((Context) parameters.get(DataCollector.EventParameters.APP_CONTEXT), bundle,
                                String.valueOf(parameters.get(DataCollector.EventParameters.SCREEN_NAME)));

                    }
                }));

        //register add to cart journey view cart
        DataCollector.EventRegistry.register(generateEventKey(Events.addToCartJourneyViewCart, getServiceKey()), Events.addToCartJourneyViewCart,
                new LoggingService.LogAddToCartJourneyViewCart(new AddToCartJourneyViewCartEvent() {
                    @Override
                    public void LogDataEvents(HashMap<String, Object> parameters) {
                        addToCartJourneyViewCart((Context) parameters.get(DataCollector.EventParameters.APP_CONTEXT));
                    }
                }));

        //register add to cart journey view product
        DataCollector.EventRegistry.register(generateEventKey(Events.addToCartJourneyViewProductCategory, getServiceKey()),
                Events.addToCartJourneyViewProductCategory,
                new LoggingService.LogAddToCartJourneyViewProductCategory(new AddToCartJourneyViewProductCategoryEvent(){
                    @Override
                    public void LogDataEvents(HashMap<String, Object> parameters) {
                        addToCartJourneyViewProductCategory((Context) parameters.get(DataCollector.EventParameters.APP_CONTEXT));
                    }
                }));

        //register sale banner event
        DataCollector.EventRegistry.register(generateEventKey(Events.CVSaleBanners, getServiceKey()), Events.CVSaleBanners,
                new LoggingService.LogSaleBanners(new SaleBannersDataEvent() {
                    @Override
                    public void LogDataEvents(HashMap<String, Object> parameters) {
                        bundle.putDouble(DataCollector.EventParameters.MILLISECONDS,
                                (Double) parameters.get(DataCollector.EventParameters.MILLISECONDS));
                        saleBanners((Context) parameters.get(DataCollector.EventParameters.APP_CONTEXT), bundle,
                                String.valueOf(parameters.get(DataCollector.EventParameters.SCREEN_NAME)));
                    }
                }));

        //register order track
        DataCollector.EventRegistry.register(generateEventKey(Events.CVOrderTrack, getServiceKey()), Events.CVOrderTrack,
                new LoggingService.LogTrackOrder(new TrackOrderDataEvent() {
                    @Override
                    public void LogDataEvents(HashMap<String, Object> parameters) {
                        String source = String.valueOf(parameters.get(DataCollector.EventParameters.SOURCE));
                        bundle.putString(DataCollector.EventParameters.CustomAttributeTypes.SOURCE.getValue(), source);
                        orderTrack((Context) parameters.get(DataCollector.EventParameters.APP_CONTEXT), bundle,
                                String.valueOf(parameters.get(DataCollector.EventParameters.SCREEN_NAME)),
                                source);
                    }
                }));

        //register click event
        DataCollector.EventRegistry.register(generateEventKey(Events.clicksEvent, getServiceKey()), Events.clicksEvent,
                new LoggingService.LogClickEvent(new ClickEvent() {
                    @Override
                    public void LogDataEvents(HashMap<String, Object> parameters) {
                        String type = String.valueOf(parameters.get(DataCollector.EventParameters.TYPE));
                        int position = (Integer) parameters.get(DataCollector.EventParameters.ITEM_ARRAY_POSITION);
                        bundle.putInt((mDataManager.isTablet() ? TABLET : PHONE) + type, position);
                        clicksEvent((Context) parameters.get(DataCollector.EventParameters.APP_CONTEXT), bundle,
                                String.valueOf(parameters.get(DataCollector.EventParameters.SCREEN_NAME)));
                    }
                }));

    }

    private static String generateEventKey(Events events, String service) {
        return events+"."+service;
    }

    private static void appOpen(Context context, Bundle bundle) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle);
    }

    private static void startCheckout(Context context, Bundle bundle, String screenName) {
        if (!mDataManager.hasActiveCheckoutSession()) {
            firebaseAnalytics = FirebaseAnalytics.getInstance(context);
            firebaseAnalytics.setCurrentScreen((Activity) context, screenName, screenName);
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.BEGIN_CHECKOUT,bundle);
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.ECOMMERCE_PURCHASE, bundle);

            checkoutJourney(context, DataCollector.EventParameters.EventProgress.START.getValue(), screenName);

        }
    }

    private static void shareEvent(Context context, Bundle bundle, String screenName) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        firebaseAnalytics.setCurrentScreen((Activity) context, screenName, screenName);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, bundle);
    }

    private static void itemList(Context context, Bundle bundle, String screenName) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        firebaseAnalytics.setCurrentScreen((Activity) context, screenName, screenName);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM_LIST, bundle);
    }

    private static void itemDetails(Context context, Bundle bundle, String screenName) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        firebaseAnalytics.setCurrentScreen((Activity) context, screenName, screenName);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);
    }

    private static void signUp(Context context, Bundle bundle, String screenName) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        firebaseAnalytics.setCurrentScreen((Activity) context, screenName, screenName);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle);
    }

    private static void login(Context context, Bundle bundle, String screenName) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        firebaseAnalytics.setCurrentScreen((Activity) context, screenName, screenName);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle);
    }

    private static void addToCart(Context context, Bundle bundle, String screenName) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        firebaseAnalytics.setCurrentScreen((Activity) context, screenName, screenName);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.ADD_TO_CART, bundle);

        if (!mDataManager.hasAddedToCart()) {
            addToCartJourney(context, DataCollector.EventParameters.CartJourneyType.ADD_TO_CART);
            mDataManager.setHasAddedToCart(true);
        }
    }

    private static void search(Context context, Bundle bundle, String screenName) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        firebaseAnalytics.setCurrentScreen((Activity) context, screenName, screenName);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH, bundle);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_SEARCH_RESULTS, bundle);
    }

    private static void checkoutJourney(Context context, String type, String screenName) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        firebaseAnalytics.setCurrentScreen((Activity) context, screenName, screenName);

        mDataManager.setHasActiveCheckoutSession(type.equals(DataCollector.EventParameters.EventProgress.START.getValue()));

        finalType = type.equals(DataCollector.EventParameters.EventProgress.END.getValue()) ?
                DataCollector.EventParameters.EventProgress.END.getValue().concat(mDataManager.getLastRedirection()) : type;

        bundle.putString(DataCollector.EventParameters.CustomAttributeTypes.TYPE.getValue(), finalType);
        firebaseAnalytics.logEvent(DataCollector.EventParameters.CustomEventType.CHECKOUT_JOURNEY.getValue(),bundle);
    }

    private static void ccScan(Context context, Bundle bundle, String screenName) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        firebaseAnalytics.setCurrentScreen((Activity) context, screenName, screenName);
        firebaseAnalytics.logEvent(DataCollector.EventParameters.CustomEventType.CC_SCAN.getValue(), bundle);
    }

    private static void purchase(Context context, Bundle bundle, String screenName) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        firebaseAnalytics.setCurrentScreen((Activity) context, screenName, screenName);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.ECOMMERCE_PURCHASE, bundle);

        mDataManager.setLastRedirection(DataCollector.EventParameters.LastRedirection.PAY);
        checkoutJourney(context, DataCollector.EventParameters.EventProgress.END.getValue(), screenName);
    }

    private static void addToCartJourneyViewCart(Context context) {
        if (!mDataManager.hasViewedCart()) {
            addToCartJourney(context, DataCollector.EventParameters.CartJourneyType.VIEW_CART);
            mDataManager.setHasViewedCart(true);
        }
    }

    public static void addToCartJourney(Context context, String type) {
        bundle.putString(mDataManager.isTablet() ? TABLET_TYPE.getValue() :
                PHONE_TYPE.getValue(), type);

        firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        firebaseAnalytics.logEvent(DataCollector.EventParameters.CustomEventType.ADDTOCART_JOURNEY.getValue(), bundle);
    }

    private static void addToCartJourneyViewProductCategory(Context context) {
        if (!mDataManager.hasViewedProductCategory()) {
            addToCartJourney(context, DataCollector.EventParameters.CartJourneyType.VIEW_PRODUCT_CATEGORY);
            mDataManager.setHasViewedProductCategory(true);
        }
    }

    private static void saleBanners(Context context, Bundle bundle, String screenName) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        firebaseAnalytics.setCurrentScreen((Activity) context, screenName, screenName);
        firebaseAnalytics.logEvent(DataCollector.EventParameters.CustomEventType.CV_SALEBANNERS.getValue(), bundle);
    }

    private static void orderTrack(Context context, Bundle bundle, String screenName,
                                   String source) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        firebaseAnalytics.setCurrentScreen((Activity) context, screenName, screenName);
        firebaseAnalytics.logEvent(DataCollector.EventParameters.CustomEventType.CV_ORDERTRACK.getValue(), bundle);

        clickOrderEvent(context, source);
    }

    private static void clickOrderEvent(Context context, String source) {
        bundle.putString(DataCollector.EventParameters.ClickType.ORDER_TRACK, source);

        firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        firebaseAnalytics.logEvent(DataCollector.EventParameters.CustomEventType.CLICKS.getValue(), bundle);
    }

    private static void clicksEvent(Context context, Bundle bundle, String screenName) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        firebaseAnalytics.setCurrentScreen((Activity) context, screenName, screenName);
        firebaseAnalytics.logEvent(DataCollector.EventParameters.CustomEventType.CLICKS.getValue(), bundle);
    }

    @Override
    public boolean hasEvent(String eventKey) {
        return DataCollector.EventRegistry.hasEvent(eventKey+"."+getServiceKey());
    }

    @Override
    public void logEvent(String eventKey, HashMap<String, Object> parameters) {
        DataCollector.EventRegistry.logData(eventKey+"."+getServiceKey(),parameters);
    }
}
