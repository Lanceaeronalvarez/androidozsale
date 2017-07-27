package au.com.dealsdirect.ui.controller.checkout.paymentselect;

import com.androidnetworking.error.ANError;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
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
        getCompositeDisposable().add(getDataManager()
                .callGetUserPaymentMethods(new GetUserPaymentMethods.RequestValue())
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(responseValue -> {

                    if (!isViewAttached()) {
                        return;
                    }

                    getMvpView().hideLoading();

                    if (responseValue.getD().getResult()) {
                        getMvpView().showPaymentList(responseValue.getUserPaymentMethods());
                    } else {
                        getMvpView().onError(responseValue.getD().getMessage());
                    }

                }, throwable -> {

                    if (!isViewAttached()) {
                        return;
                    }

                    getMvpView().hideLoading();
                    getMvpView().onError(throwable.getMessage());

                    // handle load accounts error here
                    if (throwable instanceof ANError) {
                        ANError anError = (ANError) throwable;
                        handleApiError(anError);
                    }
                })
        );
    }

    @Override
    public void removeUserPaymentMethod(PaymentMethod paymentMethod) {

        RemoveUserPaymentMethod.RequestValue requestValue = new RemoveUserPaymentMethod
                .RequestValue(paymentMethod.getToken(), paymentMethod.getPaymentType());

        getMvpView().showLoading();

        getCompositeDisposable().add(getDataManager()
                .callRemoveUserPaymentMethod(requestValue)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(responseValue -> {

                    if (!isViewAttached()) {
                        return;
                    }

                    getMvpView().hideLoading();

                    getMvpView().showRemovePaymentMethodResult(paymentMethod,
                            responseValue.getResult() && responseValue.isAuthenticated(),
                            responseValue.getMessage());

                }, throwable -> {

                    if (!isViewAttached()) {
                        return;
                    }

                    getMvpView().hideLoading();
                    getMvpView().onError(throwable.getMessage());

                    // handle load accounts error here
                    if (throwable instanceof ANError) {
                        ANError anError = (ANError) throwable;
                        handleApiError(anError);
                    }
                })
        );
    }
}
