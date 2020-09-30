package au.com.dealsdirect.ui.controller.searchfilter;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.service.datacollection.enums.SearchOperationType;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.SearchChipModel;

public interface SearchFilterMvpRepository {

    void requestCategoryMap(RequestCategoryMapCompletion completion);

    interface RequestCategoryMapCompletion {
        void receivedCategoryMap(Map<String, GetCategoryTreeResponse> categoryMap);
    }

    ;

    void requestUpdate(Set<String> categoryKeys, Set<SearchChipModel> chipsList,
                       String faceName, String facetValue, String categoryKey,
                       int brandCount, int minPrice, int maxPrice,
                       ArrayList<String> sizeList,
                       SearchOperationType searchOperationType);

    void facetsOpened();

    void facetsClosed();
}
