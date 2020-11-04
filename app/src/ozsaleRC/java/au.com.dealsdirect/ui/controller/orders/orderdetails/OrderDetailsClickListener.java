package au.com.dealsdirect.ui.controller.orders.orderdetails;

import android.view.View;

import java.util.List;

import au.com.dealsdirect.data.network.model.orders.GetOrdersResponse;

/**
 * Created by MTC on 2019-06-24.
 */
public interface OrderDetailsClickListener {
    void showOrderDialog(View anchor, int orderNumber, String invoiceId, int invoiceNumber, List<String> invoiceActions);

    void showOrderDialog(View anchor, int orderNumber, String invoiceId, int invoiceNumber, GetOrdersResponse.Order.Invoice.Product product);
}
