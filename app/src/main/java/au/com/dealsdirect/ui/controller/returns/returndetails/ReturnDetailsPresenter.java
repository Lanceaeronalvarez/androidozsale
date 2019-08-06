package au.com.dealsdirect.ui.controller.returns.returndetails;
/*
 * Created by CodeineBot on 5/15/17.
 */


import com.androidnetworking.error.ANError;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryRequest;
import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryResponse;
import au.com.dealsdirect.data.network.model.createcontact.CreateContactRequest;
import au.com.dealsdirect.data.network.model.returns.returndetails.GetReturnDetailRequest;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

public class ReturnDetailsPresenter<V extends ReturnDetailsMvpView> extends BasePresenter<V> implements ReturnDetailsMvpPresenter<V> {

    @Inject
    public ReturnDetailsPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void loadCurrentReturnDetails(String returnId) {

        GetReturnDetailRequest getReturnDetailRequest = new GetReturnDetailRequest(returnId);

        getCompositeDisposable()
                .add(getDataManager()
                        .callGetReturnDetails(getReturnDetailRequest)
                        .subscribeOn(getSchedulerProvider().io())
                        .observeOn(getSchedulerProvider().ui())
                        .subscribe(getReturnDetail -> {

                            if (!isViewAttached()) {
                                return;
                            }
                            getMvpView().hideLoading();
                            getMvpView().showCurrentReturnDetails(getReturnDetail.getGetReturnDetailsResponseBody());

                        }, throwable -> {

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
                        }));
    }

    @Override
    public void loadReturnContacts(GetContactHistoryRequest request) {
        doApiCallForResponse(getDataManager().callGetContactHistory(request), new AppApiCallback(){
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);

                GetContactHistoryResponse.ResponseValue responseValue = (GetContactHistoryResponse.ResponseValue) response;
                if (responseValue.getList() != null && !responseValue.getList().isEmpty()) {
                    getMvpView().showContactMessageReturn(responseValue);
                }
            }
        });
    }
}
