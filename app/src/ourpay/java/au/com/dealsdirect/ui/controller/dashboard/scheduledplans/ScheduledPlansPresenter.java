package au.com.dealsdirect.ui.controller.dashboard.scheduledplans;
/*
 * Created by CodeineBot on 5/15/17.
 */

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

public class ScheduledPlansPresenter<V extends ScheduledPlansMvpView> extends BasePresenter<V> implements ScheduledPlansMvpPresenter<V> {

    @Inject
    public ScheduledPlansPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }
}
