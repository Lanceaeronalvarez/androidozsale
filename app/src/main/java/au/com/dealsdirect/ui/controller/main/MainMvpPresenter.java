package au.com.dealsdirect.ui.controller.main;

import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * dp Created by Admin on 6/6/17.
 */

public interface MainMvpPresenter<V extends MvpView> extends MvpPresenter<V> {

    void callGetBasketItemsQuantity();

    boolean isAuthorized();

    boolean isInitialLaunch();

    boolean hasWishlistBeenAccessed();

    void setHasWishlistBeenAccessed(boolean isAccessed);

    void setInitialLaunchFalse();

    void loadSaleBannerDetails(String externalSaleId);
}
