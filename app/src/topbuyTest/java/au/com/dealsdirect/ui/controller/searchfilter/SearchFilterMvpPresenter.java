package au.com.dealsdirect.ui.controller.searchfilter;

import java.util.Set;

import au.com.dealsdirect.ui.base.MvpPresenter;

/**
 * Created by smartwave on 20/07/2017.
 */

public interface SearchFilterMvpPresenter<V extends SearchFilterMvpView> extends MvpPresenter<V> {


    Set<Integer> getOriginalSelectedSet();

    void setActiveTabIndicatorIcons(String facetFilterType);

    void setActiveDefaultTabIcons(String facetFilterType);

    void setInactiveDefaultTabIcons(String facetFilterType);

    void setFiltersViewPagerCurrentItem(int position);
}
