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
        @SerializedName("Value")
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

        @SerializedName("_Free")
        @Expose
        private String deliveryOptionOPSFree;
        @SerializedName("_DeliveryOption_OURPAYSELECT_Title")
        @Expose
        private String deliveryOptionOPSTitle;
        @SerializedName("_DeliveryOption_OURPAYSELECT_Description")
        @Expose
        private String deliveryOptionOPSDescription;
        @SerializedName("_DeliveryOption_EXPRESS_Title")
        @Expose
        private String deliveryOptionExpressTitle;
        @SerializedName("_DeliveryOption_EXPRESS_Description")
        @Expose
        private String deliveryOptionExpressDescription;
        @SerializedName("_DeliveryOption_STANDARD_Title")
        @Expose
        private String deliveryOptionStandardTitle;
        @SerializedName("_Ops_description_remaining")
        @Expose
        private String deliveryOptionOPSDescriptionRemaining;
        @SerializedName("_Ops_info_remaining_before_purchase")
        @Expose
        private String deliveryOptionOPSInfoBeforePurchase;
        @SerializedName("_Ops_info_remaining_before_purchase_free_delivery")
        @Expose
        private String deliveryOptionOPSInfoBeforeFreeDelivery;
        @SerializedName("_OurPaySelectTermsAndConditionsHeader")
        @Expose
        private String deliveryOptionOPSTncHeader;
        @SerializedName("_OurPaySelectTermsAndConditionsBody")
        @Expose
        private String deliveryOptionOPSTncBody;
        @SerializedName("_PleaseFillPersonalization")
        @Expose
        private String personalisationValidation;

        @SerializedName("_consentContinueText")
        @Expose
        private String consentContinueText;
        @SerializedName("_consentWithTCText")
        @Expose
        private String consentWithTCText;
        @SerializedName("_consentWithEmailsText")
        @Expose
        private String consentWithEmailsText;
        @SerializedName("_consentWithRegistrationTermsWarning")
        @Expose
        private String consentWithRegistrationTermsWarning;
        @SerializedName("ConsentShortTextPTNameV1")
        @Expose
        private String consentShortTextPTNameV1;
        @SerializedName("ConsentFullTextPTNameV1")
        @Expose
        private String consentFullTextPTNameV1;
        @SerializedName("TermsAndConditions_Text")
        @Expose
        private String termsAndConditionsText;
        @SerializedName("_VoucherNew")
        @Expose
        private String voucherNew;
        @SerializedName("_AlreadySpent")
        @Expose
        private String voucherAlreadySpent;
        @SerializedName("_VoucherExpiringSoon")
        @Expose
        private String voucherExpiringSoon;
        @SerializedName("_VoucherExpired")
        @Expose
        private String voucherExpired;
        @SerializedName("_VoucherPending")
        @Expose
        private String voucherPending;
        @SerializedName("_Shipping_Rules_hover")
        @Expose
        private String shippingRulesHover;
        @SerializedName("_Shipping_Rules_hover_title")
        @Expose
        private String shippingRulesHoverTitle;
        @SerializedName("_ImpossibleToDeliverAtLocation")
        @Expose
        private String impossibleToDeliverAtLocation;
        @SerializedName("_Unavailable")
        @Expose
        private String unavailable;
        @SerializedName("_Calculate")
        @Expose
        private String calculate;
        @SerializedName("_ImpossibleToDeliverAtLocation_Message")
        @Expose
        private String impossibleToDeliverAtLocationMessage;
        @SerializedName("_AgeRestrictedText")
        @Expose
        private String ageRestrictedText;
        @SerializedName("_PleaseConfirmAgeRestrictedText")
        @Expose
        private String pleaseConfirmAgeRestrictedText;


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

        public String getDeliveryOptionOPSFree() {
            return deliveryOptionOPSFree;
        }

        public String getDeliveryOptionOPSTitle() {
            return deliveryOptionOPSTitle;
        }

        public String getDeliveryOptionOPSDescription() {
            return deliveryOptionOPSDescription;
        }

        public String getDeliveryOptionExpressTitle() {
            return deliveryOptionExpressTitle;
        }

        public String getDeliveryOptionExpressDescription() {
            return deliveryOptionExpressDescription;
        }

        public String getDeliveryOptionStandardTitle() {
            return deliveryOptionStandardTitle;
        }

        public String getDeliveryOptionOPSDescriptionRemaining() {
            return deliveryOptionOPSDescriptionRemaining;
        }

        public String getDeliveryOptionOPSInfoBeforePurchase() {
            return deliveryOptionOPSInfoBeforePurchase;
        }

        public String getDeliveryOptionOPSInfoBeforeFreeDelivery() {
            return deliveryOptionOPSInfoBeforeFreeDelivery;
        }

        public String getDeliveryOptionOPSTncHeader() {
            return deliveryOptionOPSTncHeader;
        }

        public String getDeliveryOptionOPSTncBody() {
            return deliveryOptionOPSTncBody;
        }

        public String getPersonalisationValidation() {
            return personalisationValidation;
        }

        public String getConsentContinueText() {
            return consentContinueText;
        }

        public String getConsentWithTCText() {
            return consentWithTCText;
        }

        public String getConsentWithEmailsText() {
            return consentWithEmailsText;
        }

        public String getConsentWithRegistrationTermsWarning() {
            return consentWithRegistrationTermsWarning;
        }

        public String getConsentShortTextPTNameV1() {
            return consentShortTextPTNameV1;
        }

        public String getConsentFullTextPTNameV1() {
            return consentFullTextPTNameV1;
        }

        public String getTermsAndConditionsText() {
            return termsAndConditionsText;
        }

        public String getVoucherNew() {
            return voucherNew;
        }

        public String getVoucherAlreadySpent() {
            return voucherAlreadySpent;
        }

        public String getVoucherExpiringSoon() {
            return voucherExpiringSoon;
        }

        public String getVoucherExpired() {
            return voucherExpired;
        }

        public String getVoucherPending() {
            return voucherPending;
        }

        public String getShippingRulesHover() {
            return shippingRulesHover;
        }

        public String getShippingRulesHoverTitle() {
            return shippingRulesHoverTitle;
        }

        public String getImpossibleToDeliverAtLocation() {
            return impossibleToDeliverAtLocation;
        }

        public String getUnavailable() {
            return unavailable;
        }

        public String getCalculate() {
            return calculate;
        }

        public String getImpossibleToDeliverAtLocationMessage() {
            return impossibleToDeliverAtLocationMessage;
        }

        public String getAgeRestrictedText() {
            return ageRestrictedText;
        }

        public String getPleaseConfirmAgeRestrictedText() {
            return pleaseConfirmAgeRestrictedText;
        }
    }
}
