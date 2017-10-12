package au.com.dealsdirect.data.network.model.checkout.getpaymentmethodnonce;
/*
 * Created by CodeineBot on 9/18/17.
 */

public class GetPaymentMethodNonceRequest {
    String paymentMethodToken;

    public GetPaymentMethodNonceRequest(String token) {
        this.paymentMethodToken = token;
    }
}
