package au.com.dealsdirect.data.network.model.checkout;
/*
 * Created by CodeineBot on 1/6/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mysale.genie.utility.LegacyBaseResponseValue;

import java.util.List;

import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Item;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Value;

public class GetCurrentOrder {

    public static class RequestValue {
        private String languageID;
        private int imageSize = 100;

        public RequestValue(String languageID) {
            this.languageID = languageID;
        }
    }

    public static class ResponseValue  {
        public Response getD() {
            return d;
        }

        private Response d;

        public class Response extends LegacyBaseResponseValue {

            public Value getValue() {
                return value;
            }

            @Expose
            @SerializedName("Value")
            public Value value;
        }

        public List<Item> getItems() {
            return getD().getValue().getItems();
        }

    }
}
