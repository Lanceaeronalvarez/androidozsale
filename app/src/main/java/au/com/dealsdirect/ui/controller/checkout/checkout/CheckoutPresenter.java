package au.com.dealsdirect.ui.controller.checkout.checkout;

import android.os.Bundle;

import com.androidnetworking.error.ANError;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;
import com.google.gson.Gson;

import java.util.ArrayList;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.checkout.AdjustOrderItem;
import au.com.dealsdirect.data.network.model.checkout.GetCurrentOrder;
import au.com.dealsdirect.data.network.model.checkout.GetUserPaymentMethods;
import au.com.dealsdirect.data.network.model.checkout.SetDeliveryOption;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Value;
import au.com.dealsdirect.service.ourpay.Ourpay;
import au.com.dealsdirect.service.ourpay.OurpayPhoneVerification;
import au.com.dealsdirect.service.ourpay.OurpayState;
import au.com.dealsdirect.service.ourpay.OurpayStateManager;
import au.com.dealsdirect.service.ourpay.OurpayUtils;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.ui.custom.ProductQuantityLayout;
import au.com.dealsdirect.utils.AppEventHelper;
import au.com.dealsdirect.utils.AppLogger;
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

    //dont remove. mock user payment methods for ourpayselect
    private String mMockUserPaymentMethods = "{\n" +
            "  \"d\": {\n" +
            "    \"IsAuthenticated\": true,\n" +
            "    \"Value\": {\n" +
            "      \"PaymentMethods\": [\n" +
            "        {\n" +
            "          \"PaymentType\": \"MasterCard\",\n" +
            "          \"Description\": \"512345******2346\",\n" +
            "          \"Token\": \"kp7c946\",\n" +
            "          \"ImageUrl\": \"https://assets.braintreegateway.com/payment_method_logo/mastercard.png?environment=production\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"PaymentType\": \"Paypal\",\n" +
            "          \"Description\": \"444433******1111\",\n" +
            "\t\t\t\t  \"Token\": \"3d88dwr\",\n" +
            "\t\t\t\t  \"ImageUrl\": \"https://assets.braintreegateway.com/payment_method_logo/paypal.png?environment=production\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"PaymentType\": \"Masterpass\",\n" +
            "          \"Description\": \"400000******0002\",\n" +
            "\t\t\t\t  \"Token\": \"3d88dwr\",\n" +
            "\t\t\t\t  \"ImageUrl\": \"https://assets.braintreegateway.com/payment_method_logo/mastercard.png?environment=production\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"LastPaidToken\": \"kp7c946\"\n" +
            "    },\n" +
            "    \"Result\": true,\n" +
            "    \"Message\": \"\"\n" +
            "  }\n" +
            "}";

    @Inject
    public CheckoutPresenter(DataManager dataManager, SchedulerProvider schedulerProvider,
                             CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }


    @Override
    public void callCartContent() {
        if (!isCartAlreadyLoadedOnce() && isViewAttached()) {
            getMvpView().showLoading();
        }
        fetchCartDetails();
        fetchUserPaymentMethods();
    }

    @Override
    public void fetchCartDetails() {

//        doApiCallForResponse(getDataManager().callGetCurrentOrder(new GetCurrentOrder.RequestValue(getDataManager().getLanguageId())), new AppApiCallback(){
//            @Override
//            public void onSuccess(Object responseValue) {
//                super.onSuccess(responseValue);
//
//                getMvpView().setCartIsLoading(false);
//                updateCart((GetCurrentOrder.ResponseValue)responseValue);
//                mFetchCartFinished = true;
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                super.onFailure(t);
//                getMvpView().setCartIsLoading(false);
//            }
//        });

        getCompositeDisposable().add(getDataManager()
                .callGetCurrentOrder(new GetCurrentOrder.RequestValue(getDataManager().getLanguageId()))
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<GetCurrentOrder.ResponseValue>() {
                    @Override
                    public void accept(@NonNull GetCurrentOrder.ResponseValue responseValue) throws Exception {
                        getMvpView().setCartIsLoading(false);

                        if (!isViewAttached()) {
                            return;
                        }
                        getMvpView().hideLoading();
                        getMvpView().hideNoNetworkLayout();

                        updateCart(responseValue);
                        mFetchCartFinished = true;

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {

                        getMvpView().setCartIsLoading(false);

                        if (!isViewAttached()) {
                            return;
                        }

                        if(!isCartAlreadyLoadedOnce()){
                            getMvpView().showNoNetworkLayout();
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
        getMvpView().showLoading();
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

                        if (!responseValue.getD().isAuthenticated()) {
                            getMvpView().triggerLoginTicket();
                        }

                        if (responseValue.getD().getResult()) {
                            getMvpView().setPaymentList(responseValue.getUserPaymentMethods());
                            getMvpView().showPaymentDetails(responseValue.getD().getValue().getLastPaymentMethod());
                            getMvpView().hideLoading();
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
    public void fetchAdjustItemQuantity(String url, String itemID, ProductQuantityLayout view) {
        getCompositeDisposable().add(getDataManager()
                .callAdjustQuantityOrderItem(url, new AdjustOrderItem.RequestValue(itemID, getDataManager().getLanguageId()))
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<GetCurrentOrder.ResponseValue>() {
                    @Override
                    public void accept(@NonNull GetCurrentOrder.ResponseValue responseValue) throws Exception {

                        getMvpView().setCartIsLoading(false);

                        if (!isViewAttached()) {
                            return;
                        }

                        view.resetLoaders();
                        updateCart(responseValue);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {

                        getMvpView().setCartIsLoading(false);

                        if (!isViewAttached()) {
                            return;
                        }

                        view.resetLoaders();
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
    public boolean isCartAlreadyLoadedOnce() {
        return mFetchCartFinished && mFetchUserPaymentMethodsFinished;
    }

    @Override
    public void resetIsCartAlreadyLoaded() {
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

        assert ourpay != null;

        try {

            if (value!=null)
                ourpay.setUserAmount(value.getSummary().total);

            /* default */
            ourpay.setCanUse(value.getMyPayDetails().enabled);
            ourpay.setErrorCode(value.getMyPayDetails().reasonCode);
            ourpay.setTermsAndConditionsCheckboxState(value.getMyPayDetails().getTermsAndConditions());
            ourpay.setMinAmount(value.getMyPayDetails().getPaymentConditions().minAmountThreshold);
            ourpay.setMaxAmount(value.getMyPayDetails().getPaymentConditions().maxAmountThreshold);

            if (value.getMyPayDetails().getPaymentSchemeDescription() != null) {
                ourpay.setDetails(value.getMyPayDetails().getPaymentSchemeDescription());
            }

            /* specifics */
            try {
                ourpay.setAmount(value.myPayDetails.getAmount());
            } catch (Exception e) {
                ourpay.setAmount(0);
            }

            try {
                ourpay.setBillingPeriod(OurpayUtils.convertDaysToWeeks(value.getMyPayDetails().getBillingPeriod().getDays()));
            } catch (Exception e) {
                ourpay.setBillingPeriod(0);
            }

            try {
                ourpay.setTransactionCount(value.myPayDetails.getTransactionCount());
            } catch (Exception e) {
                ourpay.setTransactionCount(0);
            }

            try {
                ourpay.setPlannedTransactions(value.getMyPayDetails().getBillingAgreement().getPlannedTransactions());
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
    public void facebookInitiatedCheckout(String paymentType, int numItems, double price) {
        AppEventHelper.initiatedCheckout(paymentType, numItems, price, getDataManager().getCountryId());
    }

    @Override
    public boolean isMasterPassEnabled() {
        return getDataManager().isMasterpassEnabled();
    }

    @Override
    public void updateCart(GetCurrentOrder.ResponseValue response) {

        if (!isViewAttached()) {
            return;
        }

        if (!response.getD().isAuthenticated()) {
            getMvpView().triggerLoginTicket();

            return;
        }

        getMvpView().updateCheckoutBadge();

        if (response.getD().getResult()) {

            if(!response.getD().getValue().isEmpty()) {
                Value value = response.getD().getValue();

                getMvpView().showCartDetails(response.getD().getValue().getItems());

                getMvpView().showAddressDetails(response.getD().getValue().getDeliveryAddress(), response.getD().getValue().getDecorationInfoList());

                getMvpView().showDeliveryOptions(response.getD().getValue().getDeliveryOptions(),response.getD().getValue().getDeliveryServicePackageDetail());

                getMvpView().storeCartDetails(value);

                getMvpView().showVoucherDetails(response.getD().getValue().getVouchers());

                getMvpView().showSummaryDetails(response.getD().getValue().getSummary());
            } else {
                getMvpView().showCartDetails(new ArrayList<>());
            }
        } else {
            getMvpView().showCartDetails(null);
            getMvpView().onError(response.getD().getMessage());
        }

    }

    @Override
    public void setDeliveryOption(SetDeliveryOption.OptionParameters setDeliveryOptionParameters) {
        SetDeliveryOption setDeliveryOption = new SetDeliveryOption();
        setDeliveryOption.setCountryId(getDataManager().getCountryId());
        setDeliveryOption.setLanguageId(getDataManager().getLanguageId());
        setDeliveryOption.setOptionParameters(setDeliveryOptionParameters);
        setDeliveryOption.setImageSize(0);

        doApiCallForResponse(getDataManager().callSetDeliveryOption(setDeliveryOption), new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
                GetCurrentOrder.ResponseValue responseValue = (GetCurrentOrder.ResponseValue) response;
                if (responseValue.getD().isAuthenticated() && responseValue.getD().getResult()) {
                    updateCart(responseValue);
                }
            }
        });
    }

}
