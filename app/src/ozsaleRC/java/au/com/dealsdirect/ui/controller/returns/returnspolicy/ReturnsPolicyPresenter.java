package au.com.dealsdirect.ui.controller.returns.returnspolicy;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

public class ReturnsPolicyPresenter<V extends ReturnsPolicyMvpView> extends BasePresenter<V> implements
        ReturnsPolicyMvpPresenter<V> {
    @Inject
    public ReturnsPolicyPresenter(DataManager dataManager, SchedulerProvider schedulerProvider,
                                  CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }
}
