package au.com.dealsdirect.ui.controller.searchfilter;

import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;

/**
 * Created by smartwave on 25/10/2017.
 */

public interface SubCategoryClickListener {
    void onSubCategoryClicked(GetCategoryTreeResponse getCategoryTreeResponse);
}
