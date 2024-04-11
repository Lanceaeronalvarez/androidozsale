package au.com.dealsdirect.ui.controller.main;

import android.view.View;

import au.com.dealsdirect.ui.base.MvpView;

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

    MainCustomViewPager getHomeViewPager();

    void showShopController();

    void showCategoryController();

    void showAccountController();

    void showContactUsController();

    void showWishlistController();

    void showBrandsController();

    void showBasketItemCount();

    void showWishlistItemCount(int count);

    boolean isPopUpControllerVisible();

    void backClick();

    void receiveSaleBannerDetails(String saleName, String encodedId, String externalId);
}
