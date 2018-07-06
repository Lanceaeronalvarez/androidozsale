package au.com.dealsdirect.ui.controller.checkout.addpayment;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Value;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.GetCurrentOrderOurpay;
import au.com.dealsdirect.service.ourpay.Ourpay;
import au.com.dealsdirect.service.ourpay.OurpayPhoneVerification;
import au.com.dealsdirect.service.ourpay.OurpayState;
import au.com.dealsdirect.service.ourpay.OurpayStateManager;
import au.com.dealsdirect.service.ourpay.OurpayUtils;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.AppEventHelper;
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
    public void generateOurpay(Value value) {
        ourpay = new Ourpay();
        ourpay.setState(OurpayState.ONCART);

        assert ourpay != null;

        try {

            if (value!=null)
                ourpay.setTotalAmount(value.getSummary().getTotal());

            GetCurrentOrderOurpay getCurrentOrderOurpay = value.getOurpay();

            /* default */
            ourpay.setCanUse(getCurrentOrderOurpay.getSettings().getIsOurPayEnabled());
            ourpay.setErrorCode(getCurrentOrderOurpay.getReasonCode());
            ourpay.setTermsAndConditionsCheckboxState(getCurrentOrderOurpay.getSettings().getTermsAndConditions());
            ourpay.setMinAmount(getCurrentOrderOurpay.getPayment().getPaymentConditions().getMinAmountThreshold());
            ourpay.setMaxAmount(getCurrentOrderOurpay.getPayment().getPaymentConditions().getMaxAmountThreshold());

            if (getCurrentOrderOurpay.getSummary().getDescription() != null) {
                ourpay.setDetails(getCurrentOrderOurpay.getSummary().getDescription());
            }

            /* specifics */
            try {
                ourpay.setInitialAmount(getCurrentOrderOurpay.getSummary().getFirstTransactionAmount());
            } catch (Exception e) {
                ourpay.setInitialAmount(0);
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
        AppEventHelper.initiatedCheckout(paymentType, numItems, price, getDataManager().getCountryId());
    }

    @Override
    public boolean isMasterPassEnabled() {
        return getDataManager().isMasterpassEnabled();
    }

    @Override
    public boolean isPaypalCreditEnabled() {
        return getDataManager().isPaypalCreditEnabled();
    }
}
