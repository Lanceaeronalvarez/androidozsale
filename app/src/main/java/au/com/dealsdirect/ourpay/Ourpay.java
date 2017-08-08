package au.com.dealsdirect.ourpay;

import com.braintreepayments.api.models.PaymentMethodNonce;

import java.util.List;

import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.MyPayDetails;


/*
 * Created by CodeineBot on 9/28/16.
 */

public class Ourpay {

    private boolean canUse = false;
    private double amount = 0;
    private double userAmount = 0;
    private String details = "";
    private double minAmount;
    private double maxAmount;
    private String errorCode;
    private int transactionCount = 0;
    private int billingPeriod = 0;
    private List<MyPayDetails.PlannedTransaction> plannedTransactions;
    private int termsAndConditionsCheckboxState = 0;
    private int state = 0;
    private PaymentMethodNonce paymentMethodNonce;
    private String termsAndConditionsText = "";
    private OurpayPhoneVerification ourpayPhoneVerification;

    public Ourpay() {

    }

    public Ourpay(Ourpay ourpay) {
        this.canUse = ourpay.canUse;
        this.amount = ourpay.amount;
        this.userAmount = ourpay.userAmount;
        this.details = ourpay.details;
        this.minAmount = ourpay.minAmount;
        this.maxAmount = ourpay.maxAmount;
        this.errorCode = ourpay.errorCode;
        this.transactionCount = ourpay.transactionCount;
        this.billingPeriod = ourpay.billingPeriod;
        this.plannedTransactions = ourpay.plannedTransactions;
        this.termsAndConditionsCheckboxState = ourpay.termsAndConditionsCheckboxState;
        this.state = ourpay.state;
        this.paymentMethodNonce = ourpay.paymentMethodNonce;
        this.termsAndConditionsText = ourpay.termsAndConditionsText;
        this.ourpayPhoneVerification = ourpay.ourpayPhoneVerification;
    }

    public boolean isCanUse() {
        return canUse;
    }

    public double getAmount() {
        return amount;
    }

    public double getUserAmount() {
        return userAmount;
    }

    public String getDetails() {
        return details;
    }

    public double getMinAmount() {
        return minAmount;
    }

    public double getMaxAmount() {
        return maxAmount;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    public int getBillingPeriod() {
        return billingPeriod;
    }

    public List<MyPayDetails.PlannedTransaction> getPlannedTransactions() {
        return plannedTransactions;
    }

    public int getTermsAndConditionsCheckboxState() {
        return termsAndConditionsCheckboxState;
    }

    public void setCanUse(boolean canUse) {
        this.canUse = canUse;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setUserAmount(double userAmount) {
        this.userAmount = userAmount;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setMinAmount(double minAmount) {
        this.minAmount = minAmount;
    }

    public void setMaxAmount(double maxAmount) {
        this.maxAmount = maxAmount;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public void setTransactionCount(int transactionCount) {
        this.transactionCount = transactionCount;
    }

    public void setBillingPeriod(int billingPeriod) {
        this.billingPeriod = billingPeriod;
    }

    public void setPlannedTransactions(List<MyPayDetails.PlannedTransaction> plannedTransactions) {
        this.plannedTransactions = plannedTransactions;
    }

    public void setTermsAndConditionsCheckboxState(int termsAndConditionsCheckboxState) {
        this.termsAndConditionsCheckboxState = termsAndConditionsCheckboxState;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public PaymentMethodNonce getPaymentMethodNonce() {
        return paymentMethodNonce;
    }

    public void setPaymentMethodNonce(PaymentMethodNonce paymentMethodNonce) {
        this.paymentMethodNonce = paymentMethodNonce;
    }

    public String getTermsAndConditionsText() {
        return termsAndConditionsText;
    }

    public void setTermsAndConditionsText(String termsAndConditionsText) {
        this.termsAndConditionsText = termsAndConditionsText;
    }

    public OurpayPhoneVerification getOurpayPhoneVerification() {
        return ourpayPhoneVerification;
    }

    public void setOurpayPhoneVerification(OurpayPhoneVerification ourpayPhoneVerification) {
        this.ourpayPhoneVerification = ourpayPhoneVerification;
    }

    public boolean isPhoneVerificationRequired() {
        return (ourpayPhoneVerification != null && ourpayPhoneVerification.isRequired());
    }
}
