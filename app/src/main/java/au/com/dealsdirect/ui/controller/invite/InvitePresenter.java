package au.com.dealsdirect.ui.controller.invite;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
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
        getCompositeDisposable().add(getDataManager().callGetInvite(getInviteLinkRequest)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<GetInviteResponse>() {
                    @Override
                    public void accept(@NonNull GetInviteResponse response) throws Exception {
                        if(!isViewAttached()) {
                            return;
                        }
                        getMvpView().showInviteLink(response);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        if(!isViewAttached()) {
                            return;
                        }
                        getMvpView().onError(throwable.getMessage());
                    }
                }));
    }

    @Override
    public void setInviteLink(SetInviteRequest setInviteLinkRequest) {
        setInviteLinkRequest.languageId = getDataManager().getLanguageId();
        getCompositeDisposable().add(getDataManager().callSetInvite(setInviteLinkRequest)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<SetInviteResponse>() {
                    @Override
                    public void accept(@NonNull SetInviteResponse response) throws Exception {
                        if(!isViewAttached()) {
                            return;
                        }
                        getMvpView().hideLoading();
                        getMvpView().onInviteLinkSet(response);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        if(!isViewAttached()) {
                            return;
                        }
                        getMvpView().hideLoading();
                        getMvpView().onError(throwable.getMessage());
                    }
                }));
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
