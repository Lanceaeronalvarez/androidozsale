package au.com.dealsdirect.ui.controller.checkout;

import android.util.Log;

import com.androidnetworking.error.ANError;
import com.mysale.genie.utility.RxBus;

import java.util.ArrayList;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.checkout.GetCurrentOrder;
import au.com.dealsdirect.data.network.model.checkout.GetUserPaymentMethods;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.ui.base.MvpView;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

/**
 * dp Created by Admin on 6/6/17.
 */

public class CheckoutPresenter<V extends CheckoutMvpView> extends BasePresenter<V> implements
        CheckoutMvpPresenter<V> {

    private boolean mFetchCartFinished = false;
    private boolean mFetchUserPaymentMethodsFinished = false;

    @Inject
    public CheckoutPresenter(DataManager dataManager, SchedulerProvider schedulerProvider,
            CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }


    @Override
    public void start() {
        if(!isCartAlreadyLoadedOnce()) {
            getMvpView().showLoading();
        }
        fetchCartDetails();
        fetchUserPaymentMethods();
    }

    @Override
    public void fetchCartDetails() {
        getCompositeDisposable().add(getDataManager()
                .callGetCurrentOrder(new GetCurrentOrder.RequestValue(getDataManager().getLanguageId()))
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<GetCurrentOrder.ResponseValue>() {
                    @Override
                    public void accept(@NonNull GetCurrentOrder.ResponseValue responseValue) throws Exception {
                        if (!isViewAttached()) {
                            return;
                        }
                        getMvpView().hideLoading();

                        if (responseValue.getD().getResult()){
                            updateCart(responseValue);
                            mFetchCartFinished = true;
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

    @Override
    public void fetchUserPaymentMethods() {
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

                        getMvpView().hideLoading();

                        if (!responseValue.getD().isAuthenticated()) {
                            getMvpView().triggerLoginTicket();
                        }

                        if (responseValue.getD().getResult()) {
                            getMvpView().setPaymentList(responseValue.getUserPaymentMethods());
                            getMvpView().showPaymentDetails(responseValue.getD().getValue().getLastPaymentMethod());
                            mFetchUserPaymentMethodsFinished = true;
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

    @Override
    public void fetchAdjustItemQuantity(String url, String itemID) {

    }

    @Override
    public boolean isCartAlreadyLoadedOnce(){
        return mFetchCartFinished && mFetchUserPaymentMethodsFinished;
    }

    @Override
    public void resetIsCartAlreadyLoaded(){
        mFetchCartFinished = false;
        mFetchUserPaymentMethodsFinished = false;
    }

    @Override
    public boolean checkIsLoggedIn() {
        return getDataManager().isAuthorized();
    }

    private void updateCart(GetCurrentOrder.ResponseValue response) {

        if(!isViewAttached()){
            return;
        }

        if (!response.getD().isAuthenticated()) {
            getMvpView().triggerLoginTicket();
        }

        if (response.getD().getResult()) {

            if (!response.getD().getValue().isEmpty()) {

//                try {
//                    GCartUtil.setValueToCart(response.d.Value.itemsCount);
//                } catch (NullPointerException e) { //Adjust quantity no itemsCount key
//                    e.printStackTrace();
//                }

                getMvpView().showCartDetails(response.getD().getValue().getItems());

                getMvpView().showAddressDetails(response.getD().getValue().getDeliveryAddress(), response.getD().getValue().getDecorationInfoList());

                getMvpView().showVoucherDetails(response.getD().getValue().getVouchers());

                getMvpView().showSummaryDetails(response.getD().getValue().getSummary());
            } else {
//                GCartUtil.setValueToCart(0);

                getMvpView().showCartDetails(new ArrayList<>());
            }
//            RxBus.instance().post("update_cart_items_immediate");
        } else {
            getMvpView().onError(response.getD().getMessage());
        }
    }

}
