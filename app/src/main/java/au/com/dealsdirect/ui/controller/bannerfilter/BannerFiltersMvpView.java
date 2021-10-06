package au.com.dealsdirect.ui.controller.bannerfilter;

import java.util.List;

import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * Created by pauldesilva on 4/13/18.
 */

public interface BannerFiltersMvpView extends MvpView {
    void showCategories(List<GetCategoryTreeResponse> categories);

    void showNoNetworkLayout();

    void hideNoNetworklayout();
}
