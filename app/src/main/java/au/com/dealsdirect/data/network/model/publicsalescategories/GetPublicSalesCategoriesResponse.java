package au.com.dealsdirect.data.network.model.publicsalescategories;

/**
 * Created by smartwave on 07/06/2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetPublicSalesCategoriesResponse {

    @SerializedName("d")
    @Expose
    public D d;

    public static class D {

        @SerializedName("List")
        @Expose
        public List<SaleList> list = null;
        @SerializedName("Result")
        @Expose
        public Boolean result;
        @SerializedName("Message")
        @Expose
        public String message;
    }

    public static class SaleList {

        @SerializedName("ID")
        @Expose
        public String iD;
        @SerializedName("Name")
        @Expose
        public String name;
        @SerializedName("Order")
        @Expose
        public Integer order;
        @SerializedName("Url")
        @Expose
        public Object url;

    }

}
