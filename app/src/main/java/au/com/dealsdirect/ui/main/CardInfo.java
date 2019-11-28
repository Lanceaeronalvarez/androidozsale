package au.com.dealsdirect.ui.main;

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
}
