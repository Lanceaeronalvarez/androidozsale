
package au.com.dealsdirect.data.network.model.ourpaydashboard.paymentplans;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentPlan implements Parcelable {

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

    protected PaymentPlan(Parcel in) {
        iD = in.readString();
        name = in.readString();
        orderNo = in.readString();
        if (in.readByte() == 0) {
            orderBalance = null;
        } else {
            orderBalance = in.readDouble();
        }
        if (in.readByte() == 0) {
            totalAmount = null;
        } else {
            totalAmount = in.readDouble();
        }
        if (in.readByte() == 0) {
            refundAmount = null;
        } else {
            refundAmount = in.readInt();
        }
        currency = in.readString();
        currencySign = in.readString();
        byte tmpIsOverdue = in.readByte();
        isOverdue = tmpIsOverdue == 0 ? null : tmpIsOverdue == 1;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(iD);
        dest.writeString(name);
        dest.writeString(orderNo);
        if (orderBalance == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(orderBalance);
        }
        if (totalAmount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(totalAmount);
        }
        if (refundAmount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(refundAmount);
        }
        dest.writeString(currency);
        dest.writeString(currencySign);
        dest.writeByte((byte) (isOverdue == null ? 0 : isOverdue ? 1 : 2));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PaymentPlan> CREATOR = new Creator<PaymentPlan>() {
        @Override
        public PaymentPlan createFromParcel(Parcel in) {
            return new PaymentPlan(in);
        }

        @Override
        public PaymentPlan[] newArray(int size) {
            return new PaymentPlan[size];
        }
    };

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
