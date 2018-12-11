package au.com.dealsdirect.ui.controller.searchfilter;

import java.util.List;
import java.util.Set;

import au.com.dealsdirect.ui.base.MvpView;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.SearchChipModel;

/**
 * Created by smartwave on 20/07/2017.
 */

public interface SearchFilterMvpView extends MvpView{

    void showFacetItem(int position);

    void updateFacetItemToFilters(Set<Integer> selectPosSet);

    void updateActiveFacets(List<SearchChipModel> activeChips);

    Set<Integer> getOriginalSelectedSet();

    void onResetPriceRange();

    void categoryChipRemoved();

    void onShowTransparentOverlay();

    void onHideTransparentOverlay();
}
