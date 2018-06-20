package au.com.dealsdirect.ui.controller.address.viewaddress;

import au.com.dealsdirect.data.network.model.address.AddressesItem;
import au.com.dealsdirect.data.network.model.address.DeleteUserAddress;
import au.com.dealsdirect.data.network.model.address.GetAddresses;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * Created by smartwave on 20/06/2017.
 */

public interface ViewAddressMvpPresenter<V extends MvpView> extends MvpPresenter<V> {

    void loadAddresses();

    void applyDeliveryAddress(String deliveryAddressId);

    void deleteUserDeliveryAddress(AddressesItem deliveryAddress);
}
