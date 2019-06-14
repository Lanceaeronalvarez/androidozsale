package au.com.dealsdirect.ui.controller.register;
/*
 * Created by CodeineBot on 5/15/17.
 */


import android.content.Context;

import com.google.android.gms.safetynet.SafetyNet;

import java.util.HashMap;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.register.RegisterUserRequest;
import au.com.dealsdirect.data.network.model.register.RegisterUserResponse;
import au.com.dealsdirect.service.datacollection.core.DataCollector;
import au.com.dealsdirect.service.datacollection.enums.Events;
import au.com.dealsdirect.ui.base.AuthenticationBasePresenter;
import au.com.dealsdirect.ui.controller.main.Settings;
import au.com.dealsdirect.utils.AppConstants;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

public class RegisterPresenter<V extends RegisterMvpView> extends AuthenticationBasePresenter<V> implements RegisterMvpPresenter<V> {

    @Inject
    public RegisterPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }


    @Override
    public void registerUser(Context context, String firstName, String lastName, String email, String password,
                             boolean tncAccepted, boolean emailsAccepted) {
        getMvpView().showLoading();
        SafetyNet.getClient(context)
                .verifyWithRecaptcha(Settings.getReCaptchaSiteKey())
                .addOnSuccessListener(recaptchaTokenResponse -> {
                    String token = recaptchaTokenResponse.getTokenResult();
                    registerUserWithToken(
                            firstName, lastName,
                            email, password,
                            tncAccepted, emailsAccepted,
                            token);
                }).addOnFailureListener(e -> {
            getMvpView().showLoginError(e.getMessage(), false);
        });
    }

    private void registerUserWithToken(String firstName, String lastName, String email, String password,
                             boolean tncAccepted, boolean emailsAccepted, String token) {

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
                emailsAccepted,
                token);

        if (isGdprDisabled()) registerUserRequest.setToGdprDisabled();

        doApiCallForResponse(getDataManager().callRegister(registerUserRequest), new AppApiCallback(){

            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
                RegisterUserResponse registerUserResponse = (RegisterUserResponse) response;

                if (registerUserResponse.isSuccess()) {
                    getDataManager().acknowledgeAuth(registerUserResponse.getTicket());
                    getMvpView().showLoginSuccessful(registerUserResponse.getTicket(), false);

                    HashMap<String, Object> parameters = new HashMap<>();
                    parameters.put(DataCollector.EventParameters.METHOD, AppConstants.API_REGISTER);
                    DataCollector.logEvent(Events.CompleteRegistration, parameters);
                } else {
                    getMvpView().showLoginError(registerUserResponse.getMessage(), false);
                }

            }

            @Override
            public void onFailure(Throwable t) {
                super.onFailure(t);
                getMvpView().showLoginError(t.getMessage(), false);
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
