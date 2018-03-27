package au.com.dealsdirect.ui.controller.categories;

import au.com.dealsdirect.data.network.model.publicsalescategories.GetPublicSalesCategoriesRequest;
import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * dp Created by Admin on 6/6/17.
 */

public interface CategoriesMvpPresenter <V extends MvpView> extends MvpPresenter<V>{
    void callGetCategoryTree();
}
