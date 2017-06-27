package au.com.dealsdirect.data.network.model.checkout;

import java.util.List;

/*
 * Created by CodeineBot on 1/8/17.
 */
public class ApplyVouchers {
    public static class RequestValue {
        private String languageID;
        private String imageSize;
        private List<String> vouchers = null;

        public RequestValue(String languageID, String imageSize, List<String> vouchers) {
            this.languageID = languageID;
            this.imageSize = imageSize;
            this.vouchers = vouchers;
        }
    }
}
