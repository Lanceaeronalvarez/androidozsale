package au.com.dealsdirect.ui.controller.categories.listener;

import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.ui.controller.categories.adapter.SaleCategoryAdapter.SaleCategoryViewHolder;
import au.com.dealsdirect.ui.controller.categories.adapter.SubSaleCategoryAdapter.SubCategoriesViewHolder;

public interface NewSaleCategoryClickListener {
    void onCategoryClicked(int position, GetCategoryTreeResponse getCategoryTreeResponse, String categoryName);

    void onSubCategoryClicked(int position, GetCategoryTreeResponse getCategoryTreeResponse);

    void onURLClicked(String url);
}
