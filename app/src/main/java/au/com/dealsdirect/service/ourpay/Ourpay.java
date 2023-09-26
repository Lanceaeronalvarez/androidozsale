package au.com.dealsdirect.service.ourpay;

//import com.braintreepayments.api.models.PaymentMethodNonce;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.GetCurrentOrderOurpay;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.PhoneVerification;


/*
 * Created by CodeineBot on 9/28/16.
 */

public class Ourpay {

    public enum OurpayMode {
        UNAVAILABLE(0),
        NORMAL(1),
        NOBUTTON(2),
        SHOWPOPUP(3);

        private int mode;

        private OurpayMode(int mode) {
            this.mode = mode;
        }

        public int getModeNumber() {
            return mode;
        }

        private static final Map<Integer, OurpayMode> lookup = new HashMap<Integer, OurpayMode>();

        static {
            for (OurpayMode s : EnumSet.allOf(OurpayMode.class)) {
                lookup.put(s.getModeNumber(), s);
            }
        }

        public static OurpayMode get(int modeNumber) {
            return lookup.get(modeNumber);
        }
    }

    private boolean mCanUse = false;
    private Double mInitialAmount = null;
    private Double mTotalAmount = null;
    private String mDetails = "";
    private Double mMinAmount;
    private Double mMaxAmount;
    private String mErrorCode;
    private int mTransactionCount = 0;
    private int mBillingPeriod = 0;
    private List<GetCurrentOrderOurpay.PlannedTransaction> mPlannedTransactions;
    private int mTermsAndConditionsCheckboxState = 0;
    private int mState = 0;
    public OurpayMode mMode = OurpayMode.UNAVAILABLE;
//    private PaymentMethodNonce mPaymentMethodNonce;
    private String mTermsAndConditionsText = "";
    private PhoneVerification mOurpayPhoneVerification;
    private String mDescription = "";
    private Double mFirstTransactionAmount = null;
    private Double mPlannedTransactionAmount = null;
    private String mFirstTransactionText = "";
    private String mPlannedTransactionText = "";

    public Ourpay() {

    }

    @Deprecated
    public boolean isCanUse() {
        return mMode != OurpayMode.UNAVAILABLE;
    }

    public Double getInitialAmount() {
        return mInitialAmount;
    }

    public Double getTotalAmount() {
        return mTotalAmount;
    }

    public String getDetails() {
        return mDetails;
    }

    public Double getMinAmount() {
        return mMinAmount;
    }

    public Double getMaxAmount() {
        return mMaxAmount;
    }

    public String getErrorCode() {
        return mErrorCode;
    }

    public int getTransactionCount() {
        return mTransactionCount;
    }

    public int getBillingPeriod() {
        return mBillingPeriod;
    }

    public List<GetCurrentOrderOurpay.PlannedTransaction> getPlannedTransactions() {
        return mPlannedTransactions;
    }

    public int getTermsAndConditionsCheckboxState() {
        return mTermsAndConditionsCheckboxState;
    }

    public void setCanUse(boolean canUse) {
        mCanUse = canUse;
    }

    public void setInitialAmount(@Nullable Double initialAmount) {
        mInitialAmount = initialAmount;
    }

    public void setTotalAmount(@Nullable Double totalAmount) {
        mTotalAmount = totalAmount;
    }

    public void setDetails(String details) {
        mDetails = details;
    }

    public void setMinAmount(@Nullable Double minAmount) {
        mMinAmount = minAmount;
    }

    public void setMaxAmount(@Nullable Double maxAmount) {
        mMaxAmount = maxAmount;
    }

    public void setErrorCode(String errorCode) {
        mErrorCode = errorCode;
    }

    public OurpayMode getMode() {
        return mMode;
    }

    public void setMode(OurpayMode mode) {
        this.mMode = mMode;
    }

    public void setMode(int mode) {
        OurpayMode m = OurpayMode.get(mode);
        this.mMode = m != null ? m : OurpayMode.UNAVAILABLE;
    }

    public void setTransactionCount(int transactionCount) {
        mTransactionCount = transactionCount;
    }

    public void setBillingPeriod(int billingPeriod) {
        mBillingPeriod = billingPeriod;
    }

    public void setPlannedTransactions(List<GetCurrentOrderOurpay.PlannedTransaction> plannedTransactions) {
        mPlannedTransactions = plannedTransactions;
    }

    public void setTermsAndConditionsCheckboxState(int termsAndConditionsCheckboxState) {
        mTermsAndConditionsCheckboxState = termsAndConditionsCheckboxState;
    }

    public int getState() {
        return mState;
    }

    public void setState(int state) {
        mState = state;
    }

//    public PaymentMethodNonce getPaymentMethodNonce() {
//        return mPaymentMethodNonce;
//    }

//    public void setPaymentMethodNonce(PaymentMethodNonce paymentMethodNonce) {
//        mPaymentMethodNonce = paymentMethodNonce;
//    }

    public String getTermsAndConditionsText() {
        return mTermsAndConditionsText;
    }

    public void setTermsAndConditionsText(String termsAndConditionsText) {
        mTermsAndConditionsText = termsAndConditionsText;
    }

    public PhoneVerification getOurpayPhoneVerification() {
        return mOurpayPhoneVerification;
    }

    public void setOurpayPhoneVerification(PhoneVerification ourpayPhoneVerification) {
        mOurpayPhoneVerification = ourpayPhoneVerification;
    }

    public boolean isPhoneVerificationRequired() {
        return (mOurpayPhoneVerification != null && mOurpayPhoneVerification.getRequired());
    }

    public String getDescription() {
        return mDescription;
    }


    public void setFirstTransactionAmount(@Nullable Double firstTransactionAmount) {
        mFirstTransactionAmount = firstTransactionAmount;
    }

    public void setPlannedTransactionAmount(@Nullable Double plannedTransactionAmount) {
        mPlannedTransactionAmount = plannedTransactionAmount;
    }

    public void setFirstTransactionText(String firstTransactionText) {
        mFirstTransactionText = firstTransactionText;
    }

    public void setPlannedTransactionText(String plannedTransactionText) {
        mPlannedTransactionText = plannedTransactionText;
    }

    public Double getFirstTransactionAmount() {
        return mFirstTransactionAmount;
    }

    public Double getPlannedTransactionAmount() {
        return mPlannedTransactionAmount;
    }

    public String getFirstTransactionText() {
        return mFirstTransactionText;
    }

    public String getPlannedTransactionText() {
        return mPlannedTransactionText;
    }

    public void setDescription(String description) {

        mDescription = description;
    }


}
