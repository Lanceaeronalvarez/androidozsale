
package au.com.dealsdirect.data.network.model.ourpaydashboard.paymentplans;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentPlan {

    @SerializedName("ID")
    @Expose
    private String iD;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("OrderNo")
    @Expose
    private String orderNo;
    @SerializedName("OrderBalance")
    @Expose
    private Double orderBalance;
    @SerializedName("TotalAmount")
    @Expose
    private Double totalAmount;
    @SerializedName("RefundAmount")
    @Expose
    private Integer refundAmount;
    @SerializedName("Currency")
    @Expose
    private String currency;
    @SerializedName("CurrencySign")
    @Expose
    private String currencySign;
    @SerializedName("IsOverdue")
    @Expose
    private Boolean isOverdue;
    @SerializedName("PlannedTransactions")
    @Expose
    private List<PlannedTransaction> plannedTransactions = null;

    public String getID() {
        return iD;
    }

    public void setID(String iD) {
        this.iD = iD;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Double getOrderBalance() {
        return orderBalance;
    }

    public void setOrderBalance(Double orderBalance) {
        this.orderBalance = orderBalance;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(Integer refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCurrencySign() {
        return currencySign;
    }

    public void setCurrencySign(String currencySign) {
        this.currencySign = currencySign;
    }

    public Boolean getIsOverdue() {
        return isOverdue;
    }

    public void setIsOverdue(Boolean isOverdue) {
        this.isOverdue = isOverdue;
    }

    public List<PlannedTransaction> getPlannedTransactions() {
        return plannedTransactions;
    }

    public void setPlannedTransactions(List<PlannedTransaction> plannedTransactions) {
        this.plannedTransactions = plannedTransactions;
    }

}
