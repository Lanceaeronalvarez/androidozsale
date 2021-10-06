package au.com.dealsdirect.ui.controller.bannerfilter;

import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;

/**
 * Created by pauldesilva on 4/13/18.
 */

public interface BannerFilterClickListener {
    void onCategoryClicked(int position, GetCategoryTreeResponse getCategoryTreeResponse);
    void onBrandsClicked();
}
