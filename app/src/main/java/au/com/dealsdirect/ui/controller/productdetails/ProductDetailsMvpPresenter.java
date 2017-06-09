package au.com.dealsdirect.ui.controller.productdetails;

import au.com.dealsdirect.data.network.model.productdetails.GetPublicItemDetailsRequest;
import au.com.dealsdirect.data.network.model.productdetails.GetPublicSaleDetailsRequest;
import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * Created by smartwave on 08/06/2017.
 */

public interface ProductDetailsMvpPresenter<V extends MvpView> extends MvpPresenter<V>{

    void loadProductDetails(GetPublicItemDetailsRequest publicItemDetailsRequest, GetPublicSaleDetailsRequest publicSaleDetailsRequest);
}
