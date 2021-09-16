package au.com.dealsdirect.ui.controller.contact.selectorder;
/*
 * Created by CodeineBot on 5/15/17.
 */


import java.util.List;

import au.com.dealsdirect.data.network.model.contactorder.ContactOrderResponse;
import au.com.dealsdirect.ui.base.MvpView;

public interface ContactSelectOrderMvpView extends MvpView {

    void showContactOrders(List<ContactOrderResponse> contactOrderList);

    void onContactOrderSelected(ContactOrderResponse contactOrderResponse);
}
