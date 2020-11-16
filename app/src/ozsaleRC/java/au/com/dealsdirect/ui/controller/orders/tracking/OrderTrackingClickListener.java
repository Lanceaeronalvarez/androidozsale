package au.com.dealsdirect.ui.controller.orders.tracking;

import au.com.dealsdirect.data.network.model.orders.GetOrdersResponse;

public interface OrderTrackingClickListener {
    void onOrderItemTrackingButtonClick(String url, String errorMessage);

    void onNodeTapped(GetOrdersResponse.Order.Invoice.Delivery.Step upperStep, GetOrdersResponse.Order.Invoice.Delivery.Step lowerStep);

    void onOrderReceivedToggle(String invoiceId, int invoiceNumber, boolean isReceived);
}
