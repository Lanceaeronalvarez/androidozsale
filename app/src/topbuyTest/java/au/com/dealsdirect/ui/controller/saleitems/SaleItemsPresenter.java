package au.com.dealsdirect.ui.controller.saleitems;

import android.support.v7.widget.RecyclerView;

import com.androidnetworking.error.ANError;
import com.mysale.genie.utility.RxBus;

import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsRequest;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsResponse;
import au.com.dealsdirect.data.network.model.sorting.SortingResponse;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.SearchTagsAdapter;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.CartUtil;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * dp Created by Admin on 6/8/17.
 */

public class SaleItemsPresenter<V extends SaleItemsMvpView> extends BasePresenter<V>
        implements SaleItemsMvpPresenter<V> {

    @Inject
    public SaleItemsPresenter(DataManager dataManager, SchedulerProvider schedulerProvider,
                              CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void loadSaleItems(GetSaleItemsRequest getSaleItemsRequest) {
        doApiCallForResponse(getDataManager().callGetSaleItemsRequest(getSaleItemsRequest), new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
                getMvpView().hideNoNetworkLayout();
                if (getSaleItemsRequest.hasFilters()) {
                    getMvpView().showSaleItems((GetSaleItemsResponse) response, false);
                } else {
                    getMvpView().showSaleItems((GetSaleItemsResponse) response, true);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                if (t.getMessage().contains("UnknownHostException") || t.getMessage().contains("SocketTimeoutException")) {
                    getMvpView().showNoNetworkLayout();
                }
                getMvpView().onError(t.getMessage());

            }
        });
    }

    @Override
    public void loadSaleItemsWhileTyping(String searchQuery) {
        getMvpView().onLoadSaleItemsWhileTyping(searchQuery);
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
    public void showCategoriesController() {
        getMvpView().onShowCategoriesController();
    }

    @Override
    public void categoryClicked(int color) {
        getMvpView().categoryClicked(color);
    }

    @Override
    public void dismissCategoriesController() {
        getMvpView().onDismissCategoriesController();
    }

    @Override
    public void executeCategoryChangeApiCall(String chosenCategoryKey, String chosenCategoryName) {
        getMvpView().onExecuteCategoryChangeApiCall(chosenCategoryKey, chosenCategoryName);
    }

    @Override
    public void showKeyboard() {
        getMvpView().onShowKeyboard();
    }

    @Override
    public void showSearchFilters(String facetFilterName) {
        getMvpView().onShowSearchFilters(facetFilterName);
    }

    @Override
    public void hideSearchFilters() {
        getMvpView().onHideSearchFilters();
    }


    @Override
    public void showTransparentOverlay() {
        getMvpView().onShowTransparentOverlay();
    }

    @Override
    public void hideTransparentOverlay() {
        getMvpView().onHideTransparentOverlay();
    }

    @Override
    public SearchTagsAdapter getSearchTagsAdapter() {
        return getMvpView().onGetSearchTagsAdapter();
    }

    @Override
    public void updateShopFilters() {
        getMvpView().onUpdateShopFilters();
    }

    @Override
    public boolean isAuthorized() {
        return getDataManager().isAuthorized();
    }

    @Override
    public void callGetBasketItemsQuantity() {
        getCompositeDisposable().add(getDataManager()
                .callGetBasketItemsQuantity()
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(basketQuantityResponse -> {
                    if (!isViewAttached()) {
                        return;
                    }
                    CartUtil.setValueToCart(basketQuantityResponse.getItemQuantity());
                    getMvpView().onCallGetBasketItemsQuantity();
                }, throwable -> {
                    if (!isViewAttached()) {
                        return;
                    }

                    getMvpView().onError(throwable.getMessage());

                    // handle load accounts error here
                    if (throwable instanceof ANError) {
                        ANError anError = (ANError) throwable;
                        handleApiError(anError);
                    }
                })
        );
    }

    @Override
    public void callGetCategoryTree() {
        getCompositeDisposable().add(getDataManager()
                .callGetGetCategories()
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(response -> {

                    if (!isViewAttached()) {
                        return;
                    }
                    getMvpView().hideNoNetworkLayout();
                    if (response != null) {
                        getMvpView().onCallGetCategoryTree(response);
                        RxBus.instance().post(BundleKeys.CATEGORIES_API_CALL_FINISHED);
                        getMvpView().setCallGetCategoryTreeFinished(true);
                    }

                    getMvpView().hideLoading();

                }, throwable -> {

                    if (!isViewAttached()) {
                        return;
                    }

                    getMvpView().hideLoading();

                    // handle load accounts error here
                    if (throwable instanceof ANError) {
                        ANError anError = (ANError) throwable;
                        handleApiError(anError);
                    }
                }));
    }

    @Override
    public boolean isCallGetCategoryTreeFinished() {
        return getMvpView().isCallGetCategoryTreeFinished();
    }
}
