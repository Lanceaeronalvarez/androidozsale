package au.com.dealsdirect.ui.controller.checkout.checkout.steps;

import com.androidnetworking.error.ANError;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.cart.CartDetailsMapper;
import au.com.dealsdirect.data.network.ApiEndPoint;
import au.com.dealsdirect.data.network.model.checkout.GetCurrentOrder;
import au.com.dealsdirect.data.network.model.checkout.GetUserPaymentMethods;
import au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods.PaymentMethod;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

public class CheckoutStepsPresenter<V extends CheckoutStepsMvpView> extends BasePresenter<V> implements CheckoutStepsMvpPresenter<V> {

    @Inject
    public CheckoutStepsPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public CartDetailsMapper getCart() {
        return getDataManager().getCart();
    }

    @Override
    public void loadCart(String postcode, String pickupPoint, boolean willForceLoad) {
        loadPaymentMethods();
        if ((getCart() != null && !getCart().isOld()) && !willForceLoad) {
            if (isViewAttached()) {
                getMvpView().showCart();
            }
            return;
        }

        getCompositeDisposable().add(getDataManager()
                .callGetCurrentOrder(new GetCurrentOrder.RequestValue(postcode, pickupPoint, getDataManager().getLanguageId()))
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<GetCurrentOrder.ResponseValue>() {
                    @Override
                    public void accept(@NonNull GetCurrentOrder.ResponseValue responseValue) throws Exception {

                        getDataManager().saveCart(new CartDetailsMapper(responseValue));

                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().showCart();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        if (!isViewAttached()) {
                            return;
                        }
                        getMvpView().onError(throwable.getMessage());

                        // handle load accounts error here
                        if (throwable instanceof ANError) {
                            ANError anError = (ANError) throwable;
                            handleApiError(anError);
                        }
                    }
                })
        );
    }

    @Override
    public boolean checkIsLoggedIn() {
        return getDataManager().isAuthorized();
    }

    private void loadPaymentMethods() {
        if (getDataManager().getSelectedPaymentMethod() != null) {
            return;
        }
        getCompositeDisposable().add(getDataManager()
                .callGetUserPaymentMethods(new GetUserPaymentMethods.RequestValue())
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<GetUserPaymentMethods.ResponseValue>() {
                    @Override
                    public void accept(@NonNull GetUserPaymentMethods.ResponseValue responseValue) throws Exception {
                        if (!isViewAttached()) {
                            return;
                        }

                        if (responseValue.getD().getResult()) {
                            for (int i = 0; i < responseValue.getUserPaymentMethods().size(); i++) {
                                PaymentMethod paymentMethod = responseValue.getUserPaymentMethods().get(i);
                                if (paymentMethod.getPaymentType().contains("VisaCheckout")) {
                                    paymentMethod.setImageUrl(ApiEndPoint.API_VCO_ICON);
                                }
                                responseValue.getUserPaymentMethods().set(i, paymentMethod);
                            }

                            if (getDataManager().getSelectedPaymentMethod() == null) {
                                getDataManager().setSelectedPaymentMethod(responseValue.getLastPaymentMethod());
                            }
                        } else {
                            getMvpView().onError(responseValue.getD().getMessage());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
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
                    }
                })
        );
    }
}
