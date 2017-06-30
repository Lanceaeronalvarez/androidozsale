
package au.com.dealsdirect.data.network.model.returns.returndetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Value {

    @SerializedName("Items")
    @Expose
    private List<Item> items = null;
    @SerializedName("Total")
    @Expose
    private Double total;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

}
