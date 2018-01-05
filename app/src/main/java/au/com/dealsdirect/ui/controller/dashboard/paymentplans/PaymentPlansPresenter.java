package au.com.dealsdirect.ui.controller.dashboard.paymentplans;
/*
 * Created by CodeineBot on 5/15/17.
 */

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

public class PaymentPlansPresenter<V extends PaymentPlansMvpView> extends BasePresenter<V> implements PaymentPlansMvpPresenter<V> {

    @Inject
    public PaymentPlansPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }
}
