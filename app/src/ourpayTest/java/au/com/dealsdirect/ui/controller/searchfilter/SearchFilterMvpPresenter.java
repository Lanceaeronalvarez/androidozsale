package au.com.dealsdirect.ui.controller.searchfilter;

import java.util.Set;

import au.com.dealsdirect.ui.base.MvpPresenter;

/**
 * Created by smartwave on 20/07/2017.
 */

public interface SearchFilterMvpPresenter<V extends SearchFilterMvpView> extends MvpPresenter<V> {

    void onFacetClicked(int position);

    void onFacetItemClicked(Set<Integer> selectPosSet);

    Set<Integer> getOriginalSelectedSet();

    int getSearchMaxPrice();

    void resetPriceRange();

    void onCategoryChipRemoved();

    void showTransparentOverlay();

    void hideTransparentOverlay();
}
