package au.com.dealsdirect.ui.controller.login;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by smartwave on 11/06/2018.
 */

public class LoginHostPresenter<V extends LoginHostMvpView> extends BasePresenter<V> implements LoginHostMvpPresenter<V> {
    @Inject
    public LoginHostPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }
}
