package au.com.dealsdirect.ui.controller.contact.addcontact;

import au.com.dealsdirect.data.network.model.address.ChangeDeliveryAddressRequest;
import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryRequest;
import au.com.dealsdirect.data.network.model.createcontact.CreateContactRequest;
import au.com.dealsdirect.data.network.model.setattachmentforcontact.SetAttachmentForContactRequest;
import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * dp Created by Admin on 6/20/17.
 */

public interface AddContactMvpPresenter<V extends MvpView> extends MvpPresenter<V> {
    void createNewContact(CreateContactRequest createContactRequest);

    void setAttachment(SetAttachmentForContactRequest request, boolean hasUploadedImage);

    int getImageLimit();

    String getUserAgent();

    void loadContactHistory(GetContactHistoryRequest contactHistoryRequest);

    void getContactSubjectsTemplates(String id);

    void getOrderTrackingDetails(int orderNumber, int invoiceNumber);

    void changeDeliveryAddress(ChangeDeliveryAddressRequest changeDeliveryAddressRequest, Integer orderNumber);
}
