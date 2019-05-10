package au.com.dealsdirect.data.network.model.myaccountsourpay;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class GetMyAccountsOurpayPaymentPlansResponse implements Serializable {

    @SerializedName("Message")
    @Expose
    private String mMessage;

    @SerializedName("IsAuthenticated")
    @Expose
    private String mIsAuthenticated;

    @SerializedName("Value")
    @Expose
    private Value mValue;

    @SerializedName("Result")
    @Expose
    private String mResult;

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

    public Value getValue() {
        return mValue;
    }

    public void setValue(Value Value) {
        mValue = Value;
    }

    public String getResult() {
        return mResult;
    }

    public void setResult(String Result) {
        mResult = Result;
    }

    public class Value {
        @SerializedName("PaymentPlans")
        @Expose
        private List<PaymentPlans> mPaymentPlans;

        @SerializedName("RemainingCredit")
        @Expose
        private String mRemainingCredit;

        @SerializedName("Currency")
        @Expose
        private String mCurrency;

        @SerializedName("RemainingBalance")
        @Expose
        private String mRemainingBalance;

        @SerializedName("CurrencySign")
        @Expose
        private String mCurrencySign;

        @SerializedName("ActivePlansCount")
        @Expose
        private String mActivePlansCount;

        @SerializedName("OverduePlansCount")
        @Expose
        private String mOverduePlansCount;

        public List<PaymentPlans> getPaymentPlans() {
            return mPaymentPlans;
        }

        public void setPaymentPlans(List<PaymentPlans> PaymentPlans) {
            mPaymentPlans = PaymentPlans;
        }

        public String getRemainingCredit() {
            return mRemainingCredit;
        }

        public void setRemainingCredit(String RemainingCredit) {
            mRemainingCredit = RemainingCredit;
        }

        public String getCurrency() {
            return mCurrency;
        }

        public void setCurrency(String Currency) {
            mCurrency = Currency;
        }

        public String getRemainingBalance() {
            return mRemainingBalance;
        }

        public void setRemainingBalance(String RemainingBalance) {
            mRemainingBalance = RemainingBalance;
        }

        public String getCurrencySign() {
            return mCurrencySign;
        }

        public void setCurrencySign(String CurrencySign) {
            mCurrencySign = CurrencySign;
        }

        public String getActivePlansCount() {
            return mActivePlansCount;
        }

        public void setActivePlansCount(String ActivePlansCount) {
            mActivePlansCount = ActivePlansCount;
        }

        public String getOverduePlansCount() {
            return mOverduePlansCount;
        }

        public void setOverduePlansCount(String OverduePlansCount) {
            mOverduePlansCount = OverduePlansCount;
        }
    }

    public class PaymentPlans {
        @SerializedName("IsOverdue")
        @Expose
        private String mIsOverdue;

        @SerializedName("Currency")
        @Expose
        private String mCurrency;

        @SerializedName("CurrencySign")
        @Expose
        private String mCurrencySign;

        @SerializedName("PlannedTransactions")
        @Expose
        private List<PlannedTransactions> mPlannedTransactions;

        @SerializedName("OrderNo")
        @Expose
        private String mOrderNo;

        @SerializedName("RefundAmount")
        @Expose
        private String mRefundAmount;

        @SerializedName("ID")
        @Expose
        private String mId;

        @SerializedName("TotalAmount")
        @Expose
        private String mTotalAmount;

        @SerializedName("OrderBalance")
        @Expose
        private String mOrderBalance;

        @SerializedName("Name")
        @Expose
        private String mName;

        public String getIsOverdue() {
            return mIsOverdue;
        }

        public void setIsOverdue(String IsOverdue) {
            mIsOverdue = IsOverdue;
        }

        public String getCurrency() {
            return mCurrency;
        }

        public void setCurrency(String Currency) {
            mCurrency = Currency;
        }

        public String getCurrencySign() {
            return mCurrencySign;
        }

        public void setCurrencySign(String CurrencySign) {
            mCurrencySign = CurrencySign;
        }

        public List<PlannedTransactions> getPlannedTransactions() {
            return mPlannedTransactions;
        }

        public void setPlannedTransactions(List<PlannedTransactions> PlannedTransactions) {
            mPlannedTransactions = PlannedTransactions;
        }

        public String getOrderNo() {
            return mOrderNo;
        }

        public void setOrderNo(String OrderNo) {
            mOrderNo = OrderNo;
        }

        public String getRefundAmount() {
            return mRefundAmount;
        }

        public void setRefundAmount(String RefundAmount) {
            mRefundAmount = RefundAmount;
        }

        public String getID() {
            return mId;
        }

        public void setID(String ID) {
            mId = ID;
        }

        public String getTotalAmount() {
            return mTotalAmount;
        }

        public void setTotalAmount(String TotalAmount) {
            mTotalAmount = TotalAmount;
        }

        public String getOrderBalance() {
            return mOrderBalance;
        }

        public void setOrderBalance(String OrderBalance) {
            mOrderBalance = OrderBalance;
        }

        public String getName() {
            return mName;
        }

        public void setName(String Name) {
            mName = Name;
        }
    }

    public class PlannedTransactions {
        @SerializedName("Number")
        @Expose
        private String mNumber;

        @SerializedName("State")
        @Expose
        private String mState;

        @SerializedName("Amount")
        @Expose
        private String mAmount;

        @SerializedName("Currency")
        @Expose
        private String mCurrency;

        @SerializedName("PlannedDate")
        @Expose
        private String mPlannedDate;

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
    }
}
