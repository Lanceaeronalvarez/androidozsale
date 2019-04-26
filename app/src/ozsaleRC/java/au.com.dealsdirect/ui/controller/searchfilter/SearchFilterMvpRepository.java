package au.com.dealsdirect.ui.controller.searchfilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.SearchChipModel;

public interface SearchFilterMvpRepository {

    void requestCategoryMap(RequestCategoryMapCompletion completion);
    interface RequestCategoryMapCompletion {
        void receivedCategoryMap(Map<String, GetCategoryTreeResponse> categoryMap);
    };

    void requestUpdate(Set<String> categoryKeys, List<SearchChipModel> chipsList,
                       ArrayList<String> brandList, int minPrice, int maxPrice,
                       ArrayList<String> sizeList);

    void facetsOpened();

    void facetsClosed();
}
