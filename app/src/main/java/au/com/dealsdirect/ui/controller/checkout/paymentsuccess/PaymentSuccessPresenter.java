package au.com.dealsdirect.ui.controller.checkout.paymentsuccess;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.ui.controller.checkout.paymentselect.PaymentSelectMvpPresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by smartwave on 30/06/2017.
 */

public class PaymentSuccessPresenter<V extends PaymentSuccessMvpView> extends BasePresenter<V> implements PaymentSuccessMvpPresenter<V> {

    public static final int countToShowRatePopup = 2;

    @Inject
    public PaymentSuccessPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void incrementPayCount() {

        int paymentCount = getDataManager().getPaymentCount();

        if ((paymentCount + 1) == countToShowRatePopup) {
            getMvpView().showRatePopUp();

        }
        getDataManager().setPaymentCount(paymentCount + 1);
    }
}
