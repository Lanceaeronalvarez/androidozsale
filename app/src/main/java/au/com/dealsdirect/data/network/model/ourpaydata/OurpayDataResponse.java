package au.com.dealsdirect.data.network.model.ourpaydata;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.GetCurrentOrderOurpay;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.PhoneVerification;
import io.reactivex.annotations.NonNull;

/*
Created by jpsmartwave 02/23/18 for ourpay schema update
 */

public class OurpayDataResponse {

    @SerializedName("summary")
    @Expose
    private Summary summary;
    @SerializedName("payment")
    @Expose
    private Payment payment;
    @SerializedName("settings")
    @Expose
    private Settings settings;
    @SerializedName("reasonCode")
    @Expose
    @NonNull
    private String reasonCode;
    @SerializedName("message")
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

        @SerializedName("firstTransactionAmount")
        @Expose
        private Double firstTransactionAmount;
        @SerializedName("plannedTransactionsAmount")
        @Expose
        private Double plannedTransactionsAmount;
        @SerializedName("firstTransactionText")
        @Expose
        private String firstTransactionText;
        @SerializedName("plannedTransactionsText")
        @Expose
        private String plannedTransactionsText;
        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("schedule")
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

        @SerializedName("billingAgreement")
        @Expose
        private BillingAgreement billingAgreement;
        @SerializedName("paymentConditions")
        @Expose
        private PaymentConditions paymentConditions;
        @SerializedName("firstTransactionAmount")
        @Expose
        private Double firstTransactionAmount;
        @SerializedName("billingPeriod")
        @Expose
        private Integer billingPeriod;
        @SerializedName("transactionCount")
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

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("plannedTransactions")
        @Expose
        private List<PlannedTransaction> plannedTransactions = null;

        public String getID() {
            return id;
        }

        public void setID(String id) {
            this.id = id;
        }

        public List<GetCurrentOrderOurpay.PlannedTransaction> getPlannedTransactions() {
            List<GetCurrentOrderOurpay.PlannedTransaction> currentOrderPlannedTransactions = new ArrayList<>();
            for (PlannedTransaction plannedTransaction : plannedTransactions) {
                currentOrderPlannedTransactions.add(plannedTransaction.convertToIntState());
            }
            return currentOrderPlannedTransactions;
        }

        public void setPlannedTransactions(List<PlannedTransaction> plannedTransactions) {
            this.plannedTransactions = plannedTransactions;
        }

    }

    public static class PaymentConditions {

        @SerializedName("maxAmountThreshold")
        @Expose
        private Integer maxAmountThreshold;
        @SerializedName("minAmountThreshold")
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

        @SerializedName("isOurPayEnabled")
        @Expose
        private Boolean isOurPayEnabled;
        @SerializedName("isOurPayThreeDSecureRequired")
        @Expose
        private Boolean isOurPayThreeDSecureRequired;
        @SerializedName("termsAndConditions")
        @Expose
        private String termsAndConditions;

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
            switch (termsAndConditions) {
                case "notShown":
                    return 2;
                case "defaultYes":
                    return 1;
                default:
                    return 0;
            }
        }

    }

    public class PlannedTransaction {

        @SerializedName("number")
        @Expose
        private Integer number;
        @SerializedName("amount")
        @Expose
        private Double amount;
        @SerializedName("plannedDate")
        @Expose
        private String plannedDate;
        @SerializedName("state")
        @Expose
        private String status;

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

        public String getStatus() {
            return status;
        }

        public void setStatus(String state) {
            this.status = state;
        }

        public Integer getNumber() {
            return number;
        }

        public void setNumber(Integer number) {
            this.number = number;
        }

        public int getState() {
            switch (status) {
                case "pending":
                    return 0;
                case "paid":
                    return 2;
                default:
                    return 0;
            }
        }

        public GetCurrentOrderOurpay.PlannedTransaction convertToIntState() {
            GetCurrentOrderOurpay.PlannedTransaction currentOrderPlannedTransaction =
                    new GetCurrentOrderOurpay.PlannedTransaction();
            currentOrderPlannedTransaction.setNumber(this.number);
            currentOrderPlannedTransaction.setAmount(this.amount);
            currentOrderPlannedTransaction.setPlannedDate(this.plannedDate);
            currentOrderPlannedTransaction.setState(this.getState());
            return currentOrderPlannedTransaction;
        }
    }
}