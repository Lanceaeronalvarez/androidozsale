package au.com.dealsdirect.data.network.model.checkout.getcurrentorder;
/*
 * Created by CodeineBot on 1/6/17.
 */

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MyPayDetails {
    public Boolean getEnabled() {
        return enabled;
    }

    public String getMessage() {
        return message;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    public Integer getTermsAndConditions() {
        return termsAndConditions;
    }

    public PaymentConditions getPaymentConditions() {
        return paymentConditions;
    }

    public Double getAmount() { return amount; }

    @SerializedName("Enabled")
    public Boolean enabled;
    @SerializedName("Message")
    public String message;
    @SerializedName("ReasonCode")
    public String reasonCode;
    @SerializedName("TermsAndConditions")
    public Integer termsAndConditions;
    @SerializedName("PaymentConditions")
    public PaymentConditions paymentConditions;
    @SerializedName("BillingAgreement")
    private BillingAgreement billingAgreement;
    @SerializedName("Amount")
    private Double amount;
    @SerializedName("BillingPeriod")
    private BillingPeriod billingPeriod;
    @SerializedName("TransactionCount")
    private Integer transactionCount;
    @SerializedName("PaymentSchemeDescription")
    private String paymentSchemeDescription;
    @SerializedName("IsOurPayThreeDSecureRequired")
    private Boolean isOurPayThreeDSecureRequired;

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }

    public void setTermsAndConditions(Integer termsAndConditions) {
        this.termsAndConditions = termsAndConditions;
    }

    public void setPaymentConditions(PaymentConditions paymentConditions) {
        this.paymentConditions = paymentConditions;
    }

    public BillingAgreement getBillingAgreement() {
        return billingAgreement;
    }

    public void setBillingAgreement(BillingAgreement billingAgreement) {
        this.billingAgreement = billingAgreement;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public BillingPeriod getBillingPeriod() {
        return billingPeriod;
    }

    public void setBillingPeriod(BillingPeriod billingPeriod) {
        this.billingPeriod = billingPeriod;
    }

    public Integer getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(Integer transactionCount) {
        this.transactionCount = transactionCount;
    }

    public String getPaymentSchemeDescription() {
        return paymentSchemeDescription;
    }

    public void setPaymentSchemeDescription(String paymentSchemeDescription) {
        this.paymentSchemeDescription = paymentSchemeDescription;
    }

    public Boolean getOurPayThreeDSecureRequired() {
        return isOurPayThreeDSecureRequired;
    }

    public void setOurPayThreeDSecureRequired(Boolean ourPayThreeDSecureRequired) {
        isOurPayThreeDSecureRequired = ourPayThreeDSecureRequired;
    }

    public class BillingPeriod {

        @SerializedName("Ticks")
        private Integer ticks;

        @SerializedName("Days")
        private Integer days;

        @SerializedName("Hours")
        private Integer hours;

        @SerializedName("Milliseconds")
        private Integer milliseconds;
        @SerializedName("Minutes")
        private Integer minutes;

        @SerializedName("Seconds")
        private Integer seconds;

        @SerializedName("TotalDays")
        private Integer totalDays;

        @SerializedName("TotalHours")
        private Integer totalHours;

        @SerializedName("TotalMilliseconds")
        private Integer totalMilliseconds;

        @SerializedName("TotalMinutes")
        private Integer totalMinutes;

        @SerializedName("TotalSeconds")
        private Integer totalSeconds;

        public Integer getTicks() {
            return ticks;
        }

        public void setTicks(Integer ticks) {
            this.ticks = ticks;
        }

        public Integer getDays() {
            return days;
        }

        public void setDays(Integer days) {
            this.days = days;
        }

        public Integer getHours() {
            return hours;
        }

        public void setHours(Integer hours) {
            this.hours = hours;
        }

        public Integer getMilliseconds() {
            return milliseconds;
        }

        public void setMilliseconds(Integer milliseconds) {
            this.milliseconds = milliseconds;
        }

        public Integer getMinutes() {
            return minutes;
        }

        public void setMinutes(Integer minutes) {
            this.minutes = minutes;
        }

        public Integer getSeconds() {
            return seconds;
        }

        public void setSeconds(Integer seconds) {
            this.seconds = seconds;
        }

        public Integer getTotalDays() {
            return totalDays;
        }

        public void setTotalDays(Integer totalDays) {
            this.totalDays = totalDays;
        }

        public Integer getTotalHours() {
            return totalHours;
        }

        public void setTotalHours(Integer totalHours) {
            this.totalHours = totalHours;
        }

        public Integer getTotalMilliseconds() {
            return totalMilliseconds;
        }

        public void setTotalMilliseconds(Integer totalMilliseconds) {
            this.totalMilliseconds = totalMilliseconds;
        }

        public Integer getTotalMinutes() {
            return totalMinutes;
        }

        public void setTotalMinutes(Integer totalMinutes) {
            this.totalMinutes = totalMinutes;
        }

        public Integer getTotalSeconds() {
            return totalSeconds;
        }

        public void setTotalSeconds(Integer totalSeconds) {
            this.totalSeconds = totalSeconds;
        }

    }

    public class BillingAgreement {

        @SerializedName("ID")
        private String iD;
        @SerializedName("PlannedTransactions")
        private List<PlannedTransaction> plannedTransactions = null;

        public String getID() {
            return iD;
        }

        public void setID(String iD) {
            this.iD = iD;
        }

        public List<PlannedTransaction> getPlannedTransactions() {
            return plannedTransactions;
        }

        public void setPlannedTransactions(List<PlannedTransaction> plannedTransactions) {
            this.plannedTransactions = plannedTransactions;
        }
    }

    public class PlannedTransaction {

        @SerializedName("Number")
        private Integer number;
        @SerializedName("Amount")
        private Double amount;
        @SerializedName("PlannedDate")
        private String plannedDate;
        @SerializedName("State")
        private Integer state;

        public Integer getNumber() {
            return number;
        }

        public void setNumber(Integer number) {
            this.number = number;
        }

        public Double getAmount() {
            return amount;
        }

        public void setAmount(Double amount) {
            this.amount = amount;
        }

        public String getPlannedDate() {
            return plannedDate;
        }

        public void setPlannedDate(String plannedDate) {
            this.plannedDate = plannedDate;
        }

        public Integer getState() {
            return state;
        }

        public void setState(Integer state) {
            this.state = state;
        }
    }
}
