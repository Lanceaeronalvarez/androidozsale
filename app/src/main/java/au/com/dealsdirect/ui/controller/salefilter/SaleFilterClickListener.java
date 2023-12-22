package au.com.dealsdirect.ui.controller.salefilter;

import au.com.dealsdirect.ui.controller.searchfilter.adapter.SearchChipModel;

/**
 * dp Created by Admin on 6/9/17.
 */

public interface SaleFilterClickListener {

    void onCategoryClicked(String category, int type);

    void onAddRemoveFilter(boolean addOrRemove, String name, String title, boolean isCategory, SearchChipModel chip, boolean isLast);

    void removeAllSort();

    void updateSeeAllProducts();
}
