package au.com.dealsdirect.ui.controller.details;

import au.com.dealsdirect.data.network.model.userdetails.SetUserDetailsRequest;
import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * Created by Paul on 6/20/17.
 */

public interface DetailsMvpPresenter<V extends MvpView>  extends MvpPresenter<V>{
    void loadUser(SetUserDetailsRequest setUserDetailsRequest);

    void sendUserDetails(String username, String firstname, String lastname, String dateofbirth,
            boolean gender, String email, String password, String newpassword, String confirmpassword);

    void saveUser(SetUserDetailsRequest userDetails);
}
