package au.com.dealsdirect.ui.controller.saleitemdetails.listener;

import au.com.dealsdirect.data.network.model.productdetails.GetYouMayAlsoLikeResponse;
import au.com.dealsdirect.data.network.model.saleitemdetails.RecommendedItemsResponse;

/**
 * Created by MTC on 12/6/18.
 */

public interface SaleDetailsImageListener {

    void onImageRescale(float scale);

    void reloadSaleItemDetails(GetYouMayAlsoLikeResponse response);

    void reloadSaleItemDetails(RecommendedItemsResponse response);

    void toggleClipPadding(boolean isClipped);

    int getVerticalOffset();
}
