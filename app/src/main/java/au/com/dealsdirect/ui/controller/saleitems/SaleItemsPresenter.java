package au.com.dealsdirect.ui.controller.saleitems;

import android.support.v7.widget.RecyclerView;

import com.androidnetworking.error.ANError;

import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsRequest;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsResponse;
import au.com.dealsdirect.data.network.model.sorting.SortingResponse;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

/**
 * dp Created by Admin on 6/8/17.
 */

public class SaleItemsPresenter<V extends SaleItemsMvpView> extends BasePresenter<V>
        implements SaleItemsMvpPresenter<V> {

    private static final String SEARCH_QUERY_TAG = "search_query";

    String mSaleId = "";

    @Inject
    public SaleItemsPresenter(DataManager dataManager, SchedulerProvider schedulerProvider,
                              CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void loadSaleItems(GetSaleItemsRequest getSaleItemsRequest) {
        getCompositeDisposable().add(getDataManager()
                .callGetSaleItemsRequest(getSaleItemsRequest)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<GetSaleItemsResponse>() {
                               @Override
                               public void accept(GetSaleItemsResponse response) throws Exception {

                                   if (!isViewAttached()) {
                                       return;
                                   }

                                   getMvpView().hideLoading();
                                   if (getSaleItemsRequest.hasFilters()) {
                                       getMvpView().showSaleItems(response, false);
                                   } else {
                                       getMvpView().showSaleItems(response, true);
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

                           }
                ));
    }

    @Override
    public void loadProductDetails(RecyclerView.ViewHolder viewHolder, int position, String
            seoIdentifierId, String imageUrl, String skuId, String saleId) {
        getMvpView().showProductDetails(viewHolder, position, seoIdentifierId, imageUrl, skuId, saleId);
    }

    @Override
    public void loadSortingFacets() {
        doApiCallForResponse(getDataManager().callSortingFacets(), new AppApiCallback() {
            @Override
            public void onSuccess(List<?> response) {
                super.onSuccess(response);
                getMvpView().onLoadSortingFacetsFinished((List<SortingResponse>) response);
            }
        });
    }

}
