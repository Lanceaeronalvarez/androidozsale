package au.com.dealsdirect.ui.controller.checkout.deliveryoptions;

import java.util.List;

import au.com.dealsdirect.data.network.model.checkout.GetCurrentOrder;
import au.com.dealsdirect.data.network.model.checkout.GetDeliveryServicePackageDetails;
import au.com.dealsdirect.ui.base.MvpView;

/**
 * Created by smartwave on 30/05/2018.
 */

public interface DeliveryOptionsMvpView extends MvpView {

    void onDeliveryServicePackageDetailsLoaded(List<GetDeliveryServicePackageDetails.ResponseValue.Value> ourpaySelectDeliveryOptions);

    void onSetDeliveryOption(GetCurrentOrder.ResponseValue responseValue);

    void showTermsAndConditionsController();
}
