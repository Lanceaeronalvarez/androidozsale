package au.com.dealsdirect.data.network.model.afterpay;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetAfterpayDataResponse {
    @SerializedName("applicabilityStatus")
    @Expose
    private String mApplicabilityStatus;

    @SerializedName("paymentInfo")
    @Expose
    private PaymentInfo mPaymentInfo;

    @SerializedName("thresholdInfo")
    @Expose
    private ThresholdInfo mTresholdInfo;

    public String getApplicabilityStatus() {
        return mApplicabilityStatus;
    }

    public void setApplicabilityStatus(String applicabilityStatus) {
        mApplicabilityStatus = applicabilityStatus;
    }

    public PaymentInfo getPaymentInfo() {
        return mPaymentInfo;
    }

    public void setPaymentInfo(PaymentInfo paymentInfo) {
        mPaymentInfo = paymentInfo;
    }

    public ThresholdInfo getTresholdInfo() {
        return mTresholdInfo;
    }

    public void setTresholdInfo(ThresholdInfo tresholdInfo) {
        mTresholdInfo = tresholdInfo;
    }

    public class PaymentInfo {
        @SerializedName("paymentsCount")
        @Expose
        private int mPaymentsCount;

        @SerializedName("paymentsAmount")
        @Expose
        private double mPaymentsAmount;

        @SerializedName("currency")
        @Expose
        private String mCurrency;

        public int getPaymentsCount() {
            return mPaymentsCount;
        }

        public void setPaymentsCount(int paymentsCount) {
            mPaymentsCount = paymentsCount;
        }

        public double getPaymentsAmount() {
            return mPaymentsAmount;
        }

        public void setPaymentsAmount(double paymentsAmount) {
            mPaymentsAmount = paymentsAmount;
        }

        public String getCurrency() {
            return mCurrency;
        }

        public void setCurrency(String Currency) {
            mCurrency = Currency;
        }
    }

    public class ThresholdInfo {
        @SerializedName("maximumAmount")
        @Expose
        private double mMaximumAmount;

        @SerializedName("minimumAmount")
        @Expose
        private double mMinimimmount;

        @SerializedName("currency")
        @Expose
        private String mCurrency;

        public double getMaximumAmount() {
            return mMaximumAmount;
        }

        public void setMaximumAmount(double maximumAmount) {
            mMaximumAmount = maximumAmount;
        }

        public double getMinimimmount() {
            return mMinimimmount;
        }

        public void setMinimimmount(double minimimmount) {
            mMinimimmount = minimimmount;
        }

        public String getCurrency() {
            return mCurrency;
        }

        public void setCurrency(String Currency) {
            mCurrency = Currency;
        }
    }
}
