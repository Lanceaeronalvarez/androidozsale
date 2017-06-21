package au.com.dealsdirect.ui.controller.address.viewaddress;

import au.com.dealsdirect.data.network.model.address.DeleteUserAddress;
import au.com.dealsdirect.data.network.model.address.GetAddresses;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * Created by smartwave on 21/06/2017.
 */

public interface ViewAddressMvpView extends MvpView {

    void showAddresses(GetAddresses.ResponseValue responseValue);

    void onUserDeliveryAddressDeleted(DeleteUserAddress.ResponseValue responseValue);

    void onDeleteItemClicked(DeleteUserAddress.RequestValues deleteUserAddressRequest, int position, int itemRange);
}
