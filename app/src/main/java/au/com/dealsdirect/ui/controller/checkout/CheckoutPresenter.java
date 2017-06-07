package au.com.dealsdirect.ui.controller.checkout;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.ui.base.MvpView;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * dp Created by Admin on 6/6/17.
 */

public class CheckoutPresenter<V extends CheckoutMvpView & MvpView> extends BasePresenter<V> implements
        CheckoutMvpPresenter<V> {

    @Inject
    public CheckoutPresenter(DataManager dataManager, SchedulerProvider schedulerProvider,
            CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }
}
