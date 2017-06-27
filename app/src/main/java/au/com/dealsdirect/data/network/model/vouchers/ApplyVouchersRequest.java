package au.com.dealsdirect.data.network.model.vouchers;

import java.util.List;

/**
 * dp Created by Admin on 1/16/17.
 */

public class ApplyVouchersRequest {

    public List<String> vouchers;

    public int imageSize;

    public String languageID;

    public ApplyVouchersRequest(List<String> vouchers, int imageSize, String languageID) {
        this.vouchers = vouchers;
        this.imageSize = imageSize;
        this.languageID = languageID;
    }
}
