package au.com.dealsdirect.ui.controller.orders.orders;

public interface OrderItemClickListener {
    void onOrderItemClick(String referenceNumber);

    void onOrderItemTrackingButtonClick(String url, String errorMessage);
}
