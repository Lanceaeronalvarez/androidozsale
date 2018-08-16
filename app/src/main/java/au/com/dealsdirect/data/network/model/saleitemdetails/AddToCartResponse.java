package au.com.dealsdirect.data.network.model.saleitemdetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mysale.genie.utility.LegacyBaseResponseValue;

import java.util.List;

import au.com.dealsdirect.data.network.model.checkout.GetCurrentOrder;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Item;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Value;

/**
 * Created by smartwave on 11/07/2017.
 */

public class AddToCartResponse {
    public GetCurrentOrder.ResponseValue.Response getD() {
        return d;
    }

    private GetCurrentOrder.ResponseValue.Response d;

    public class Response extends LegacyBaseResponseValue {

        public Value getValue() {
            return value;
        }

        @Expose
        @SerializedName("papiResponse")
        public Value value;
    }

    public List<Item> getItems() {
        return getD().getValue().getItems();
    }

}
