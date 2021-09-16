package au.com.dealsdirect.ui.controller.contact.viewcontacts;

import com.bluelinelabs.conductor.Router;

import java.util.List;

import au.com.dealsdirect.data.network.model.contactitem.GetContactsResponse;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * dp Created by Admin on 6/6/17.
 */

public interface ViewContactsMvpView extends MvpView {

    String TAG = "ContactController";

    void showContactItems(List<GetContactsResponse> contacts);

    void onContactClicked(GetContactsResponse contact);

    ViewContactsMvpPresenter getPresenter();

    Router getDisplayRouter();
}
