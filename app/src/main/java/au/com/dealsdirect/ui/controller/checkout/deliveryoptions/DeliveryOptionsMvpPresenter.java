package au.com.dealsdirect.ui.controller.checkout.deliveryoptions;

import au.com.dealsdirect.data.network.model.checkout.SetDeliveryOption;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.ui.base.MvpPresenter;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * Created by smartwave on 30/05/2018.
 */

public interface DeliveryOptionsMvpPresenter<V extends MvpView> extends MvpPresenter<V> {

    void getDeliveryServicePackageDetails();

    void setDeliveryOption(SetDeliveryOption.OptionParameters setDeliveryOptionParameters);

    void onTermsAndConditionsClicked();
}
