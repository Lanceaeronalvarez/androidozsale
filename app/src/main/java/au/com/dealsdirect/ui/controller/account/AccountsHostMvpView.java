package au.com.dealsdirect.ui.controller.account;

import com.bluelinelabs.conductor.Router;

import au.com.dealsdirect.ui.base.MvpView;

/**
 * Created by smartwave on 11/06/2018.
 */

public interface AccountsHostMvpView extends MvpView {

    Router getMasterRouter();
    Router getDetailRouter();
}
