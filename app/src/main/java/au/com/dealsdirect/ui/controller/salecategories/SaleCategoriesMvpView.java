package au.com.dealsdirect.ui.controller.salecategories;

import au.com.dealsdirect.data.network.model.banner.BannerResponse;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * dp Created by Admin on 6/8/17.
 */

public interface SaleCategoriesMvpView extends MvpView {

    void showSaleCategories(BannerResponse bannerResponse);

}
