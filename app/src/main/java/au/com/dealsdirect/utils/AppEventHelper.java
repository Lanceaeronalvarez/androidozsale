package au.com.dealsdirect.utils;
/*
 * Created by CodeineBot on 11/27/17.
 */

import android.os.Bundle;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;

import java.math.BigDecimal;
import java.util.Currency;

public class AppEventHelper {

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
}
