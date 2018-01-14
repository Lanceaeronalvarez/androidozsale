package au.com.dealsdirect.ui.controller.searchfilter.facetfilter;

import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.SearchChipModel;

/**
 * Created by smartwave on 23/11/2017.
 */

public interface FacetFilterMvpPresenter<V extends FacetFilterMvpView> extends MvpPresenter<V> {

    void resetPriceRange();

    int getSearchMaxPrice();

    void removeChipOnKeyboardDelete(SearchChipModel searchChipModel);
}
