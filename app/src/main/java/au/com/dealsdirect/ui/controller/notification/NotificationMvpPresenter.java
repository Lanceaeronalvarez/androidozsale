package au.com.dealsdirect.ui.controller.notification;

import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * Created by smartwave on 21/06/2018.
 */

public interface NotificationMvpPresenter<V extends MvpView> extends MvpPresenter<V> {

    void setIsNotificationsEnabled(boolean val);

    boolean getIsNotificationsEnabled();
}


