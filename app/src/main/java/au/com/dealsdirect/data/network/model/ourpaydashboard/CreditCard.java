package au.com.dealsdirect.data.network.model.ourpaydashboard;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ayi on 19/09/2017.
 */

public class CreditCard implements Parcelable {

    String cardNumber;

    String cardHolder;

    String expiryDate;

    public CreditCard() {
        this.cardNumber = "4000 1234 5678 9010";
        this.cardHolder = "Mary Doe";
        this.expiryDate = "09/17";
    }

    protected CreditCard(Parcel in) {
        cardNumber = in.readString();
        cardHolder = in.readString();
        expiryDate = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cardNumber);
        dest.writeString(cardHolder);
        dest.writeString(expiryDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CreditCard> CREATOR = new Creator<CreditCard>() {
        @Override
        public CreditCard createFromParcel(Parcel in) {
            return new CreditCard(in);
        }

        @Override
        public CreditCard[] newArray(int size) {
            return new CreditCard[size];
        }
    };

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardHolder() {
        return cardHolder;
    }

    public void setCardHolder(String cardHolder) {
        this.cardHolder = cardHolder;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }
}
