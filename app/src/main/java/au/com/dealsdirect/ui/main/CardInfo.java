package au.com.dealsdirect.ui.main;

import au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods.PaymentMethod;

/**
 * Created by MTC on 2019-11-11.
 */
public class CardInfo {

    public static String cardNumber;
    public static int cardMonth;
    public static int cardYear;
    public static String cardCVV;

    public static String getCardNumber() {
        return cardNumber;
    }

    public static void setCardNumber(String cardNumber) {
        CardInfo.cardNumber = cardNumber;
    }

    public static int getCardMonth() {
        return cardMonth;
    }

    public static void setCardMonth(int cardMonth) {
        CardInfo.cardMonth = cardMonth;
    }

    public static int getCardYear() {
        return cardYear;
    }

    public static void setCardYear(int cardYear) {
        CardInfo.cardYear = cardYear;
    }

    public static String getCardCVV() {
        return cardCVV;
    }

    public static void setCardCVV(String cardCVV) {
        CardInfo.cardCVV = cardCVV;
    }

    public static void clearCardInfo() {
        PaymentMethod paymentMethod = new PaymentMethod();

        CardInfo.cardNumber = "";
        CardInfo.cardMonth = 0;
        CardInfo.cardYear = 0;
        CardInfo.cardCVV = "";

        paymentMethod.setPaymentType(null);
        paymentMethod.setDescription(null);
        paymentMethod.setProviderType(null);
    }
}
