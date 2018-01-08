package au.com.dealsdirect.ui.controller.checkout.paymentselect;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.checkout.GetUserPaymentMethods;
import au.com.dealsdirect.data.network.model.checkout.RemoveUserPaymentMethod;
import au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods.PaymentMethod;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by smartwave on 30/06/2017.
 */

public class PaymentSelectPresenter<V extends PaymentSelectMvpView> extends BasePresenter<V> implements PaymentSelectMvpPresenter<V> {

    @Inject
    public PaymentSelectPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void fetchUserPaymentMethods() {
        if (getMvpView() != null) getMvpView().showLoading();
        doApiCallForResponse(getDataManager().callGetUserPaymentMethods(
                new GetUserPaymentMethods.RequestValue()), new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
                if (((GetUserPaymentMethods.ResponseValue) response).getD().getResult()) {
                    getMvpView().showPaymentList(((GetUserPaymentMethods.ResponseValue) response).getUserPaymentMethods());
                } else {
                    getMvpView().onError(((GetUserPaymentMethods.ResponseValue) response).getD().getMessage());
                }
            }
        });
    }

    @Override
    public void removeUserPaymentMethod(PaymentMethod paymentMethod) {
        doApiCallForResponse(getDataManager().callRemoveUserPaymentMethod(new RemoveUserPaymentMethod
                .RequestValue(paymentMethod.getToken(), paymentMethod.getPaymentType())), new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
                getMvpView().showRemovePaymentMethodResult(paymentMethod,
                        ((RemoveUserPaymentMethod.ResponseValue) response).getResult() &&
                                ((RemoveUserPaymentMethod.ResponseValue) response).isAuthenticated(),
                        ((RemoveUserPaymentMethod.ResponseValue) response).getMessage());
            }
        });
    }
}
