package au.com.dealsdirect.ui.controller.bannerfilter;

import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * Created by pauldesilva on 4/13/18.
 */

public interface BannerFiltersMvpPresenter<V extends MvpView> extends MvpPresenter<V> {
    void callGetCategoryTree();
}
