package au.com.dealsdirect.ui.controller.searchfilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.SearchChipModel;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by smartwave on 20/07/2017.
 */

public class SearchFilterPresenter<V extends SearchFilterMvpView> extends BasePresenter<V> implements SearchFilterMvpPresenter<V> {

    @Inject
    public SearchFilterPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void onFacetItemClicked(List<SearchChipModel> selectedChips) {
        getMvpView().updateFacetItemToFilters(selectedChips);
    }

    @Override
    public int getSearchMaxPrice(){
        return getDataManager().getSearchMaxPrice();
    }

    @Override
    public void resetPriceRange() {
        getMvpView().onResetPriceRange();
    }

    @Override
    public void selectCategory(GetCategoryTreeResponse category) {
        getMvpView().onCategoryClicked(category);
    }

}
