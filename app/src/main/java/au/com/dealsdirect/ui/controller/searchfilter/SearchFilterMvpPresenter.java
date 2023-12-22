package au.com.dealsdirect.ui.controller.searchfilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsRequest;
import au.com.dealsdirect.service.datacollection.enums.SearchOperationType;
import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.SearchChipModel;

/**
 * Created by smartwave on 20/07/2017.
 */

public interface SearchFilterMvpPresenter<V extends SearchFilterMvpView> extends MvpPresenter<V> {

    void setRepository(SearchFilterMvpRepository repository);

    void requestCategoryMap();

    void requestUpdate(Set<String> categoryKeys, Set<SearchChipModel> chipsList,
                       String facetName, String facetValue, String categoryKey,
                       int brandCount, int minPrice, int maxPrice,
                       ArrayList<String> sizeList,
                       SearchOperationType searchOperationType);

    void onFacetItemClicked(Set<SearchChipModel> selectedChips, SearchChipModel chipChanged, boolean isAdded);

    int getSearchMaxPrice();

    void resetPriceRange();

    void selectCategory(GetCategoryTreeResponse category);

    void facetsOpened();

    void facetsClosed();
    void loadSaleItems(GetSaleItemsRequest getSaleItemsRequest);
}
