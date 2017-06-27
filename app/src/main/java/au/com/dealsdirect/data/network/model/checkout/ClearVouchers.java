package au.com.dealsdirect.data.network.model.checkout;
/*
 * Created by CodeineBot on 1/9/17.
 */

public class ClearVouchers {


    public static class RequestValue {
        private String languageID;
        private String imageSize;

        public RequestValue(String languageID, String imageSize) {
            this.languageID = languageID;
            this.imageSize = imageSize;
        }
    }

}
