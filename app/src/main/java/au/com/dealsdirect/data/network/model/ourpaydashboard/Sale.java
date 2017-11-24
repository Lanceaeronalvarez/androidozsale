package au.com.dealsdirect.data.network.model.ourpaydashboard;

/**
 * Created by Ayi on 20/09/2017.
 */

public class Sale {

    String name;

    String brand;

    String currency;

    Double value;

    public Sale() {
        this.name = "Trendy Collections";
        this.brand = "Women's Fashion Fit";
        this.currency = "$";
        this.value = 45.00;
    }

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
