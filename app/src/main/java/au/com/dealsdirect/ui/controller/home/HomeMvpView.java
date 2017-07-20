package au.com.dealsdirect.ui.controller.home;

import com.bluelinelabs.conductor.Router;

import au.com.dealsdirect.data.auth.AuthHandler;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * dp Created by Admin on 6/6/17.
 */

public interface HomeMvpView extends MvpView {

    void showShopController();

    void showAccountController();

    void showContactController();

    void showInviteController();

    void showCheckoutController();

    void showLoginController(Router router, AuthHandler handler);
}
