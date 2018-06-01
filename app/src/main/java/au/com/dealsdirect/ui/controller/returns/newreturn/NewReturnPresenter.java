package au.com.dealsdirect.ui.controller.returns.newreturn;
/*
 * Created by CodeineBot on 5/15/17.
 */


import com.androidnetworking.error.ANError;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.returns.createreturn.CreateReturnRequest;
import au.com.dealsdirect.data.network.model.returns.newreturn.NewReturnOrderDetailRequest;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

public class NewReturnPresenter<V extends NewReturnMvpView> extends BasePresenter<V> implements NewReturnMvpPresenter<V> {

    @Inject
    public NewReturnPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }


    @Override
    public boolean addNewReturnOrderRequest(CreateReturnRequest createReturnRequest) {

        getCompositeDisposable()
                .add(getDataManager()
                        .callCreateReturnRequest(createReturnRequest)
                        .subscribeOn(getSchedulerProvider().io())
                        .observeOn(getSchedulerProvider().ui())
                        .subscribe(getNewReturnCreateResponse -> {

                            if (!isViewAttached()) {
                                return;
                            }
                            getMvpView().hideLoading();
                            getMvpView().finishCreateReturnRequest(getNewReturnCreateResponse);

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



        return false;
    }

    @Override
    public void getReturnOrderDetail(int invoiceNo) {
        NewReturnOrderDetailRequest newReturnOrderDetailRequest = new NewReturnOrderDetailRequest();
        newReturnOrderDetailRequest.invoiceNo = invoiceNo;

        getCompositeDisposable()
                .add(getDataManager()
                        .callGetNewReturnOrderDetail(newReturnOrderDetailRequest)
                        .subscribeOn(getSchedulerProvider().io())
                        .observeOn(getSchedulerProvider().ui())
                        .subscribe(getNewReturnOrderDetail -> {

                            if (!isViewAttached()) {
                                return;
                            }
                            getMvpView().hideLoading();
                            getMvpView().loadReturnOrderDetail(getNewReturnOrderDetail.getNewReturnOrderDetailResponse());

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

    public void updateReturnValue(String itemId, int position, int productQuantityValue){
        getMvpView().onReturnValueUpdated(itemId,position,productQuantityValue);
    }
}
