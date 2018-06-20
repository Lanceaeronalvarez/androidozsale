package au.com.dealsdirect.ui.controller.returns.newreturn;
/*
 * Created by CodeineBot on 5/15/17.
 */


import com.androidnetworking.error.ANError;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.returns.createreturn.CreateReturnRequest;
import au.com.dealsdirect.data.network.model.returns.createreturn.CreateReturnRequestResponse;
import au.com.dealsdirect.data.network.model.returns.createreturn.CreateReturnRequestResponseBody;
import au.com.dealsdirect.data.network.model.returns.newreturn.NewReturnOrderDetailRequest;
import au.com.dealsdirect.data.network.model.returns.newreturn.NewReturnOrderDetailResponse;
import au.com.dealsdirect.data.network.model.returns.newreturn.NewReturnOrderDetailResponseBody;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

public class NewReturnPresenter<V extends NewReturnMvpView> extends BasePresenter<V> implements NewReturnMvpPresenter<V> {

    @Inject
    public NewReturnPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }


    @Override
    public void addNewReturnOrderRequest(CreateReturnRequest createReturnRequest) {
        getMvpView().showLoading();
        doApiCallForResponse(getDataManager().callCreateReturnRequest(createReturnRequest), new AppApiCallback(){
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
                CreateReturnRequestResponseBody getNewReturnCreateResponse = (CreateReturnRequestResponseBody) response;
                getMvpView().finishCreateReturnRequest(getNewReturnCreateResponse.getCreateReturnRequestResponse());
            }

            @Override
            public void onFailure(Throwable t) {
                super.onFailure(t);
                getMvpView().finishCreateReturnRequest(null);
            }
        });

    }

    @Override
    public void getReturnOrderDetail(int invoiceNo) {
        NewReturnOrderDetailRequest newReturnOrderDetailRequest = new NewReturnOrderDetailRequest();
        newReturnOrderDetailRequest.invoiceNo = invoiceNo;

        doApiCallForResponse(getDataManager()
                .callGetNewReturnOrderDetail(newReturnOrderDetailRequest), new AppApiCallback(){
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
                NewReturnOrderDetailResponseBody getNewReturnOrderDetail = (NewReturnOrderDetailResponseBody) response;
                getMvpView().loadReturnOrderDetail(getNewReturnOrderDetail.getNewReturnOrderDetailResponse());
            }
        });
    }

    public void updateReturnValue(String itemId, int position, int productQuantityValue){
        getMvpView().onReturnValueUpdated(itemId,position,productQuantityValue);
    }
}
