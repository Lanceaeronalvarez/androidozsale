package au.com.dealsdirect.data.network.model.orders;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mysale.genie.utility.LegacyBaseResponseValue;

/**
 * Created by MTC on 2019-07-26.
 */
public class OrderReceivedResponse {

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
        public Value getValue() {
            return value;
        }

        @SerializedName("Value")
        @Expose
        private Value value;

    }

    public class Value {

        public String getReceived() {
            return received;
        }

        @SerializedName("Received")
        @Expose
        private String received;
    }

}
