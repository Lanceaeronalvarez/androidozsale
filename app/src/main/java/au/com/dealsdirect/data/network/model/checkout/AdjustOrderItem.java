package au.com.dealsdirect.data.network.model.checkout;
/*
 * Created by CodeineBot on 1/8/17.
 */

public class AdjustOrderItem {

    public static class RequestValue  {

        String languageID;
        String itemID;
        String postcode;
        String imageSize = "100";

        public RequestValue(String itemID, String postcode, String languageID) {
            this.itemID = itemID;
            this.languageID = languageID;
            this.postcode = postcode;
        }
    }

}
