package au.com.dealsdirect.ui.controller.checkout.paymentselect;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.ApiEndPoint;
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
        if(getDataManager().isAuthorized()) {
            doApiCallForResponse(getDataManager().callGetUserPaymentMethods(
                    new GetUserPaymentMethods.RequestValue()), new AppApiCallback() {
                @Override
                public void onSuccess(Object response) {
                    super.onSuccess(response);
                    if (((GetUserPaymentMethods.ResponseValue) response).getD().getResult()) {
                        for (int i = 0; i < ((GetUserPaymentMethods.ResponseValue) response).getUserPaymentMethods().size(); i++) {
                            PaymentMethod paymentMethod = ((GetUserPaymentMethods.ResponseValue) response).getUserPaymentMethods().get(i);
                            if (paymentMethod.getPaymentType().contains("VisaCheckout")) {
                                paymentMethod.setImageUrl(ApiEndPoint.API_VCO_ICON);
                            }

                            ((GetUserPaymentMethods.ResponseValue) response).getUserPaymentMethods().set(i,paymentMethod);
                        }
                        getMvpView().showPaymentList(((GetUserPaymentMethods.ResponseValue) response).getUserPaymentMethods());
                    } else {
                        getMvpView().onError(((GetUserPaymentMethods.ResponseValue) response).getD().getMessage());
                    }
                }
            });
        }
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

            @Override
            public void onFailure(Throwable t) {
                super.onFailure(t);
                getMvpView().removePaymentFailed();
            }
        });
    }
}
