package au.com.dealsdirect.ui.controller.checkout.checkout;

import android.content.Context;

import com.androidnetworking.error.ANError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.ApiEndPoint;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.agerestriction.SaveAgeRestrictedConsentDataRequest;
import au.com.dealsdirect.data.network.model.checkout.AdjustOrderItem;
import au.com.dealsdirect.data.network.model.checkout.GetCurrentOrder;
import au.com.dealsdirect.data.network.model.checkout.GetUserPaymentMethods;
import au.com.dealsdirect.data.network.model.checkout.SetDeliveryOption;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.GetCurrentOrderOurpay;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Item;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Shipment;
import au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods.PaymentMethod;
import au.com.dealsdirect.data.network.model.events.CommonCheckoutRequest;
import au.com.dealsdirect.data.templatetexts.TemplateTextsHelper;
import au.com.dealsdirect.data.wishlist.WishlistObject;
import au.com.dealsdirect.service.datacollection.core.DataCollector;
import au.com.dealsdirect.service.datacollection.enums.EventTypeId;
import au.com.dealsdirect.service.datacollection.enums.Events;
import au.com.dealsdirect.service.ourpay.Ourpay;
import au.com.dealsdirect.service.ourpay.OurpayPhoneVerification;
import au.com.dealsdirect.service.ourpay.OurpayState;
import au.com.dealsdirect.service.ourpay.OurpayStateManager;
import au.com.dealsdirect.service.ourpay.OurpayUtils;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.ui.controller.main.Settings;
import au.com.dealsdirect.ui.custom.ProductQuantityLayout;
import au.com.dealsdirect.utils.AppConstants;
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

    @Inject
    public CheckoutPresenter(DataManager dataManager, SchedulerProvider schedulerProvider,
                             CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void callCartContent(String postcode) {
        if (!isCartAlreadyLoadedOnce() && isViewAttached()) {
            getMvpView().showLoading();
        }
        fetchCartDetails(postcode);
    }

    private void fetchCartDetails(String postcode) {
        getCompositeDisposable().add(getDataManager()
                .callGetCurrentOrder(new GetCurrentOrder.RequestValue(postcode, getDataManager().getLanguageId()))
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
                        fetchUserPaymentMethods();
                        mFetchCartFinished = true;
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {

                        getMvpView().setCartIsLoading(false);

                        if (!isViewAttached()) {
                            return;
                        }

                        if (!isCartAlreadyLoadedOnce()) {
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
                            for (int i = 0; i < responseValue.getUserPaymentMethods().size(); i++) {
                                PaymentMethod paymentMethod = responseValue.getUserPaymentMethods().get(i);
                                if (paymentMethod.getPaymentType().contains("VisaCheckout")) {
                                    paymentMethod.setImageUrl(ApiEndPoint.API_VCO_ICON);
                                }
                                responseValue.getUserPaymentMethods().set(i, paymentMethod);
                            }
                            getMvpView().showPaymentDetails(responseValue.getD().getValue().getLastPaymentMethod());
                            getMvpView().setPaymentList(responseValue.getUserPaymentMethods());
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
    public void fetchAdjustItemQuantity(String url, String itemID, String postcode, ProductQuantityLayout view) {
        getCompositeDisposable().add(getDataManager()
                .callAdjustQuantityOrderItem(url, new AdjustOrderItem.RequestValue(itemID, postcode, getDataManager().getLanguageId()))
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
                        fetchUserPaymentMethods();
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
    public void generateOurpay(CheckoutDetailsMapper value) {
        ourpay = new Ourpay();
        ourpay.setState(OurpayState.ONCART);

        assert ourpay != null;

        try {

            if (value != null)
                ourpay.setTotalAmount(value.getSummary().getTotal());

            GetCurrentOrderOurpay getCurrentOrderOurpay = value.getOurpay();

            /* default */
            ourpay.setDescription(getCurrentOrderOurpay.getSummary().getDescription());
            ourpay.setCanUse(getCurrentOrderOurpay.getSettings().getIsOurPayEnabled());
            ourpay.setErrorCode(getCurrentOrderOurpay.getReasonCode());
            ourpay.setTermsAndConditionsCheckboxState(getCurrentOrderOurpay.getSettings().getTermsAndConditions());
            ourpay.setMinAmount(getCurrentOrderOurpay.getPayment().getPaymentConditions().getMinAmountThreshold().doubleValue());
            ourpay.setMaxAmount(getCurrentOrderOurpay.getPayment().getPaymentConditions().getMaxAmountThreshold().doubleValue());
            ourpay.setFirstTransactionAmount(getCurrentOrderOurpay.getSummary().getFirstTransactionAmount());
            ourpay.setFirstTransactionText(getCurrentOrderOurpay.getSummary().getFirstTransactionText());
            ourpay.setPlannedTransactionAmount(getCurrentOrderOurpay.getSummary().getPlannedTransactionsAmount());
            ourpay.setPlannedTransactionText(getCurrentOrderOurpay.getSummary().getPlannedTransactionsText());

            if (getCurrentOrderOurpay.getSummary().getDescription() != null) {
                ourpay.setDetails(getCurrentOrderOurpay.getSummary().getDescription());
            }

            /* specifics */
            try {
                ourpay.setInitialAmount(getCurrentOrderOurpay.getSummary().getFirstTransactionAmount());
            } catch (Exception e) {
                ourpay.setInitialAmount(0.0);
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
    public void logInitiateCheckout(Context context, String paymentType, int numItems, double price,
                                    String selectedPaymentType) {
        CommonCheckoutRequest commonCheckoutRequest = new CommonCheckoutRequest();
        commonCheckoutRequest.setEventType(EventTypeId.EVENT_CHECKOUT);
        commonCheckoutRequest.setErrorDescription("");
        commonCheckoutRequest.setResult(0);
        commonCheckoutRequest.setGuestCheckout(false);

        switch (selectedPaymentType) {
            case AppConstants.VCO:
                commonCheckoutRequest.setOperation(DataCollector.EventParameters.Operation.VCO.getValue());
                break;
            case AppConstants.AFTERPAY:
                commonCheckoutRequest.setOperation(DataCollector.EventParameters.Operation.AFTERPAY.getValue());
                break;
            case AppConstants.REGULAR:
                commonCheckoutRequest.setOperation(DataCollector.EventParameters.Operation.REGULAR.getValue());
                break;
            case AppConstants.STRIPE:
                commonCheckoutRequest.setOperation(DataCollector.EventParameters.Operation.STRIPE.getValue());
                break;
            case AppConstants.OURPAY:
                commonCheckoutRequest.setOperation(DataCollector.EventParameters.Operation.OURPAY.getValue());
                break;
            case AppConstants.MASTERPASS:
                commonCheckoutRequest.setOperation(DataCollector.EventParameters.Operation.MASTERPASS.getValue());
                break;
            case AppConstants.PAYPALCREDIT:
                commonCheckoutRequest.setOperation(DataCollector.EventParameters.Operation.PAYPALCREDIT.getValue());
                break;
            case AppConstants.PAYPAL:
                commonCheckoutRequest.setOperation(DataCollector.EventParameters.Operation.PAYPAL.getValue());
                break;
            default: // UNKNOWN
                commonCheckoutRequest.setOperation(DataCollector.EventParameters.Operation.UNKNOWN.getValue());
                break;
        }

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(DataCollector.EventParameters.APP_CONTEXT, context);
        parameters.put(DataCollector.EventParameters.SCREEN_NAME, CheckoutController.class.getSimpleName());
        parameters.put(DataCollector.EventParameters.START_CHECKOUT_VALUE, price);
        parameters.put(DataCollector.EventParameters.START_CHECKOUT_CURRENCY,
                Settings.getSelectedCountry().currencySign);

        parameters.put(DataCollector.EventParameters.PAYMENT_METHOD_TYPE, paymentType);
        parameters.put(DataCollector.EventParameters.NUMBER_OF_ITEMS, numItems);
        parameters.put(DataCollector.EventParameters.PRICE, price);
        parameters.put(DataCollector.EventParameters.COUNTRY_ID, getDataManager().getCountryId());
        parameters.put(DataCollector.EventParameters.START_CHECKOUT_REQUEST, commonCheckoutRequest);

        DataCollector.logEvent(Events.InitiateCheckout, parameters);

        getDataManager().setHasActiveCheckoutSession(true);
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
    public boolean isPaypalEnabled() {
        return getDataManager().isPaypalEnabled();
    }

    @Override
    public boolean isShippingByPostcodeEnabled() {
        return getDataManager().getShippingByPostcodeEnabled();
    }

    private void updateCart(GetCurrentOrder.ResponseValue response) {
        CheckoutDetailsMapper mappedValues = new CheckoutDetailsMapper(response);
        if (!response.getD().isAuthenticated()) {
            getMvpView().triggerLoginTicket();
            return;
        }

        if (response.getD().getResult()) {
            updateCartValues(mappedValues);
        } else {
            getDataManager().setCheckoutHasWishlistItem(false);
            getMvpView().showCartDetails(null);
            getMvpView().onError(response.getD().getMessage());
        }

        getMvpView().showAgeRestriction(mappedValues.isAgeRestricted() == null ? false : mappedValues.isAgeRestricted());

        checkIfCartIsChanged(mappedValues);
    }

    @Override
    public void updateCartValues(CheckoutDetailsMapper mappedValues) {

        if (!isViewAttached()) {
            return;
        }

        getMvpView().updateCheckoutBadge();

        if (mappedValues != null && !mappedValues.isEmpty()) {
            getMvpView().setIsShipmentAvailable(isShipmentAvailable(mappedValues.getShipments()));

            getDataManager().setCheckoutHasWishlistItem(doesItemsContainAWishlistItem(mappedValues.getItems()));

            getMvpView().showAddressDetails(mappedValues.getDeliveryAddress(), mappedValues.getDecorationInfoList());

            getMvpView().showCartDetailsFooter(mappedValues.getDeliveryAddress() != null);

            getMvpView().showCartDetailsPostcode(mappedValues.getDeliveryAddress() != null ?
                    mappedValues.getDeliveryAddress().getPostcode() : null);

            getMvpView().showDeliveryOptions(mappedValues.getDeliveryOptions(), mappedValues.getDeliveryServicePackageDetail());

            getMvpView().storeCartDetails(mappedValues);

            getMvpView().showVoucherDetails(mappedValues.getVouchers());

            getMvpView().showSummaryDetails(mappedValues.getSummary());

            getMvpView().initializeVisaCheckout();

            getMvpView().showCartDetails(mappedValues.getMappedShipments());
        } else {
            getDataManager().setCheckoutHasWishlistItem(false);
            getMvpView().showCartDetails(new ArrayList<>());
        }

        if (getDataManager().isAfterpayEnabled() &&
                mappedValues.getAfterpay() != null &&
                mappedValues.getAfterpay().isAvailableMobileApp()) {
            getMvpView().showAfterpayPanel(
                    mappedValues.getAfterpay().isAvailable(),
                    mappedValues.getAfterpay().getDescription());
        } else {
            getMvpView().hideAfterpayPanel();
        }
    }

    private boolean isShipmentAvailable(List<Shipment> shipments) {
        for (Shipment shipment : shipments) {
            if (!shipment.getShippingAvailability()) {
                return false;
            }
        }
        return true;
    }

    private boolean doesItemsContainAWishlistItem(List<Item> items) {
        for (int i = 0; i < items.size(); i++) {
            String anId = items.get(i).itemID;
            if (anId != null && doesIdMatchAnyIdInWishlist(anId)) {
                return true;
            }
        }
        return false;
    }

    private boolean doesIdMatchAnyIdInWishlist(String anId) {
        List<WishlistObject> wishlist = getDataManager().getWishlist();
        for (int i = 0; i < wishlist.size(); i++) {
            WishlistObject item = wishlist.get(i);
            if (anId.equals(item.getProductId()) ||
                    anId.equals(item.getMasterProductId()) ||
                    anId.equals(item.getSeoId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setDeliveryOption(SetDeliveryOption.OptionParameters setDeliveryOptionParameters, String postcode) {
        SetDeliveryOption setDeliveryOption = new SetDeliveryOption();
        setDeliveryOption.setCountryId(getDataManager().getCountryId());
        setDeliveryOption.setLanguageId(getDataManager().getLanguageId());
        setDeliveryOption.setOptionParameters(setDeliveryOptionParameters);
        setDeliveryOption.setImageSize(0);
        setDeliveryOption.setPostcode(null);

        // postcode will be null as input but will be used as override
        doApiCallForResponse(getDataManager().callSetDeliveryOption(setDeliveryOption), new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
                GetCurrentOrder.ResponseValue responseValue = (GetCurrentOrder.ResponseValue) response;
                if (responseValue.getD().isAuthenticated() && responseValue.getD().getResult()) {
                    updateCart(responseValue);
                    fetchUserPaymentMethods();
                }
            }
        });
    }

    @Override
    public String getAfterpayLightboxImgUrl() {
        return getDataManager().getAfterpayLightboxImageUrl();
    }

    @Override
    public String getAfterpayTermsLink() {
        return getDataManager().getAfterpayTermsLink();
    }

    @Override
    public boolean isVcoEnabled() {
        return getDataManager().getIsVisaCheckoutEnabled();
    }

    @Override
    public String stripePaymentMethodId() {
        return getDataManager().getStripePaymentMethodId();
    }

    @Override
    public void setStripePaymentMethodId(String paymentMethodId) {
        getDataManager().setStripePaymentMethodId(paymentMethodId);
    }

    @Override
    public boolean isStripeEnabled() {
        return getDataManager().isStripeEnabled();
    }

    @Override
    public String getStripePublicKey() {
        return getDataManager().getStripePublicKey();
    }

    private void checkIfCartIsChanged(CheckoutDetailsMapper mappedValues) {
        if (mappedValues == null || mappedValues.getItems() == null) {
            getDataManager().setHasActiveCheckoutSession(false);
            return;
        }
        int newHashCode = mappedValues.getItems().hashCode();
        if (getDataManager().getCartHashCode() != newHashCode) {
            getDataManager().setCartHashCode(newHashCode);
            getDataManager().setHasActiveCheckoutSession(false);
        }
    }

    @Override
    public TemplateTextsHelper.TemplateTextsRepository getTemplateTextsRepository() {
        return getDataManager().getTemplateTextsRepository();
    }

    @Override
    public void saveAgeRestrictionData(String date, String postcode) {
        doApiCallForResponse(getDataManager().callSaveAgeRestrictedConsentData(
                new SaveAgeRestrictedConsentDataRequest(date, postcode, getDataManager().getCountryId())),
                new AppApiCallback() {
                    @Override
                    public void onSuccess(Object response) {
                        super.onSuccess(response);
                    }
                });
    }
}
