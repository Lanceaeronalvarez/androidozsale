package au.com.dealsdirect.ui.controller.searchfilter;

import com.google.android.material.tabs.TabLayout;

import java.util.Set;

import au.com.dealsdirect.ui.base.MvpView;

/**
 * Created by smartwave on 20/07/2017.
 */

public interface SearchFilterMvpView extends MvpView{

    void onSetActiveTabIndicatorIcons(String facetFilterType);

    void onSetActiveDefaultTabIcons(String facetFilterType);

    TabLayout.Tab onSetInactiveDefaultTabIcons(String facetFilterType);

    void onSelectTabOfFilterType(String facetFilterName);

}
