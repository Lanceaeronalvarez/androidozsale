package au.com.dealsdirect.ui.controller.searchfilter;

import au.com.dealsdirect.ui.base.MvpView;

/**
 * Created by smartwave on 20/07/2017.
 */

public interface SearchFilterMvpView extends MvpView{

    void showFacetItem();

    void includeFacetItemToFilters();
}
