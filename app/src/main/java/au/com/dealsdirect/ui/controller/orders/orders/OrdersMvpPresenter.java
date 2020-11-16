package au.com.dealsdirect.ui.controller.orders.orders;

import au.com.dealsdirect.data.network.model.address.ChangeDeliveryAddressRequest;
import au.com.dealsdirect.data.network.model.orders.CancelInvoiceItemRequest;
import au.com.dealsdirect.data.network.model.orders.OrderReceivedRequest;
import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * Created by smartwave on 22/06/2017.
 */

public interface OrdersMvpPresenter<V extends MvpView> extends MvpPresenter<V> {

    void loadOrders();

    void loadOrders(String dateTime, int months);

    void callSetOrderReceived(OrderReceivedRequest receivedRequest);

    void callSetOrderNotReceived(OrderReceivedRequest receivedRequest);

    void callGetOrderReceivedSatisfaction(OrderReceivedRequest receivedRequest);

    void cancelInvoice(CancelInvoiceItemRequest request);

    void changeDeliveryAddress(ChangeDeliveryAddressRequest changeDeliveryAddressRequest);
}
