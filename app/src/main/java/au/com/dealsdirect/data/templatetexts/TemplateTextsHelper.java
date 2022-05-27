package au.com.dealsdirect.data.templatetexts;

import au.com.dealsdirect.data.network.model.legalities.GetTemplateTextsResponse;

public interface TemplateTextsHelper {

    TemplateTextsRepository getTemplateTextsRepository();

    void setTemplateTextsSource(GetTemplateTextsResponse.GetTemplateTextsValue source);

    interface TemplateTextsRepository {

        String getCheckoutMyPayPayExceedLimit();

        String getCheckoutMyPayPayInvalidPaymentMethod();

        String getCheckoutMyPayPayOutOfRangeMobileApp();

        String getCheckoutMyPayPayOutUpToMobileApp();

        String getCheckoutMyPayPayUntrusted();

        String getMyPayDetailsMobileApp();

        String getOurPayThankYouTextMobileApp();

        String getOurPayTC_text();

        String getOurPayTCValidationFailed();

        String getPaymentSchedule();

        String getDeliveryOptionOPSFree();

        String getDeliveryOptionOPSTitle();

        String getDeliveryOptionOPSDescription();

        String getDeliveryOptionExpressTitle();

        String getDeliveryOptionExpressDescription();

        String getDeliveryOptionStandardTitle();

        String getDeliveryOptionOPSDescriptionRemaining();

        String getDeliveryOptionOPSInfoBeforePurchase();

        String getDeliveryOptionOPSInfoBeforeFreeDelivery();

        String getDeliveryOptionOPSTncHeader();

        String getDeliveryOptionOPSTncBody();

        String getPersonalisationValidation();

        String getConsentContinueText();

        String getConsentWithTCText();

        String getConsentWithEmailsText();

        String getConsentWithRegistrationTermsWarning();

        String getConsentShortTextPTNameV1();

        String getConsentFullTextPTNameV1();

        String getTermsAndConditionsText();

        String getVoucherNew();

        String getVoucherAlreadySpent();

        String getVoucherExpiringSoon();

        String getVoucherExpired();

        String getVoucherPending();

        String getShippingRulesHover();

        String getShippingRulesHoverTitle();

        String getImpossibleToDeliverAtLocation();

        String getUnavailable();

        String getCalculate();

        String getImpossibleToDeliverAtLocationMessage();

        String getAgeRestrictedText();

        String getPleaseConfirmAgeRestrictedText();

        String getKlarnaDescription();

    }
}
