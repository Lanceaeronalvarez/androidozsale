package au.com.dealsdirect.ui.controller.home;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * dp Created by Admin on 6/6/17.
 */

public class HomePresenter<V extends HomeMvpView> extends BasePresenter<V> implements
        HomeMvpPresenter<V> {

    @Inject
    public HomePresenter(DataManager dataManager, SchedulerProvider schedulerProvider,
                         CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

}
