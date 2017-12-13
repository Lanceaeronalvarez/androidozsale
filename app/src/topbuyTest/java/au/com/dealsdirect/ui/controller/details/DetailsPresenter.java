package au.com.dealsdirect.ui.controller.details;

import android.util.Log;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.userdetails.GetUserDetailsResponse;
import au.com.dealsdirect.data.network.model.userdetails.SetUserDetailsRequest;
import au.com.dealsdirect.data.network.model.userdetails.SetUserDetailsResponse;
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
    public void sendUserDetails(SetUserDetailsRequest userDetailsRequest) {

        userDetailsRequest.setLanguageID(getDataManager().getLanguageId());

        doApiCallForResponse(getDataManager().getSaveUserDetailsApiCall(userDetailsRequest), new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);

                if (((SetUserDetailsResponse) response).getSetUserDetailsResponseValue().isResult()) {
                    getMvpView().saveUserDetailsSuccess();
                } else {
                    getMvpView().saveUserDetailsFailed(((SetUserDetailsResponse) response).getSetUserDetailsResponseValue().getMessage());
                }
            }
        });
    }
}
