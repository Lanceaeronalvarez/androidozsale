package au.com.dealsdirect.ui.controller.checkout.addpayment;

import java.util.HashMap;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.GetCurrentOrderOurpay;
import au.com.dealsdirect.service.datacollection.core.DataCollector;
import au.com.dealsdirect.service.datacollection.enums.Events;
import au.com.dealsdirect.service.ourpay.Ourpay;
import au.com.dealsdirect.service.ourpay.OurpayPhoneVerification;
import au.com.dealsdirect.service.ourpay.OurpayState;
import au.com.dealsdirect.service.ourpay.OurpayStateManager;
import au.com.dealsdirect.service.ourpay.OurpayUtils;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutDetailsMapper;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by smartwave on 29/06/2017.
 */

public class AddPaymentPresenter<V extends AddPaymentMvpView> extends BasePresenter<V> implements AddPaymentMvpPresenter<V> {

    private Ourpay ourpay;

    @Inject
    public AddPaymentPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void generateOurpay(CheckoutDetailsMapper value) {
        ourpay = new Ourpay();
        ourpay.setState(OurpayState.ONCART);

        assert ourpay != null;

        try {

            if (value != null)
                ourpay.setTotalAmount(value.getSummary().getTotal());

            GetCurrentOrderOurpay getCurrentOrderOurpay = value.getOurpay();

            /* default */
            ourpay.setDescription(getCurrentOrderOurpay.getSummary().getDescription());
            ourpay.setCanUse(getCurrentOrderOurpay.getSettings().getIsOurPayEnabled());
            ourpay.setErrorCode(getCurrentOrderOurpay.getReasonCode());
            ourpay.setTermsAndConditionsCheckboxState(getCurrentOrderOurpay.getSettings().getTermsAndConditions());
            ourpay.setMinAmount(getCurrentOrderOurpay.getPayment().getPaymentConditions().getMinAmountThreshold().doubleValue());
            ourpay.setMaxAmount(getCurrentOrderOurpay.getPayment().getPaymentConditions().getMaxAmountThreshold().doubleValue());

            if (getCurrentOrderOurpay.getSummary().getDescription() != null) {
                ourpay.setDetails(getCurrentOrderOurpay.getSummary().getDescription());
            }

            /* specifics */
            try {
                ourpay.setInitialAmount(getCurrentOrderOurpay.getSummary().getFirstTransactionAmount());
            } catch (Exception e) {
                ourpay.setInitialAmount(0.0);
            }

            try {
                ourpay.setBillingPeriod(OurpayUtils.convertDaysToWeeks(getCurrentOrderOurpay.getPayment().getBillingPeriod()));
            } catch (Exception e) {
                ourpay.setBillingPeriod(0);
            }

            try {
                ourpay.setTransactionCount(getCurrentOrderOurpay.getPayment().getTransactionCount());
            } catch (Exception e) {
                ourpay.setTransactionCount(0);
            }

            try {
                ourpay.setPlannedTransactions(getCurrentOrderOurpay.getPayment().getBillingAgreement().getPlannedTransactions());
            } catch (Exception e) {
                ourpay.setPlannedTransactions(null);
                ourpay.setState(ourpay.getState() | OurpayState.ERROR);
            }

            try {
                OurpayPhoneVerification ourpayPhoneVerification = new OurpayPhoneVerification();
                ourpayPhoneVerification.setRequired(value.getPhoneVerification().isRequired);
                ourpay.setOurpayPhoneVerification(value.getPhoneVerification());
            } catch (Exception e) {
                e.printStackTrace();
            }

            OurpayStateManager.setDetails(ourpay, getDataManager().getIsMyPayEnabled());

            getMvpView().showMyPayDetails(value, ourpay);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isDebug() {
        return getDataManager().isDebugMode();
    }

    @Override
    public void facebookInitiatedCheckout(String paymentType, int numItems, double price) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(DataCollector.EventParameters.PAYMENT_METHOD_TYPE, paymentType);
        parameters.put(DataCollector.EventParameters.NUMBER_OF_ITEMS, numItems);
        parameters.put(DataCollector.EventParameters.PRICE, price);
        parameters.put(DataCollector.EventParameters.COUNTRY_ID, getDataManager().getCountryId());
        DataCollector.logEvent(Events.InitiateCheckout, parameters);
    }

    @Override
    public boolean isMasterPassEnabled() {
        return getDataManager().isMasterpassEnabled();
    }

    @Override
    public boolean isPaypalCreditEnabled() {
        return getDataManager().isPaypalCreditEnabled();
    }

    @Override
    public boolean isPayPalEnabled() {
        return getDataManager().isPaypalEnabled();
    }
}
