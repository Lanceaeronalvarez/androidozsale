package au.com.dealsdirect.ui.controller.saleitems;

import com.androidnetworking.error.ANError;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsRequest;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsResponse;
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

    private static final String SEARCH_QUERY_TAG = "search_query";

    String mSaleId="";
    @Inject
    public SaleItemsPresenter(DataManager dataManager, SchedulerProvider schedulerProvider,
            CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void loadSaleItems(String categoryKey, String saleId) {
        getMvpView().showLoading();

        List<String> saleIds = new LinkedList<>();
        LinkedHashMap<String, String> mSearchQueryModelFiltered = new LinkedHashMap<>();
        HashMap<String, List<String>> facetFilters = new HashMap<>();

        if (!saleId.isEmpty()){
            saleIds.add(saleId);
            facetFilters.put("attributes.saleId", saleIds);

        }


        String facetFiltersString = new Gson().toJson(facetFilters);
        GetSaleItemsRequest getSaleItemsRequest = new GetSaleItemsRequest();
        getSaleItemsRequest.setFacetFilter(facetFiltersString);

        if (categoryKey!=null){
            getSaleItemsRequest.setCategoryKey("[\"" + categoryKey + "\"]");
        }else{
            getSaleItemsRequest.setCategoryKey("[]");
        }

        getSaleItemsRequest.setLanguageID("");
        getSaleItemsRequest.setPageNumber(String.valueOf(0));
        getSaleItemsRequest.setQuery("");
        getSaleItemsRequest.setPageSize("50");

        getCompositeDisposable()
                .add(getDataManager()
                        .callGetSaleItemsRequest(getSaleItemsRequest)
                        .subscribeOn(getSchedulerProvider().io())
                        .observeOn(getSchedulerProvider().ui())
                        .subscribe(new Consumer<GetSaleItemsResponse>() {
                            @Override public void accept(
                                    @NonNull GetSaleItemsResponse getSaleItemsResponse)
                                    throws Exception {

                                if (!isViewAttached()) {
                                    return;
                                }
                                getMvpView().hideLoading();
                                if(!getSaleItemsResponse.products.isEmpty()){

                                    getMvpView().showSaleItems(getSaleItemsResponse);
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
    public void loadProductDetails(String seoIdentifierId, String imageUrl, String itemId, String saleId){
        getMvpView().showProductDetails(seoIdentifierId,imageUrl,itemId,saleId);
    }

}
