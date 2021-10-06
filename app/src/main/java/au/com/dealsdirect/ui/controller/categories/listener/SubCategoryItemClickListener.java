package au.com.dealsdirect.ui.controller.categories.listener;

import java.util.List;

import au.com.dealsdirect.ui.controller.searchfilter.adapter.SearchChipModel;

/**
 * dp Created by Admin on 6/25/17.
 */

public interface SubCategoryItemClickListener {

    void onSubCategoryItemClicked(String categoryID, String categoryName, String categoryKey,
                                  List<SearchChipModel> chipFilterArray);
}
