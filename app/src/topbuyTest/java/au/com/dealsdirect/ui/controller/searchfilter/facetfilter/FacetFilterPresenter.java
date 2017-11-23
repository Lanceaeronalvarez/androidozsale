package au.com.dealsdirect.ui.controller.searchfilter.facetfilter;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.SearchChipModel;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by smartwave on 23/11/2017.
 */

public class FacetFilterPresenter<V extends FacetFilterMvpView> extends BasePresenter<V> implements FacetFilterMvpPresenter<V> {
    @Inject
    public FacetFilterPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void resetPriceRange() {
        getMvpView().onResetPriceRange();
    }

    @Override
    public int getSearchMaxPrice(){
        return getDataManager().getSearchMaxPrice();
    }

    @Override
    public void removeChipOnKeyboardDelete(SearchChipModel searchChipModel) {
        getMvpView().onRemoveChipOnKeyboardDelete(searchChipModel);
    }
}
