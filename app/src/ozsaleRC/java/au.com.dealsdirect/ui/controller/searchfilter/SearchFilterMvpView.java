package au.com.dealsdirect.ui.controller.searchfilter;

import android.support.v4.util.Pair;

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

    void replaceCategoryTree(List<GetCategoryTreeResponse> categoryTree);

    void onCategoryClicked(GetCategoryTreeResponse category);

    Set<String> getCategoryKeys();

    void setSearchFilterControllerActive(boolean isTabActive);

    void setFacetFilterItems(List<Pair<String,String>> mFacetFilters);

    List<Pair<String, String>> parseFacets(List<GetSaleItemsResponse.Facets> facets);

    void updateFacets(List<GetSaleItemsResponse.Facets> facets);

    void closeFacets();

}
