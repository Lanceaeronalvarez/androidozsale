
package au.com.dealsdirect.data.network.model.ourpaydashboard.scheduledpayments;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ScheduledPayment {

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
    @SerializedName("OrderNo")
    @Expose
    private String orderNo;
    @SerializedName("PaymentMethod")
    @Expose
    private String paymentMethod;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("MaskedNumber")
    @Expose
    private String maskedNumber;

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

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMaskedNumber() {
        return maskedNumber;
    }

    public void setMaskedNumber(String maskedNumber) {
        this.maskedNumber = maskedNumber;
    }

}
