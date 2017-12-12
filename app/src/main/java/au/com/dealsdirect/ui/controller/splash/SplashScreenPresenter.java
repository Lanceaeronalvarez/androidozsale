package au.com.dealsdirect.ui.controller.splash;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by smartwave on 08/12/2017.
 */

public class SplashScreenPresenter<V extends SplashScreenMvpView> extends BasePresenter<V> implements SplashScreenMvpPresenter<V> {

    @Inject
    public SplashScreenPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public boolean isInitialLaunch() {
        return getDataManager().getIsInitialLaunch();
    }

    @Override
    public void setIsInitialLaunch(boolean isInitialLaunch) {
        getDataManager().setIsInitialLaunch(isInitialLaunch);
    }
}
