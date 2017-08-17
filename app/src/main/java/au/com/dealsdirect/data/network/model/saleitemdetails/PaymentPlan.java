
package au.com.dealsdirect.data.network.model.saleitemdetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentPlan {

    @SerializedName("BillingPeriod")
    @Expose
    private int billingPeriod;
    @SerializedName("TransactionCount")
    @Expose
    private int transactionCount;

    public int getBillingPeriod() {
        return billingPeriod;
    }

    public void setBillingPeriod(int billingPeriod) {
        this.billingPeriod = billingPeriod;
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(int transactionCount) {
        this.transactionCount = transactionCount;
    }
}
