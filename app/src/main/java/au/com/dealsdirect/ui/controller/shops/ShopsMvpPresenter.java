package au.com.dealsdirect.ui.controller.shops;
/*
 * Created by CodeineBot on 5/15/17.
 */


import au.com.dealsdirect.ui.base.MvpPresenter;

public interface ShopsMvpPresenter<V extends ShopsMvpView> extends MvpPresenter<V> {

    void loadShopsBanner(String categoryName, String categoryId, int bannerOffset, int bannerLimit);

    void loadShopsBanner(String categoryName, String categoryId, int bannerOffset, int bannerLimit, boolean getOnlyFromNetwork);

    void loadCategoryTree();

    boolean isAccessAnonymousEnabled();

    boolean isAuthorized();

}
