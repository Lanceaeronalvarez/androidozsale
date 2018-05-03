package au.com.dealsdirect.ui.controller.searchfilter;

import java.util.List;
import java.util.Set;

import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsResponse;
import au.com.dealsdirect.ui.base.MvpView;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.SearchChipModel;

/**
 * Created by smartwave on 20/07/2017.
 */

public interface SearchFilterMvpView extends MvpView{

    void showFacetItem(int position);

    void updateFacetItemToFilters(List<SearchChipModel> selectedChips);

    void onResetPriceRange();

    void replaceFacets(List<GetSaleItemsResponse.Facets> newFacets);

    void replaceCategoryTree(List<GetCategoryTreeResponse> categoryTree);

    void onCategoryClicked(String categoryName, String categoryKey);

}
