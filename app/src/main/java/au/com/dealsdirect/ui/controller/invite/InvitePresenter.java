package au.com.dealsdirect.ui.controller.invite;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.invite.GetInviteRequest;
import au.com.dealsdirect.data.network.model.invite.GetInviteResponse;
import au.com.dealsdirect.data.network.model.invite.SetInviteRequest;
import au.com.dealsdirect.data.network.model.invite.SetInviteResponse;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

/**
 * dp Created by Admin on 6/6/17.
 */

public class InvitePresenter<V extends InviteMvpView> extends BasePresenter<V> implements
        InviteMvpPresenter<V> {

    @Inject
    public InvitePresenter(DataManager dataManager, SchedulerProvider schedulerProvider,
            CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void getInviteLink(GetInviteRequest getInviteLinkRequest) {
        getInviteLinkRequest.countryId = getDataManager().getCountryId();
        getInviteLinkRequest.languageId = getDataManager().getLanguageId();
        doApiCallForResponse(getDataManager().callGetInvite(getInviteLinkRequest), new AppApiCallback(){
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
                GetInviteResponse getInviteResponse = (GetInviteResponse) response;
                if(getInviteResponse.getD().isAuthenticated()) {
                    getMvpView().showInviteLink((GetInviteResponse) response);
                }
            }
        });
    }

    @Override
    public void setInviteLink(SetInviteRequest setInviteLinkRequest) {
        setInviteLinkRequest.languageId = getDataManager().getLanguageId();
        doApiCallForResponse(getDataManager().callSetInvite(setInviteLinkRequest), new AppApiCallback(){
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
                getMvpView().onInviteLinkSet((SetInviteResponse) response);
            }
        });
    }

    @Override
    public String getFollowUsFbLink() {
        return getDataManager().getFollowUsFbLink();
    }

    @Override
    public String getFollowUsTwitterLink() {
        return getDataManager().getFollowUsTwitterLink();
    }

    @Override
    public void start() {
        GetInviteRequest getInviteRequest = new GetInviteRequest();
        getInviteRequest.countryId = getDataManager().getCountryId();
        getInviteRequest.languageId = getDataManager().getLanguageId();

        getInviteLink(getInviteRequest);
    }
}
