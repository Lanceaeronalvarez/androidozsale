package au.com.dealsdirect.ui.controller.searchfilter;

import java.util.ArrayList;
import java.util.Set;

import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.SearchChipModel;

/**
 * Created by smartwave on 20/07/2017.
 */

public interface SearchFilterMvpPresenter<V extends SearchFilterMvpView> extends MvpPresenter<V> {

    void onFacetClicked(int position);

    void onFacetItemClicked(Set<Integer> selectPosSet);

    Set<Integer> getOriginalSelectedSet();

    int getSearchMaxPrice();

    void resetPriceRange();

    void showTransparentOverlay();

    void hideTransparentOverlay();

    void onUpdateActiveFacets(ArrayList<SearchChipModel> chips);
}
