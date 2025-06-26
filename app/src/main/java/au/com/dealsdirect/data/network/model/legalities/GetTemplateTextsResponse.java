package au.com.dealsdirect.data.network.model.legalities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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

        @SerializedName("_PaymentSchedule")
        @Expose
        private String paymentSchedule;

        @SerializedName("_DeliveryOption_EXPRESS_Title")
        @Expose
        private String deliveryOptionExpressTitle;
        @SerializedName("_DeliveryOption_EXPRESS_Description")
        @Expose
        private String deliveryOptionExpressDescription;
        @SerializedName("_DeliveryOption_STANDARD_Title")
        @Expose
        private String deliveryOptionStandardTitle;
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
        @SerializedName("_KlarnaDescription")
        @Expose
        private String klarnaDescription;

        public String getPaymentSchedule() {
            return paymentSchedule;
        }

        public void setPaymentSchedule(String paymentSchedule) {
            this.paymentSchedule = paymentSchedule;
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

        public String getKlarnaDescription() {
            return klarnaDescription;
        }
    }
}
