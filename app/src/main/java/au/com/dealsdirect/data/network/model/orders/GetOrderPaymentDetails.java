package au.com.dealsdirect.data.network.model.orders;

/**
 * Created by smartwave on 09/01/2017.
 */

public class GetOrderPaymentDetails {

    public static final class RequestValues {

        private final GetOrderPaymentDetailsRequest mOrderDetailsRequest;

        public RequestValues(GetOrderPaymentDetailsRequest orderDetailsRequest) {
            mOrderDetailsRequest = orderDetailsRequest;
        }

        public GetOrderPaymentDetailsRequest getOrderDetailsRequest() {
            return mOrderDetailsRequest;
        }

    }


    public static final class ResponseValue {

        private GetOrderPaymentDetailsResponse mOrderDetailsResponse;

        public GetOrderPaymentDetailsResponse getOrderPaymentDetailsResponse() {
            return mOrderDetailsResponse;
        }
    }
}
