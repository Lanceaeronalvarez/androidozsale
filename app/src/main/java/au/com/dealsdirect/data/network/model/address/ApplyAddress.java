package au.com.dealsdirect.data.network.model.address;

/**
 * Created by smartwave on 07/01/2017.
 */

public class ApplyAddress {

    public static final class RequestValues{


        private final ApplyAddressRequest applyAddressRequest;

        public RequestValues(ApplyAddressRequest request) {
            applyAddressRequest = request;
        }

        public ApplyAddressRequest getApplyAddressRequest() {
            return applyAddressRequest;
        }
    }


    public static final class ResponseValue {

        private final ApplyAddressResponse applyAddressResponse;

        public ResponseValue(ApplyAddressResponse response) {
            applyAddressResponse = response;
        }

        public ApplyAddressResponse getApplyAddressResponse() {
            return applyAddressResponse;
        }
    }

}
