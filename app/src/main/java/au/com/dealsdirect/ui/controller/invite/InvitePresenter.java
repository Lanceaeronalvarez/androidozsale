package au.com.dealsdirect.ui.controller.invite;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * dp Created by Admin on 6/6/17.
 */

public class InvitePresenter<V extends InviteMvpView> extends BasePresenter<V> implements
        InviteMvpPresenter<V> {

    @Inject
    public InvitePresenter(DataManager dataManager, SchedulerProvider schedulerProvider,
            CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }
}
