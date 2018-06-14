package au.com.dealsdirect.ui.controller.checkout.checkouthost;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by smartwave on 13/06/2018.
 */

public class CheckoutHostPresenter<V extends CheckoutHostMvpView> extends BasePresenter<V> implements CheckoutHostMvpPresenter<V>{

    @Inject
    public CheckoutHostPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }
}
