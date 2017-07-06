
package au.com.dealsdirect.data.network.model.contactreply;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReplyContactResponse {

    @SerializedName("d")
    @Expose
    private ReplyContact replyContact;

    public ReplyContact getReplyContact() {
        return replyContact;
    }

    public void setReplyContact(ReplyContact replyContact) {
        this.replyContact = replyContact;
    }

}
