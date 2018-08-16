package au.com.dealsdirect.ui.controller.searchfilter.facetfilter;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by smartwave on 04/12/2017.
 */

public class FacetFilterPresenter<V extends FacetFilterMvpView> extends BasePresenter<V> implements FacetFilterMvpPresenter<V>{
    @Inject
    public FacetFilterPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }
}
