package au.com.dealsdirect.data.network.model.checkout.getcurrentorder;

/**
 * Created by smartwave on 29/05/2018.
 */
import androidx.annotation.NonNull;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeliveryOption {
    public static final String DELIVERY_OPTION_STANDARD = "STANDARD";
    public static final String DELIVERY_OPTION_EXPRESS = "EXPRESS";

    @SerializedName(value = "DeliveryOptions")
    @Expose
    private List<String> deliveryOptions = null;
    @SerializedName(value = "PostServiceId", alternate = {"postServiceId"})
    @Expose
    private String postServiceId;
    @SerializedName(value = "Price", alternate = {"price"})
    @Expose
    private Double price;
    @SerializedName(value = "Selected", alternate = {"selected"})
    @Expose
    private Boolean selected;
    @SerializedName(value = "Name", alternate = {"alternate"})
    @Expose
    private String name;
    @SerializedName(value = "Description", alternate = {"description"})
    @Expose
    private String description;
    @SerializedName(value = "AgreedWithTerms", alternate = {"agreedWithTerms"})
    @Expose
    private boolean agreedWithTerms;
    @SerializedName(value = "IsAvailable", alternate = {"isAvailable"})
    @Expose
    private boolean isAvailable;

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

    public void setAgreedWithTerms(boolean agreedWithTerms) {
        this.agreedWithTerms = agreedWithTerms;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}