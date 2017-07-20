package au.com.dealsdirect.ui.controller.search;
/*
 * Created by DP on 6/19/17.
 */


import android.support.v7.widget.RecyclerView;

import com.androidnetworking.error.ANError;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsRequest;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

public class SearchPresenter<V extends SearchMvpView> extends BasePresenter<V> implements SearchMvpPresenter<V> {

    @Inject
    public SearchPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void loadSaleItems(String categoryKey, String saleId, String searchQuery, int pageNumber) {
        List<String> saleIds = new LinkedList<>();
        HashMap<String, List<String>> facetFilters = new HashMap<>();

        if (saleId!=null)
            saleIds.add(saleId);
        facetFilters.put("saleId", saleIds);


        String facetFiltersString = new Gson().toJson(facetFilters);
        GetSaleItemsRequest getSaleItemsRequest = new GetSaleItemsRequest();
        getSaleItemsRequest.setFacetFilter(facetFiltersString);

        if (categoryKey!=null)
            getSaleItemsRequest.setCategoryKey("[\"" + categoryKey + "\"]");
        else
            getSaleItemsRequest.setCategoryKey("[]");


        getSaleItemsRequest.setLanguageID("");
        getSaleItemsRequest.setPageNumber(String.valueOf(pageNumber));

        if (searchQuery!=null)
            getSaleItemsRequest.setQuery(searchQuery);
        else
            getSaleItemsRequest.setQuery("");

        getSaleItemsRequest.setPageSize("50");

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
    public void loadProductDetails(RecyclerView.ViewHolder viewHolder, int position, String seoIdentifierId, String imageUrl, String itemId, String saleId) {
        getMvpView().showProductDetails(viewHolder, position,seoIdentifierId,imageUrl,itemId,saleId);
    }
}
