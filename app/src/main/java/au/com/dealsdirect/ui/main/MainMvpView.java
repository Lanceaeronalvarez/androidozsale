package au.com.dealsdirect.ui.main;
/*
 * Created by CodeineBot on 5/15/17.
 */



import com.bluelinelabs.conductor.Router;

import au.com.dealsdirect.data.auth.AuthHandler;
import au.com.dealsdirect.ui.base.MvpView;

public interface MainMvpView extends MvpView {


    void showCategoryController();

    void showShopController();

    void showAccountController();

    void showContactController();

    void showInviteController();

    void showCheckoutController();

    void showLoginController(Router router, AuthHandler handler);

}
