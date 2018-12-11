package au.com.dealsdirect.ui.controller.orders.orderdetails;

import au.com.dealsdirect.data.network.model.orders.GetOrderPaymentDetails;
import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * Created by smartwave on 22/06/2017.
 */

public interface OrderDetailsMvpPresenter<V extends MvpView> extends MvpPresenter<V> {

    void loadOrderDetails(GetOrderPaymentDetails.RequestValues requestValues);

    void showTrackingWeb(String link);
}
