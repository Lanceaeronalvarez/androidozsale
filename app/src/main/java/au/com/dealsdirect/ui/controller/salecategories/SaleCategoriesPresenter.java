package au.com.dealsdirect.ui.controller.salecategories;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * dp Created by Admin on 6/8/17.
 */

public class SaleCategoriesPresenter<V extends SaleCategoriesMvpView> extends BasePresenter<V>
        implements SaleCategoriesMvpPresenter<V> {

    @Inject
    public SaleCategoriesPresenter(DataManager dataManager, SchedulerProvider schedulerProvider,
            CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }
}
