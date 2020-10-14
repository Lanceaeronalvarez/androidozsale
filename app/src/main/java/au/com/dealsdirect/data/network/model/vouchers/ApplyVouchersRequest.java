package au.com.dealsdirect.data.network.model.vouchers;

import java.util.List;

/**
 * dp Created by Admin on 1/16/17.
 */

public class ApplyVouchersRequest {

    private List<String> vouchers;

    private int imageSize;

    private String languageID;

    private String postcode;

    public ApplyVouchersRequest(List<String> vouchers, String postcode, int imageSize, String languageID) {
        this.vouchers = vouchers;
        this.imageSize = imageSize;
        this.languageID = languageID;
        this.postcode = postcode;
    }
}
