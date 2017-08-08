package au.com.dealsdirect.ui.controller.saleitems;

import android.support.v7.widget.RecyclerView;

import com.androidnetworking.error.ANError;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsRequest;
import au.com.dealsdirect.data.network.model.sorting.SortingResponse;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.SearchChipModel;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

import static au.com.dealsdirect.ui.controller.searchfilter.SearchFilterController.BRANDS_FACETFILTER_NAME;
import static au.com.dealsdirect.ui.controller.searchfilter.SearchFilterController.COLORS_FACETFILTER_NAME;
import static au.com.dealsdirect.ui.controller.searchfilter.SearchFilterController.PRICE_FACETFILTER_NAME;
import static au.com.dealsdirect.ui.controller.searchfilter.SearchFilterController.SEARCH_QUERY_NAME;
import static au.com.dealsdirect.ui.controller.searchfilter.SearchFilterController.SIZES_FACETFILTER_NAME;
import static au.com.dealsdirect.ui.controller.searchfilter.SearchFilterController.SORT_FACETFILTER_NAME;

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


            getCompositeDisposable()
                .add(getDataManager()
                        .callGetSaleItemsRequest(getSaleItemsRequest)
                        .subscribeOn(getSchedulerProvider().io())
                        .observeOn(getSchedulerProvider().ui())
                        .subscribe(getSaleItemsResponse -> {

                            if (!isViewAttached()) {
                                return;
                            }
                            getMvpView().hideLoading();
                            getMvpView().showSaleItems(getSaleItemsResponse);

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
    public void loadProductDetails(RecyclerView.ViewHolder viewHolder, int position, String seoIdentifierId, String imageUrl, String skuId, String saleId) {
        getMvpView().showProductDetails(viewHolder, position, seoIdentifierId, imageUrl, skuId, saleId);
    }

    @Override
    public void loadSortingFacets() {
        getCompositeDisposable().add(getDataManager().callSortingFacets()
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<List<SortingResponse>>() {
                    @Override
                    public void accept(@NonNull List<SortingResponse> sortingResponses) throws Exception {
                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().hideLoading();

                        getMvpView().onLoadSortingFacetsFinished(sortingResponses);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        getMvpView().hideLoading();
                        getMvpView().onError(throwable.getMessage());

                        // handle load accounts error here
                        if (throwable instanceof ANError) {
                            ANError anError = (ANError) throwable;
                            handleApiError(anError);
                        }
                    }
                })
        );
    }

}
