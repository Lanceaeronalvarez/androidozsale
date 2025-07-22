package au.com.dealsdirect.ui.controller.login;
/*
 * Created by CodeineBot on 6/15/17.
 */

import com.androidnetworking.error.ANError;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.login.LoginEmail;
import au.com.dealsdirect.ui.base.AuthenticationBasePresenter;
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
    public void loginViaEmail(String username, String password, String token) {
        if (!isViewAttached()) {
            return;
        }
        getMvpView().showLoginStart();

        LoginEmail.RequestValue requestValue = new LoginEmail.RequestValue(
                username,
                password,
                getDataManager().getCountryId(),
                getDataManager().getLanguageId(),
                token);

        doApiCallForResponse(getDataManager().callLoginViaEmail(requestValue), new AppApiCallback() {
            @Override
            public void onSuccess(Object o) {
                if (!isViewAttached() || !(o instanceof LoginEmail.ResponseValue)) {
                    return;
                }

                final LoginEmail.ResponseValue responseValue = (LoginEmail.ResponseValue) o;

                if (responseValue.isSuccess()) {
                    getDataManager().acknowledgeAuth(responseValue.getTicket());
                    getMvpView().showLoginSuccessful(responseValue.getTicket(), false);
                } else {
                    getMvpView().showLoginError(responseValue.getMessage(), false);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                if (!isViewAttached()) {
                    return;
                }

                getMvpView().hideLoading();
                getMvpView().showLoginError(t.getMessage(), false);
            }
        });
    }

    public boolean isFacebookLoginEnabled(){
        return getDataManager().getFacebookLoginEnabled();
    }
}
