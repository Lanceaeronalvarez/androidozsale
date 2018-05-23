package au.com.dealsdirect.ui.controller.returns.currentreturns;
/*
 * Created by dp on 5/15/17.
 */


import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.returns.currentreturn.CurrentReturnResponseBody;
import au.com.dealsdirect.data.network.model.returns.returndetails.GetReturnDetailRequest;
import au.com.dealsdirect.data.network.model.returns.returndetails.GetReturnDetailsResponse;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.ui.controller.returns.currentreturns.viewholder.CurrentReturnViewHolder;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

public class CurrentReturnsPresenter<V extends CurrentReturnsMvpView> extends BasePresenter<V> implements CurrentReturnsMvpPresenter<V> {

    @Inject
    public CurrentReturnsPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void loadCurrentReturns() {
        getMvpView().showLoading();
        doApiCallForResponse(getDataManager().callGetCurrentReturns(), new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
                getMvpView().showCurrentReturns((CurrentReturnResponseBody) response);
            }
        });
    }

    @Override
    public void loadReturnDetails(GetReturnDetailRequest getReturnDetailRequest) {
        doApiCallForResponse(getDataManager().callGetReturnDetails(getReturnDetailRequest), new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
                getMvpView().showCurrentReturnDetails(((GetReturnDetailsResponse) response).getGetReturnDetailsResponseBody());
            }
        });
    }

    @Override
    public void currentReturnSelected(int orderNumber, int position, String productName, String productRequestStatus, String productRAN, String returnRequestDateFormat, String isRequestApproved, String returnId) {
        getMvpView().onCurrentReturnClickListener(orderNumber,
                position,
                productName,
                productRequestStatus,
                productRAN,
                returnRequestDateFormat,
                isRequestApproved
                ,returnId);
    }
}
