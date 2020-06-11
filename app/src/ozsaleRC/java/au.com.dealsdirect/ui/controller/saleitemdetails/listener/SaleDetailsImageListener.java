package au.com.dealsdirect.ui.controller.saleitemdetails.listener;

import android.graphics.Matrix;
import android.graphics.RectF;
import android.widget.ImageView;

import com.github.chrisbanes.photoview.OnScaleChangedListener;

import au.com.dealsdirect.data.network.model.productdetails.GetYouMayAlsoLikeResponse;
import au.com.dealsdirect.data.network.model.saleitemdetails.RecommendedItemsResponse;

/**
 * Created by MTC on 12/6/18.
 */

public interface SaleDetailsImageListener {

    void scaleImage(boolean hideImage);

    void reloadSaleItemDetails(GetYouMayAlsoLikeResponse response);

    void reloadSaleItemDetails(RecommendedItemsResponse response);

}
