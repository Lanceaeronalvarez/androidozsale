package au.com.dealsdirect.ui.controller.saleitems;

import android.os.Parcel;
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

    @Inject
    public SaleItemsPresenter(DataManager dataManager, SchedulerProvider schedulerProvider,
                              CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void loadSaleItems(GetSaleItemsRequest getSaleItemsRequest) {
        doApiCallForResponse(getDataManager().callGetSaleItemsRequest(getSaleItemsRequest), new AppApiCallback(){
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
                if (getSaleItemsRequest.hasFilters()) {
                    getMvpView().showSaleItems((GetSaleItemsResponse) response, false);
                } else {
                    getMvpView().showSaleItems((GetSaleItemsResponse) response, true);
                }
            }

            @Override
            public void onFailure() {
                getMvpView().unbindPaginate();
            }
        });
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

    @Override
    public void openCategoriesController() {
        getMvpView().showCategoriesController();
    }

    @Override
    public void categoryClicked(String chosenCategoryName, int color) {
        getMvpView().onCategoryClicked(chosenCategoryName,color);
    }

    @Override
    public void showSelectedCategoryText() {
        getMvpView().onShowSelectedCategoryText();
    }

    @Override
    public void executeCategoryChangeApiCall(String chosenCategoryKey) {
        getMvpView().onExecuteCategoryChangeApiCall(chosenCategoryKey);
    }


    @Override
    public void showTransparentOverlay() {
        getMvpView().onShowTransparentOverlay();
    }

    @Override
    public void hideTransparentOverlay() {
        getMvpView().onHideTransparentOverlay();
    }


}
