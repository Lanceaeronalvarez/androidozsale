package au.com.dealsdirect.ui.controller.main;

import android.view.View;

import au.com.dealsdirect.ui.base.MvpView;
import au.com.dealsdirect.ui.controller.home.HomeController;

/**
 * dp Created by Admin on 6/6/17.
 */

public interface MainMvpView extends MvpView {

    void hideBottomNav();

    void showBottomNav();

    void setChosenCategoryItemKey(String key);

    void setSelectedSubCategoryItem(View view);

    View getSelectedSubCategoryItem();

    String getChosenCategoryItemKey();

    HomeController getHomeController();

    MainCustomViewPager getHomeViewPager();
}
