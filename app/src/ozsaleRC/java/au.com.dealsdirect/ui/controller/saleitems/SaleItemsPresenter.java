package au.com.dealsdirect.ui.controller.saleitems;

import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsRequest;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsResponse;
import au.com.dealsdirect.data.network.model.sorting.SortingResponse;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.BiFunction;

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

        Observable dualApiCall = Observable.zip(wrapObservable(getDataManager().callGetSaleItemsRequest(getSaleItemsRequest)),
                wrapObservable(getDataManager().callSortingFacets()),
                new BiFunction<GetSaleItemsResponse, List<SortingResponse>, Pair<GetSaleItemsResponse, List<SortingResponse>>>() {
                    @Override
                    public Pair<GetSaleItemsResponse, List<SortingResponse>> apply(GetSaleItemsResponse t1, List<SortingResponse> t2) throws Exception {
                        return new Pair<>(t1, t2);
                    }
                });


        doApiCallForResponse(dualApiCall, new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
                Pair pair = (Pair) response;
                getMvpView().onLoadSortingFacetsFinished((List<SortingResponse>)pair.second);
                getMvpView().showSaleItems((GetSaleItemsResponse) pair.first, !getSaleItemsRequest.hasFilters());
            }
        });
    }

    @Override
    public void loadProductDetails(RecyclerView.ViewHolder viewHolder, int position, String
            seoIdentifierId, String imageUrl, String skuId, String saleId, boolean isFreeDelivery) {
        getMvpView().hideKeyboard();
        getMvpView().showProductDetails(viewHolder, position, seoIdentifierId, imageUrl, skuId, saleId, isFreeDelivery);
    }

    protected <T> Observable<T> wrapObservable(Observable<T> observable) {
        return observable.subscribeOn(getSchedulerProvider().io());
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

    @Override
    public boolean isSortingEnabled() {
        return getDataManager().getIsSortingEnabled();
    }

}
