package au.com.dealsdirect.ui.controller.details;

import com.androidnetworking.error.ANError;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.userdetails.GetUserDetailsResponse;
import au.com.dealsdirect.data.network.model.userdetails.SetUserDetailsRequest;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

/**
 * Created by Paul on 6/20/17.
 */

public class DetailsPresenter<V extends DetailsMvpView> extends BasePresenter<V> implements
        DetailsMvpPresenter<V> {

    @Inject
    public DetailsPresenter(DataManager dataManager, SchedulerProvider schedulerProvider,
                            CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void loadUser(SetUserDetailsRequest setUserDetailsRequest) {
        getCompositeDisposable().add(getDataManager()
                .getLoadUserDetailsApiCall(setUserDetailsRequest)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<GetUserDetailsResponse>() {
                    @Override
                    public void accept(@NonNull GetUserDetailsResponse response) throws Exception {
                        getMvpView().loadDetails(response);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().onError(throwable.getMessage());

                        // handle load accounts error here
                        if (throwable instanceof ANError) {
                            ANError anError = (ANError) throwable;
                            handleApiError(anError);
                        }
                    }
                }));
    }

    @Override
    public void sendUserDetails(String username, String firstname, String lastname,
                                String dateofbirth, boolean gender, String email, String password,
                                String newpassword, String confirmpassword) {

        SetUserDetailsRequest setUserDetailsRequest = new SetUserDetailsRequest();
        setUserDetailsRequest.setUserName(username);
        setUserDetailsRequest.setFirstname(firstname);
        setUserDetailsRequest.setSurname(lastname);
        setUserDetailsRequest.setDateBirth(dateofbirth);
        setUserDetailsRequest.setGender(gender);
        setUserDetailsRequest.setEmail(email);
        setUserDetailsRequest.setPassword(password);
        setUserDetailsRequest.setNewPassword(newpassword);
        setUserDetailsRequest.setConfirmPassword(confirmpassword);
        setUserDetailsRequest.setLanguageID(getDataManager().getLanguageId());

        getCompositeDisposable().add(getDataManager()
                .getSaveUserDetailsApiCall(setUserDetailsRequest)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<GetUserDetailsResponse>() {
                    @Override
                    public void accept(@NonNull GetUserDetailsResponse response) throws Exception {
                        if(response.getResponse().getResult()) {
                            getMvpView().saveUserDetailsSuccess();
                        } else {
                            getMvpView().saveUserDetailsFailed(response.getResponse().getMessage());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().onError(throwable.getMessage());

                        // handle load accounts error here
                        if (throwable instanceof ANError) {
                            ANError anError = (ANError) throwable;
                            handleApiError(anError);
                        }
                    }
                }));
    }

    @Override
    public void saveUser(SetUserDetailsRequest request) {
        request.setLanguageID(getDataManager().getLanguageId());
    }
}
