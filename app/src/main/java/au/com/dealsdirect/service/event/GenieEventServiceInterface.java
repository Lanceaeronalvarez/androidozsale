package au.com.dealsdirect.service.event;

import au.com.dealsdirect.data.network.model.events.CategoryRequest;
import au.com.dealsdirect.data.network.model.events.ProductViewRequest;
import au.com.dealsdirect.data.network.model.events.SearchEventRequest;

/**
 * Created by MTC on 3/4/19.
 */

public interface GenieEventServiceInterface {

    static void callSearchEvent(SearchEventRequest request){};

    static void callProductViewEvent(ProductViewRequest request){};

    static void callEventUser(){};

    static void callCategoryEvent(CategoryRequest request){};

}
