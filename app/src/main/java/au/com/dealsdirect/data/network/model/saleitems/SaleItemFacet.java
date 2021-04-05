package au.com.dealsdirect.data.network.model.saleitems;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SaleItemFacet {
    public String getFacetName() {
        return facetName;
    }

    public List<Value> getFacetValues() {
        return facetValues;
    }

    @SerializedName("name")
    @Expose
    public String facetName;
    @Expose
    @SerializedName("values")
    public List<Value> facetValues;

    public static class Value {
        public String getValue() {
            return value;
        }

        public int getCount() {
            return count;
        }

        public String value;
        public int count;
    }
}
