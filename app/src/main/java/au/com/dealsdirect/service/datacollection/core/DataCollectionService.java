package au.com.dealsdirect.service.datacollection.core;

import java.util.HashMap;

/**
 * Created by MTC on 4/4/19.
 */

public interface DataCollectionService {
    boolean hasEvent(String eventKey);
    void logEvent(String eventKey, HashMap<String, Object> parameters);
}
