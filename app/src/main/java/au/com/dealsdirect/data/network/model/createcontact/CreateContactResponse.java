package au.com.dealsdirect.data.network.model.createcontact;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreateContactResponse {

    @SerializedName("d")
    @Expose
    private CreateContact createContact;

    public CreateContact getCreateContact() {
        return createContact;
    }

    public void setCreateContact(CreateContact createContact) {
        this.createContact = createContact;
    }

}
