package au.com.dealsdirect.ui.controller.splash;

import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.ui.base.MvpPresenter;

/**
 * Created by smartwave on 08/12/2017.
 */

public interface SplashScreenMvpPresenter<V extends SplashScreenMvpView> extends MvpPresenter<V> {

    boolean isInitialLaunch();

    void setIsInitialLaunch(boolean isInitialLaunch);
}
