package au.com.dealsdirect.ui.controller.salefilter;

import au.com.dealsdirect.ui.controller.searchfilter.adapter.SearchChipModel;

/**
 * dp Created by Admin on 6/9/17.
 */

public interface SaleFilterClickListener {

    void onCategoryClicked(String category, int type);

    void onAddFilter(String name, String title,SearchChipModel chip);
    void onRemoveFilter(String name, String title, SearchChipModel chip);
    void onAddCategoryFilter(String name, String title);
    void onRemoveCategoryFilter(String name, String title);

    void removeAllSort();

    void updateSeeAllProducts();
}
