package au.com.dealsdirect.ui.controller.categories;

import java.util.List;

import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.data.network.model.publicsalescategories.GetPublicSalesCategoriesResponse;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * dp Created by Admin on 6/6/17.
 */

public interface CategoriesMvpView extends MvpView {
    void showPublicSalesCategories(List<GetPublicSalesCategoriesResponse.SaleList> saleList);

    void showCategories(List<GetCategoryTreeResponse> categories);
}
