package au.com.dealsdirect.ui.controller.orders.orderdetails;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by MTC on 2019-06-24.
 */
public interface OrderDetailsClickListener {

    void showOrderDialog(ArrayList<String> arrayList, HashMap<String,String> hashMap);

    void callOrderReceived(String orderID);
}
