package au.com.dealsdirect.data.network.model.legalities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Paul on 7/14/17.
 */

public class GetTemplateTextsResponse {

    private Response d;

    public Response getResponse() {
        return d;
    }

    public class Response {
        @SerializedName("ScheduledPayment")
        @Expose
        private GetTemplateTextsValue value;

        public GetTemplateTextsValue getValue() {
            return value;
        }
    }

    public class GetTemplateTextsValue {

        @SerializedName("_checkoutMyPayPayExceedLimit")
        @Expose
        private String checkoutMyPayPayExceedLimit;

        @SerializedName("_checkoutMyPayPayInvalidPaymentMethod")
        @Expose
        private String checkoutMyPayPayInvalidPaymentMethod;

        @SerializedName("_checkoutMyPayPayOutOfRangeMobileApp")
        @Expose
        private String checkoutMyPayPayOutOfRangeMobileApp;

        @SerializedName("_checkoutMyPayPayOutUpToMobileApp")
        @Expose
        private String checkoutMyPayPayOutUpToMobileApp;

        @SerializedName("_checkoutMyPayPayUntrusted")
        @Expose
        private String checkoutMyPayPayUntrusted;

        @SerializedName("myPayDetailsMobileApp")
        @Expose
        private String myPayDetailsMobileApp;

        @SerializedName("_OurPayThankYouTextMobileApp")
        @Expose
        private String ourPayThankYouTextMobileApp;

        @SerializedName("_OurPayTC_text")
        @Expose
        private String ourPayTC_text;

        @SerializedName("_OurPayTCValidationFailed")
        @Expose
        private String ourPayTCValidationFailed;

        @SerializedName("_PaymentSchedule")
        @Expose
        private String paymentSchedule;

        public String getCheckoutMyPayPayExceedLimit() {
            return checkoutMyPayPayExceedLimit;
        }

        public void setCheckoutMyPayPayExceedLimit(String checkoutMyPayPayExceedLimit) {
            this.checkoutMyPayPayExceedLimit = checkoutMyPayPayExceedLimit;
        }

        public String getCheckoutMyPayPayInvalidPaymentMethod() {
            return checkoutMyPayPayInvalidPaymentMethod;
        }

        public void setCheckoutMyPayPayInvalidPaymentMethod(String checkoutMyPayPayInvalidPaymentMethod) {
            this.checkoutMyPayPayInvalidPaymentMethod = checkoutMyPayPayInvalidPaymentMethod;
        }

        public String getCheckoutMyPayPayOutOfRangeMobileApp() {
            return checkoutMyPayPayOutOfRangeMobileApp;
        }

        public void setCheckoutMyPayPayOutOfRangeMobileApp(String checkoutMyPayPayOutOfRangeMobileApp) {
            this.checkoutMyPayPayOutOfRangeMobileApp = checkoutMyPayPayOutOfRangeMobileApp;
        }

        public String getCheckoutMyPayPayOutUpToMobileApp() {
            return checkoutMyPayPayOutUpToMobileApp;
        }

        public void setCheckoutMyPayPayOutUpToMobileApp(String checkoutMyPayPayOutUpToMobileApp) {
            this.checkoutMyPayPayOutUpToMobileApp = checkoutMyPayPayOutUpToMobileApp;
        }

        public String getCheckoutMyPayPayUntrusted() {
            return checkoutMyPayPayUntrusted;
        }

        public void setCheckoutMyPayPayUntrusted(String checkoutMyPayPayUntrusted) {
            this.checkoutMyPayPayUntrusted = checkoutMyPayPayUntrusted;
        }

        public String getMyPayDetailsMobileApp() {
            return myPayDetailsMobileApp;
        }

        public void setMyPayDetailsMobileApp(String myPayDetailsMobileApp) {
            this.myPayDetailsMobileApp = myPayDetailsMobileApp;
        }

        public String getOurPayThankYouTextMobileApp() {
            return ourPayThankYouTextMobileApp;
        }

        public void setOurPayThankYouTextMobileApp(String ourPayThankYouTextMobileApp) {
            this.ourPayThankYouTextMobileApp = ourPayThankYouTextMobileApp;
        }

        public String getOurPayTC_text() {
            return ourPayTC_text;
        }

        public void setOurPayTC_text(String ourPayTC_text) {
            this.ourPayTC_text = ourPayTC_text;
        }

        public String getOurPayTCValidationFailed() {
            return ourPayTCValidationFailed;
        }

        public void setOurPayTCValidationFailed(String ourPayTCValidationFailed) {
            this.ourPayTCValidationFailed = ourPayTCValidationFailed;
        }

        public String getPaymentSchedule() {
            return paymentSchedule;
        }

        public void setPaymentSchedule(String paymentSchedule) {
            this.paymentSchedule = paymentSchedule;
        }
    }
}
