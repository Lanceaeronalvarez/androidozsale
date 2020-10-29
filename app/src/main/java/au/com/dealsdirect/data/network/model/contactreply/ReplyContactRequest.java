package au.com.dealsdirect.data.network.model.contactreply;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * dp Created by Admin on 1/13/17.
 */
public class ReplyContactRequest {

    private transient Integer number;

    @SerializedName("text")
    @Expose
    private String text;

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
