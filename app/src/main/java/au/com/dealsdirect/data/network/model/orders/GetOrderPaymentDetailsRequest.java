package au.com.dealsdirect.data.network.model.orders;

/**
 * Created by smartwave on 09/01/2017.
 */

public class GetOrderPaymentDetailsRequest {

    public int imageSize;
    public String paymentReferenceNo;

    public GetOrderPaymentDetailsRequest(String invoiceNo) {
        this.imageSize = 100;
        this.paymentReferenceNo = invoiceNo;
    }
}
