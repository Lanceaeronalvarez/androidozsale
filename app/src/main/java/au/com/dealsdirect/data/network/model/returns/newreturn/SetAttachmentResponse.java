package au.com.dealsdirect.data.network.model.returns.newreturn;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mysale.genie.utility.LegacyBaseResponseValue;

/**
 * Created by MTC on 2019-08-14.
 */
public class SetAttachmentResponse {
    @SerializedName("d")
    @Expose
    private D d;

    public D getD() {
        return d;
    }

    public void setD(D d) {
        this.d = d;
    }

    public class D extends LegacyBaseResponseValue {
        @SerializedName("Value")
        @Expose
        private String value;

        public String getValue() {
            return value;
        }

    }



}
