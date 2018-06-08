package au.com.dealsdirect.ui.controller.checkout.deliveryoptions;

import au.com.dealsdirect.data.network.model.checkout.SetDeliveryOption;

/**
 * Created by smartwave on 31/05/2018.
 */

public interface SetDeliveryOptionsObjectGenerator {
    SetDeliveryOption.OptionParameters createSetDeliveryOptionRequest(String deliveryServicePackageDetailId, boolean isOurpaySelect);
}
