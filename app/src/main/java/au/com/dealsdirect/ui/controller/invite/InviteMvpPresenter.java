package au.com.dealsdirect.ui.controller.invite;

import au.com.dealsdirect.data.network.model.invite.GetInviteRequest;
import au.com.dealsdirect.data.network.model.invite.SetInviteRequest;
import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * dp Created by Admin on 6/6/17.
 */

public interface InviteMvpPresenter<V extends MvpView> extends MvpPresenter<V> {
    void getInviteLink(GetInviteRequest getInviteLinkRequest);

    void setInviteLink(SetInviteRequest setInviteLinkRequest);

    String getFollowUsFbLink();

    String getFollowUsTwitterLink();

    void start();
}
