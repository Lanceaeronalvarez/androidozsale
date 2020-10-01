package au.com.dealsdirect.data.network.model.vouchers;

/**
 * dp Created by Admin on 1/5/17.
 */
public class AddVoucherByKeyRequest {
    private String key;
    private String postcode;
    private int imageSize;
    private String languageID;

    public AddVoucherByKeyRequest(String key, String postcode, int imageSize, String languageID) {
        this.key = key;
        this.postcode = postcode;
        this.imageSize = imageSize;
        this.languageID = languageID;
    }
}
