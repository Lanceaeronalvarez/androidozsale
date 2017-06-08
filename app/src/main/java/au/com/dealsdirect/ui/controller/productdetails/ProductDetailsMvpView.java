package au.com.dealsdirect.ui.controller.productdetails;

import au.com.dealsdirect.data.network.model.productdetails.GetPublicItemDetailsResponse;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * Created by smartwave on 08/06/2017.
 */

public interface ProductDetailsMvpView extends MvpView {

    void showProductDetails(GetPublicItemDetailsResponse.Value productDetail);
}
