package au.com.dealsdirect.ui.controller.register;
/*
 * Created by CodeineBot on 5/15/17.
 */


import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

public class RegisterPresenter<V extends RegisterMvpView> extends BasePresenter<V> implements RegisterMvpPresenter<V> {

    @Inject
    public RegisterPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }


    @Override
    public void registerUser() {
//
//        RegisterUserRequest registerUserRequest
//                = new RegisterUserRequest(getDataManager().getLanguageId(),);
//
//        getCompositeDisposable().add(getDataManager()
//                .callRegiser()
//                .subscribeOn(getSchedulerProvider().io())
//                .observeOn(getSchedulerProvider().ui())
//                .subscribe(new Consumer<LoginEmail.ResponseValue>() {
//                    @Override
//                    public void accept(@NonNull LoginEmail.ResponseValue responseValue) throws Exception {
//
//                        if (!isViewAttached()) {
//                            return;
//                        }
//
//                        if(responseValue.isSuccess()){
//                            getDataManager().acknowledgeAuth(responseValue.getTicket());
//                            getMvpView().showLoginSuccessful(responseValue.getTicket());
//                        } else{
//                            getMvpView().showLoginError(responseValue.getMessage());
//                        }
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(@NonNull Throwable throwable) throws Exception {
//                        if (!isViewAttached()) {
//                            return;
//                        }
//
//                        getMvpView().hideLoading();
//                        getMvpView().onError(throwable.getMessage());
//                        getMvpView().showLoginError(throwable.getMessage());
//
//                        // handle load accounts error here
//                        if (throwable instanceof ANError) {
//                            ANError anError = (ANError) throwable;
//                            handleApiError(anError);
//                        }
//                    }
//                }));
//
//        return true;

    }
}
