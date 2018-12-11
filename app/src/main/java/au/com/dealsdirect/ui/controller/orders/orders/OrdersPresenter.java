package au.com.dealsdirect.ui.controller.orders.orders;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.orders.GetPaymentsList;
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
    public void loadOrders() {
        doApiCallForResponse(getDataManager()
                .callGetPaymentsList(new GetPaymentsList.RequestValues()), new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
                if (((GetPaymentsList.ResponseValue) response).getD().getResult()) {
                    getMvpView().showOrders(((GetPaymentsList.ResponseValue) response).getD().getList());
                }
            }
        });
    }
}
