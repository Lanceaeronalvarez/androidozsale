package au.com.dealsdirect.service.ourpay;

import android.text.format.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * dp Created by Admin on 8/8/17.
 */

public class OurpayUtils {

    public static int convertDaysToWeeks(int days) {
        return (int) Math.ceil((double)days/7);
    }

    public static String convertDateToTrimmedString(String dateString) {
        Date date = au.com.dealsdirect.utils.DateUtils.dateFromServerDateString(dateString);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM", Locale.US);

        if ( DateUtils.isToday(date.getTime())) {
            return "Today";
        } else {
            return sdf.format(date);
        }
    }
}
