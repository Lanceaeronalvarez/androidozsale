package au.com.dealsdirect.ui.controller.brands;
/*
 * Created by CodeineBot on 5/15/17.
 */


import au.com.dealsdirect.data.network.model.banner.GetBannerRequest;
import au.com.dealsdirect.ui.base.MvpPresenter;

public interface TopBrandsMvpPresenter<V extends TopBrandsMvpView> extends MvpPresenter<V> {

    void loadTopBrands();

    void loadTrendingBrands(GetBannerRequest request);

    void cancelRequest();

    boolean isAuthorized();

    int getBannerColumnCount();

}
