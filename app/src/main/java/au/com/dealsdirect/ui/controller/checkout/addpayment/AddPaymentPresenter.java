package au.com.dealsdirect.ui.controller.checkout.addpayment;

import java.util.HashMap;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.service.datacollection.core.DataCollector;
import au.com.dealsdirect.service.datacollection.enums.Events;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

public class AddPaymentPresenter<V extends AddPaymentMvpView> extends BasePresenter<V> implements AddPaymentMvpPresenter<V> {

    @Inject
    public AddPaymentPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
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

    @Override
    public boolean isStripe() {
        return getDataManager().isStripeEnabled();
    }
}
