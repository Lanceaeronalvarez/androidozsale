package au.com.dealsdirect.data.network.model.checkout;
/*
 * Created by CodeineBot on 1/9/17.
 */

import com.mysale.genie.utility.LegacyBaseResponseValue;


public class ClearOrder {
    public static class RequestValue {

    }

    public static class ResponseValue  {

        public Response d;

        public class Response extends LegacyBaseResponseValue {

        }
    }
}
