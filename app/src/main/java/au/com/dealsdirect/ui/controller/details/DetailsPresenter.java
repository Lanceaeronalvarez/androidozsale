package au.com.dealsdirect.ui.controller.details;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.userdetails.GetUserDetailsResponse;
import au.com.dealsdirect.data.network.model.userdetails.SetUserDetailsRequest;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

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
        doApiCallForResponse(getDataManager().getLoadUserDetailsApiCall(setUserDetailsRequest), new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
                getMvpView().loadDetails((GetUserDetailsResponse) response);
            }
        });
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

        doApiCallForResponse(getDataManager().getSaveUserDetailsApiCall(setUserDetailsRequest), new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
                if (((GetUserDetailsResponse) response).getResponse().getResult()) {
                    getMvpView().saveUserDetailsSuccess();
                } else {
                    getMvpView().saveUserDetailsFailed(((GetUserDetailsResponse) response).getResponse().getMessage());
                }
            }
        });
    }

    @Override
    public void saveUser(SetUserDetailsRequest request) {
        request.setLanguageID(getDataManager().getLanguageId());
    }
}
