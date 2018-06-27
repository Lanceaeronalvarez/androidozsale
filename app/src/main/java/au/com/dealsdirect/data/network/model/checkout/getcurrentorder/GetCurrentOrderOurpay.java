package au.com.dealsdirect.data.network.model.checkout.getcurrentorder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.PhoneVerification;
import io.reactivex.annotations.NonNull;

/*
Created by jpsmartwave 02/23/18 for ourpay schema update
 */

public class GetCurrentOrderOurpay {

    @SerializedName("Summary")
    @Expose
    private Summary summary;
    @SerializedName("Payment")
    @Expose
    private Payment payment;
    @SerializedName("Settings")
    @Expose
    private Settings settings;
    @SerializedName("ReasonCode")
    @Expose
    @NonNull
    private String reasonCode;
    @SerializedName("Message")
    @Expose
    @NonNull
    private String message;

    private int state;

    /* string equivalent of state */
    private String status;

    private String details;
    private String termsAndConditionsText;

    public PhoneVerification getPhoneVerification() {
        return phoneVerification;
    }

    public void setPhoneVerification(PhoneVerification phoneVerification) {
        this.phoneVerification = phoneVerification;
    }

    private PhoneVerification phoneVerification;

    public String getTermsAndConditionsText() {
        return termsAndConditionsText;
    }

    public void setTermsAndConditionsText(String termsAndConditionsText) {
        this.termsAndConditionsText = termsAndConditionsText;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Summary getSummary() {
        return summary;
    }

    public void setSummary(Summary summary) {
        this.summary = summary;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(@NonNull String reasonCode) {
        if (reasonCode != null) this.reasonCode = reasonCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        if (message != null)
            this.message = message;
    }


    public static class Summary {

        @SerializedName("FirstTransactionAmount")
        @Expose
        private Double firstTransactionAmount;
        @SerializedName("PlannedTransactionsAmount")
        @Expose
        private Double plannedTransactionsAmount;
        @SerializedName("FirstTransactionText")
        @Expose
        private String firstTransactionText;
        @SerializedName("PlannedTransactionsText")
        @Expose
        private String plannedTransactionsText;
        @SerializedName("Description")
        @Expose
        private String description;
        @SerializedName("Schedule")
        @Expose
        private List<PlannedTransaction> schedule = null;

        public Double getFirstTransactionAmount() {
            return firstTransactionAmount;
        }

        public void setFirstTransactionAmount(Double firstTransactionAmount) {
            this.firstTransactionAmount = firstTransactionAmount;
        }

        public Double getPlannedTransactionsAmount() {
            return plannedTransactionsAmount;
        }

        public void setPlannedTransactionsAmount(Double plannedTransactionsAmount) {
            this.plannedTransactionsAmount = plannedTransactionsAmount;
        }

        public String getFirstTransactionText() {
            return firstTransactionText;
        }

        public void setFirstTransactionText(String firstTransactionText) {
            this.firstTransactionText = firstTransactionText;
        }

        public String getPlannedTransactionsText() {
            return plannedTransactionsText;
        }

        public void setPlannedTransactionsText(String plannedTransactionsText) {
            if (plannedTransactionsText != null)
                this.plannedTransactionsText = plannedTransactionsText;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public List<PlannedTransaction> getSchedule() {
            return schedule;
        }

        public void setSchedule(List<PlannedTransaction> schedule) {
            this.schedule = schedule;
        }

    }

    public static class Payment {

        @SerializedName("BillingAgreement")
        @Expose
        private BillingAgreement billingAgreement;
        @SerializedName("PaymentConditions")
        @Expose
        private PaymentConditions paymentConditions;
        @SerializedName("FirstTransactionAmount")
        @Expose
        private Double firstTransactionAmount;
        @SerializedName("BillingPeriod")
        @Expose
        private Integer billingPeriod;
        @SerializedName("TransactionCount")
        @Expose
        private Integer transactionCount;

        public BillingAgreement getBillingAgreement() {
            return billingAgreement;
        }

        public void setBillingAgreement(BillingAgreement billingAgreement) {
            this.billingAgreement = billingAgreement;
        }

        public PaymentConditions getPaymentConditions() {
            return paymentConditions;
        }

        public void setPaymentConditions(PaymentConditions paymentConditions) {
            this.paymentConditions = paymentConditions;
        }

        public Double getFirstTransactionAmount() {
            return firstTransactionAmount;
        }

        public void setFirstTransactionAmount(Double firstTransactionAmount) {
            this.firstTransactionAmount = firstTransactionAmount;
        }

        public Integer getBillingPeriod() {
            return billingPeriod;
        }

        public void setBillingPeriod(Integer billingPeriod) {
            this.billingPeriod = billingPeriod;
        }

        public Integer getTransactionCount() {
            return transactionCount;
        }

        public void setTransactionCount(Integer transactionCount) {
            this.transactionCount = transactionCount;
        }

    }

    public static class BillingAgreement {

        @SerializedName("ID")
        @Expose
        private String id;
        @SerializedName("PlannedTransactions")
        @Expose
        private List<PlannedTransaction> plannedTransactions = null;

        public String getID() {
            return id;
        }

        public void setID(String id) {
            this.id = id;
        }

        public List<PlannedTransaction> getPlannedTransactions() {
            return plannedTransactions;
        }

        public void setPlannedTransactions(List<PlannedTransaction> plannedTransactions) {
            this.plannedTransactions = plannedTransactions;
        }

    }

    public static class PaymentConditions {

        @SerializedName("MaxAmountThreshold")
        @Expose
        private Integer maxAmountThreshold;
        @SerializedName("MinAmountThreshold")
        @Expose
        private Integer minAmountThreshold;

        public Integer getMaxAmountThreshold() {
            return maxAmountThreshold;
        }

        public void setMaxAmountThreshold(Integer maxAmountThreshold) {
            this.maxAmountThreshold = maxAmountThreshold;
        }

        public Integer getMinAmountThreshold() {
            return minAmountThreshold;
        }

        public void setMinAmountThreshold(Integer minAmountThreshold) {
            this.minAmountThreshold = minAmountThreshold;
        }

    }

    public static class Settings {

        @SerializedName("IsOurPayEnabled")
        @Expose
        private Boolean isOurPayEnabled;
        @SerializedName("IsOurPayThreeDSecureRequired")
        @Expose
        private Boolean isOurPayThreeDSecureRequired;
        @SerializedName("TermsAndConditions")
        @Expose
        private int termsAndConditions;

        public Boolean getIsOurPayEnabled() {
            return isOurPayEnabled;
        }

        public void setIsOurPayEnabled(Boolean isOurPayEnabled) {
            this.isOurPayEnabled = isOurPayEnabled;
        }

        public Boolean getIsOurPayThreeDSecureRequired() {
            return isOurPayThreeDSecureRequired;
        }

        public void setIsOurPayThreeDSecureRequired(Boolean isOurPayThreeDSecureRequired) {
            this.isOurPayThreeDSecureRequired = isOurPayThreeDSecureRequired;
        }

        public int getTermsAndConditions() {
            return termsAndConditions;
        }

        public void setTermsAndConditions(int termsAndConditions) {
            this.termsAndConditions = termsAndConditions;
        }

    }

    public static class PlannedTransaction {

        @SerializedName("Number")
        @Expose
        private Integer number;
        @SerializedName("Amount")
        @Expose
        private Double amount;
        @SerializedName("PlannedDate")
        @Expose
        private String plannedDate;
        @SerializedName("State")
        @Expose
        private int status;

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

        public int getState() {
            return status;
        }

        public void setState(int state) {
            this.status = state;
        }

        public Integer getNumber() {
            return number;
        }

        public void setNumber(Integer number) {
            this.number = number;
        }
    }
}