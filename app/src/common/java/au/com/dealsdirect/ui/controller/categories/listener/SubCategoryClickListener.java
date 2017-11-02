package au.com.dealsdirect.ui.controller.categories.listener;

import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;

/**
 * dp Created by Admin on 6/25/17.
 */

public interface SubCategoryClickListener {

    void onSubCategoryClicked(GetCategoryTreeResponse getCategoryTreeResponse);
}
