package au.com.dealsdirect.data.templatetexts;

import au.com.dealsdirect.data.network.model.legalities.GetTemplateTextsResponse;

public interface TemplateTextsHelper {

    TemplateTextsRepository getTemplateTextsRepository();

    void setTemplateTextsSource(GetTemplateTextsResponse.GetTemplateTextsValue source);

    interface TemplateTextsRepository {

        String getPaymentSchedule();

        String getDeliveryOptionExpressTitle();

        String getDeliveryOptionExpressDescription();

        String getDeliveryOptionStandardTitle();

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
