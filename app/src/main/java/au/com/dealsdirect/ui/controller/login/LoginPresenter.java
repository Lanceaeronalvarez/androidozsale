package au.com.dealsdirect.ui.controller.login;
/*
 * Created by CodeineBot on 6/15/17.
 */

import com.androidnetworking.error.ANError;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.login.LoginEmail;
import au.com.dealsdirect.data.network.model.login.LoginTicket;
import au.com.dealsdirect.data.network.model.login.Logout;
import au.com.dealsdirect.ui.base.AuthenticationBasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

public class LoginPresenter<V extends LoginMvpView> extends AuthenticationBasePresenter<V> implements LoginMvpPresenter<V> {

    @Inject
    public LoginPresenter(
            DataManager dataManager,
            SchedulerProvider schedulerProvider,
            CompositeDisposable compositeDisposable) {

        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public boolean loginViaEmail(String username, String password) {
        getCompositeDisposable().add(getDataManager()
                .callLoginViaEmail(
                        new LoginEmail.RequestValue(
                                username,
                                password,
                                getDataManager().getCountryId(),
                                getDataManager().getLanguageId()))
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<LoginEmail.ResponseValue>() {
                               @Override
                               public void accept(@NonNull LoginEmail.ResponseValue responseValue) throws Exception {
                                   if (!isViewAttached()) {
                                       return;
                                   }

                                   if (responseValue.isSuccess()) {
                                       getDataManager().acknowledgeAuth(responseValue.getTicket());
                                       getMvpView().showLoginSuccessful(responseValue.getTicket());
                                   } else {
                                       getMvpView().showLoginError(responseValue.getMessage());
                                   }
                               }

                           },
                        throwable ->
                        {
                            if (!isViewAttached()) {
                                return;
                            }

                            getMvpView().hideLoading();
                            getMvpView().showLoginError(throwable.getMessage());

                            // handle load accounts error here
                            if (throwable instanceof ANError) {
                                ANError anError = (ANError) throwable;
                                handleApiError(anError);
                            }
                        }));

        return true;
    }

    @Override
    public boolean logout() {
        getCompositeDisposable().add(getDataManager()
                .callLogout(new Logout.RequestValue())
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(responseValue -> {
                    if (!isViewAttached()) {
                        return;
                    }

                    getDataManager().revokeAuth();
                }, throwable -> {
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
                }));

        return true;
    }

    @Override
    public boolean loginTicket(String ticket, String countryId) {
        getCompositeDisposable().add(getDataManager()
                .callLoginTicket(new LoginTicket.RequestValue(ticket, countryId))
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<LoginEmail.ResponseValue>() {
                    @Override
                    public void accept(@NonNull LoginEmail.ResponseValue responseValue) throws Exception {
                        if (!isViewAttached()) {
                            return;
                        }

                        if (responseValue.isSuccess()) {
                            getDataManager().acknowledgeAuth(responseValue.getTicket());
                        } else {
                            logout();
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

                }));

        return true;
    }
}
