package au.com.dealsdirect.data.network.model.masterpass;
/*
 * Created by CodeineBot on 8/15/17.
 */


public class MasterPassPostTransactionRequest {

    private Data data;

    public MasterPassPostTransactionRequest(Data data) {
        this.data = data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        private String OAuthToken;
        private String OAuthVerifier;
        private String CheckoutResourceUrl;
        private boolean IsMobile;

        public Data(String token, String verifier, String url, boolean isMobile) {
            this.OAuthToken = token;
            this.OAuthVerifier = verifier;
            this.CheckoutResourceUrl = url;
            this.IsMobile = isMobile;
        }
    }
}
