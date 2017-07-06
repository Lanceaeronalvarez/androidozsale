package au.com.dealsdirect.ui.controller.invite;

import au.com.dealsdirect.data.network.model.invite.GetInviteResponse;
import au.com.dealsdirect.data.network.model.invite.SetInviteResponse;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * dp Created by Admin on 6/6/17.
 */

public interface InviteMvpView extends MvpView {
    void showInviteLink(GetInviteResponse getInviteLinkBody);

    void onInviteLinkSet(SetInviteResponse setInviteLinkResponseBody);
}
