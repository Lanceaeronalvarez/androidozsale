package au.com.dealsdirect.ui.controller.returns.currentreturns;
/*
 * Created by dp on 5/15/17.
 */


import com.androidnetworking.error.ANError;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.returns.returndetails.GetReturnDetailRequest;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

public class CurrentReturnsPresenter<V extends CurrentReturnsMvpView> extends BasePresenter<V> implements CurrentReturnsMvpPresenter<V> {

    @Inject
    public CurrentReturnsPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void loadCurrentReturns() {

        /* mock */

//        List<CurrentReturns> currentReturns = new LinkedList<>();
//        CurrentReturns currentReturns1 = new CurrentReturns();
//
//        currentReturns1.setID("0");
//        currentReturns1.setDescription("Current Return 1");
//        currentReturns1.setApprovedDate("1/11/11");
//        currentReturns1.setInvoiceNo(1);
//        currentReturns1.setInvoiceNoRef(010110);
//        currentReturns1.setOrderNumber(1);
//        currentReturns1.setLastSavedDate("1/11/11");
//        currentReturns1.setRan("102");
//        currentReturns1.setReturnStatus("progress");
//
//        CurrentReturns currentReturns2 = new CurrentReturns();
//
//        currentReturns2.setID("1");
//        currentReturns2.setDescription("Current Return 2");
//        currentReturns2.setApprovedDate("1/11/11");
//        currentReturns2.setInvoiceNo(2);
//        currentReturns2.setInvoiceNoRef(010112);
//        currentReturns2.setOrderNumber(2);
//        currentReturns2.setLastSavedDate("1/11/11");
//        currentReturns2.setRan("102");
//        currentReturns2.setReturnStatus("progress");
//
//        CurrentReturns currentReturns3 = new CurrentReturns();
//
//        currentReturns3.setID("2");
//        currentReturns3.setDescription("Current Return 3");
//        currentReturns3.setApprovedDate("1/11/11");
//        currentReturns3.setInvoiceNo(3);
//        currentReturns3.setInvoiceNoRef(010113);
//        currentReturns3.setOrderNumber(03);
//        currentReturns3.setLastSavedDate("1/11/11");
//        currentReturns3.setRan("103");
//        currentReturns3.setReturnStatus("progress");
//
//        CurrentReturns currentReturns4 = new CurrentReturns();
//
//        currentReturns4.setID("3");
//        currentReturns4.setDescription("Current Return 1");
//        currentReturns4.setApprovedDate("1/11/11");
//        currentReturns4.setInvoiceNo(0104);
//        currentReturns4.setInvoiceNoRef(010114);
//        currentReturns4.setOrderNumber(4);
//        currentReturns4.setLastSavedDate("1/11/11");
//        currentReturns4.setRan("104");
//        currentReturns4.setReturnStatus("progress");
//
//        currentReturns.add(currentReturns1);
//        currentReturns.add(currentReturns2);
//        currentReturns.add(currentReturns3);
//        currentReturns.add(currentReturns4);
//
//        CurrentReturnResponse currentReturnResponse = new CurrentReturnResponse();
//        currentReturnResponse.setMessage("ok");
//        currentReturnResponse.setIsAuthenticated(true);
//        currentReturnResponse.setResult(true);
//        currentReturnResponse.setCurrentReturns(currentReturns);
//
//        CurrentReturnResponseBody currentReturnResponseBody  = new CurrentReturnResponseBody();
//        currentReturnResponseBody.setCurrentReturnResponse(currentReturnResponse);
//        getMvpView().showCurrentReturns(currentReturns);

        getCompositeDisposable()
                .add(getDataManager()
                        .callGetCurrentReturns()
                        .subscribeOn(getSchedulerProvider().io())
                        .observeOn(getSchedulerProvider().ui())
                        .subscribe(getCurrentReturnsResponse -> {

                            if (!isViewAttached()) {
                                return;
                            }
                            getMvpView().hideLoading();
                            getMvpView().showCurrentReturns(getCurrentReturnsResponse);

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
    public void loadReturnDetails(String returnID, int position) {

        GetReturnDetailRequest getReturnDetailRequest = new GetReturnDetailRequest();
        getReturnDetailRequest.returnID = returnID;

        getCompositeDisposable()
                .add(getDataManager()
                        .callGetReturnDetails(getReturnDetailRequest)
                        .subscribeOn(getSchedulerProvider().io())
                        .observeOn(getSchedulerProvider().ui())
                        .subscribe(getCurrentReturnDetailResponse -> {

                            if (!isViewAttached()) {
                                return;
                            }
                            getMvpView().hideLoading();
                            getMvpView().showCurrentReturnDetails(getCurrentReturnDetailResponse.getGetReturnDetailsResponseBody());

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
}
