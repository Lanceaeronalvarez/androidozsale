package au.com.dealsdirect.ui.controller.details;

import au.com.dealsdirect.data.network.model.userdetails.SetUserDetailsRequest;
import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.base.MvpView;
import au.com.dealsdirect.ui.controller.gdpr.GdprMvpPresenter;

/**
 * Created by Paul on 6/20/17.
 */

public interface DetailsMvpPresenter<V extends MvpView> extends GdprMvpPresenter<V> {
    void loadUser(SetUserDetailsRequest setUserDetailsRequest);

    void sendUserDetails(SetUserDetailsRequest userDetailsRequest);

    void saveReceiveSales(boolean receiveInvitations);
}
