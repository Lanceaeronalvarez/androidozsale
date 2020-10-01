package au.com.dealsdirect.data.network.model.vouchers;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by smartwave on 19/01/2017.
 */

public class AddAndApplyVoucherByKeyRequest {

    @SerializedName("languageID")
    @Expose
    private String languageId;

    private String key;

    private int imageSize;

    private String postcode;

    public AddAndApplyVoucherByKeyRequest(String key, String postcode, int imageSize, String languageId) {
        this.key = key;
        this.imageSize = imageSize;
        this.languageId = languageId;
        this.postcode = postcode;
    }
}
