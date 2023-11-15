package au.com.dealsdirect.service.datacollection.registerservices;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.HashMap;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.events.GA4EventParams;
import au.com.dealsdirect.data.network.model.events.WishlistEventRequest;
import au.com.dealsdirect.service.datacollection.core.DataCollectionService;
import au.com.dealsdirect.service.datacollection.core.DataCollector;
import au.com.dealsdirect.service.datacollection.core.DataCollector.EventParameters;
import au.com.dealsdirect.service.datacollection.core.LoggingService;
import au.com.dealsdirect.service.datacollection.enums.Events;
import au.com.dealsdirect.service.datacollection.events.AddToCartJourneyViewCartEvent;
import au.com.dealsdirect.service.datacollection.events.AddToCartJourneyViewProductCategoryEvent;
import au.com.dealsdirect.service.datacollection.events.AddedToCartEvent;
import au.com.dealsdirect.service.datacollection.events.BannerClickEvent;
import au.com.dealsdirect.service.datacollection.events.CCScanEvent;
import au.com.dealsdirect.service.datacollection.events.CVAppLaunchEvent;
import au.com.dealsdirect.service.datacollection.events.CheckOutJourneyEvent;
import au.com.dealsdirect.service.datacollection.events.ClickEvent;
import au.com.dealsdirect.service.datacollection.events.FailedTransactionEvent;
import au.com.dealsdirect.service.datacollection.events.GA4AddPaymentInfoDataEvent;
import au.com.dealsdirect.service.datacollection.events.GA4AddShippingInfoDataEvent;
import au.com.dealsdirect.service.datacollection.events.GA4SelectItemDataEvent;
import au.com.dealsdirect.service.datacollection.events.InitiateCheckOutEvent;
import au.com.dealsdirect.service.datacollection.events.ItemDetailsDataEvent;
import au.com.dealsdirect.service.datacollection.events.ItemListDataEvent;
import au.com.dealsdirect.service.datacollection.events.LoginDataEvent;
import au.com.dealsdirect.service.datacollection.events.ProductListGridViewPreferenceEvent;
import au.com.dealsdirect.service.datacollection.events.PurchaseDataEvent;
import au.com.dealsdirect.service.datacollection.events.RecentlyViewedDataEvent;
import au.com.dealsdirect.service.datacollection.events.RegistrationDataEvent;
import au.com.dealsdirect.service.datacollection.events.RemoveFromCartDataEvent;
import au.com.dealsdirect.service.datacollection.events.SaleBannersDataEvent;
import au.com.dealsdirect.service.datacollection.events.ShareDataEvent;
import au.com.dealsdirect.service.datacollection.events.ToggleColumnEvent;
import au.com.dealsdirect.service.datacollection.events.TrackOrderDataEvent;
import au.com.dealsdirect.service.datacollection.events.WishlistDataEvent;
import au.com.dealsdirect.service.datacollection.events.YouMayAlsoLikeClickEvent;
import au.com.dealsdirect.service.event.FirebaseEventServiceInterface;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

import static au.com.dealsdirect.service.datacollection.core.DataCollector.EventParameters.ClickType.PHONE;
import static au.com.dealsdirect.service.datacollection.core.DataCollector.EventParameters.ClickType.TABLET;
import static au.com.dealsdirect.service.datacollection.core.DataCollector.EventParameters.CustomAttributeTypes.PHONE_TYPE;
import static au.com.dealsdirect.service.datacollection.core.DataCollector.EventParameters.CustomAttributeTypes.TABLET_TYPE;


/**
 * Created by MTC on 3/8/19.
 */

public class FirebaseAnalyticsService implements FirebaseEventServiceInterface, DataCollectionService {
    private static DataManager mDataManager;
    private static SchedulerProvider mSchedulerProvider;
    private static CompositeDisposable mCompositeDisposable;
    static String finalType;
    private static final String FAILED_TRANSACTION_EVENT = "FAILED_PAYMENT_TRANSACTION";
    private static final String TOGGLE_LIST_COUNT_EVENT = "PRODUCT_LIST_TOGGLE_COLUMN_COUNT";
    private static final String PRODUCT_LIST_GRID_VIEW_PREFERENCE = "PRODUCT_LIST_GRID_VIEW_PREFERENCE";
    private static final String WISHLIST_ADDTOCART = "WISHLIST_ADDTOCART";
    private static final String WISHLIST_PAYMENTSUCCESS = "WISHLIST_PAYMENTSUCCESS";
    private static final String YOU_MAY_ALSO_LIKE_BANNER_CLICK = "YouMayAlsoLikeBanners";
    private static final String BANNER_CLICK = "BANNER_CLICK";
    private static final String SPONSORED_BANNER_CLICK = "SponsoredBanners";
    private static final String REGULAR_BANNER_CLICK = "RegularBanners";
    private static final String RECENTLY_VIEWED_ITEMS_BANNER_CLICK = "RecentlyViewedItemsBanners";

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
    public FirebaseAnalyticsService(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        mDataManager = dataManager;
        mSchedulerProvider = schedulerProvider;
        mCompositeDisposable = compositeDisposable;
    }

    private FirebaseAnalyticsService() {
        registerFirebaseEvents();
    }

    public static void registerFirebaseEvents() {

        //register open app
        DataCollector.EventRegistry.register(generateEventKey(Events.CVAppLaunch, getServiceKey()), new LoggingService.LogCVAppLaunch(new CVAppLaunchEvent() {
            @Override
            public void LogDataEvents(HashMap<String, Object> parameters) {
                Bundle bundle = new Bundle();
                bundle.putDouble(EventParameters.LOAD_TIME, (Double) parameters.get(EventParameters.MILLISECONDS));
                appOpen((Context) parameters.get(EventParameters.APP_CONTEXT), bundle);
            }
        }));

        //register start checkout
        DataCollector.EventRegistry.register(generateEventKey(Events.InitiateCheckout, getServiceKey()), new LoggingService.LogInitiateCheckout(new InitiateCheckOutEvent() {
            @Override
            public void LogDataEvents(HashMap<String, Object> parameters) {
                // old
                Bundle bundle = new Bundle();
                bundle.putString(EventParameters.PAYMENT_METHOD_TYPE, String.valueOf(parameters.get(EventParameters.PAYMENT_METHOD_TYPE)));
                Integer numberOfItems = (Integer) parameters.get(EventParameters.NUMBER_OF_ITEMS);
                bundle.putInt(EventParameters.NUMBER_OF_ITEMS, numberOfItems == null ? 0 : numberOfItems);
                bundle.putString(FirebaseAnalytics.Param.VALUE, String.valueOf(parameters.get(EventParameters.START_CHECKOUT_VALUE)));
                bundle.putString(FirebaseAnalytics.Param.CURRENCY, String.valueOf(parameters.get(EventParameters.START_CHECKOUT_CURRENCY)));
                startCheckout((Context) parameters.get(EventParameters.APP_CONTEXT), bundle, String.valueOf(parameters.get(EventParameters.SCREEN_NAME)));

                // ga4
                final Object eventParams = parameters.get(EventParameters.GA4_EVENT_PARAMS);
                if (eventParams instanceof GA4EventParams) {
                    ga4BeginCheckoutEvent((Context) parameters.get(EventParameters.APP_CONTEXT), (GA4EventParams) eventParams);
                }
            }
        }));

        //register share
        DataCollector.EventRegistry.register(generateEventKey(Events.Share, getServiceKey()), new LoggingService.LogShare(new ShareDataEvent() {
            @Override
            public void LogDataEvents(HashMap<String, Object> parameters) {
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.SOURCE, String.valueOf(parameters.get(EventParameters.SOURCE)));
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, String.valueOf(parameters.get(EventParameters.METHOD)));
                bundle.putString(FirebaseAnalytics.Param.SUCCESS, String.valueOf(parameters.get(EventParameters.SHARE_SUCCESS)));
                shareEvent((Context) parameters.get(EventParameters.APP_CONTEXT), bundle, String.valueOf(parameters.get(EventParameters.SCREEN_NAME)));
            }
        }));

        //register item list
        DataCollector.EventRegistry.register(generateEventKey(Events.CVItemList, getServiceKey()), new LoggingService.LogItemList(new ItemListDataEvent() {
            @Override
            public void LogDataEvents(HashMap<String, Object> parameters) {
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_LIST_ID, String.valueOf(parameters.get(EventParameters.ITEM_LIST)));
                bundle.putString(EventParameters.LOAD_TIME, String.valueOf(parameters.get(EventParameters.MILLISECONDS)));
                itemList((Context) parameters.get(EventParameters.APP_CONTEXT), bundle, String.valueOf(parameters.get(EventParameters.SCREEN_NAME)));

                final Object eventParams = parameters.get(EventParameters.GA4_EVENT_PARAMS);
                if (eventParams instanceof GA4EventParams) {
                    ga4ViewItemListEvent((Context) parameters.get(EventParameters.APP_CONTEXT), (GA4EventParams) eventParams);
                }
            }
        }));

        //register item details
        DataCollector.EventRegistry.register(generateEventKey(Events.CVItemDetails, getServiceKey()), new LoggingService.LogItemDetails(new ItemDetailsDataEvent() {
            @Override
            public void LogDataEvents(HashMap<String, Object> parameters) {
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, String.valueOf(parameters.get(EventParameters.ITEM_ID)));
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, String.valueOf(parameters.get(EventParameters.ITEM_NAME)));
                bundle.putString(FirebaseAnalytics.Param.PRICE, String.valueOf(parameters.get(EventParameters.PRICE)));
                bundle.putString(FirebaseAnalytics.Param.SOURCE, String.valueOf(parameters.get(EventParameters.SCREEN_NAME)));
                itemDetails((Context) parameters.get(EventParameters.APP_CONTEXT), bundle, String.valueOf(parameters.get(EventParameters.SCREEN_NAME)));

                final Object eventParams = parameters.get(EventParameters.GA4_EVENT_PARAMS);
                if (eventParams instanceof GA4EventParams) {
                    ga4ViewItemEvent((Context) parameters.get(EventParameters.APP_CONTEXT), (GA4EventParams) eventParams);
                }
            }
        }));

        // register sign up
        DataCollector.EventRegistry.register(generateEventKey(Events.SignUp, getServiceKey()), new LoggingService.LogRegistration(new RegistrationDataEvent() {
            @Override
            public void LogDataEvents(HashMap<String, Object> parameters) {
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.METHOD, String.valueOf(parameters.get(EventParameters.METHOD)));
                bundle.putString(EventParameters.SIGN_UP_GAVE_UP, String.valueOf(parameters.get(EventParameters.RESULT)));
                signUp((Context) parameters.get(EventParameters.APP_CONTEXT), bundle, String.valueOf(parameters.get(EventParameters.SCREEN_NAME)));
            }
        }));

        //register login
        DataCollector.EventRegistry.register(generateEventKey(Events.Login, getServiceKey()), new LoggingService.LogLogin(new LoginDataEvent() {
            @Override
            public void LogDataEvents(HashMap<String, Object> parameters) {
                Boolean result = (Boolean) parameters.get(EventParameters.RESULT);
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.METHOD, String.valueOf(parameters.get(EventParameters.METHOD)));
                bundle.putString(EventParameters.LOGIN_GAVE_UP, String.valueOf(result == null || !result));
                login((Context) parameters.get(EventParameters.APP_CONTEXT), bundle, String.valueOf(parameters.get(EventParameters.SCREEN_NAME)));
            }
        }));

        //register add to cart
        DataCollector.EventRegistry.register(generateEventKey(Events.AddedToCartEvent, getServiceKey()), new LoggingService.LogAddedToCart(new AddedToCartEvent() {
            @Override
            public void LogDataEvents(HashMap<String, Object> parameters) {
                // old
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, String.valueOf(parameters.get(EventParameters.ITEM_ID)));
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, String.valueOf(parameters.get(EventParameters.ITEM_NAME)));
                bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, String.valueOf(parameters.get(EventParameters.ITEM_CATEGORY)));
                bundle.putString(FirebaseAnalytics.Param.QUANTITY, String.valueOf(parameters.get(EventParameters.ADD_TO_CART_QUANTITY)));
                bundle.putString(FirebaseAnalytics.Param.VALUE, String.valueOf(parameters.get(EventParameters.PRICE)));
                bundle.putString(FirebaseAnalytics.Param.CURRENCY, String.valueOf(parameters.get(EventParameters.ADD_TO_CART_CURRENCY)));
                bundle.putString(FirebaseAnalytics.Param.SOURCE, String.valueOf(parameters.get(EventParameters.ADD_TO_CART_SOURCE)));
                bundle.putString(EventParameters.ADD_TO_CART_ATTEMPTS, String.valueOf(parameters.get(EventParameters.ATTEMPTS)));
                addToCart((Context) parameters.get(EventParameters.APP_CONTEXT), bundle, String.valueOf(parameters.get(EventParameters.SCREEN_NAME)));

                // ga4
                final Object eventParams = parameters.get(EventParameters.GA4_EVENT_PARAMS);
                if (eventParams instanceof GA4EventParams) {
                    ga4AddToCartEvent((Context) parameters.get(EventParameters.APP_CONTEXT), (GA4EventParams) eventParams);
                }
            }
        }));

        //register ccscan
        DataCollector.EventRegistry.register(generateEventKey(Events.CCScan, getServiceKey()), new LoggingService.LogCCScan(new CCScanEvent() {
            @Override
            public void LogDataEvents(HashMap<String, Object> parameters) {
                EventParameters.EventProgress eventProgress = (EventParameters.EventProgress) parameters.get(EventParameters.EVENT_PROGRESS);
                Bundle bundle = new Bundle();
                bundle.putString(EventParameters.CustomAttributeTypes.TYPE.getValue(), String.valueOf(eventProgress.getValue()));
                ccScan((Context) parameters.get(EventParameters.APP_CONTEXT), bundle, String.valueOf(parameters.get(EventParameters.SCREEN_NAME)));
            }
        }));

        //register checkout journey event
        DataCollector.EventRegistry.register(generateEventKey(Events.checkoutJourney, getServiceKey()), new LoggingService.LogCheckoutJourney(new CheckOutJourneyEvent() {
            @Override
            public void LogDataEvents(HashMap<String, Object> parameters) {

                checkoutJourney((Context) parameters.get(EventParameters.APP_CONTEXT), String.valueOf(parameters.get(EventParameters.TYPE)), String.valueOf(parameters.get(EventParameters.SCREEN_NAME)));

            }
        }));

        //register purchase
        DataCollector.EventRegistry.register(generateEventKey(Events.PurchaseEvent, getServiceKey()), new LoggingService.LogPurchase(new PurchaseDataEvent() {
            @Override
            public void LogDataEvents(HashMap<String, Object> parameters) {
                // old
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.PAYMENT_TYPE, String.valueOf(parameters.get(EventParameters.PAYMENT_METHOD_TYPE)));
                bundle.putString(EventParameters.PURCHASE_NEW_USER, String.valueOf(parameters.get(EventParameters.IS_NEW_USER)));
                bundle.putDouble(FirebaseAnalytics.Param.VALUE, (Double) parameters.get(EventParameters.PRICE));
                bundle.putString(FirebaseAnalytics.Param.CURRENCY, String.valueOf(parameters.get(EventParameters.PURCHASE_CURRENCY)));
                bundle.putString(FirebaseAnalytics.Param.TRANSACTION_ID, String.valueOf(parameters.get(EventParameters.PURCHASE_TRANSACTION_ID)));
                purchase((Context) parameters.get(EventParameters.APP_CONTEXT), bundle, String.valueOf(parameters.get(EventParameters.SCREEN_NAME)));

                // ga4
                final Object eventParams = parameters.get(EventParameters.GA4_EVENT_PARAMS);
                if (eventParams instanceof GA4EventParams) {
                    ga4PurchaseEvent((Context) parameters.get(EventParameters.APP_CONTEXT), (GA4EventParams) eventParams);
                }
            }
        }));

        //register add to cart journey view cart
        DataCollector.EventRegistry.register(generateEventKey(Events.addToCartJourneyViewCart, getServiceKey()), new LoggingService.LogAddToCartJourneyViewCart(new AddToCartJourneyViewCartEvent() {
            @Override
            public void LogDataEvents(HashMap<String, Object> parameters) {
                addToCartJourneyViewCart((Context) parameters.get(EventParameters.APP_CONTEXT));

                final Object eventParams = parameters.get(EventParameters.GA4_EVENT_PARAMS);
                if (eventParams instanceof GA4EventParams) {
                    ga4ViewCartEvent((Context) parameters.get(EventParameters.APP_CONTEXT), (GA4EventParams) eventParams);
                }
            }
        }));

        //register add to cart journey view product
        DataCollector.EventRegistry.register(generateEventKey(Events.addToCartJourneyViewProductCategory, getServiceKey()), new LoggingService.LogAddToCartJourneyViewProductCategory(new AddToCartJourneyViewProductCategoryEvent() {
            @Override
            public void LogDataEvents(HashMap<String, Object> parameters) {
                addToCartJourneyViewProductCategory((Context) parameters.get(EventParameters.APP_CONTEXT));
            }
        }));

        //register remove item from cart
        DataCollector.EventRegistry.register(generateEventKey(Events.RemoveFromCart, getServiceKey()), new LoggingService.LogRemoveFromCart(new RemoveFromCartDataEvent() {
            @Override
            public void LogDataEvents(HashMap<String, Object> parameters) {
                final Object eventParams = parameters.get(EventParameters.GA4_EVENT_PARAMS);
                if (eventParams instanceof GA4EventParams) {
                    ga4RemoveFromCart((Context) parameters.get(EventParameters.APP_CONTEXT), (GA4EventParams) eventParams);
                }
            }
        }));

        //register sale banner event
        DataCollector.EventRegistry.register(generateEventKey(Events.CVSaleBanners, getServiceKey()), new LoggingService.LogSaleBanners(new SaleBannersDataEvent() {
            @Override
            public void LogDataEvents(HashMap<String, Object> parameters) {
                Bundle bundle = new Bundle();
                bundle.putDouble(EventParameters.MILLISECONDS, (Double) parameters.get(EventParameters.MILLISECONDS));
                saleBanners((Context) parameters.get(EventParameters.APP_CONTEXT), bundle, String.valueOf(parameters.get(EventParameters.SCREEN_NAME)));
            }
        }));

        //register order track
        DataCollector.EventRegistry.register(generateEventKey(Events.CVOrderTrack, getServiceKey()), new LoggingService.LogTrackOrder(new TrackOrderDataEvent() {
            @Override
            public void LogDataEvents(HashMap<String, Object> parameters) {
                String source = String.valueOf(parameters.get(EventParameters.SOURCE));
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.SOURCE, source);
                orderTrack((Context) parameters.get(EventParameters.APP_CONTEXT), bundle, String.valueOf(parameters.get(EventParameters.SCREEN_NAME)), source);
            }
        }));

        //register click event
        DataCollector.EventRegistry.register(generateEventKey(Events.clicksEvent, getServiceKey()), new LoggingService.LogClickEvent(new ClickEvent() {
            @Override
            public void LogDataEvents(HashMap<String, Object> parameters) {
                String type = String.valueOf(parameters.get(EventParameters.TYPE));
                int position = (Integer) parameters.get(EventParameters.ITEM_ARRAY_POSITION);
                Bundle bundle = new Bundle();
                bundle.putInt((mDataManager.isTablet() ? TABLET : PHONE) + type, position);
                clicksEvent((Context) parameters.get(EventParameters.APP_CONTEXT), bundle, String.valueOf(parameters.get(EventParameters.SCREEN_NAME)));
            }
        }));

        //register failed transaction
        DataCollector.EventRegistry.register(generateEventKey(Events.FailedTransaction, getServiceKey()), new LoggingService.LogFailedTransaction(new FailedTransactionEvent() {
            @Override
            public void LogDataEvents(HashMap<String, Object> parameters) {
                Bundle bundle = new Bundle();
                bundle.putString(EventParameters.FAILED_TRANSACTION_OPTION, String.valueOf(parameters.get(EventParameters.PAYMENT_METHOD_TYPE)));
                bundle.putString(EventParameters.FAILED_TRANSACTION_MESSAGE, String.valueOf(parameters.get(EventParameters.FAILED_TRANSACTION_MESSAGE)));
                failedTransaction((Context) parameters.get(EventParameters.APP_CONTEXT), bundle, String.valueOf(parameters.get(EventParameters.SCREEN_NAME)));
            }
        }));

        // register toggle column
        DataCollector.EventRegistry.register(generateEventKey(Events.ToggleColumn, getServiceKey()), new LoggingService.LogToggleColumn(new ToggleColumnEvent() {
            @Override
            public void LogDataEvents(HashMap<String, Object> parameters) {
                Bundle bundle = new Bundle();
                bundle.putString(EventParameters.TOGGLE_LIST_PORTRAIT, String.valueOf(parameters.get(EventParameters.TOGGLE_LIST_PORTRAIT)));
                bundle.putString(EventParameters.TOGGLE_LIST_LANDSCAPE, String.valueOf(parameters.get(EventParameters.TOGGLE_LIST_LANDSCAPE)));
                toggleColumnCount((Context) parameters.get(EventParameters.APP_CONTEXT), bundle, String.valueOf(parameters.get(EventParameters.SCREEN_NAME)));
            }
        }));

        // register grid view preference
        DataCollector.EventRegistry.register(generateEventKey(Events.ProductListGridViewPreference, getServiceKey()), new LoggingService.LogProductListGridViewPreference(new ProductListGridViewPreferenceEvent() {
            @Override
            public void LogDataEvents(HashMap<String, Object> parameters) {
                Bundle bundle = new Bundle();
                bundle.putString(EventParameters.TOGGLE_LIST_PREFERENCE, String.valueOf(parameters.get(EventParameters.TOGGLE_LIST_PREFERENCE)));
                logProductListGridViewPreference((Context) parameters.get(EventParameters.APP_CONTEXT), bundle, String.valueOf(parameters.get(EventParameters.SCREEN_NAME)));
            }
        }));

        // register wishlist event
        DataCollector.EventRegistry.register(generateEventKey(Events.WishlistEvent, getServiceKey()), new LoggingService.LogWishlistDataEvent(new WishlistDataEvent() {
            @Override
            public void LogDataEvents(HashMap<String, Object> parameters) {
                final Object obj = parameters.get(EventParameters.WISHLIST_EVENT_REQUEST);
                if (obj instanceof WishlistEventRequest) {
                    final WishlistEventRequest request = (WishlistEventRequest) obj;
                    if (request.getWishlistInfo().getOperation() == 1) {
                        final Object eventParams = parameters.get(EventParameters.GA4_EVENT_PARAMS);
                        if (eventParams instanceof GA4EventParams) {
                            ga4AddToWishlistEvent((Context) parameters.get(EventParameters.APP_CONTEXT), (GA4EventParams) eventParams);
                        }
                    }
                }
            }
        }));

        // register wishlist item add to cart
        DataCollector.EventRegistry.register(generateEventKey(Events.WishlistAddToCartEvent, getServiceKey()), new LoggingService.LogWishlistDataEvent(new WishlistDataEvent() {
            @Override
            public void LogDataEvents(HashMap<String, Object> parameters) {
                wishlistItemAddToCart((Context) parameters.get(EventParameters.APP_CONTEXT), String.valueOf(parameters.get(EventParameters.SCREEN_NAME)));
            }
        }));

        // register wishlist payment success
        DataCollector.EventRegistry.register(generateEventKey(Events.WishlistPaymentSuccessEvent, getServiceKey()), new LoggingService.LogWishlistDataEvent(new WishlistDataEvent() {
            @Override
            public void LogDataEvents(HashMap<String, Object> parameters) {
                wishlistPaymentSuccess((Context) parameters.get(EventParameters.APP_CONTEXT), String.valueOf(parameters.get(EventParameters.SCREEN_NAME)));
            }
        }));

        // register you may also like event
        DataCollector.EventRegistry.register(generateEventKey(Events.YouMayAlsoLikeEvent, getServiceKey()), new LoggingService.LogYouMayAlsoLikeEvent(new YouMayAlsoLikeClickEvent() {
            @Override
            public void LogDataEvents(HashMap<String, Object> parameters) {

                Bundle bundle = new Bundle();
                bundle.putString(EventParameters.SALE_NAME, String.valueOf(parameters.get(EventParameters.SALE_NAME)));
                bundle.putString(EventParameters.SCREEN_NAME, String.valueOf(parameters.get(EventParameters.SCREEN_NAME)));

                youMayAlsoLikeClick((Context) parameters.get(EventParameters.APP_CONTEXT), String.valueOf(parameters.get(EventParameters.SCREEN_NAME)), bundle);
            }
        }));

        // register banner click
        DataCollector.EventRegistry.register(generateEventKey(Events.BannerClickEvent, getServiceKey()), new LoggingService.LogBannerClickEvent(new BannerClickEvent() {
            @Override
            public void LogDataEvents(HashMap<String, Object> parameters) {

                Bundle bundle = new Bundle();
                bundle.putString(EventParameters.BANNER_TYPE, String.valueOf(parameters.get(EventParameters.BANNER_TYPE)));
                bundle.putString(EventParameters.SALE_NAME, String.valueOf(parameters.get(EventParameters.SALE_NAME)));
                bundle.putString(EventParameters.SCREEN_NAME, String.valueOf(parameters.get(EventParameters.SCREEN_NAME)));

                bannerClick((Context) parameters.get(EventParameters.APP_CONTEXT), String.valueOf(parameters.get(EventParameters.SCREEN_NAME)), bundle);
            }
        }));


        // register recently viewed event
        DataCollector.EventRegistry.register(generateEventKey(Events.RecentlyViewed, getServiceKey()), new LoggingService.LogRecentlyViewedEvent(new RecentlyViewedDataEvent() {
            @Override
            public void LogDataEvents(HashMap<String, Object> parameters) {

                Bundle bundle = new Bundle();
                bundle.putString(EventParameters.SALE_NAME, String.valueOf(parameters.get(EventParameters.SALE_NAME)));
                bundle.putString(EventParameters.SCREEN_NAME, String.valueOf(parameters.get(EventParameters.SCREEN_NAME)));

                recentlyViewedItemClick((Context) parameters.get(EventParameters.APP_CONTEXT), String.valueOf(parameters.get(EventParameters.SCREEN_NAME)), bundle);
            }
        }));

        // GA4 Add Payment Info
        DataCollector.EventRegistry.register(generateEventKey(Events.GA4AddPaymentInfo, getServiceKey()), new LoggingService.LogGA4AddPaymentInfoEvent(new GA4AddPaymentInfoDataEvent() {
            @Override
            public void LogDataEvents(HashMap<String, Object> parameters) {
                final Object eventParams = parameters.get(EventParameters.GA4_EVENT_PARAMS);
                if (eventParams instanceof GA4EventParams) {
                    ga4AddPaymentInfoEvent((Context) parameters.get(EventParameters.APP_CONTEXT), (GA4EventParams) eventParams);
                }
            }
        }));

        DataCollector.EventRegistry.register(generateEventKey(Events.GA4AddShippingInfo, getServiceKey()), new LoggingService.LogGA4AddShippingInfoEvent(new GA4AddShippingInfoDataEvent() {
            @Override
            public void LogDataEvents(HashMap<String, Object> parameters) {

                Bundle bundle = new Bundle();
                bundle.putString(EventParameters.SCREEN_NAME, String.valueOf(parameters.get(EventParameters.SCREEN_NAME)));

                final Object eventParams = parameters.get(EventParameters.GA4_EVENT_PARAMS);
                if (eventParams instanceof GA4EventParams) {
                    ga4AddShipmentInfoEvent((Context) parameters.get(EventParameters.APP_CONTEXT), (GA4EventParams) eventParams);
                }
            }
        }));

        DataCollector.EventRegistry.register(generateEventKey(Events.GA4SelectItem, getServiceKey()), new LoggingService.LogGA4SelectItemEvent(new GA4SelectItemDataEvent() {
            @Override
            public void LogDataEvents(HashMap<String, Object> parameters) {

                Bundle bundle = new Bundle();
                bundle.putString(EventParameters.SCREEN_NAME, String.valueOf(parameters.get(EventParameters.SCREEN_NAME)));

                final Object eventParams = parameters.get(EventParameters.GA4_EVENT_PARAMS);
                if (eventParams instanceof GA4EventParams) {
                    ga4SelectItemEvent((Context) parameters.get(EventParameters.APP_CONTEXT), (GA4EventParams) eventParams);
                }
            }
        }));
    }

    private static String generateEventKey(Events events, String service) {
        return events + "." + service;
    }

    private static void appOpen(Context context, Bundle bundle) {
        final FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle);
    }

    private static void startCheckout(Context context, Bundle bundle, String screenName) {
        final FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.BEGIN_CHECKOUT, bundle);

        checkoutJourney(context, EventParameters.EventProgress.START.getValue(), screenName);
    }

    private static void shareEvent(Context context, Bundle bundle, String screenName) {
        final FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, bundle);
    }

    private static void itemList(Context context, Bundle bundle, String screenName) {
        final FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM_LIST, bundle);
    }

    private static void itemDetails(Context context, Bundle bundle, String screenName) {
        final FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);
    }

    private static void signUp(Context context, Bundle bundle, String screenName) {
        final FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle);
    }

    private static void login(Context context, Bundle bundle, String screenName) {
        final FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle);
    }

    private static void addToCart(Context context, Bundle bundle, String screenName) {
        final FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.ADD_TO_CART, bundle);

        if (!mDataManager.hasAddedToCart()) {
            addToCartJourney(context, EventParameters.CartJourneyType.ADD_TO_CART);
            mDataManager.setHasAddedToCart(true);
        }
    }

    private static void search(Context context, Bundle bundle, String screenName) {
        final FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH, bundle);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_SEARCH_RESULTS, bundle);
    }

    private static void checkoutJourney(Context context, String type, String screenName) {
        final FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);

        finalType = type.equals(EventParameters.EventProgress.END.getValue()) ? EventParameters.EventProgress.END.getValue().concat(mDataManager.getLastRedirection()) : type;

        Bundle bundle = new Bundle();
        bundle.putString(EventParameters.CustomAttributeTypes.TYPE.getValue(), finalType);
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName);
        firebaseAnalytics.logEvent(EventParameters.CustomEventType.CHECKOUT_JOURNEY.getValue(), bundle);
    }

    private static void ccScan(Context context, Bundle bundle, String screenName) {
        final FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName);
        firebaseAnalytics.logEvent(EventParameters.CustomEventType.CC_SCAN.getValue(), bundle);
    }

    private static void purchase(Context context, Bundle bundle, String screenName) {
        final FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.PURCHASE, bundle);

        mDataManager.setLastRedirection(EventParameters.LastRedirection.PAY);
        checkoutJourney(context, EventParameters.EventProgress.END.getValue(), screenName);
    }

    private static void addToCartJourneyViewCart(Context context) {
        if (!mDataManager.hasViewedCart()) {
            addToCartJourney(context, EventParameters.CartJourneyType.VIEW_CART);
            mDataManager.setHasViewedCart(true);
        }
    }

    public static void addToCartJourney(Context context, String type) {
        Bundle bundle = new Bundle();
        bundle.putString(mDataManager.isTablet() ? TABLET_TYPE.getValue() : PHONE_TYPE.getValue(), type);

        final FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        firebaseAnalytics.logEvent(EventParameters.CustomEventType.ADDTOCART_JOURNEY.getValue(), bundle);
    }

    private static void addToCartJourneyViewProductCategory(Context context) {
        if (!mDataManager.hasViewedProductCategory()) {
            addToCartJourney(context, EventParameters.CartJourneyType.VIEW_PRODUCT_CATEGORY);
            mDataManager.setHasViewedProductCategory(true);
        }
    }

    private static void saleBanners(Context context, Bundle bundle, String screenName) {
        final FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName);
        firebaseAnalytics.logEvent(EventParameters.CustomEventType.CV_SALEBANNERS.getValue(), bundle);
    }

    private static void orderTrack(Context context, Bundle bundle, String screenName, String source) {
        final FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName);
        firebaseAnalytics.logEvent(EventParameters.CustomEventType.CV_ORDERTRACK.getValue(), bundle);

        clickOrderEvent(context, source);
    }

    private static void clickOrderEvent(Context context, String source) {
        Bundle bundle = new Bundle();
        bundle.putString(EventParameters.ClickType.ORDER_TRACK, source);

        final FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        firebaseAnalytics.logEvent(EventParameters.CustomEventType.CLICKS.getValue(), bundle);
    }

    private static void clicksEvent(Context context, Bundle bundle, String screenName) {
        final FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName);
        firebaseAnalytics.logEvent(EventParameters.CustomEventType.CLICKS.getValue(), bundle);
    }

    private static void failedTransaction(Context context, Bundle bundle, String screenName) {
        final FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName);
        firebaseAnalytics.logEvent(FAILED_TRANSACTION_EVENT, bundle);

        mDataManager.setLastRedirection(EventParameters.LastRedirection.PAY);
        checkoutJourney(context, EventParameters.EventProgress.END.getValue(), screenName);
    }

    private static void toggleColumnCount(Context context, Bundle bundle, String screenName) {
        final FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName);
        firebaseAnalytics.logEvent(TOGGLE_LIST_COUNT_EVENT, bundle);
    }

    private static void logProductListGridViewPreference(Context context, Bundle bundle, String screenName) {
        final FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName);
        firebaseAnalytics.logEvent(PRODUCT_LIST_GRID_VIEW_PREFERENCE, bundle);
    }

    private static void wishlistItemAddToCart(Context context, String screenName) {
        final FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName);
        firebaseAnalytics.logEvent(WISHLIST_ADDTOCART, bundle);
    }

    private static void wishlistPaymentSuccess(Context context, String screenName) {
        final FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName);
        firebaseAnalytics.logEvent(WISHLIST_PAYMENTSUCCESS, bundle);
    }

    private static void youMayAlsoLikeClick(Context context, String screenName, Bundle bundle) {
        final FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName);
        firebaseAnalytics.logEvent(YOU_MAY_ALSO_LIKE_BANNER_CLICK, bundle);
    }

    private static void bannerClick(Context context, String screenName, Bundle bundle) {
        final FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName);
        firebaseAnalytics.logEvent(BANNER_CLICK, bundle);
    }

    private static void recentlyViewedItemClick(Context context, String screenName, Bundle bundle) {
        final FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName);
        firebaseAnalytics.logEvent(RECENTLY_VIEWED_ITEMS_BANNER_CLICK, bundle);
    }

    public static void ga4AddPaymentInfoEvent(Context context, GA4EventParams eventParams) {
        final FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        firebaseAnalytics.logEvent(EventParameters.CustomEventType.GA4_ADD_PAYMENT_INFO.getValue(), eventParams.toBundle());
    }

    public static void ga4AddShipmentInfoEvent(Context context, GA4EventParams eventParams) {
        final FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        firebaseAnalytics.logEvent(EventParameters.CustomEventType.GA4_ADD_SHIPPING_INFO.getValue(), eventParams.toBundle());
    }

    public static void ga4AddToCartEvent(Context context, GA4EventParams eventParams) {
        final FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        firebaseAnalytics.logEvent(EventParameters.CustomEventType.GA4_ADD_TO_CART.getValue(), eventParams.toBundle());
    }

    public static void ga4AddToWishlistEvent(Context context, GA4EventParams eventParams) {
        final FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        firebaseAnalytics.logEvent(EventParameters.CustomEventType.GA4_ADD_TO_WISHLIST.getValue(), eventParams.toBundle());
    }

    public static void ga4BeginCheckoutEvent(Context context, GA4EventParams eventParams) {
        final FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        firebaseAnalytics.logEvent(EventParameters.CustomEventType.GA4_BEGIN_CHECKOUT.getValue(), eventParams.toBundle());
    }

    public static void ga4PurchaseEvent(Context context, GA4EventParams eventParams) {
        final FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        firebaseAnalytics.logEvent(EventParameters.CustomEventType.GA4_PURCHASE.getValue(), eventParams.toBundle());
    }

    public static void ga4RemoveFromCart(Context context, GA4EventParams eventParams) {
        final FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        firebaseAnalytics.logEvent(EventParameters.CustomEventType.GA4_REMOVE_FROM_CART.getValue(), eventParams.toBundle());
    }

    public static void ga4ViewCartEvent(Context context, GA4EventParams eventParams) {
        final FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        firebaseAnalytics.logEvent(EventParameters.CustomEventType.GA4_VIEW_CART.getValue(), eventParams.toBundle());
    }

    public static void ga4ViewItemEvent(Context context, GA4EventParams eventParams) {
        final FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        firebaseAnalytics.logEvent(EventParameters.CustomEventType.GA4_VIEW_ITEM.getValue(), eventParams.toBundle());
    }

    public static void ga4ViewItemListEvent(Context context, GA4EventParams eventParams) {
        final FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        firebaseAnalytics.logEvent(EventParameters.CustomEventType.GA4_VIEW_ITEM_LIST.getValue(), eventParams.toBundle());
    }

    public static void ga4SelectItemEvent(Context context, GA4EventParams eventParams) {
        final FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        firebaseAnalytics.logEvent(EventParameters.CustomEventType.GA4_SELECT_ITEM.getValue(), eventParams.toBundle());
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
