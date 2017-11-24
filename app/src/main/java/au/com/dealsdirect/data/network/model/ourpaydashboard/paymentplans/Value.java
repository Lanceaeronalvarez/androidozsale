
package au.com.dealsdirect.data.network.model.ourpaydashboard.paymentplans;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Value {

    @SerializedName("PaymentPlans")
    @Expose
    private List<PaymentPlan> paymentPlans = null;
    @SerializedName("Currency")
    @Expose
    private String currency;
    @SerializedName("CurrencySign")
    @Expose
    private String currencySign;
    @SerializedName("OverduePlansCount")
    @Expose
    private Integer overduePlansCount;
    @SerializedName("RemainingBalance")
    @Expose
    private Double remainingBalance;
    @SerializedName("ActivePlansCount")
    @Expose
    private Integer activePlansCount;
    @SerializedName("RemainingCredit")
    @Expose
    private Double remainingCredit;

    public List<PaymentPlan> getPaymentPlans() {
        return paymentPlans;
    }

    public void setPaymentPlans(List<PaymentPlan> paymentPlans) {
        this.paymentPlans = paymentPlans;
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

    public Integer getOverduePlansCount() {
        return overduePlansCount;
    }

    public void setOverduePlansCount(Integer overduePlansCount) {
        this.overduePlansCount = overduePlansCount;
    }

    public Double getRemainingBalance() {
        return remainingBalance;
    }

    public void setRemainingBalance(Double remainingBalance) {
        this.remainingBalance = remainingBalance;
    }

    public Integer getActivePlansCount() {
        return activePlansCount;
    }

    public void setActivePlansCount(Integer activePlansCount) {
        this.activePlansCount = activePlansCount;
    }

    public Double getRemainingCredit() {
        return remainingCredit;
    }

    public void setRemainingCredit(Double remainingCredit) {
        this.remainingCredit = remainingCredit;
    }

}
