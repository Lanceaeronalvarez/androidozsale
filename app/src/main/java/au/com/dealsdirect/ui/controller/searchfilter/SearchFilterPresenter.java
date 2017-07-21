package au.com.dealsdirect.ui.controller.searchfilter;

import java.util.Set;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.ui.base.BasePresenter;
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
    public void onFacetClicked(int position) {
        getMvpView().showFacetItem(position);
    }

    @Override
    public void onFacetItemClicked(Set<Integer> selectPosSet) {
        getMvpView().updateFacetItemToFilters(selectPosSet);
    }
}
