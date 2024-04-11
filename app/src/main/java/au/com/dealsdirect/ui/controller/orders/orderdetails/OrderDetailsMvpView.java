package au.com.dealsdirect.ui.controller.orders.orderdetails;

import au.com.dealsdirect.data.network.model.orders.GetOrdersResponse;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * Created by smartwave on 22/06/2017.
 */

public interface OrderDetailsMvpView extends MvpView {

    void showOrderDetails(GetOrdersResponse.Order orderDetails);

    void showOrderTrackingWeb(String link);

    void addressChanged();

    void onReceivedSet(int invoiceNumber);

    void orderSatisfactionReceived(int invoiceNumber, boolean hasSetSatisfaction);

    void addressChangeError(String message);
}
