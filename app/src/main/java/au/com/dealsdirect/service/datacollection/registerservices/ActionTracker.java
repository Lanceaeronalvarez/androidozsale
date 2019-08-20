package au.com.dealsdirect.service.datacollection.registerservices;

import com.crashlytics.android.answers.AddToCartEvent;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.crashlytics.android.answers.InviteEvent;
import com.crashlytics.android.answers.LoginEvent;
import com.crashlytics.android.answers.PurchaseEvent;
import com.crashlytics.android.answers.SignUpEvent;
import com.crashlytics.android.answers.StartCheckoutEvent;

import java.util.HashMap;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
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
import au.com.dealsdirect.service.event.ActionTrackerInterface;
import au.com.dealsdirect.utils.AppLogger;

import static au.com.dealsdirect.service.datacollection.registerservices.ActionTracker.CartJourneyType.ADD_TO_CART;
import static au.com.dealsdirect.service.datacollection.registerservices.ActionTracker.CartJourneyType.VIEW_CART;
import static au.com.dealsdirect.service.datacollection.registerservices.ActionTracker.CartJourneyType.VIEW_PRODUCT;
import static au.com.dealsdirect.service.datacollection.registerservices.ActionTracker.CartJourneyType.VIEW_PRODUCT_CATEGORY;
import static au.com.dealsdirect.service.datacollection.registerservices.ActionTracker.CartJourneyType.VIEW_SALE;
import static au.com.dealsdirect.service.datacollection.registerservices.ActionTracker.ClickType.ORDER_TRACK;
import static au.com.dealsdirect.service.datacollection.registerservices.ActionTracker.ClickType.PHONE;
import static au.com.dealsdirect.service.datacollection.registerservices.ActionTracker.ClickType.TABLET;
import static au.com.dealsdirect.service.datacollection.registerservices.ActionTracker.CustomAttributeTypes.PHONE_TYPE;
import static au.com.dealsdirect.service.datacollection.registerservices.ActionTracker.CustomAttributeTypes.TABLET_TYPE;
import static au.com.dealsdirect.service.datacollection.registerservices.ActionTracker.LoginType.GAVE_UP_LOGIN;
import static au.com.dealsdirect.service.datacollection.registerservices.ActionTracker.LoginType.LOGIN;
import static au.com.dealsdirect.service.datacollection.registerservices.ActionTracker.LoginType.SUCCESSFUL_LOGIN;
import static au.com.dealsdirect.service.datacollection.registerservices.ActionTracker.RegisterMethod.GAVE_UP_REGISTRATION;
import static au.com.dealsdirect.service.datacollection.registerservices.ActionTracker.RegisterMethod.REGISTRATION;
import static au.com.dealsdirect.service.datacollection.registerservices.ActionTracker.RegisterMethod.SUCCESSFUL_REGISTRATION;
import static com.crashlytics.android.answers.Answers.getInstance;


/**
 * ActionTracker implements ActionTrackerInterface
 * Logs events to fabric - Answers.
 *
 * @author Diofel Pallega
 * @version 1.0
 * @since 8/15/16
 */

public class ActionTracker implements ActionTrackerInterface {
    private static Answers mAnswers;
    private static DataManager mDataManager;

    public static void registerFabricEvents() {

        // register add to cart
        DataCollector.EventRegistry.register(generateEventKey(Events.AddedToCartEvent, getServiceKey()), Events.AddedToCartEvent,
                new LoggingService.LogAddedToCart(new AddedToCartEvent() {
                    @Override
                    public void LogDataEvents(HashMap<String, Object> parameters) {
                        addToCartEvent(String.valueOf(parameters.get(DataCollector.EventParameters.SOURCE)),
                                (Integer) parameters.get(DataCollector.EventParameters.ATTEMPTS));
                    }
                }));

        // register start checkout event
        DataCollector.EventRegistry.register(generateEventKey(Events.InitiateCheckout, getServiceKey()), Events.InitiateCheckout,
                new LoggingService.LogInitiateCheckout(new InitiateCheckOutEvent() {
                    @Override
                    public void LogDataEvents(HashMap<String, Object> parameters) {
                        startCheckoutEvent();
                    }
                }));

        // register sign up event
        DataCollector.EventRegistry.register(generateEventKey(Events.SignUp, getServiceKey()), Events.SignUp,
                new LoggingService.LogRegistration(new RegistrationDataEvent() {
                    @Override
                    public void LogDataEvents(HashMap<String, Object> parameters) {
                        signUp(String.valueOf(parameters.get(DataCollector.EventParameters.METHOD)),
                                (Boolean) parameters.get(DataCollector.EventParameters.RESULT));
                    }
                }));

        // register purchase event
        DataCollector.EventRegistry.register(generateEventKey(Events.PurchaseEvent, getServiceKey()), Events.PurchaseEvent,
                new LoggingService.LogPurchase(new PurchaseDataEvent() {
                    @Override
                    public void LogDataEvents(HashMap<String, Object> parameters) {
                        purchase(String.valueOf(parameters.get(DataCollector.EventParameters.PAYMENT_OPTION)),
                                (Boolean) parameters.get(DataCollector.EventParameters.IS_NEW_USER),
                                (Boolean) parameters.get(DataCollector.EventParameters.RESULT));
                    }
                }));

        // register app launch event
        DataCollector.EventRegistry.register(generateEventKey(Events.CVAppLaunch, getServiceKey()), Events.CVAppLaunch,
                new LoggingService.LogCVAppLaunch(new CVAppLaunchEvent() {
                    @Override
                    public void LogDataEvents(HashMap<String, Object> parameters) {
                        CVAppLaunch((Double) parameters.get(DataCollector.EventParameters.MILLISECONDS));
                    }
                }));

        // register login
        DataCollector.EventRegistry.register(generateEventKey(Events.Login, getServiceKey()), Events.Login,
                new LoggingService.LogLogin(new LoginDataEvent() {
                    @Override
                    public void LogDataEvents(HashMap<String, Object> parameters) {
                        login(String.valueOf(parameters.get(DataCollector.EventParameters.METHOD)),
                                (Boolean) parameters.get(DataCollector.EventParameters.RESULT));
                    }
                }));

        //register ccscan
        DataCollector.EventRegistry.register(generateEventKey(Events.CCScan, getServiceKey()), Events.CCScan,
                new LoggingService.LogCCScan(new CCScanEvent() {
                    @Override
                    public void LogDataEvents(HashMap<String, Object> parameters) {
                        CCScan((EventProgress) parameters.get(DataCollector.EventParameters.EVENT_PROGRESS));
                    }
                }));

        // register sale banners
        DataCollector.EventRegistry.register(generateEventKey(Events.CVSaleBanners, getServiceKey()), Events.CVSaleBanners,
                new LoggingService.LogSaleBanners(new SaleBannersDataEvent() {
                    @Override
                    public void LogDataEvents(HashMap<String, Object> parameters) {
                        CVSaleBanners((Double) parameters.get(DataCollector.EventParameters.MILLISECONDS));
                    }
                }));

        // register item list
        DataCollector.EventRegistry.register(generateEventKey(Events.CVItemList, getServiceKey()), Events.CVItemList,
                new LoggingService.LogItemList(new ItemListDataEvent() {
                    @Override
                    public void LogDataEvents(HashMap<String, Object> parameters) {
                        CVItemList((Double) parameters.get(DataCollector.EventParameters.MILLISECONDS));
                    }
                }));

        // register item details
        DataCollector.EventRegistry.register(generateEventKey(Events.CVItemDetails, getServiceKey()), Events.CVItemDetails,
                new LoggingService.LogItemDetails(new ItemDetailsDataEvent() {
                    @Override
                    public void LogDataEvents(HashMap<String, Object> parameters) {
                        CVItemDetails((Double) parameters.get(DataCollector.EventParameters.MILLISECONDS));
                    }
                }));

        //register track order
        DataCollector.EventRegistry.register(generateEventKey(Events.CVOrderTrack, getServiceKey()), Events.CVOrderTrack,
                new LoggingService.LogTrackOrder(new TrackOrderDataEvent() {
                    @Override
                    public void LogDataEvents(HashMap<String, Object> parameters) {
                        CVOrderTrack(String.valueOf(parameters.get(DataCollector.EventParameters.SOURCE)));
                    }
                }));

        //register share
        DataCollector.EventRegistry.register(generateEventKey(Events.Share, getServiceKey()), Events.Share,
                new LoggingService.LogShare(new ShareDataEvent() {
                    @Override
                    public void LogDataEvents(HashMap<String, Object> parameters) {
                        share(String.valueOf(parameters.get(DataCollector.EventParameters.METHOD)),
                                String.valueOf(parameters.get(DataCollector.EventParameters.SOURCE)));
                    }
                }));

        // register add to cart journey view cart
        DataCollector.EventRegistry.register(generateEventKey(Events.addToCartJourneyViewCart, getServiceKey()), Events.addToCartJourneyViewCart,
                new LoggingService.LogAddToCartJourneyViewCart(new AddToCartJourneyViewCartEvent() {
                    @Override
                    public void LogDataEvents(HashMap<String, Object> parameters) {
                        addToCartJourneyViewCart();
                    }
                }));

        //register add to cart journey view product
        DataCollector.EventRegistry.register(generateEventKey(Events.addToCartJourneyViewProductCategory, getServiceKey()),
                Events.addToCartJourneyViewProductCategory,
                new LoggingService.LogAddToCartJourneyViewProductCategory(new AddToCartJourneyViewProductCategoryEvent() {
                    @Override
                    public void LogDataEvents(HashMap<String, Object> parameters) {
                        addToCartJourneyViewProductCategory();
                    }
                }));

        // register checkout journey
        DataCollector.EventRegistry.register(generateEventKey(Events.checkoutJourney, getServiceKey()), Events.checkoutJourney,
                new LoggingService.LogCheckoutJourney(new CheckOutJourneyEvent() {
                    @Override
                    public void LogDataEvents(HashMap<String, Object> parameters) {
                        checkoutJourney(String.valueOf(parameters.get(DataCollector.EventParameters.TYPE)));
                    }
                }));

        //register click event
        DataCollector.EventRegistry.register(generateEventKey(Events.clicksEvent, getServiceKey()), Events.clicksEvent,
                new LoggingService.LogClickEvent(new ClickEvent() {
                    @Override
                    public void LogDataEvents(HashMap<String, Object> parameters) {
                        clicksEvent(String.valueOf(parameters.get(DataCollector.EventParameters.TYPE)),
                                (Integer) parameters.get(DataCollector.EventParameters.ITEM_ARRAY_POSITION));
                    }
                }));

    }

    private static String generateEventKey(Events events, String service) {
        return events+"."+service;
    }

    public static String getServiceKey() {
        return "fabric";
    }

    @Inject
    public ActionTracker(DataManager dataManager) {
        mAnswers = getInstance();
        mDataManager = dataManager;
    }

    public static void startCheckoutEvent() {
        if (!mDataManager.hasActiveCheckoutSession()) {
            mAnswers.logStartCheckout(new StartCheckoutEvent());
            checkoutJourney(EventProgress.START.getValue());
        }
    }

    public static void addToCartEvent(String source, int attempts) {
        mAnswers.logAddToCart(new AddToCartEvent()
                .putCustomAttribute(CustomAttributeTypes.SOURCE.getValue(), source)
                .putCustomAttribute(CustomAttributeTypes.ATTEMPTS.getValue(), attempts));

        if (!mDataManager.hasAddedToCart()) {
            addToCartJourney(ADD_TO_CART);
            mDataManager.setHasAddedToCart(true);
        }
    }

    public static void CCScan(EventProgress eventProgress) {
        mAnswers.logCustom(new CustomEvent(CustomEventType.CC_SCAN.getValue())
                .putCustomAttribute(CustomAttributeTypes.TYPE.getValue(), eventProgress.getValue()));

        AppLogger.d("event progress: "+eventProgress.getValue());
    }

    public static void CVAppLaunch(double milliseconds) {
        mAnswers.logCustom(new CustomEvent(CustomEventType.CV_APPLAUNCH.getValue())
                .putCustomAttribute(CustomAttributeTypes.TYPE.getValue(), milliseconds));

        AppLogger.d("App launch: "+milliseconds);
    }

    public static void CVSaleBanners(double milliseconds) {
        mAnswers.logCustom(new CustomEvent(CustomEventType.CV_SALEBANNERS.getValue())
                .putCustomAttribute(CustomAttributeTypes.TYPE.getValue(), milliseconds));
    }

    public static void CVItemList(double milliseconds) {
        mAnswers.logCustom(new CustomEvent(CustomEventType.CV_ITEMLIST.getValue())
                .putCustomAttribute(CustomAttributeTypes.TYPE.getValue(), milliseconds));

        if (!mDataManager.hasViewedSale()) {
            addToCartJourney(VIEW_SALE);
            mDataManager.setHasViewedSale(true);
        }
    }

    public static void CVItemDetails(double milliseconds) {
        mAnswers.logCustom(new CustomEvent(CustomEventType.CV_ITEMDETAILS.getValue())
                .putCustomAttribute(CustomAttributeTypes.TYPE.getValue(), milliseconds));

        if (!mDataManager.hasViewedProduct()) {
            addToCartJourney(VIEW_PRODUCT);
            mDataManager.setHasViewedProduct(true);
        }
    }

    public static void CVOrderTrack(String source) {
        mAnswers.logCustom(new CustomEvent(CustomEventType.CV_ORDERTRACK.getValue())
                .putCustomAttribute(CustomAttributeTypes.SOURCE.getValue(), source));
        clicksOrdersEvent(source);
    }

    public static void purchase(String paymentOption, boolean isNewUser, boolean result) {
        mAnswers.logPurchase(new PurchaseEvent()
                .putSuccess(result)
                .putCustomAttribute(CustomAttributeTypes.PAYMENT_OPTION.getValue(), paymentOption)
                .putCustomAttribute(CustomAttributeTypes.NEW_USER.getValue(), String.valueOf(isNewUser)));
        mDataManager.setLastRedirection(LastRedirection.PAY);
        checkoutJourney(EventProgress.END.getValue());
    }

    public static void signUp(String method, boolean result) {
        mAnswers.logSignUp(new SignUpEvent()
                .putMethod(REGISTRATION)
                .putSuccess(result)
                .putCustomAttribute(result ? SUCCESSFUL_REGISTRATION : GAVE_UP_REGISTRATION, method));
    }

    public static void login(String method, boolean result) {
        mAnswers.logLogin(new LoginEvent()
                .putMethod(LOGIN)
                .putSuccess(result)
                .putCustomAttribute(result ? SUCCESSFUL_LOGIN : GAVE_UP_LOGIN, method));
    }

    public static void share(String method, String source) {
        mAnswers.logInvite(new InviteEvent().putMethod(method)
                .putCustomAttribute(CustomAttributeTypes.SOURCE.getValue(), source)
                .putCustomAttribute(EventProgress.SUCCESS.getValue(), String.valueOf(true)));
    }

    public static void addToCartJourneyViewCart() {
        if (!mDataManager.hasViewedCart()) {
            addToCartJourney(VIEW_CART);
            mDataManager.setHasViewedCart(true);
        }
    }

    public static void addToCartJourneyViewProductCategory() {
        if (!mDataManager.hasViewedProductCategory()) {
            addToCartJourney(VIEW_PRODUCT_CATEGORY);
            mDataManager.setHasViewedProductCategory(true);
        }
    }

//    @Override
    public static void addToCartJourney(String type) {
        mAnswers.logCustom(new CustomEvent(CustomEventType.ADDTOCART_JOURNEY.getValue())
                .putCustomAttribute(mDataManager.isTablet() ? TABLET_TYPE.getValue() :
                        PHONE_TYPE.getValue(), type));
    }

    public static void checkoutJourney(String type) {
        mDataManager.setHasActiveCheckoutSession(type.equals(EventProgress.START.getValue()));

        String finalType = type.equals(EventProgress.END.getValue()) ?
                EventProgress.END.getValue().concat(mDataManager.getLastRedirection()) : type;

        mAnswers.logCustom(new CustomEvent(CustomEventType.CHECKOUT_JOURNEY.getValue())
                .putCustomAttribute(CustomAttributeTypes.TYPE.getValue(), finalType));
    }

    public static void clicksOrdersEvent(String source) {
        mAnswers.logCustom(new CustomEvent(CustomEventType.CLICKS.getValue())
                .putCustomAttribute(ORDER_TRACK, source));
    }

    public static void clicksEvent(String type, int itemArrPos) {
        mAnswers.logCustom(new CustomEvent(CustomEventType.CLICKS.getValue())
                .putCustomAttribute((mDataManager.isTablet() ? TABLET : PHONE) + type, itemArrPos));
    }

    public final class ClickType {
        final static String PHONE = "Phone";
        final static String TABLET = "Tablet";
        public final static String BANNER_CLICK = "BannerClick";
        public final static String PRODUCT_CLICK = "ProductClick";
        final static String ORDER_TRACK = "OrderTrack";
    }

    public final class LoginType {
        public final static String FACEBOOK = "LoginFacebook";
        public final static String LOGIN = "Login";
        public final static String TICKET = "LoginTicket";
        final static String SUCCESSFUL_LOGIN = "SuccessfulLogin";
        final static String GAVE_UP_LOGIN = "GaveUpLogin";
        public final static String FORGOT_PASSWORD = "ForgotPassword";
        public final static String NO_ACTION = "NoAction";
    }

    public final class RegisterMethod {
        public final static String FACEBOOK = "LoginFacebook";
        public final static String REGISTRATION = "Registration";
        public final static String VCO = "LoginVisa";
        final static String SUCCESSFUL_REGISTRATION = "SuccessfulRegistration";
        final static String GAVE_UP_REGISTRATION = "GaveUpRegistration";
        public final static String NO_ACTION = "NoAction";
    }

    public final class ViewSource {
        public final static String SALE = "Sale";
        public final static String CATEGORY = "Category";
        public final static String SEARCH = "Search";
        public final static String ITEM_DETAILS = "ItemDetails";
        public final static String INVITE = "Invite";
        public final static String ORDER_LIST = "OrderList";
        public final static String ORDER_DETAILS = "OrderDetails";
    }

    public final class InviteType {
        public final static String FACEBOOK = "Facebook";
        public final static String TWITTER = "Twitter";
        public final static String SMS = "SMS";
        public final static String EMAIL = "Email";
        public final static String CANCEL = "Cancel";
    }

    public final class CartJourneyType {
        final static String VIEW_SALE = "ViewSale";
        final static String VIEW_PRODUCT_CATEGORY = "ViewProductCategory";
        final static String VIEW_PRODUCT = "ViewProduct";
        final static String ADD_TO_CART = "AddToCart";
        final static String VIEW_CART = "ViewCart";
    }

    public final class LastRedirection {
        public final static String PAY = "Pay";
        public final static String ADD_ADDRESS = "AddAddress";
        public final static String ADD_PAYMENT_METHOD = "AddPaymentMethod";
        public final static String OURPAY_OFFER = "OurpayOffer"; // This is Legacy only
        public final static String OURPAY_PHONE_VERIFIATION = "OurpayPhoneVerification";
        public final static String PAYPAL = "Paypal";
        public final static String VISACHECKOUT = "VisaCheckout";
        public final static String MASTERPASS = "Masterpass";
        public final static String THREEDSECURE_OTP = "3DSOTP";
    }

    public enum PaymentOption {
        VCO("VisaCheckout"),
        PAYPAL("PayPal"),
        MASTERPASS("Masterpass"),
        OURPAY("Ourpay"),
        OURPAY3DS("Ourpay3DS"),
        THREEDS("3DS"),
        REGULAR("Regular");

        private String value;

        PaymentOption(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum EventProgress {
        START("Start"),
        SUCCESS("Success"),
        END("End");

        private String value;

        EventProgress(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum CustomAttributeTypes {
        TYPE("Type"),
        PHONE_TYPE("PhoneType"),
        TABLET_TYPE("TabletType"),
        SOURCE("Source"),
        LOAD_TIME("LoadTime"),
        PAYMENT_OPTION("PaymentOption"),
        ATTEMPTS("Attempts"),
        NEW_USER("NewUser");

        private String value;

        CustomAttributeTypes(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum CustomEventType {
        CC_SCAN("CC_SCAN"),
        CV_APPLAUNCH("CV_APPLAUNCH"),
        CV_SALEBANNERS("CV_SALEBANNERS"),
        CV_ITEMLIST("CV_ITEMLIST"),
        CV_ITEMDETAILS("CV_ITEMDETAILS"),
        CV_ORDERTRACK("CV_ORDERTRACK"),
        CLICKS("CLICKS"),
        ADDTOCART_JOURNEY("ADDTOCART_JOURNEY"),
        CHECKOUT_JOURNEY("CHECKOUT_JOURNEY");

        private String value;

        CustomEventType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

}
