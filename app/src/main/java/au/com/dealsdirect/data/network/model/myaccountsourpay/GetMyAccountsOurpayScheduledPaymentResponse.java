package au.com.dealsdirect.data.network.model.myaccountsourpay;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GetMyAccountsOurpayScheduledPaymentResponse implements Serializable {

    @SerializedName("ScheduledPayments")
    @Expose
    private ScheduledPayments mScheduledPayments;

    public ScheduledPayments getScheduledPayments() {
        return mScheduledPayments;
    }

    public void setScheduledPayments(ScheduledPayments ScheduledPayments) {
        mScheduledPayments = ScheduledPayments;
    }

    public class ScheduledPayments {
        @SerializedName("Message")
        @Expose
        private String mMessage;

        @SerializedName("IsAuthenticated")
        @Expose
        private String mIsAuthenticated;

        @SerializedName("Value")
        @Expose
        private Value[] mValue;

        @SerializedName("Result")
        @Expose
        private String Result;

        public String getMessage() {
            return mMessage;
        }

        public void setMessage(String Message) {
            mMessage = Message;
        }

        public String getIsAuthenticated() {
            return mIsAuthenticated;
        }

        public void setIsAuthenticated(String IsAuthenticated) {
            mIsAuthenticated = IsAuthenticated;
        }

        public Value[] getValue() {
            return mValue;
        }

        public void setValue(Value[] Value) {
            mValue = Value;
        }

        public String getResult() {
            return Result;
        }

        public void setResult(String Result) {
            Result = Result;
        }
    }

    public class Value {
        @SerializedName("Number")
        @Expose
        private String mNumber;

        @SerializedName("State")
        @Expose
        private String mState;

        @SerializedName("MaskedNumber")
        @Expose
        private String mMaskedNumber;

        @SerializedName("Amount")
        @Expose
        private String mAmount;

        @SerializedName("Currency")
        @Expose
        private String mCurrency;

        @SerializedName("PlannedDate")
        @Expose
        private String mPlannedDate;

        @SerializedName("PaymentMethod")
        @Expose
        private String mPaymentMethod;

        @SerializedName("OrderNo")
        @Expose
        private String mOrderNo;

        @SerializedName("Name")
        @Expose
        private String mName;

        public String getNumber() {
            return mNumber;
        }

        public void setNumber(String Number) {
            mNumber = Number;
        }

        public String getState() {
            return mState;
        }

        public void setState(String State) {
            mState = State;
        }

        public String getMaskedNumber() {
            return mMaskedNumber;
        }

        public void setMaskedNumber(String MaskedNumber) {
            mMaskedNumber = MaskedNumber;
        }

        public String getAmount() {
            return mAmount;
        }

        public void setAmount(String Amount) {
            mAmount = Amount;
        }

        public String getCurrency() {
            return mCurrency;
        }

        public void setCurrency(String Currency) {
            mCurrency = Currency;
        }

        public String getPlannedDate() {
            return mPlannedDate;
        }

        public void setPlannedDate(String PlannedDate) {
            mPlannedDate = PlannedDate;
        }

        public String getPaymentMethod() {
            return mPaymentMethod;
        }

        public void setPaymentMethod(String PaymentMethod) {
            mPaymentMethod = PaymentMethod;
        }

        public String getOrderNo() {
            return mOrderNo;
        }

        public void setOrderNo(String OrderNo) {
            mOrderNo = OrderNo;
        }

        public String getName() {
            return mName;
        }

        public void setName(String Name) {
            mName = Name;
        }
    }
}
