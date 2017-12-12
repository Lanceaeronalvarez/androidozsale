package au.com.dealsdirect.ui.controller.categories;

import java.util.List;

import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.ui.base.MvpView;
import au.com.dealsdirect.ui.controller.categories.adapter.CategoriesAdapter;

/**
 * dp Created by Admin on 6/6/17.
 */

public interface CategoriesMvpView extends MvpView {
    void showCategories(List<GetCategoryTreeResponse> categories);

    void onCategoryClicked(CategoriesAdapter.CategoriesViewHolder holder, int position, String categoryName, String categoryKey);

    boolean isActive();
}
