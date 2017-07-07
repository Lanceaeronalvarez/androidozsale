package au.com.dealsdirect.ui.controller.details;

import au.com.dealsdirect.data.network.model.userdetails.GetUserDetailsResponse;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * Created by Paul on 6/20/17.
 */

public interface DetailsMvpView extends MvpView {
    void loadDetails(GetUserDetailsResponse userDetailsResponse);

    void saveUserDetailSuccess();

    boolean isActive();
}
