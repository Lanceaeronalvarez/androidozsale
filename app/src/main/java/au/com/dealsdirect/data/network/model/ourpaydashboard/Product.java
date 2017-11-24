package au.com.dealsdirect.data.network.model.ourpaydashboard;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ayi on 20/09/2017.
 */

public class Product implements Parcelable {

    String name;

    String brand;

    String currency;

    Double value;

    public Product() {
        this.name = "Lavish Alice";
        this.brand = "Women's Fashion Fit";
        this.currency = "$";
        this.value = 45.00;
    }

    protected Product(Parcel in) {
        name = in.readString();
        brand = in.readString();
        currency = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(brand);
        dest.writeString(currency);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

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
