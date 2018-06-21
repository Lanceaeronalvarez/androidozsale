package au.com.dealsdirect.ui.controller.notification;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by smartwave on 21/06/2018.
 */

public class NotificationPresenter<V extends NotificationMvpView> extends BasePresenter<V> implements NotificationMvpPresenter<V> {
    @Inject
    public NotificationPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void setIsNotificationsEnabled(boolean val) {
        getDataManager().setIsNotificationsEnabled(val);
    }

    @Override
    public boolean getIsNotificationsEnabled() {
        return getDataManager().getIsNotificationsEnabled();
    }
}
