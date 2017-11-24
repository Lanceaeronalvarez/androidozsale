package au.com.dealsdirect.ui.controller.dashboard;
/*
 * Created by CodeineBot on 5/15/17.
 */

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.ourpaydashboard.paymentplans.GetPaymentPlansResponse;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

public class DashboardPresenter<V extends DashboardMvpView> extends BasePresenter<V> implements DashboardMvpPresenter<V> {

    @Inject
    public DashboardPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void getPaymentPlans() {
        doApiCallForResponse(getDataManager().callGetPaymentPlans(), new AppApiCallback(){
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
                getMvpView().showPaymentPlans((GetPaymentPlansResponse) response);
            }
        });
    }

    @Override
    public void getScheduledPayments() {
        doApiCallForResponse(getDataManager().callGetScheduledPayments(), new AppApiCallback(){
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
            }
        });
    }

    @Override
    public void getPastPayments() {
        doApiCallForResponse(getDataManager().callGetPastPayments(), new AppApiCallback(){
            @Override
            public void onSuccess(Object response) {
                super.onSuccess(response);
            }
        });
    }
}
