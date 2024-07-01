package au.com.dealsdirect.ui.controller.brands;
/*
 * Created by CodeineBot on 5/15/17.
 */

import java.util.List;

import au.com.dealsdirect.data.network.model.banner.GetBannerResponse;
import au.com.dealsdirect.data.network.model.banner.GetTopBrandsResponse;
import au.com.dealsdirect.ui.base.MvpView;

public interface TopBrandsMvpView extends MvpView {

    void showTopBrands(List<GetTopBrandsResponse> topBrands);

    void showTrendingBrands(GetBannerResponse response);

    boolean isChangeInProgress();
}
