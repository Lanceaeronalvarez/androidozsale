package au.com.dealsdirect.ui.controller.returns.currentreturns;
/*
 * Created by dp on 5/15/17.
 */


import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.returns.createreturn.ReturnReceivedRequest;
import au.com.dealsdirect.data.network.model.returns.createreturn.ReturnReceivedSatisfactionResponse;
import au.com.dealsdirect.data.network.model.returns.currentreturn.CurrentReturn;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class CurrentReturnsPresenter<V extends CurrentReturnsMvpView> extends BasePresenter<V> implements CurrentReturnsMvpPresenter<V> {

    private Disposable setReturnReceivedRequestDisposable = null;

    @Inject
    public CurrentReturnsPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void loadCurrentReturns() {
        doApiCallForResponse(getDataManager().callGetCurrentReturns(), new AppApiCallback() {
            @Override
            public void onSuccess(List<?> response) {
                super.onSuccess(response);
                getMvpView().showCurrentReturns((List<CurrentReturn>) response);
            }
        });
    }

    @Override
    public void callSetReturnReceived(ReturnReceivedRequest receivedRequest) {
        cancelPreviousSetReturnReceivedRequest();
        setReturnReceivedRequestDisposable = doApiCallForResponse(
                getDataManager().callSetReturnReceived(receivedRequest), new AppApiCallback() {
                    @Override
                    public void onSuccess(Object response) {
                        super.onSuccess(response);
                    }
                });
    }

    @Override
    public void callSetReturnNotReceived(ReturnReceivedRequest receivedRequest) {
        cancelPreviousSetReturnReceivedRequest();
        setReturnReceivedRequestDisposable = doApiCallForResponse(
                getDataManager().callSetReturnNotReceived(receivedRequest), new AppApiCallback() {
                    @Override
                    public void onSuccess(Object response) {
                        super.onSuccess(response);
                    }
                });
    }

    private void cancelPreviousSetReturnReceivedRequest() {
        if (setReturnReceivedRequestDisposable != null) {
            getCompositeDisposable().delete(setReturnReceivedRequestDisposable);
            setReturnReceivedRequestDisposable = null;
        }
    }

    @Override
    public void callGetReturnReceivedSatisfaction(ReturnReceivedRequest receivedRequest) {
        doApiCallForResponse(getDataManager().callGetReturnReceivedSatisfaction(receivedRequest), new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
                getMvpView().returnSatisfactionReceived(
                        receivedRequest,
                        response instanceof ReturnReceivedSatisfactionResponse && ((ReturnReceivedSatisfactionResponse) response).getHasRating());
            }
        });
    }
}
