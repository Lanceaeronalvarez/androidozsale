package au.com.dealsdirect.data.network.model.orders;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by MTC on 2019-07-25.
 */
public class OrderReceivedRequest {

    @SerializedName("invoice_id")
    @Expose
    private String invoiceId;
    @SerializedName("invoice_number")
    @Expose
    private int invoiceNumber;
    @SerializedName("satisfaction")
    @Expose
    private Integer satisfaction;

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public int getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(int invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public Integer getSatisfaction() {
        return satisfaction;
    }

    public void setSatisfaction(Integer satisfaction) {
        this.satisfaction = satisfaction;
    }

    public Map<String, Integer> getSatisfactionMap() {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("global", satisfaction);
        return map;
    }
}
