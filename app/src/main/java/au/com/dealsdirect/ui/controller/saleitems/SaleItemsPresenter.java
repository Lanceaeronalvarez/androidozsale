package au.com.dealsdirect.ui.controller.saleitems;

import android.util.Log;

import com.androidnetworking.error.ANError;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.saleitems.GetPublicSaleItemsRequest;
import au.com.dealsdirect.data.network.model.saleitems.GetPublicSaleItemsResponse;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

/**
 * dp Created by Admin on 6/8/17.
 */

public class SaleItemsPresenter<V extends SaleItemsMvpView> extends BasePresenter<V>
        implements SaleItemsMvpPresenter<V> {

    String mSaleId="";
    @Inject
    public SaleItemsPresenter(DataManager dataManager, SchedulerProvider schedulerProvider,
            CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void loadSaleItems(GetPublicSaleItemsRequest getPublicSaleItemsRequest) {

        Log.d("saleitems", "load");
        getMvpView().showLoading();
        getCompositeDisposable()
                .add(getDataManager()
                             .getPublicSaleItemsApiCall(getPublicSaleItemsRequest)
                             .subscribeOn(getSchedulerProvider().io())
                             .observeOn(getSchedulerProvider().ui())
                             .subscribe(new Consumer<GetPublicSaleItemsResponse>() {
                                 @Override public void accept(@NonNull
                                         GetPublicSaleItemsResponse getPublicSaleItemsResponse)
                                         throws Exception {

                                     if (!isViewAttached()) {
                                         return;
                                     }

                                     Log.d("saleitems","hmm");
                                     getMvpView().hideLoading();
                                     if(!getPublicSaleItemsResponse.getGetPublicSaleItemsObject()
                                                .getList().isEmpty()){
                                         Log.d("saleitems","okay");

                                         getMvpView().showSaleItems(getPublicSaleItemsResponse);
                                     }
                                 }

                             }, new Consumer<Throwable>() {
                                 @Override
                                 public void accept(Throwable throwable) throws Exception {
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
                                 }
                             }));
    }

    @Override
    public void loadProductDetails(String itemId, String saleId){
        getMvpView().showProductDetails(itemId,saleId);
    }

}
