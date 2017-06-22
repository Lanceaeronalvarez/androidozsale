package au.com.dealsdirect.ui.controller.orders.orderdetails;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by smartwave on 22/06/2017.
 */

public class OrderDetailsPresenter<V extends OrderDetailsMvpView> extends BasePresenter<V> implements OrderDetailsMvpPresenter<V> {
    @Inject
    public OrderDetailsPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }
}
