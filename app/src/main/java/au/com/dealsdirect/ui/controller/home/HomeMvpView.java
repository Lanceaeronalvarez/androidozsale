package au.com.dealsdirect.ui.controller.home;

import com.bluelinelabs.conductor.Router;

import au.com.dealsdirect.data.auth.AuthHandler;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * dp Created by Admin on 6/6/17.
 */

public interface HomeMvpView extends MvpView {

    void showFirstTabController();

    void showSecondTabController();

    void showThirdTabController();

    void showFourthTabController();

    void showFifthTabController();

    void updateBasketItemCount();

    boolean isPopUpControllerVisible();

    void backClick();
}
