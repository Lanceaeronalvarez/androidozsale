package au.com.dealsdirect.ui.controller.categories.listener;

import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;

/**
 * dp Created by Admin on 6/9/17.
 */

public interface CategoryClickListener {

    void onCategoryClicked(int position, GetCategoryTreeResponse getCategoryTreeResponse);
}
