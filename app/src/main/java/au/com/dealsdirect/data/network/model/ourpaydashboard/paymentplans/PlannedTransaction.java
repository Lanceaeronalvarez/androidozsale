
package au.com.dealsdirect.data.network.model.ourpaydashboard.paymentplans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlannedTransaction {

    @SerializedName("PlannedDate")
    @Expose
    private String plannedDate;
    @SerializedName("Amount")
    @Expose
    private Double amount;
    @SerializedName("Currency")
    @Expose
    private String currency;
    @SerializedName("State")
    @Expose
    private String state;
    @SerializedName("Number")
    @Expose
    private Integer number;

    public String getPlannedDate() {
        return plannedDate;
    }

    public void setPlannedDate(String plannedDate) {
        this.plannedDate = plannedDate;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

}
