package au.com.dealsdirect.ui.controller.searchfilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.service.datacollection.enums.SearchOperationType;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.SearchChipModel;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by smartwave on 20/07/2017.
 */

public class SearchFilterPresenter<V extends SearchFilterMvpView> extends BasePresenter<V> implements SearchFilterMvpPresenter<V> {

    private SearchFilterMvpRepository mRepository;

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

}
