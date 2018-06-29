package au.com.dealsdirect.ui.controller.register;
/*
 * Created by CodeineBot on 5/15/17.
 */


import android.support.annotation.NonNull;

import com.androidnetworking.error.ANError;
import com.visa.checkout.Profile;
import com.visa.checkout.PurchaseInfo;
import com.visa.checkout.VisaPaymentSummary;

import java.math.BigDecimal;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.login.LoginVisa;
import au.com.dealsdirect.data.network.model.register.RegisterUserRequest;
import au.com.dealsdirect.data.network.model.register.RegisterUserResponse;
import au.com.dealsdirect.ui.base.AuthenticationBasePresenter;
import au.com.dealsdirect.ui.base.BaseActivity;
import au.com.dealsdirect.ui.base.VisaCheckoutMvpPresenter;
import au.com.dealsdirect.ui.base.VisaCheckoutMvpView;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutMvpView;
import au.com.dealsdirect.ui.controller.main.MainPresenter;
import au.com.dealsdirect.utils.AppConstants;
import au.com.dealsdirect.utils.AppEventHelper;
import au.com.dealsdirect.utils.AppLogger;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

public class RegisterPresenter<V extends RegisterMvpView> extends AuthenticationBasePresenter<V> implements RegisterMvpPresenter<V> {

    @Inject
    public RegisterPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }


    @Override
    public void registerUser(String firstName, String lastName, String email, String password,
                             boolean tncAccepted, boolean emailsAccepted) {

        getMvpView().showLoading();
        RegisterUserRequest registerUserRequest
                = new RegisterUserRequest(
                getDataManager().getLanguageId(),
                getDataManager().getCountryId(),
                1,
                firstName,
                lastName,
                email,
                password,
                "android",
                "",
                "00000000-0000-0000-0000-000000000000",
                tncAccepted,
                emailsAccepted);

        doApiCallForResponse(getDataManager().callRegister(registerUserRequest), new AppApiCallback(){

            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
                RegisterUserResponse registerUserResponse = (RegisterUserResponse) response;

                if (registerUserResponse.isSuccess()) {
                    getDataManager().acknowledgeAuth(registerUserResponse.getTicket());
                    getMvpView().showLoginSuccessful(registerUserResponse.getTicket());
                    AppEventHelper.completedRegistration(AppConstants.API_REGISTER);
                } else {
                    getMvpView().showLoginError(registerUserResponse.getMessage());
                }

            }

            @Override
            public void onFailure(Throwable t) {
                super.onFailure(t);
                getMvpView().showLoginError(t.getMessage());
            }
        });

    }

    @Override
    public String getGdprTemplateTexts(String key) {
        return getDataManager().getConsentTemplateTexts(key);
    }

    @Override
    public boolean getGdprIsChecked(String key) {
        return getDataManager().getAppSettingsConsentIsChecked(key);
    }


}
