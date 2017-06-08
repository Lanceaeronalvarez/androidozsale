package au.com.dealsdirect.ui.controller.salecategories;

import au.com.dealsdirect.data.network.model.banner.GetPublicSalesBannerResponse;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * dp Created by Admin on 6/8/17.
 */

public interface SaleCategoriesMvpView extends MvpView {

    void showSaleCategories(GetPublicSalesBannerResponse getPublicSalesBannerResponse);

}
