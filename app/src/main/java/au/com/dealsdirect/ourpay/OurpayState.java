package au.com.dealsdirect.ourpay;

public class OurpayState {
    public static final int DISABLED = 1<<0; // 0x01
    public static final int PRECART = 1<<1; // 0x02
    public static final int ONCART = 1<<2; // 0x04
    public static final int POSTCART = 1<<3; // 0x08
    public static final int ERROR = 1<<4; // 0x16

}
