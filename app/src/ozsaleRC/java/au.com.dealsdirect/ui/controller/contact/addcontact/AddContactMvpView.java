package au.com.dealsdirect.ui.controller.contact.addcontact;

import java.util.List;

import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryResponse;
import au.com.dealsdirect.data.network.model.contactsubjecttemplates.ContactSubjectTemplatesResponse;
import au.com.dealsdirect.data.network.model.orders.GetOrdersResponse;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * dp Created by Admin on 6/20/17.
 */

public interface AddContactMvpView extends MvpView {

    void contactCreatedSwitchView(String createContactResponse);

    void setAttachmentId(String attachmentId);

    void showViewContactHistory();

    void showContactSuccess(GetContactHistoryResponse myContactItems);

    void showContactSuggestions(ContactSubjectTemplatesResponse contactSubjectTemplatesResponse);

    void showOrderTracker(GetOrdersResponse.Order.Invoice.Delivery delivery);

    void showDeliveryAddressChanged(boolean success, GetOrdersResponse.Order preloadedOrderDetails);
}
