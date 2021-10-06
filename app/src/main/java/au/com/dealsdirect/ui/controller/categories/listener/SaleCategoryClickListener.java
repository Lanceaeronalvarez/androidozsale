package au.com.dealsdirect.ui.controller.categories.listener;

import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.ui.controller.categories.adapter.SaleCategoryAdapter.SaleCategoryViewHolder;
import au.com.dealsdirect.ui.controller.categories.adapter.SubSaleCategoryAdapter.SubCategoriesViewHolder;

public interface SaleCategoryClickListener {
    void onCategoryClicked(int position, SaleCategoryViewHolder saleCategoryViewHolder, GetCategoryTreeResponse getCategoryTreeResponse);

    void onSubCategoryClicked(int position, SubCategoriesViewHolder subCategoriesViewHolder, GetCategoryTreeResponse getCategoryTreeResponse);
}
