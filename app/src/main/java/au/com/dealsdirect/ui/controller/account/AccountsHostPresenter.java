package au.com.dealsdirect.ui.controller.account;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by smartwave on 11/06/2018.
 */

public class AccountsHostPresenter<V extends AccountsHostMvpView> extends BasePresenter<V> implements AccountsHostMvpPresenter<V> {

    @Inject
    public AccountsHostPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }
}
