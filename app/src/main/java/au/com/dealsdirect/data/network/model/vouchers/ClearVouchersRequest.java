package au.com.dealsdirect.data.network.model.vouchers;

/**
 * Created by Admin on 3/6/17.
 */
public class ClearVouchersRequest {

    public int imageSize;

    public String languageID;

    public ClearVouchersRequest(int imageSize, String languageID) {
        this.imageSize = imageSize;
        this.languageID = languageID;
    }
}
