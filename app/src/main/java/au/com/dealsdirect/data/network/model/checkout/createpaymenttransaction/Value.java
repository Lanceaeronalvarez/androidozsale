
package au.com.dealsdirect.data.network.model.checkout.createpaymenttransaction;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.GetCurrentOrderOurpay;

public class Value {

    @SerializedName("AddressString")
    @Expose
    private String addressString;

    @SerializedName("invoiceNo")
    @Expose
    private String invoiceNo;
    @SerializedName("isPaid")
    @Expose
    private Boolean isPaid;


    @SerializedName("InvoiceNo")
    @Expose
    private int transactionInvoiceNo;
    @SerializedName("IsPaid")
    @Expose
    private Boolean transactionIsPaid;


    @SerializedName("liabilityShifted")
    @Expose
    private Object liabilityShifted;
    @SerializedName("liabilityShiftPossible")
    @Expose
    private Object liabilityShiftPossible;
    @SerializedName("OrderInfoResult")
    @Expose
    private OrderInfoResult orderInfoResult;
    @SerializedName("PaymentID")
    @Expose
    private String paymentID;
    @SerializedName("PaymentReceipt")
    @Expose
    private String paymentReceipt;
    @SerializedName("PaymentType")
    @Expose
    private Integer paymentType;
    @SerializedName("response")
    @Expose
    private String response;
    @SerializedName("threeDsStatus")
    @Expose
    private Object threeDsStatus;
    @SerializedName("transactionStatus")
    @Expose
    private String transactionStatus;

    @SerializedName("PlannedTransactions")
    @Expose
    private java.util.List<GetCurrentOrderOurpay.PlannedTransaction> plannedTransactions;
    @SerializedName("clientSecret")
    @Expose
    private String clientSecret;
    @SerializedName("payPalPayerEmail")
    @Expose
    private String payPalPayerEmail;
    @SerializedName("un")
    @Expose
    private String un;
    @SerializedName("errorMessage")
    @Expose
    private String errorMessage;

    public String getAddressString() {
        return addressString;
    }

    public void setAddressString(String addressString) {
        this.addressString = addressString;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public Boolean getIsPaid() {
        return isPaid;
    }

    public void setIsPaid(Boolean isPaid) {
        this.isPaid = isPaid;
    }

    public Object getLiabilityShifted() {
        return liabilityShifted;
    }

    public void setLiabilityShifted(Object liabilityShifted) {
        this.liabilityShifted = liabilityShifted;
    }

    public Object getLiabilityShiftPossible() {
        return liabilityShiftPossible;
    }

    public void setLiabilityShiftPossible(Object liabilityShiftPossible) {
        this.liabilityShiftPossible = liabilityShiftPossible;
    }

    public OrderInfoResult getOrderInfoResult() {
        return orderInfoResult;
    }

    public void setOrderInfoResult(OrderInfoResult orderInfoResult) {
        this.orderInfoResult = orderInfoResult;
    }

    public String getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(String paymentID) {
        this.paymentID = paymentID;
    }

    public String getPaymentReceipt() {
        return paymentReceipt;
    }

    public void setPaymentReceipt(String paymentReceipt) {
        this.paymentReceipt = paymentReceipt;
    }

    public Integer getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(Integer paymentType) {
        this.paymentType = paymentType;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Object getThreeDsStatus() {
        return threeDsStatus;
    }

    public void setThreeDsStatus(Object threeDsStatus) {
        this.threeDsStatus = threeDsStatus;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public Boolean getPaid() {
        return isPaid;
    }

    public void setPaid(Boolean paid) {
        isPaid = paid;
    }

    public java.util.List<GetCurrentOrderOurpay.PlannedTransaction> getPlannedTransactions() {
        return plannedTransactions;
    }

    public void setPlannedTransaction(java.util.List<GetCurrentOrderOurpay.PlannedTransaction> plannedTransactions) {
        this.plannedTransactions = plannedTransactions;
    }

    public int getTransactionInvoiceNo() {
        return transactionInvoiceNo;
    }

    public void setTransactionInvoiceNo(int transactionInvoiceNo) {
        this.transactionInvoiceNo = transactionInvoiceNo;
    }

    public Boolean getTransactionIsPaid() {
        return transactionIsPaid;
    }

    public void setTransactionIsPaid(Boolean transactionIsPaid) {
        this.transactionIsPaid = transactionIsPaid;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getPayPalPayerEmail() {
        return payPalPayerEmail;
    }

    public void setPayPalPayerEmail(String payPalPayerEmail) {
        this.payPalPayerEmail = payPalPayerEmail;
    }

    public String getUn() {
        return un;
    }

    public void setUn(String un) {
        this.un = un;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
