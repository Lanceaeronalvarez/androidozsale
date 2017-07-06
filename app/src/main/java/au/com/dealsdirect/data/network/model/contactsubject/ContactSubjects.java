
package au.com.dealsdirect.data.network.model.contactsubject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ContactSubjects {

    @SerializedName("d")
    @Expose
    private ContactSubjectResponse contactSubjectResponse;

    public ContactSubjectResponse getContactSubjectResponse() {
        return contactSubjectResponse;
    }

    public void setD(ContactSubjectResponse contactSubjectResponse) {
        this.contactSubjectResponse = contactSubjectResponse;
    }

}
