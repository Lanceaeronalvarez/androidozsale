package au.com.dealsdirect.ui.controller.searchfilter;

import androidx.core.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.cachedresponses.ListOfSortingResponses;
import au.com.dealsdirect.data.cachedresponses.ParamaterizedCachableRequest;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsRequest;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsResponse;
import au.com.dealsdirect.data.network.model.sorting.SortingResponse;
import au.com.dealsdirect.service.datacollection.enums.SearchOperationType;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.SearchChipModel;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by smartwave on 20/07/2017.
 */

public class SearchFilterPresenter<V extends SearchFilterMvpView> extends BasePresenter<V> implements SearchFilterMvpPresenter<V> {

    private SearchFilterMvpRepository mRepository;

    private Disposable mPreviousGetSaleItemsRequest = null;

    @Override
    public void setRepository(SearchFilterMvpRepository repository) {
        mRepository = repository;
    }

    @Inject
    public SearchFilterPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void requestCategoryMap() {
        if (mRepository != null) {
            mRepository.requestCategoryMap(new SearchFilterMvpRepository.RequestCategoryMapCompletion() {
                @Override
                public void receivedCategoryMap(Map<String, GetCategoryTreeResponse> categoryMap) {
                    if (isViewAttached()) {
                        getMvpView().onReceiveCategoryMap(categoryMap);
                    }
                }
            });
        }
    }

    @Override
    public void onFacetItemClicked(Set<SearchChipModel> selectedChips, SearchChipModel newChip, boolean isAdded) {
        if (isViewAttached()) {
            getMvpView().updateFacetItemToFilters(selectedChips, newChip, isAdded);
        }
    }

    @Override
    public int getSearchMaxPrice() {
        return getDataManager().getSearchMaxPrice();
    }

    @Override
    public void resetPriceRange() {
        if (isViewAttached()) {
            getMvpView().onResetPriceRange();
        }
    }

    @Override
    public void requestUpdate(Set<String> categoryKeys, Set<SearchChipModel> chipsList,
            String facetName, String facetValue, String categoryKey,
                              int brandCount, int minPrice, int maxPrice,
                              ArrayList<String> sizeList,
                              SearchOperationType searchOperationType) {
        if (mRepository != null) {
            mRepository.requestUpdate(
                    categoryKeys,
                    chipsList,
                    facetName,
                    facetValue,
                    categoryKey,
                    brandCount,
                    minPrice,
                    maxPrice,
                    sizeList,
                    searchOperationType);
        }
    }

    @Override
    public void selectCategory(GetCategoryTreeResponse category) {
        if (isViewAttached()) {
            getMvpView().onCategoryClicked(category);
        }
    }

    @Override
    public void facetsOpened() {
        if (mRepository != null) {
            mRepository.facetsOpened();
        }
    }

    @Override
    public void facetsClosed() {
        if (mRepository != null) {
            mRepository.facetsClosed();
        }
    }
    protected <T> Observable<T> wrapObservable(Observable<T> observable) {
        return observable.subscribeOn(getSchedulerProvider().io());
    }

    @Override
    public void loadSaleItems(GetSaleItemsRequest getSaleItemsRequest) {
        final int pageNumber = getSaleItemsRequest.getPageNumber();
        getDataManager().pruneCachedResponse(getSaleItemsRequest);
        GetSaleItemsResponse saleItemsResponse = getDataManager().getCachedResponse(getSaleItemsRequest, GetSaleItemsResponse.class);
        if (saleItemsResponse != null) {
            getMvpView().showSaleItems(saleItemsResponse, pageNumber, !getSaleItemsRequest.hasFilters(), true);
        }

        ParamaterizedCachableRequest loadSortingFacetsRequest = new ParamaterizedCachableRequest("loadSortingFacets");
        getDataManager().pruneCachedResponse(loadSortingFacetsRequest);

        // Cancels any previous loadSaleItems request
        clearPreviousGetSaleItemsRequest();

        Observable dualApiCall = Observable.zip(wrapObservable(getDataManager().callGetSaleItemsRequest(getSaleItemsRequest)),
                wrapObservable(getDataManager().callSortingFacets()),
                Pair::new);

        mPreviousGetSaleItemsRequest = doApiCallForResponse(dualApiCall, new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
                final Pair pair = (Pair) response;
                getMvpView().showSaleItems((GetSaleItemsResponse) pair.first, pageNumber, !getSaleItemsRequest.hasFilters(), false);
                getDataManager().setCachedResponse(getSaleItemsRequest, (GetSaleItemsResponse) pair.first);
                getDataManager().setCachedResponse(loadSortingFacetsRequest, new ListOfSortingResponses((List<SortingResponse>) pair.second));
            }

            @Override
            public void onFailure(Throwable t) {
                clearPreviousGetSaleItemsRequest();
            }
        });
    }

    private void clearPreviousGetSaleItemsRequest() {
        if (mPreviousGetSaleItemsRequest != null) {
            getCompositeDisposable().remove(mPreviousGetSaleItemsRequest);
            mPreviousGetSaleItemsRequest = null;
        }
    }


}
