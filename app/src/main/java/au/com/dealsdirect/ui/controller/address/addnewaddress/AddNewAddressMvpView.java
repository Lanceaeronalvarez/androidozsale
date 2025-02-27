package au.com.dealsdirect.ui.controller.address.addnewaddress;

import android.view.View;

import au.com.dealsdirect.ui.base.MvpView;

/**
 * Created by smartwave on 20/06/2017.
 */

public interface AddNewAddressMvpView extends MvpView {
    void addNewAddressSuccessful();

    void setFieldErrorState(View view);

    void showErrorMessage(boolean hasSpecialCharacter);
}
