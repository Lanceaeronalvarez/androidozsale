package au.com.dealsdirect.service.datacollection.core;

import java.util.HashMap;

/**
 * Created by MTC on 2/20/19.
 */

public class LoggingService {

    public interface LoggingEventData {
        void logEventData(HashMap<String, Object> parameters);
    }

    // Log Data Event for add to cart
    public static class LogAddedToCart implements LoggingEventData {

        private LogDataEvents logAddCartEvent;

        public LogAddedToCart(LogDataEvents logAddCartEvent) {
            this.logAddCartEvent = logAddCartEvent;
        }

        @Override
        public void logEventData(HashMap<String, Object> parameters) {
            logAddCartEvent.LogDataEvents(parameters);
        }
    }

    // Log data event for purchase
    public static class LogPurchase implements LoggingEventData {

        private LogDataEvents logPurchaseEvent;

        public LogPurchase(LogDataEvents logPurchaseEvent) {
            this.logPurchaseEvent = logPurchaseEvent;
        }

        @Override
        public void logEventData(HashMap<String, Object> parameters) {
            logPurchaseEvent.LogDataEvents(parameters);
        }
    }

    // Log data event for registration
    public static class LogRegistration implements LoggingEventData {

        private LogDataEvents logRegistrationEvent;

        public LogRegistration(LogDataEvents logRegistrationEvent) {
            this.logRegistrationEvent = logRegistrationEvent;
        }

        @Override
        public void logEventData(HashMap<String, Object> parameters) {
            logRegistrationEvent.LogDataEvents(parameters);
        }
    }

    // Log data event for invite
    public static class LogInvite implements LoggingEventData {

        private LogDataEvents logInviteEvent;

        public LogInvite(LogDataEvents logInviteEvent) {
            this.logInviteEvent = logInviteEvent;
        }

        @Override
        public void logEventData(HashMap<String, Object> parameters) {
            logInviteEvent.LogDataEvents(parameters);
        }
    }

    // Log data event for add payment info
    public static class LogAddPaymentInfo implements LoggingEventData {

        private LogDataEvents logAddPaymentInfo;

        public LogAddPaymentInfo(LogDataEvents logAddPaymentInfo) {
            this.logAddPaymentInfo = logAddPaymentInfo;
        }

        @Override
        public void logEventData(HashMap<String, Object> parameters) {
            logAddPaymentInfo.LogDataEvents(parameters);
        }
    }

    // Log data for initiate checkout event
    public static class LogInitiateCheckout implements LoggingEventData {

        private LogDataEvents logInitiateCheckout;

        public LogInitiateCheckout(LogDataEvents logInitiateCheckout) {
            this.logInitiateCheckout = logInitiateCheckout;
        }

        @Override
        public void logEventData(HashMap<String, Object> parameters) {
            logInitiateCheckout.LogDataEvents(parameters);
        }
    }

    // Log data for event user
    public static class LogEventUser implements LoggingEventData {

        private LogDataEvents logEventUser;

        public LogEventUser(LogDataEvents logEventUser) {
            this.logEventUser = logEventUser;
        }

        @Override
        public void logEventData(HashMap<String, Object> parameters) {
            logEventUser.LogDataEvents(parameters);
        }
    }

    // Log data for CV app launch
    public static class LogCVAppLaunch implements LoggingEventData {

        private LogDataEvents logCVAppLaunch;

        public LogCVAppLaunch(LogDataEvents logCVAppLaunch) {
            this.logCVAppLaunch = logCVAppLaunch;
        }

        @Override
        public void logEventData(HashMap<String, Object> parameters) {
            logCVAppLaunch.LogDataEvents(parameters);
        }
    }

    // Log Data for login
    public static class LogLogin implements LoggingEventData {

        private LogDataEvents logLoginEvent;

        public LogLogin(LogDataEvents logLoginEvent) {
            this.logLoginEvent = logLoginEvent;
        }

        @Override
        public void logEventData(HashMap<String, Object> parameters) {
            logLoginEvent.LogDataEvents(parameters);
        }
    }

    // Log data for cc scan
    public static class LogCCScan implements LoggingEventData {

        private LogDataEvents logCCScanEvent;

        public LogCCScan(LogDataEvents logCCScanEvent) {
            this.logCCScanEvent = logCCScanEvent;
        }

        @Override
        public void logEventData(HashMap<String, Object> parameters) {
            logCCScanEvent.LogDataEvents(parameters);
        }
    }

    // Log data for sale banners
    public static class LogSaleBanners implements LoggingEventData {

        private LogDataEvents logSaleBannersEvent;

        public LogSaleBanners(LogDataEvents logSaleBannersEvent) {
            this.logSaleBannersEvent = logSaleBannersEvent;
        }

        @Override
        public void logEventData(HashMap<String, Object> parameters) {
            logSaleBannersEvent.LogDataEvents(parameters);
        }
    }

    // Log data for item list
    public static class LogItemList implements LoggingEventData {

        private LogDataEvents logItemListEvent;

        public LogItemList(LogDataEvents logItemListEvent) {
            this.logItemListEvent = logItemListEvent;
        }

        @Override
        public void logEventData(HashMap<String, Object> parameters) {
            logItemListEvent.LogDataEvents(parameters);
        }
    }

    // Log data for item details
    public static class LogItemDetails implements LoggingEventData {

        private LogDataEvents logItemDetailsEvent;

        public LogItemDetails(LogDataEvents logItemDetailsEvent) {
            this.logItemDetailsEvent = logItemDetailsEvent;
        }

        @Override
        public void logEventData(HashMap<String, Object> parameters) {
            logItemDetailsEvent.LogDataEvents(parameters);
        }
    }

    // Log data for track order
    public static class LogTrackOrder implements LoggingEventData {

        private LogDataEvents logTrackOrderEvent;

        public LogTrackOrder(LogDataEvents logTrackOrderEvent) {
            this.logTrackOrderEvent = logTrackOrderEvent;
        }

        @Override
        public void logEventData(HashMap<String, Object> parameters) {
            logTrackOrderEvent.LogDataEvents(parameters);
        }
    }

    // Log data for share
    public static class LogShare implements LoggingEventData {

        private LogDataEvents logShareEvent;

        public LogShare(LogDataEvents logShareEvent) {
            this.logShareEvent = logShareEvent;
        }

        @Override
        public void logEventData(HashMap<String, Object> parameters) {
            logShareEvent.LogDataEvents(parameters);
        }
    }

    // Log data for add to cart journey
    public static class LogAddToCartJourneyViewCart implements LoggingEventData {

        private LogDataEvents logAddToCartJourneyViewCartEvent;

        public LogAddToCartJourneyViewCart(LogDataEvents logAddToCartJourneyViewCartEvent) {
            this.logAddToCartJourneyViewCartEvent = logAddToCartJourneyViewCartEvent;
        }

        @Override
        public void logEventData(HashMap<String, Object> parameters) {
            logAddToCartJourneyViewCartEvent.LogDataEvents(parameters);
        }
    }

    // Log data for view product category
    public static class LogAddToCartJourneyViewProductCategory implements LoggingEventData {

        private LogDataEvents logViewProductCategory;

        public LogAddToCartJourneyViewProductCategory(LogDataEvents logViewProductCategory) {
            this.logViewProductCategory = logViewProductCategory;
        }

        @Override
        public void logEventData(HashMap<String, Object> parameters) {
            logViewProductCategory.LogDataEvents(parameters);
        }
    }

    // Log data for checkout journey
    public static class LogCheckoutJourney implements LoggingEventData {

        private LogDataEvents logCheckoutJourney;

        public LogCheckoutJourney(LogDataEvents logCheckoutJourney) {
            this.logCheckoutJourney = logCheckoutJourney;
        }

        @Override
        public void logEventData(HashMap<String, Object> parameters) {
            logCheckoutJourney.LogDataEvents(parameters);
        }
    }

    // Log data for click event
    public static class LogClickEvent implements LoggingEventData {

        private LogDataEvents logClickEvent;

        public LogClickEvent(LogDataEvents logClickEvent) {
            this.logClickEvent = logClickEvent;
        }

        @Override
        public void logEventData(HashMap<String, Object> parameters) {
            logClickEvent.LogDataEvents(parameters);
        }
    }

    // Log data for search event
    public static class LogSearchEvent implements LoggingEventData {

        private LogDataEvents logSearchEvent;

        public LogSearchEvent(LogDataEvents logSearchEvent) {
            this.logSearchEvent = logSearchEvent;
        }

        @Override
        public void logEventData(HashMap<String, Object> parameters) {
            logSearchEvent.LogDataEvents(parameters);
        }
    }

    // Log data for sale event
    public static class LogSaleEvent implements LoggingEventData {

        private LogDataEvents logSaleEvent;

        public LogSaleEvent(LogDataEvents logSaleEvent) {
            this.logSaleEvent = logSaleEvent;
        }

        @Override
        public void logEventData(HashMap<String, Object> parameters) {
            logSaleEvent.LogDataEvents(parameters);
        }
    }

    public static class LogFailedTransaction implements LoggingEventData {

        private LogDataEvents logFailedTransaction;

        public LogFailedTransaction(LogDataEvents logFailedTransaction) {
            this.logFailedTransaction = logFailedTransaction;
        }

        @Override
        public void logEventData(HashMap<String, Object> parameters) {
            logFailedTransaction.LogDataEvents(parameters);
        }
    }

    // Log data for remove fro cart
    public static class LogRemoveFromCart implements LoggingEventData {

        private LogDataEvents logRemoveFromCartEvent;

        public LogRemoveFromCart(LogDataEvents logRemoveFromCartEvent) {
            this.logRemoveFromCartEvent = logRemoveFromCartEvent;
        }

        @Override
        public void logEventData(HashMap<String, Object> parameters) {
            logRemoveFromCartEvent.LogDataEvents(parameters);
        }
    }

    public static class LogToggleColumn implements LoggingEventData {

        private LogDataEvents logToggleColumn;

        public LogToggleColumn(LogDataEvents logToggleColumn) {
            this.logToggleColumn = logToggleColumn;
        }

        @Override
        public void logEventData(HashMap<String, Object> parameters) {
            logToggleColumn.LogDataEvents(parameters);
        }
    }

    public static class LogProductListGridViewPreference implements LoggingEventData {

        private LogDataEvents logProductListGridViewPreference;

        public LogProductListGridViewPreference(LogDataEvents logProductListGridViewPreference) {
            this.logProductListGridViewPreference = logProductListGridViewPreference;
        }

        @Override
        public void logEventData(HashMap<String, Object> parameters) {
            logProductListGridViewPreference.LogDataEvents(parameters);
        }
    }

    public static class LogWishlistDataEvent implements LoggingEventData {
        private LogDataEvents logWishlistDataEvent;

        public LogWishlistDataEvent(LogDataEvents logWishlistDataEvent) {
            this.logWishlistDataEvent = logWishlistDataEvent;
        }

        @Override
        public void logEventData(HashMap<String, Object> parameters) {
            logWishlistDataEvent.LogDataEvents(parameters);
        }
    }

    public static class LogYouMayAlsoLikeEvent implements LoggingEventData {
        private LogDataEvents logYouMayAlsoLikeEvent;

        public LogYouMayAlsoLikeEvent(LogDataEvents logYouMayAlsoLikeEvent) {
            this.logYouMayAlsoLikeEvent = logYouMayAlsoLikeEvent;
        }

        @Override
        public void logEventData(HashMap<String, Object> parameters) {
            logYouMayAlsoLikeEvent.LogDataEvents(parameters);
        }
    }

    public static class LogBannerClickEvent implements LoggingEventData {
        private LogDataEvents logBannerClickEvent;

        public LogBannerClickEvent (LogDataEvents logBannerClickEvent) {
            this.logBannerClickEvent = logBannerClickEvent;
        }

        @Override
        public void logEventData(HashMap<String, Object> parameters) {
            logBannerClickEvent.LogDataEvents(parameters);
        }
    }

    public static class LogFeatureUsageEvent implements LoggingEventData {
        private LogDataEvents logFeatureUsageEvent;

        public LogFeatureUsageEvent(LogDataEvents logFeatureUsageEvent) {
            this.logFeatureUsageEvent = logFeatureUsageEvent;
        }

        @Override
        public void logEventData(HashMap<String, Object> parameters) {
            logFeatureUsageEvent.LogDataEvents(parameters);
        }
    }
}
