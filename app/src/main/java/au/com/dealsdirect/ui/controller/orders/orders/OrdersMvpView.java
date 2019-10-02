package au.com.dealsdirect.ui.controller.orders.orders;

import java.util.ArrayList;
import java.util.HashMap;

import au.com.dealsdirect.data.network.model.orders.GetPaymentsList;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * Created by smartwave on 22/06/2017.
 */

public interface OrdersMvpView extends MvpView {

    void showOrders(ArrayList<GetPaymentsList.ResponseValue.PaymentItem> orders);

    void showOrderDetails(String referenceNumber, HashMap<String, String> status, String link,
                          HashMap<String, String> estimatedDelivery, int position);

    void showOrderDetails(int position);
}
