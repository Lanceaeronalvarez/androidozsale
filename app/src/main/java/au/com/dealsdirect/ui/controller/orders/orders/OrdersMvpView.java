package au.com.dealsdirect.ui.controller.orders.orders;

import java.util.HashMap;
import java.util.List;

import au.com.dealsdirect.data.network.model.orders.GetOrdersResponse;
import au.com.dealsdirect.data.network.model.orders.OrderReceivedRequest;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * Created by smartwave on 22/06/2017.
 */

public interface OrdersMvpView extends MvpView {

    void showOrders(GetOrdersResponse orders);

    void showOrders(List<GetOrdersResponse.Order> orders);

    void showOrder(GetOrdersResponse.Order order);

    void showOrderDetails(int position);

    void addressChanged(String newAddress);

    void orderSatisfactionReceived(OrderReceivedRequest request, boolean hasSetSatisfactionAlready);

    void addressChangeError(String message);
}
