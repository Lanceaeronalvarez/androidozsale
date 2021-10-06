package au.com.dealsdirect.ui.controller.contact.listener;

import au.com.dealsdirect.data.network.model.contactsubjecttemplates.ContactSubjectTemplatesResponse.Suggestion.Mobile.Link;

public interface ContactSuggestionsClickListener {
    void onClickAction(Link link);

    void onClickInfo(Link link);
}
