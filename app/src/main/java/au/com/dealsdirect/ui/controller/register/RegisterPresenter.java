package au.com.dealsdirect.ui.controller.register;
/*
 * Created by CodeineBot on 5/15/17.
 */


import android.support.annotation.NonNull;

import com.androidnetworking.error.ANError;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.register.RegisterUserRequest;
import au.com.dealsdirect.data.network.model.register.RegisterUserResponse;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

public class RegisterPresenter<V extends RegisterMvpView> extends BasePresenter<V> implements RegisterMvpPresenter<V> {

    @Inject
    public RegisterPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }


    @Override
    public void registerUser(String firstName, String lastName, String email, String password) {

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
                        false
                        );

        getCompositeDisposable().add(getDataManager()
                .callRegiser(registerUserRequest)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new io.reactivex.functions.Consumer<RegisterUserResponse>() {
                    @Override
                    public void accept(RegisterUserResponse registerUserResponse) {
                        if (!isViewAttached()) {
                            return;
                        }

                        if(registerUserResponse.isSuccess()){
                            getDataManager().acknowledgeAuth(registerUserResponse.getTicket());
                            getMvpView().showRegisterSuccessful(registerUserResponse.getTicket());
                        } else{
                            getMvpView().showRegisterError(registerUserResponse.getMessage());
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
                        getMvpView().showRegisterError(throwable.getMessage());

                        // handle load accounts error here
                        if (throwable instanceof ANError) {
                            ANError anError = (ANError) throwable;
                            handleApiError(anError);
                        }
                    }
                }));

    }
}
