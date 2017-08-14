package au.com.dealsdirect.service.event;
/*
 * Created by CodeineBot on 8/11/17.
 */

import java.util.Arrays;

public class RegionType {

    private static final String[] ANZ = {"AS", "BA", "CA", "NZ", "BN", "CN", "EA", "OA", "DA", "TA", "GE"};
    private static final String[] RoW = {"UK", "CU", "US", "DK"};
    private static final String[] Asia = {"SI", "MY", "HK", "PH", "TH"};
    private static final String[] JV = {"LA", "LN"};

    public static String getType(String countryId) {

        if (Arrays.asList(ANZ).contains(countryId)) {
            return "ANZ";
        }

        if (Arrays.asList(RoW).contains(countryId)) {
            return "RoW";
        }

        if (Arrays.asList(Asia).contains(countryId)) {
            return "Asia";
        }

        if (Arrays.asList(JV).contains(countryId)) {
            return "JV";
        }

        return "";
    }
}
