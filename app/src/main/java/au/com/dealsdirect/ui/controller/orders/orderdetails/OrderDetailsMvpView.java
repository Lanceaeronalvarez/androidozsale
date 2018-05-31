package au.com.dealsdirect.ui.controller.orders.orderdetails;

import au.com.dealsdirect.data.network.model.orders.GetOrderPaymentDetails;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * Created by smartwave on 22/06/2017.
 */

public interface OrderDetailsMvpView extends MvpView{

    void showOrderDetails(GetOrderPaymentDetails.ResponseValue response);

    void showOrderTrackingWeb(String link);
}
