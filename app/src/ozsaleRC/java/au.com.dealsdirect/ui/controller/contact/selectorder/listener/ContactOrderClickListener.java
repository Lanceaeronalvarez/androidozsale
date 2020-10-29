package au.com.dealsdirect.ui.controller.contact.selectorder.listener;

import au.com.dealsdirect.data.network.model.contactorder.ContactOrderResponse;

/**
 * dp Created by Admin on 7/5/17.
 */

public interface ContactOrderClickListener {

    void onContactOrderItemClicked(ContactOrderResponse contactOrder);

}
