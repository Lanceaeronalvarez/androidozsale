package au.com.dealsdirect.data.templatetexts;

import javax.inject.Inject;

import au.com.dealsdirect.data.network.model.legalities.GetTemplateTextsResponse;

public class AppTemplateTextsHelper implements TemplateTextsHelper {

    private AppTemplateTextsRepository repository;

    @Inject
    public AppTemplateTextsHelper() {
    }

    @Override
    public TemplateTextsRepository getTemplateTextsRepository() {
        return repository;
    }

    @Override
    public void setTemplateTextsSource(GetTemplateTextsResponse.GetTemplateTextsValue source) {
        repository = new AppTemplateTextsRepository(source);
    }

    public class AppTemplateTextsRepository implements TemplateTextsRepository {
        private final GetTemplateTextsResponse.GetTemplateTextsValue source;

        public AppTemplateTextsRepository(GetTemplateTextsResponse.GetTemplateTextsValue source) {
            this.source = source;
        }

        @Override
        public String getPaymentSchedule() {
            return source.getPaymentSchedule();
        }

        @Override
        public String getDeliveryOptionExpressTitle() {
            return source.getDeliveryOptionExpressTitle();
        }

        @Override
        public String getDeliveryOptionExpressDescription() {
            return source.getDeliveryOptionExpressDescription();
        }

        @Override
        public String getDeliveryOptionStandardTitle() {
            return source.getDeliveryOptionStandardTitle();
        }

        @Override
        public String getPersonalisationValidation() {
            return source.getPersonalisationValidation();
        }

        @Override
        public String getConsentContinueText() {
            return source.getConsentContinueText();
        }

        @Override
        public String getConsentWithTCText() {
            return source.getConsentWithTCText();
        }

        @Override
        public String getConsentWithEmailsText() {
            return source.getConsentWithEmailsText();
        }

        @Override
        public String getConsentWithRegistrationTermsWarning() {
            return source.getConsentWithRegistrationTermsWarning();
        }

        @Override
        public String getConsentShortTextPTNameV1() {
            return source.getConsentShortTextPTNameV1();
        }

        @Override
        public String getConsentFullTextPTNameV1() {
            return source.getConsentFullTextPTNameV1();
        }

        @Override
        public String getTermsAndConditionsText() {
            return source.getTermsAndConditionsText();
        }

        @Override
        public String getVoucherNew() {
            return source.getVoucherNew();
        }

        @Override
        public String getVoucherAlreadySpent() {
            return source.getVoucherAlreadySpent();
        }

        @Override
        public String getVoucherExpiringSoon() {
            return source.getVoucherExpiringSoon();
        }

        @Override
        public String getVoucherExpired() {
            return source.getVoucherExpired();
        }

        @Override
        public String getVoucherPending() {
            return source.getVoucherPending();
        }

        @Override
        public String getShippingRulesHover() {
            return source.getShippingRulesHover();
        }

        @Override
        public String getShippingRulesHoverTitle() {
            return source.getShippingRulesHoverTitle();
        }

        @Override
        public String getImpossibleToDeliverAtLocation() {
            return source.getImpossibleToDeliverAtLocation();
        }

        @Override
        public String getUnavailable() {
            return source.getUnavailable();
        }

        @Override
        public String getCalculate() {
            return source.getCalculate();
        }

        @Override
        public String getImpossibleToDeliverAtLocationMessage() {
            return source.getImpossibleToDeliverAtLocationMessage();
        }

        @Override
        public String getAgeRestrictedText() {
            return source.getAgeRestrictedText();
        }

        @Override
        public String getPleaseConfirmAgeRestrictedText() {
            return source.getPleaseConfirmAgeRestrictedText();
        }

        @Override
        public String getKlarnaDescription() {
            return source.getKlarnaDescription();
        }
    }


}
