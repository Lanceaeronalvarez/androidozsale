package au.com.dealsdirect.service.deliveryoptions;

public enum DeliveryOptions {
    STANDARD("STANDARD"),
    EXPRESS("EXPRESS"),
    OURPAYSELECT("OURPAYSELECT");

    private final String name;

    private DeliveryOptions(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }

    public String toString() {
        return this.name;
    }

    public static final String KEY_DELIVERYOPTION_EXPRESS_TITLE = "_DeliveryOption_EXPRESS_Title";
    public static final String KEY_DELIVERYOPTION_EXPRESS_DESCRIPTION = "_DeliveryOption_EXPRESS_Description";
    public static final String KEY_DELIVERYOPTION_STANDARD_TITLE = "_DeliveryOption_STANDARD_Title";
}