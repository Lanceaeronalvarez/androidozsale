package au.com.dealsdirect.ui.controller.checkout.checkout;

import android.util.Log;

import com.androidnetworking.error.ANError;

import java.util.ArrayList;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.checkout.AdjustOrderItem;
import au.com.dealsdirect.data.network.model.checkout.GetCurrentOrder;
import au.com.dealsdirect.data.network.model.checkout.GetUserPaymentMethods;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Value;
import au.com.dealsdirect.service.ourpay.Ourpay;
import au.com.dealsdirect.service.ourpay.OurpayPhoneVerification;
import au.com.dealsdirect.service.ourpay.OurpayState;
import au.com.dealsdirect.service.ourpay.OurpayStateManager;
import au.com.dealsdirect.service.ourpay.OurpayUtils;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.CartUtil;
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
    private Ourpay ourpay;
    private boolean isPaymentsCalled = false;
    private boolean isCartDetailsCalled = false;

    @Inject
    public CheckoutPresenter(DataManager dataManager, SchedulerProvider schedulerProvider,
            CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }


    @Override
    public void start() {
        if(!isCartAlreadyLoadedOnce() && isViewAttached()) {
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

                        updateCart(responseValue);
                        mFetchCartFinished = true;

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
                            isPaymentsCalled = true;

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
        getCompositeDisposable().add(getDataManager()
                .callAdjustQuantityOrderItem(url,new AdjustOrderItem.RequestValue(itemID,getDataManager().getLanguageId()))
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<GetCurrentOrder.ResponseValue>() {
                    @Override
                    public void accept(@NonNull GetCurrentOrder.ResponseValue responseValue) throws Exception {
                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().hideLoading();

                        if(responseValue.getD().getResult()) {
                            if (url.equalsIgnoreCase("IncreaseOrderItem")) {
                                CartUtil.addValueToCart(1);
                            } else {
                                CartUtil.addValueToCart(-1);
                            }

                            getMvpView().updateCheckoutBadge();
                        }
                        updateCart(responseValue);
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

    @Override
    public void generateOurpay(Value value) {
        ourpay = new Ourpay();
        ourpay.setState(OurpayState.ONCART);

        try {

            ourpay.setUserAmount(value.getSummary().total);

            /* default */
            ourpay.setCanUse(value.getMyPayDetails().enabled);
            ourpay.setErrorCode(value.getMyPayDetails().reasonCode);
            ourpay.setTermsAndConditionsCheckboxState(value.getMyPayDetails().getTermsAndConditions());
            ourpay.setMinAmount(value.getMyPayDetails().getPaymentConditions().minAmountThreshold);
            ourpay.setMaxAmount(value.getMyPayDetails().getPaymentConditions().maxAmountThreshold);

            if (value.getMyPayDetails().getPaymentSchemeDescription()!=null){

            }
            ourpay.setDetails(value.getMyPayDetails().getPaymentSchemeDescription());


            /* specifics */
            try {
                ourpay.setAmount(value.myPayDetails.getAmount());
            } catch (Exception e){
                ourpay.setAmount(0);
            }

            try {
                ourpay.setBillingPeriod(OurpayUtils.convertDaysToWeeks(value.getMyPayDetails().getBillingPeriod().getDays()));
            } catch (Exception e){
                ourpay.setBillingPeriod(0);
            }

            try {
                ourpay.setTransactionCount(value.myPayDetails.getTransactionCount());
            } catch (Exception e){
                ourpay.setTransactionCount(0);
            }

            try {
                ourpay.setPlannedTransactions(value.getMyPayDetails().getBillingAgreement().getPlannedTransactions());
            } catch (Exception e){
                ourpay.setPlannedTransactions(null);
                ourpay.setState(ourpay.getState() | OurpayState.ERROR);
            }

            try {
                OurpayPhoneVerification ourpayPhoneVerification = new OurpayPhoneVerification();
                ourpayPhoneVerification.setRequired(value.getPhoneVerification().isRequired);
                ourpay.setOurpayPhoneVerification(value.getPhoneVerification());
            } catch (Exception e){
                e.printStackTrace();
            }

            OurpayStateManager.setDetails(ourpay, getDataManager().getIsMyPayEnabled());

            getMvpView().showMyPayDetails(value, ourpay);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

                Value value = response.getD().getValue();
                isCartDetailsCalled = true;

                getMvpView().storeCartDetails(value);

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
