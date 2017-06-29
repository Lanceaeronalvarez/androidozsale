package au.com.dealsdirect.data.network.model.checkout;
/*
 * Created by CodeineBot on 1/8/17.
 */

public class AdjustOrderItem {

    public static class RequestValue  {

        String languageID;
        String itemID;
        String imageSize = "100";
//        String urlEndPoint;

        public RequestValue(String url, String itemID, String languageID) {
            this.itemID = itemID;
//            this.urlEndPoint = url;
            this.languageID = languageID;
        }

        public RequestValue(String itemID, String languageID) {
            this.itemID = itemID;
//            this.urlEndPoint = url;
            this.languageID = languageID;
        }
    }

}
