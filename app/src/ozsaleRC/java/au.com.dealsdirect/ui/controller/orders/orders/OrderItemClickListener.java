package au.com.dealsdirect.ui.controller.orders.orders;

import android.view.View;

import java.util.List;

public interface OrderItemClickListener {
    void onOrderItemClick(int index);

    void onOrderItemShowOptions(View anchor, int orderNumber, String invoiceId, int invoiceNumber, List<String> actions);
}
