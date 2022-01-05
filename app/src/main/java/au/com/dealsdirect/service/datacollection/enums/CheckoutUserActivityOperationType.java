package au.com.dealsdirect.service.datacollection.enums;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum CheckoutUserActivityOperationType {
    LPAY_BUTTON_CLICK(350),
    LPAY_BUTTON_CREATE_ORDER(351),
    LPAY_BUTTON_CREATE_CHARGE(352);

    private int value;

    private static final Map<Integer, CheckoutUserActivityOperationType> ENUM_MAP;

    CheckoutUserActivityOperationType(int value) {
        this.value = value;
    }

    static {
        Map<Integer, CheckoutUserActivityOperationType> map = new ConcurrentHashMap<Integer, CheckoutUserActivityOperationType>();
        for (CheckoutUserActivityOperationType instance : CheckoutUserActivityOperationType.values()) {
            map.put(instance.getValue(),instance);
        }
        ENUM_MAP = Collections.unmodifiableMap(map);
    }

    public int getValue() {
        return value;
    }

    public static CheckoutUserActivityOperationType fromNumber(int i) {
        return ENUM_MAP.get(i);
    }
}
