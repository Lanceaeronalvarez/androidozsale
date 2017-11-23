package au.com.dealsdirect.data.network.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ayi on 14/09/2017.
 */

public class Payment implements Parcelable {

    String day;

    String month;

    String title;

    String id;

    int value;

    String currency;

    String cardNumber;

    public Payment(String title) {
        this.day = "";
        this.month = "";
        this.title = title;
        this.id = "95068491";
        this.value = 100;
        this.currency = "$";
        this.cardNumber = "54xxxxxxxx7688";
    }

    public Payment(String day, String month, String title) {
        this.day = day;
        this.month = month;
        this.title = title;
        this.id = "95068491";
        this.value = 100;
        this.currency = "$";
        this.cardNumber = "54xxxxxxxx7688";
    }

    protected Payment(Parcel in) {
        day = in.readString();
        month = in.readString();
        title = in.readString();
        id = in.readString();
        value = in.readInt();
        currency = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(day);
        dest.writeString(month);
        dest.writeString(title);
        dest.writeString(id);
        dest.writeInt(value);
        dest.writeString(currency);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Payment> CREATOR = new Creator<Payment>() {
        @Override
        public Payment createFromParcel(Parcel in) {
            return new Payment(in);
        }

        @Override
        public Payment[] newArray(int size) {
            return new Payment[size];
        }
    };

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }
}
