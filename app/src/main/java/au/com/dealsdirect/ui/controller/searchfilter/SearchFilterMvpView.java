package au.com.dealsdirect.ui.controller.searchfilter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import au.com.dealsdirect.data.network.model.sorting.SortingResponse;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * Created by smartwave on 20/07/2017.
 */

public interface SearchFilterMvpView extends MvpView{

    void showFacetItem(int position);

    void updateFacetItemToFilters(Set<Integer> selectPosSet);

    Set<Integer> getOriginalSelectedSet();

    void onResetPriceRange();
}
