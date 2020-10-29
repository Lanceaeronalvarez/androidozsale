package au.com.dealsdirect.data.network.model.orders;

/**
 * Created by MTC on 2019-07-10.
 */
public class CancelInvoiceItemRequest {
    private String invoiceId = "";
    private int invoiceNumber = 0;
    private String orderItemId = "";
    private int quantity = 0;

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public int getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(int invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(String orderItemId) {
        this.orderItemId = orderItemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
