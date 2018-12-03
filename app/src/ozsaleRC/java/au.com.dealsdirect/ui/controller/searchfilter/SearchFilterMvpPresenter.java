package au.com.dealsdirect.ui.controller.searchfilter;

import java.util.List;
import java.util.Map;
import java.util.Set;

import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.SearchChipModel;

/**
 * Created by smartwave on 20/07/2017.
 */

public interface SearchFilterMvpPresenter<V extends SearchFilterMvpView> extends MvpPresenter<V> {

    void setRepository(SearchFilterMvpRepository repository);

    void requestCategoryMap();

    void requestUpdate(Set<String> categoryKeys, List<SearchChipModel> chipsList);

    void onFacetItemClicked(List<SearchChipModel> selectedChips);

    int getSearchMaxPrice();

    void resetPriceRange();

    void selectCategory(GetCategoryTreeResponse category);

    void facetsOpened();

    void facetsClosed();
}
