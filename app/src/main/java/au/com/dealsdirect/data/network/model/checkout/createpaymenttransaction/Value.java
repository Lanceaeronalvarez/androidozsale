
package au.com.dealsdirect.data.network.model.checkout.createpaymenttransaction;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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
    private Object response;
    @SerializedName("threeDsStatus")
    @Expose
    private Object threeDsStatus;
    @SerializedName("transactionStatus")
    @Expose
    private String transactionStatus;

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

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
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

}
