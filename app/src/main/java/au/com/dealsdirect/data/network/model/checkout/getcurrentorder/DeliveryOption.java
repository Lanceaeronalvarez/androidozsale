package au.com.dealsdirect.data.network.model.checkout.getcurrentorder;

/**
 * Created by smartwave on 29/05/2018.
 */
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeliveryOption {

    @SerializedName("DeliveryOptions")
    @Expose
    private List<String> deliveryOptions = null;
    @SerializedName("PostServiceId")
    @Expose
    private String postServiceId;
    @SerializedName("Price")
    @Expose
    private Double price;
    @SerializedName("Selected")
    @Expose
    private Boolean selected;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("Description")
    @Expose
    private String description;
    @SerializedName("OurpaySelect")
    @Expose
    private boolean ourPaySelect;
    @SerializedName("AgreedWithTerms")
    @Expose
    private boolean agreedWithTerms;

    public List<String> getDeliveryOptions() {
        return deliveryOptions;
    }

    public void setDeliveryOptions(List<String> deliveryOptions) {
        this.deliveryOptions = deliveryOptions;
    }

    public String getPostServiceId() {
        return postServiceId;
    }

    public void setPostServiceId(String postServiceId) {
        this.postServiceId = postServiceId;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setOurPaySelect(boolean ourPaySelect) {
        this.ourPaySelect = ourPaySelect;
    }

    public void setAgreedWithTerms(boolean agreedWithTerms) {
        this.agreedWithTerms = agreedWithTerms;
    }
}