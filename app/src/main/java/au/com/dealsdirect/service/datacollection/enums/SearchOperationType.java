package au.com.dealsdirect.service.datacollection.enums;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum SearchOperationType {
    CHECKBOX(11),
    UNCHECKBOX(12),
    ADJUSTSLIDINGBAR(13),
    CATEGORYCLICK(14),
    ENTERTERM(15),

    EXPANDLIST(16),
    CONTRACTLIST(17),
    CLICKALLCATEGORIES(18),
    CLICKONTAG(19),
    CHECKINDROPDOWN(20),
    UNCHECKINDROPDOWN(21);

    private int value;

    private static final Map<Integer,SearchOperationType> ENUM_MAP;

    SearchOperationType(int value) {
        this.value = value;
    }

    static {
        Map<Integer,SearchOperationType> map = new ConcurrentHashMap<Integer, SearchOperationType>();
        for (SearchOperationType instance : SearchOperationType.values()) {
            map.put(instance.getValue(),instance);
        }
        ENUM_MAP = Collections.unmodifiableMap(map);
    }

    public int getValue() {
        return value;
    }

    public static SearchOperationType fromNumber(int i) {
        return ENUM_MAP.get(i);
    }
}
