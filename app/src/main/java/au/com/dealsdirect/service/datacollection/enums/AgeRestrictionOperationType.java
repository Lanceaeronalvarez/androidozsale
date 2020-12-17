package au.com.dealsdirect.service.datacollection.enums;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum AgeRestrictionOperationType {
    OPEN(700),
    VALID(704),
    NOTVALID(705);

    private int value;

    private static final Map<Integer, AgeRestrictionOperationType> ENUM_MAP;

    AgeRestrictionOperationType(int value) {
        this.value = value;
    }

    static {
        Map<Integer, AgeRestrictionOperationType> map = new ConcurrentHashMap<Integer, AgeRestrictionOperationType>();
        for (AgeRestrictionOperationType instance : AgeRestrictionOperationType.values()) {
            map.put(instance.getValue(),instance);
        }
        ENUM_MAP = Collections.unmodifiableMap(map);
    }

    public int getValue() {
        return value;
    }

    public static AgeRestrictionOperationType fromNumber(int i) {
        return ENUM_MAP.get(i);
    }
}
