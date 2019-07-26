package au.com.dealsdirect.ui.controller.orders.orders;

import java.util.ArrayList;
import java.util.HashMap;

public interface OrderItemClickListener {
    void onOrderItemClick(String referenceNumber, HashMap<String, String> status, String link, HashMap<String, String> estimatedDelivery);

    void onOrderItemTrackingButtonClick(String url, String errorMessage);

    void onOrderItemShowOptions(ArrayList<String> arrayList, HashMap<String,String> hashMap);
}
