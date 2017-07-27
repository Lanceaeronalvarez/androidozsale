package au.com.dealsdirect.utils;

/*
 * Created by CodeineBot on 2/27/17.
 */

public class CartUtil {

    private static int CART_ITEM_COUNT = 0;

    public static void addValueToCart(int value) {
        CART_ITEM_COUNT = CART_ITEM_COUNT + value;
    }

    public static void setValueToCart(int value) {
        CART_ITEM_COUNT = value;
    }

    public static int getCartValue() {
        return CART_ITEM_COUNT;
    }
}