package au.com.dealsdirect.ui.controller.login;
/*
 * Created by CodeineBot on 6/15/17.
 */

import android.content.Context;
import androidx.annotation.NonNull;

import com.androidnetworking.error.ANError;
import com.google.android.gms.safetynet.SafetyNet;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.login.LoginEmail;
import au.com.dealsdirect.ui.base.AuthenticationBasePresenter;
import au.com.dealsdirect.ui.controller.main.Settings;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

public class LoginPresenter<V extends LoginMvpView> extends AuthenticationBasePresenter<V> implements LoginMvpPresenter<V> {

    @Inject
    public LoginPresenter(
            DataManager dataManager,
            SchedulerProvider schedulerProvider,
            CompositeDisposable compositeDisposable) {

        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public boolean loginViaEmail(Context context, String username, String password) {
        SafetyNet.getClient(context)
                .verifyWithRecaptcha(Settings.getReCaptchaSiteKey())
                .addOnSuccessListener(recaptchaTokenResponse -> {
                    String token = recaptchaTokenResponse.getTokenResult();
                    loginViewEmailWithToken(username, password, token);
                }).addOnFailureListener(e -> {
                    if (!isViewAttached()) {
                        return;
                    }
                    getMvpView().showLoginError(e.getMessage(), false);
                });
        return true;
    }

    private void loginViewEmailWithToken(String username, String password, String token) {
        if (!isViewAttached()) {
            return;
        }
        getMvpView().showLoginStart();
        getCompositeDisposable().add(getDataManager()
                .callLoginViaEmail(
                        new LoginEmail.RequestValue(
                                username,
                                password,
                                getDataManager().getCountryId(),
                                getDataManager().getLanguageId(),
                                token))
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(responseValue -> {
                    if (!isViewAttached()) {
                        return;
                    }

                    if (responseValue.isSuccess()) {
                        getDataManager().acknowledgeAuth(responseValue.getTicket());
                        getMvpView().showLoginSuccessful(responseValue.getTicket(), false);
                    } else {
                        getMvpView().showLoginError(responseValue.getMessage(), false);
                    }
                },
                        throwable ->
                        {
                            if (!isViewAttached()) {
                                return;
                            }

                            getMvpView().hideLoading();
                            getMvpView().showLoginError(throwable.getMessage(), false);

                            // handle load accounts error here
                            if (throwable instanceof ANError) {
                                ANError anError = (ANError) throwable;
                                handleApiError(anError);
                            }
                        }));
    }
}
