package au.com.dealsdirect.service.datacollection.registerservices;

import android.os.Bundle;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;

import au.com.dealsdirect.service.datacollection.core.DataCollectionService;
import au.com.dealsdirect.service.datacollection.core.DataCollector;
import au.com.dealsdirect.service.datacollection.core.LoggingService;
import au.com.dealsdirect.service.datacollection.enums.Events;
import au.com.dealsdirect.service.datacollection.events.AddPaymentInfoEvent;
import au.com.dealsdirect.service.datacollection.events.AddedToCartEvent;
import au.com.dealsdirect.service.datacollection.events.InitiateCheckOutEvent;
import au.com.dealsdirect.service.datacollection.events.ItemDetailsDataEvent;
import au.com.dealsdirect.service.datacollection.events.PurchaseDataEvent;
import au.com.dealsdirect.service.datacollection.events.RegistrationDataEvent;
import au.com.dealsdirect.utils.CurrencyUtil;

/**
 * Created by MTC on 2/28/19.
 */

public class FacebookEventService implements DataCollectionService {

    private static FacebookEventService instance;
    public static FacebookEventService getInstance() {
        if (instance == null) {
            instance = new FacebookEventService();
        }
        return instance;
    }

    private FacebookEventService() {
        registerFBEvents();
    }

    public static String getServiceKey() {
        return "facebook";
    }

    public static void registerFBEvents() {

        // register add to cart
        DataCollector.EventRegistry.register(generateEventKey(Events.AddedToCartEvent,getServiceKey()),
                new LoggingService.LogAddedToCart(new AddedToCartEvent() {
                    @Override
                    public void LogDataEvents(HashMap<String, Object> parameters) {
                        addedToCart(String.valueOf(parameters.get(DataCollector.EventParameters.ITEM_ID)),
                                String.valueOf(parameters.get(DataCollector.EventParameters.ITEM_NAME)),
                                (Double) parameters.get(DataCollector.EventParameters.PRICE),
                                String.valueOf(parameters.get(DataCollector.EventParameters.COUNTRY_ID)));
                    }
                }));

        //register purchase
        DataCollector.EventRegistry.register(generateEventKey(Events.PurchaseEvent,getServiceKey()),
                new LoggingService.LogPurchase(new PurchaseDataEvent() {
                    @Override
                    public void LogDataEvents(HashMap<String, Object> parameters) {
                        completedPurchase(String.valueOf(parameters.get(DataCollector.EventParameters.PAYMENT_METHOD_TYPE)),
                                (Integer) parameters.get(DataCollector.EventParameters.NUMBER_OF_ITEMS),
                                (Double) parameters.get(DataCollector.EventParameters.PRICE),
                                String.valueOf(parameters.get(DataCollector.EventParameters.COUNTRY_ID)));
                    }
                }));

        //register registration
        DataCollector.EventRegistry.register(generateEventKey(Events.CompleteRegistration, getServiceKey()),
                new LoggingService.LogRegistration(new RegistrationDataEvent() {
                    @Override
                    public void LogDataEvents(HashMap<String, Object> parameters) {
                        completedRegistration(String.valueOf(parameters.get(DataCollector.EventParameters.METHOD)));
                    }
                }));

        // register add payment info
        DataCollector.EventRegistry.register(generateEventKey(Events.AddPaymentInfo, getServiceKey()),
                new LoggingService.LogAddPaymentInfo(new AddPaymentInfoEvent() {
                    @Override
                    public void LogDataEvents(HashMap<String, Object> parameters) {
                        addedPaymentInfo(String.valueOf(parameters.get(DataCollector.EventParameters.PAYMENT_METHOD_TYPE)));
                    }
                }));

        // register initiate checkout
        DataCollector.EventRegistry.register(generateEventKey(Events.InitiateCheckout, getServiceKey()),
                new LoggingService.LogInitiateCheckout(new InitiateCheckOutEvent() {
                    @Override
                    public void LogDataEvents(HashMap<String, Object> parameters) {
                        initiatedCheckout(String.valueOf(parameters.get(DataCollector.EventParameters.PAYMENT_METHOD_TYPE)),
                                (Integer) parameters.get(DataCollector.EventParameters.NUMBER_OF_ITEMS),
                                (Double) parameters.get(DataCollector.EventParameters.PRICE),
                                String.valueOf(parameters.get(DataCollector.EventParameters.COUNTRY_ID)));
                    }
                }));

        //register item details
        DataCollector.EventRegistry.register(generateEventKey(Events.CVItemDetails, getServiceKey()),
                new LoggingService.LogItemDetails(new ItemDetailsDataEvent() {
                    @Override
                    public void LogDataEvents(HashMap<String, Object> parameters) {
                        viewedContent(String.valueOf(parameters.get(DataCollector.EventParameters.ITEM_ID)),
                                String.valueOf(parameters.get(DataCollector.EventParameters.ITEM_NAME)),
                                (Double) parameters.get(DataCollector.EventParameters.PRICE),
                                String.valueOf(parameters.get(DataCollector.EventParameters.COUNTRY_ID)));
                    }
                }));
    }


    public static String generateEventKey(Events events, String service) {
        return events+"."+service;
    }

    /**
     * Registration tracking for facebook
     * To be called upon successful registration
     */
    public static void completedRegistration(String method) {
        AppEventsLogger logger = AppEventsLogger.newLogger(FacebookSdk.getApplicationContext());


        Bundle parameters = new Bundle();
        parameters.putString(AppEventsConstants.EVENT_PARAM_REGISTRATION_METHOD, method);

        logger.logEvent(AppEventsConstants.EVENT_NAME_COMPLETED_REGISTRATION, parameters);
    }


    /**
     * View content tracking for facebook
     * To be called in success block of GetItemDetails
     */
    public static void viewedContent(String itemId,
                                     String itemName,
                                     double price,
                                     String countryId) {

        AppEventsLogger logger = AppEventsLogger.newLogger(FacebookSdk.getApplicationContext());

        Bundle parameters = new Bundle();
        parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, itemId);
        parameters.putString(AppEventsConstants.EVENT_PARAM_DESCRIPTION, itemName);
        parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "phone");
        parameters.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, CurrencyUtil.getCurrency(countryId));

        logger.logEvent(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT,
                price,
                parameters);
    }

    /**
     * Add to cart tracking for facebook
     * To be called in success block of AddItemToCart
     */
    public static void addedToCart(String itemId,
                                   String itemName,
                                   double price,
                                   String countryId) {

        AppEventsLogger logger = AppEventsLogger.newLogger(FacebookSdk.getApplicationContext());

        Bundle parameters = new Bundle();
        parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, itemId);
        parameters.putString(AppEventsConstants.EVENT_PARAM_DESCRIPTION, itemName);
        parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "phone");
        parameters.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, CurrencyUtil.getCurrency(countryId));

        logger.logEvent(AppEventsConstants.EVENT_NAME_ADDED_TO_CART,
                price,
                parameters);
    }

    /**
     *  Added Payment Info tracking for facebook
     *  On successful payment method creation
     */
    public static void addedPaymentInfo(String paymentMethodType) {

        AppEventsLogger logger = AppEventsLogger.newLogger(FacebookSdk.getApplicationContext());

        Bundle parameters = new Bundle();
        parameters.putString(AppEventsConstants.EVENT_PARAM_DESCRIPTION, paymentMethodType);

        logger.logEvent(AppEventsConstants.EVENT_NAME_ADDED_PAYMENT_INFO, parameters);
    }

    /**
     * Initiated checkout tracking for facebook
     * To be called on user click any payment button
     */
    public static void initiatedCheckout(String paymentType,
                                         int numItems,
                                         double price,
                                         String countryId) {

        AppEventsLogger logger = AppEventsLogger.newLogger(FacebookSdk.getApplicationContext());

        Bundle parameters = new Bundle();
        parameters.putString(AppEventsConstants.EVENT_PARAM_DESCRIPTION, paymentType);
        parameters.putInt(AppEventsConstants.EVENT_PARAM_NUM_ITEMS, numItems);
        parameters.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, CurrencyUtil.getCurrency(countryId));

        logger.logEvent(AppEventsConstants.EVENT_NAME_INITIATED_CHECKOUT,
                price,
                parameters);
    }

    /**
     * Completed purchase tracking for facebook
     * Should be called on success of CreatePaymentTransaction
     */
    public static void completedPurchase(String paymentType,
                                         int numItems,
                                         double price,
                                         String countryId) {

        AppEventsLogger logger = AppEventsLogger.newLogger(FacebookSdk.getApplicationContext());

        Bundle parameters = new Bundle();
        parameters.putString(AppEventsConstants.EVENT_PARAM_DESCRIPTION, paymentType);
        parameters.putInt(AppEventsConstants.EVENT_PARAM_NUM_ITEMS, numItems);

        logger.logPurchase(BigDecimal.valueOf(price),
                Currency.getInstance(CurrencyUtil.getCurrency(countryId)),
                parameters);

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
