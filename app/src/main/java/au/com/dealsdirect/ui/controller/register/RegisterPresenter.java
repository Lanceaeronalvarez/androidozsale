package au.com.dealsdirect.ui.controller.register;
/*
 * Created by CodeineBot on 5/15/17.
 */


import android.os.Bundle;
import android.support.annotation.NonNull;

import com.androidnetworking.error.ANError;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.register.RegisterUserRequest;
import au.com.dealsdirect.data.network.model.register.RegisterUserResponse;
import au.com.dealsdirect.ui.base.AuthenticationBasePresenter;
import au.com.dealsdirect.utils.AppConstants;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

public class RegisterPresenter<V extends RegisterMvpView> extends AuthenticationBasePresenter<V> implements RegisterMvpPresenter<V> {

    @Inject
    public RegisterPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }


    @Override
    public void registerUser(String firstName, String lastName, String email, String password,
                             boolean hasReadTermsAndCondition) {

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
                hasReadTermsAndCondition);

        getCompositeDisposable().add(getDataManager()
                .callRegister(registerUserRequest)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new io.reactivex.functions.Consumer<RegisterUserResponse>() {
                    @Override
                    public void accept(RegisterUserResponse registerUserResponse) {
                        if (!isViewAttached()) {
                            return;
                        }

                        if (registerUserResponse.isSuccess()) {
                            getDataManager().acknowledgeAuth(registerUserResponse.getTicket());
                            getMvpView().showLoginSuccessful(registerUserResponse.getTicket());
                            completedRegistration(AppConstants.API_REGISTER);
                        } else {
                            getMvpView().showLoginError(registerUserResponse.getMessage());
                        }
                    }

                }, new io.reactivex.functions.Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) {
                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().hideLoading();
                        getMvpView().onError(throwable.getMessage());
                        getMvpView().showLoginError(throwable.getMessage());

                        // handle load accounts error here
                        if (throwable instanceof ANError) {
                            ANError anError = (ANError) throwable;
                            handleApiError(anError);
                        }
                    }
                }));

    }

    private void completedRegistration(String method) {
        AppEventsLogger logger = AppEventsLogger.newLogger(FacebookSdk.getApplicationContext());

        Bundle parameters = new Bundle();
        parameters.putString(AppEventsConstants.EVENT_PARAM_REGISTRATION_METHOD, method);

        logger.logEvent(AppEventsConstants.EVENT_NAME_COMPLETED_REGISTRATION, parameters);
    }
}
