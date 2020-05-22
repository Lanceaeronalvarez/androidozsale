package au.com.dealsdirect.ui.base;

import com.braintreepayments.api.models.VisaCheckoutNonce;
import com.visa.checkout.Profile;
import com.visa.checkout.PurchaseInfo;
import com.visa.checkout.VisaPaymentSummary;

import java.math.BigDecimal;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.login.LoginVisa;
import au.com.dealsdirect.ui.main.PaymentInfo;
import au.com.dealsdirect.utils.AppLogger;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

import static au.com.dealsdirect.ui.base.VisaCheckoutMvpView.VISA_CHECKOUT_LOGIN;
import static au.com.dealsdirect.ui.base.VisaCheckoutMvpView.VISA_CHECKOUT_PAY;

/**
 * Created by smartwave on 13/02/2018.
 */

public class VisaCheckoutPresenter<V extends VisaCheckoutMvpView> extends BasePresenter<V> implements VisaCheckoutMvpPresenter<V> {

    public static final int CYBERSOURCE = 1;
    public static final int BRAINTREE = 2;

    @Inject
    public VisaCheckoutPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void setupVisaCheckout(boolean isFromCheckout) {
        setupVisaCheckoutBraintree(isFromCheckout);
    }


    private void setupVisaCheckoutNative() {
        Profile profile = new Profile.ProfileBuilder(
                getDataManager().getVisaCheckoutApiKey(),
                getDataManager().getVisaCheckoutApiUrl())
                .setProfileName("")
                .setDataLevel(Profile.DataLevel.FULL)
                .setCardBrands(new String[]{
                        Profile.CardBrand.VISA,
                        Profile.CardBrand.MASTERCARD
                })
                .setEnableTokenization(true)
                .build();

        getMvpView().onSetupVisaCheckoutNative(profile);

    }

    private void setupVisaCheckoutBraintree(boolean isFromCheckout) {
        getMvpView().onSetupVisaCheckoutBraintree(getDataManager().getPublicPaymentToken(), getDataManager().getPublicPaymentType(),
                isFromCheckout);
    }

    @Override
    public void loginWithVisaCheckout() {
        getMvpView().setVisaCheckoutActionType(VISA_CHECKOUT_LOGIN);
        switch (getDataManager().getVisaCheckoutProviderType()){
            case CYBERSOURCE:
                getMvpView().onStartVisaCheckoutIntent(new PurchaseInfo.PurchaseInfoBuilder(new BigDecimal("0.00"),
                        getDataManager().getCurrency()).build());
                break;
            case BRAINTREE:
                getMvpView().initializeVisaCheckoutButton(new PurchaseInfo.PurchaseInfoBuilder(new BigDecimal("0.00"),
                        getDataManager().getCurrency()), false);
                break;
        }
    }

    @Override
    public void payWithVisaCheckout(Double cartTotal) {
        getMvpView().setVisaCheckoutActionType(VISA_CHECKOUT_PAY);
        switch (getDataManager().getVisaCheckoutProviderType()){
            case CYBERSOURCE:
                PaymentInfo.setPaymentType(PaymentInfo.VISA_CHECKOUT_CYBERSOURCE);
                getMvpView().onStartVisaCheckoutIntent(new PurchaseInfo.PurchaseInfoBuilder(new BigDecimal(cartTotal),
                        getDataManager().getCurrency()).build());
                break;
            case BRAINTREE:
                PaymentInfo.setPaymentType(PaymentInfo.VISA_CHECKOUT_BRAINTREE);
                getMvpView().initializeVisaCheckoutButton(new PurchaseInfo.PurchaseInfoBuilder(new BigDecimal(cartTotal),
                        getDataManager().getCurrency()), true);
                break;
        }
    }

    @Override
    public void authenticateLoginWithVisaCheckoutNative(VisaPaymentSummary visaPaymentSummary) {
        LoginVisa.RequestValue.Data requestData = new LoginVisa.RequestValue.Data();
        requestData.setCallID(visaPaymentSummary.getCallId());
        requestData.setEncKey(visaPaymentSummary.getEncKey());
        requestData.setEncPaymentData(visaPaymentSummary.getEncPaymentData());
        requestData.setLoginVisaType(getDataManager().getVisaCheckoutProviderType());

        executeLoginVisa(requestData, "");
    }

    @Override
    public void authenticateLoginWithVisaCheckoutBraintree(VisaCheckoutNonce visaCheckoutNonce) {
        LoginVisa.RequestValue.Data requestData = new LoginVisa.RequestValue.Data();
        requestData.setFirstName(visaCheckoutNonce.getUserData().getUserFirstName());
        requestData.setLastName(visaCheckoutNonce.getUserData().getUserLastName());
        requestData.setEmail(visaCheckoutNonce.getUserData().getUserEmail());
        requestData.setCallID(visaCheckoutNonce.getCallId());
        requestData.setPaymentNonce(visaCheckoutNonce.getNonce());
        requestData.setLoginVisaType(getDataManager().getVisaCheckoutProviderType());

        executeLoginVisa(requestData, "");
    }

    @Override
    public void executeLoginVisa(LoginVisa.RequestValue.Data requestData, String password) {
        executeLoginVisa(requestData, password, true, false);
    }

    @Override
    public void executeLoginVisa(LoginVisa.RequestValue.Data requestData, String password,
                                 boolean tncAccepted, boolean emailsAccepted) {
        LoginVisa.RequestValue requestValue = new LoginVisa.RequestValue();
        requestValue.setCountryID(getDataManager().getCountryId());
        requestValue.setLanguageID(getDataManager().getLanguageId());
        requestValue.setInvitedBy("");
        requestValue.setReferredBy("android");
        requestValue.setVoucherID("00000000-0000-0000-0000-000000000000");
        requestValue.setPassword(password);
        requestValue.setData(requestData);
        requestValue.setParameters(tncAccepted, emailsAccepted);

        if (isGdprDisabled()) requestValue.setToGdprDisabled();

        getMvpView().showLoading();
        doApiCallForResponse(getDataManager().callLoginVisaCheckout(requestValue), new AppApiCallback() {
            @Override
            public void onSuccess(Object o) {
                LoginVisa.ResponseValue responseValue = (LoginVisa.ResponseValue) o;
                getMvpView().hideLoading();
                if (!responseValue.getResult()) {
                    getMvpView().onError(responseValue.getMessage());
                } else {
                    if (responseValue.isPasswordRequired()) {
                        AppLogger.d("VC_LoginVisa", "Non existing account");
                        getMvpView().showPasswordVerification(requestData, responseValue.isAccountExists(), responseValue.getEmail());
                    } else if (responseValue.isAuthenticated() && !responseValue.getTicket().isEmpty()) {
                        AppLogger.d("VC_LoginVisa", "Success");

                        getDataManager().setLoginTicket(responseValue.getTicket());
                        getDataManager().acknowledgeAuth(responseValue.getTicket());
                        getMvpView().showLoginVisaSuccess(responseValue.getTicket());
                    }
                }
            }


            @Override
            public void onFailure(Throwable t) {
                getMvpView().onError("An Error Occured");
            }
        });
    }

    @Override
    public boolean isVisaCheckoutEnabled() {
        return getDataManager().getIsVisaCheckoutEnabled();
    }

    @Override
    public void initializeBraintree() {
        getMvpView().initializeBrainTree(getDataManager().getPublicPaymentToken(), getDataManager().getPublicPaymentType());
    }
}
