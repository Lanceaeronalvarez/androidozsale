package au.com.dealsdirect.ui.controller.checkout.deliveryoptions;

import au.com.dealsdirect.data.network.model.checkout.SetDeliveryOption;

public interface SetDeliveryOptionsObjectGenerator {
    SetDeliveryOption.OptionParameters createSetDeliveryOptionRequest(String deliveryServicePackageDetailId);
}
