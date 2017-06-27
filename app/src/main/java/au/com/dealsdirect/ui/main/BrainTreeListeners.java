package au.com.dealsdirect.ui.main;

import com.braintreepayments.api.interfaces.BraintreeCancelListener;
import com.braintreepayments.api.interfaces.BraintreeErrorListener;
import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener;

/**
 * Created by smartwave on 27/06/2017.
 */

public interface BrainTreeListeners extends PaymentMethodNonceCreatedListener, BraintreeErrorListener, BraintreeCancelListener {
}
