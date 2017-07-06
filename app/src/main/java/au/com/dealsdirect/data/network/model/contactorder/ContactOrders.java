
package au.com.dealsdirect.data.network.model.contactorder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ContactOrders {

    @SerializedName("d")
    @Expose
    private ContactOrderResponse contactOrderResponse;

    public ContactOrderResponse getContactOrderResponse() {
        return contactOrderResponse;
    }

    public void setD(ContactOrderResponse contactOrderResponse) {
        this.contactOrderResponse = contactOrderResponse;
    }

}
