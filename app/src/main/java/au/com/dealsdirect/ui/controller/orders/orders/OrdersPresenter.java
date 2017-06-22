package au.com.dealsdirect.ui.controller.orders.orders;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by smartwave on 22/06/2017.
 */

public class OrdersPresenter<V extends OrdersMvpView> extends BasePresenter<V> implements OrdersMvpPresenter<V> {
    @Inject
    public OrdersPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void loadOrderDetails(int position) {
        getMvpView().showOrderDetails(position);
    }

    @Override
    public void loadOrders() {

    }
}
