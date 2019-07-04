
package au.com.dealsdirect.data.network.model.ourpaydashboard.scheduledplans;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ScheduledPlan implements Parcelable {

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
    @SerializedName("TransactionId")
    @Expose
    private String transactionId;
    @SerializedName("BillingAgreementId")
    @Expose
    private String billingAgreementId;

    protected ScheduledPlan(Parcel in) {
        plannedDate = in.readString();
        if (in.readByte() == 0) {
            amount = null;
        } else {
            amount = in.readDouble();
        }
        currency = in.readString();
        state = in.readString();
        if (in.readByte() == 0) {
            number = null;
        } else {
            number = in.readInt();
        }
        orderNo = in.readString();
        paymentMethod = in.readString();
        name = in.readString();
        maskedNumber = in.readString();
        transactionId = in.readString();
        billingAgreementId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(plannedDate);
        if (amount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(amount);
        }
        dest.writeString(currency);
        dest.writeString(state);
        if (number == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(number);
        }
        dest.writeString(orderNo);
        dest.writeString(paymentMethod);
        dest.writeString(name);
        dest.writeString(maskedNumber);
        dest.writeString(transactionId);
        dest.writeString(billingAgreementId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ScheduledPlan> CREATOR = new Creator<ScheduledPlan>() {
        @Override
        public ScheduledPlan createFromParcel(Parcel in) {
            return new ScheduledPlan(in);
        }

        @Override
        public ScheduledPlan[] newArray(int size) {
            return new ScheduledPlan[size];
        }
    };

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

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getBillingAgreementId() {
        return billingAgreementId;
    }

    public void setBillingAgreementId(String billingAgreementId) {
        this.billingAgreementId = billingAgreementId;
    }

}
