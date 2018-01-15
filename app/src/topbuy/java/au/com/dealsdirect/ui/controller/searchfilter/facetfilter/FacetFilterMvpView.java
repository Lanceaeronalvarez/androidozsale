package au.com.dealsdirect.ui.controller.searchfilter.facetfilter;

import au.com.dealsdirect.ui.base.MvpView;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.SearchChipModel;

/**
 * Created by smartwave on 23/11/2017.
 */

public interface FacetFilterMvpView extends MvpView {

    void onResetPriceRange();

    void onRemoveChipOnKeyboardDelete(SearchChipModel searchChipModel);
}
