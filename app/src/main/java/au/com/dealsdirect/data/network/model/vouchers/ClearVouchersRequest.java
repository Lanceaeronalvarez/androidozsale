package au.com.dealsdirect.data.network.model.vouchers;

/**
 * Created by Admin on 3/6/17.
 */
public class ClearVouchersRequest {

    private String postcode;

    private int imageSize;

    private String languageID;

    public ClearVouchersRequest(String postcode, int imageSize, String languageID) {
        this.postcode = postcode;
        this.imageSize = imageSize;
        this.languageID = languageID;
    }
}
