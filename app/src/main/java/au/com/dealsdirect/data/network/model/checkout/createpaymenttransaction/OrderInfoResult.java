
package au.com.dealsdirect.data.network.model.checkout.createpaymenttransaction;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderInfoResult {

    @SerializedName("OrderID")
    @Expose
    private String orderID;
    @SerializedName("SaleName")
    @Expose
    private String saleName;
    @SerializedName("InvoiceNo")
    @Expose
    private String invoiceNo;
    @SerializedName("Total")
    @Expose
    private double total;
    @SerializedName("Tax")
    @Expose
    private double tax;
    @SerializedName("Shipping")
    @Expose
    private double shipping;
    @SerializedName("CustomerCity")
    @Expose
    private Object customerCity;
    @SerializedName("CustomerState")
    @Expose
    private String customerState;
    @SerializedName("CustomerCountry")
    @Expose
    private String customerCountry;
    @SerializedName("Items")
    @Expose
    private List<Item> items = null;
    @SerializedName("EstimatedDeliveryText")
    @Expose
    private String estimatedDeliveryText;
    @SerializedName("DeliveryDateFrom")
    @Expose
    private String deliveryDateFrom;
    @SerializedName("DeliveryDateTo")
    @Expose
    private String deliveryDateTo;

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getSaleName() {
        return saleName;
    }

    public void setSaleName(String saleName) {
        this.saleName = saleName;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public double getShipping() {
        return shipping;
    }

    public void setShipping(double shipping) {
        this.shipping = shipping;
    }

    public Object getCustomerCity() {
        return customerCity;
    }

    public void setCustomerCity(Object customerCity) {
        this.customerCity = customerCity;
    }

    public String getCustomerState() {
        return customerState;
    }

    public void setCustomerState(String customerState) {
        this.customerState = customerState;
    }

    public String getCustomerCountry() {
        return customerCountry;
    }

    public void setCustomerCountry(String customerCountry) {
        this.customerCountry = customerCountry;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public String getEstimatedDeliveryText() {
        return estimatedDeliveryText;
    }

    public void setEstimatedDeliveryText(String estimatedDeliveryText) {
        this.estimatedDeliveryText = estimatedDeliveryText;
    }

    public String getDeliveryDateFrom() {
        return deliveryDateFrom;
    }

    public void setDeliveryDateFrom(String deliveryDateFrom) {
        this.deliveryDateFrom = deliveryDateFrom;
    }

    public String getDeliveryDateTo() {
        return deliveryDateTo;
    }

    public void setDeliveryDateTo(String deliveryDateTo) {
        this.deliveryDateTo = deliveryDateTo;
    }

}
