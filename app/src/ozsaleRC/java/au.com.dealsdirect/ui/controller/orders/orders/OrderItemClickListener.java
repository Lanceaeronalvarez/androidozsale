package au.com.dealsdirect.ui.controller.orders.orders;

import java.util.List;

public interface OrderItemClickListener {
    void onOrderItemClick(int index);

    void onOrderItemShowOptions(int orderNumber, String invoiceId, int invoiceNumber, List<String> actions);
}
