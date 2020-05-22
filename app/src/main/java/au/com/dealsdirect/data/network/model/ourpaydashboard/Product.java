package au.com.dealsdirect.data.network.model.ourpaydashboard;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Ayi on 20/09/2017.
 */

public class Product {

    @SerializedName("name")
    @Expose
    String name;
    @SerializedName("brand")
    @Expose
    String brand;
    @SerializedName("currency")
    @Expose
    String currency;
    @SerializedName("value")
    @Expose
    Double value;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
