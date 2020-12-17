package au.com.dealsdirect.utils;

import android.annotation.SuppressLint;
import android.text.format.DateFormat;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/*
 * Created by Ayi on 18/05/2017.
 */

public class DateUtils {

    public static String[] months = new DateFormatSymbols().getMonths();

    public static final String GMT_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    public static final String GMT_FORMAT_NO_MILLISECONDS = "yyyy-MM-dd'T'HH:mm:ss";

    public static final String TIME_FORMAT = "HH:mm:ss";

    public static final String KEY_MILLI_SECONDS = "milliseconds";

    public static final String KEY_SECONDS = "seconds";

    public static final String KEY_MINUTES = "minutes";

    public static final String KEY_HOURS = "hours";

    public static final String KEY_DAYS = "days";

    public static final String KEY_WEEKS = "weeks";

    public static final int DATE_UTIL_MILLIS_TO_SEC = 1000;

    public static final int DATE_UTIL_MILLIS_TO_MIN = 60000;

    public static final int DATE_UTIL_MILLIS_TO_HOUR = 3600000;

    public static final int DATE_UTIL_MILLIS_TO_DAY = 86400000;

    public static final int DATE_UTIL_MILLIS_TO_WEEK = 604800000;

    private static HashMap<String, String> mDiffTimeMap = new HashMap<>();

    public static String convertStartEndDateToString(String startString, String endString) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat apiDateFormat = new SimpleDateFormat(AppConstants.API_DATE_FORMAT);
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat displayDateFormat = new SimpleDateFormat(AppConstants.MP_DATE_FORMAT);

        try {
            Date startDate = apiDateFormat.parse(startString);
            Date endDate = apiDateFormat.parse(endString);
            return displayDateFormat.format(startDate) + " - " + displayDateFormat.format(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        } catch (NullPointerException e) {
            return "";
        }
    }

    public static String convertDateToString(Date date) {
        return new SimpleDateFormat(AppConstants.MP_DATE_FORMAT).format(date);
    }

    public static Calendar convertApiEpochtoDateObject(String dateString) {
        int start = dateString.indexOf("(") + 1;
        int end = dateString.indexOf(")");
        String actualString;

        if (start >= 0 && start < dateString.length() && end >= 0) {
            actualString = dateString.substring(start, end);
        } else {
            actualString = dateString;
        }

        return convertEpochToDateObject(actualString);
    }

    public static Calendar convertEpochToDateObject(String epoch) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(Long.parseLong(epoch)));
        return calendar;
    }

    public static Calendar convertApiDateToDateObject(String dateString) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat apiDateFormat = new SimpleDateFormat(AppConstants.API_DATE_FORMAT);

        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(apiDateFormat.parse(dateString));
            return calendar;
        } catch (ParseException e) {
            e.printStackTrace();
            return convertApiEpochtoDateObject(dateString);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public static String convertApiDateToDateString(String dateString) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat apiDateFormat = new SimpleDateFormat(AppConstants.API_DATE_FORMAT);
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat displayDateFormat = new SimpleDateFormat(AppConstants.DD_DATE_FORMAT);

        try {
            return displayDateFormat.format(apiDateFormat.parse(dateString));
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        } catch (NullPointerException e) {
            return "";
        }
    }

    public static String convertApiDateToTimeString(String dateString) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat apiDateFormat = new SimpleDateFormat(AppConstants.API_DATE_FORMAT);
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat displayDateFormat = new SimpleDateFormat(AppConstants.MP_TIME_FORMAT);

        try {
            return displayDateFormat.format(apiDateFormat.parse(dateString));
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        } catch (NullPointerException e) {
            return "";
        }
    }

    public static boolean checkIfApiDate(String dateString) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat apiDateFormat = new SimpleDateFormat(AppConstants.API_DATE_FORMAT);

        try {
            apiDateFormat.parse(dateString);
            return true;
        } catch (ParseException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
    }

    public static String convertHourMinuteToString(int hour, int min) {
        int hh = hour > 12 ? hour - 12 : hour;
        hh = hh == 0 ? 12 : hh;
        String a = hour > 12 ? "PM" : "AM";
        String HH = hh > 10 ? hh + "" : "0" + hh;
        String mm = min > 10 ? min + "" : "0" + min;
        return HH + ":" + mm + " " + a;
    }

    public static Date gmtDateFromServerDateString(String dateString) {
        Date date;
        SimpleDateFormat format = new SimpleDateFormat(GMT_FORMAT, Locale.ENGLISH);
        try {
            date = format.parse(dateString);
        } catch (ParseException e) {
            date = new Date(getUTCFromServerDateString(dateString));
        }
        return date;
    }

    public static long getUTCFromServerDateString(String dateString) {
        String longString = "";
        try {
            longString = (String) dateString.subSequence(dateString.indexOf("(") + 1, dateString.indexOf("+"));
        } catch (Exception e) {
            longString = (String) dateString.subSequence(dateString.indexOf("(") + 1, dateString.indexOf(")"));
        }

        Long ms = Long.parseLong(longString, 10);

        return ms;
    }


    public static String getDayOfWeekFromDateString(String dateString) {
        if (null != dateString
                && !dateString.equalsIgnoreCase("null")
                && dateString.length() > 0) {
            Date date = DateUtils.gmtDateFromServerDateString(dateString);

            Calendar c = Calendar.getInstance();
            //Get current date
            Date dateNow = c.getTime();
            int weekNow = c.get(Calendar.WEEK_OF_YEAR);

            //Get date after 6 days
            c.add(Calendar.DATE, 6);
            Date dateFromNow = c.getTime();

            //Get sale week
            c.setTime(date);
            int weekOfSale = c.get(Calendar.WEEK_OF_YEAR);

            if (weekNow == weekOfSale || (date.after(dateNow) && date.before(dateFromNow)))
                return (String) android.text.format.DateFormat.format("EEEE", date);
            else
                return (String) android.text.format.DateFormat.format("d MMMM", date);
        } else {
            return "";
        }
    }

    public static Date dateFromServerDateString(String dateString) {
        try {
            if (checkIfGmtFormat(dateString, true)) {
                return new SimpleDateFormat(GMT_FORMAT, Locale.getDefault()).parse(dateString);
            } else if (checkIfGmtFormat(dateString, false)) {
                return new SimpleDateFormat(GMT_FORMAT_NO_MILLISECONDS, Locale.getDefault()).parse(dateString);
            }
        } catch (ParseException e) {
            return Calendar.getInstance().getTime();
        }

        String longString = "";
        try {
            longString = (String) dateString.subSequence(dateString.indexOf("(") + 1, dateString.indexOf("+"));
        } catch (Exception e) {
            longString = (String) dateString.subSequence(dateString.indexOf("(") + 1, dateString.indexOf(")"));
        }

        Long ms = Long.parseLong(longString, 10);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(ms);

        return cal.getTime();
    }

    public static boolean checkIfGmtFormat(String dateString, boolean hasMilliseconds) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat apiDateFormat = new SimpleDateFormat(hasMilliseconds ? GMT_FORMAT : GMT_FORMAT_NO_MILLISECONDS);

        try {
            apiDateFormat.parse(dateString);
            return true;
        } catch (ParseException | NullPointerException e) {
            return false;
        }
    }

    public static String getDateForOrderProgress(String dateString) {
        if (null != dateString
                && !dateString.equalsIgnoreCase("null")
                && dateString.length() > 0) {
            Date date = dateFromServerDateString(dateString);

            return (String) DateFormat.format("dd MMM", date);//(String) date.toString().subSequence(4, date.toString().indexOf("GMT"));
        } else {
            return "";
        }
    }

    public static String getDateForContactMessages(String dateString) {
        if (null != dateString
                && !dateString.equalsIgnoreCase("null")
                && !dateString.isEmpty()) {
            Date date = dateFromServerDateString(dateString);

            return (String) DateFormat.format("MMM dd", date);//(String) date.toString().subSequence(4, date.toString().indexOf("GMT"));
        } else {
            return "";
        }
    }

    public static String getTimeFromDateString(String dateString) {

        if (null != dateString
                && !dateString.equalsIgnoreCase("null")
                && dateString.length() > 0) {
            Date date = DateUtils.gmtDateFromServerDateString(dateString);
            return (String) android.text.format.DateFormat.format("hh:mm AA", date).toString();//(String) date.toString().subSequence(4, date.toString().indexOf("GMT"));
        } else {
            return "";
        }
    }

    public static String getTrimmedServerDateString(String dateString) {
        if (null != dateString
                && !dateString.equalsIgnoreCase("null")
                && dateString.length() > 0) {
            Date date = dateFromServerDateString(dateString);
            android.text.format.DateFormat.format("MM dd, yyyy", date);

            return (String) DateFormat.format("MMMM dd, yyyy", date);//(String) date.toString().subSequence(4, date.toString().indexOf("GMT"));
        } else {
            return "";
        }
    }

    public static String getTrimmedServerDateStringOrders(String dateString) {
        if (null != dateString
                && !dateString.equalsIgnoreCase("null")
                && dateString.length() > 0) {
            Date date = dateFromServerDateString(dateString);
            android.text.format.DateFormat.format(AppConstants.MP2_DATE_FORMAT, date);

            return (String) DateFormat.format(AppConstants.MP2_DATE_FORMAT, date);//(String) date.toString().subSequence(4, date.toString().indexOf("GMT"));
        } else {
            return "";
        }
    }

    public static String getDateStringFromCalendar(Calendar calendar) {
        return months[calendar.get(Calendar.MONTH)] + " "
                + calendar.get(Calendar.DAY_OF_MONTH) + ", "
                + calendar.get(Calendar.YEAR);
    }

    public static String getDateStringWithTimeZone(Date date) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");

        return df.format(date);
    }

    public static String getServerDateStringWithTimeZone(Date date) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat df = new SimpleDateFormat(GMT_FORMAT);

        return df.format(date);
    }

    public static String getDateFromStringInFormat(String dateString, String format) {
        if (null != dateString
                && !dateString.equalsIgnoreCase("null")
                && dateString.length() > 0) {
            Date date = dateFromServerDateString(dateString);

            return (String) DateFormat.format(format, date);//(String) date.toString().subSequence(4, date.toString().indexOf("GMT"));
        } else {
            return "";
        }
    }

    public static HashMap<String, String> timeDivision(long remainingDiffInMilliSeconds) {
        try {

            //get hours
            long hours = remainingDiffInMilliSeconds / DATE_UTIL_MILLIS_TO_HOUR;

            //get days
            long days;
            days = hours / 24;
            hours -= days * 24;

            //get weeks
            long weeks = days / 7;
            days -= weeks * 7;

            mDiffTimeMap.put(KEY_HOURS, String.valueOf(hours));
            mDiffTimeMap.put(KEY_WEEKS, String.valueOf(weeks));
            mDiffTimeMap.put(KEY_DAYS, String.valueOf(days));

            return mDiffTimeMap;
        } catch (NullPointerException e) {
            return null;
        }

    }

    public static boolean isLessThanADay(long remainingDiffInMilliSeconds) {
        //get hours
        long hours = remainingDiffInMilliSeconds / DATE_UTIL_MILLIS_TO_HOUR;
        return hours < 24;
    }

    public static boolean isWithin48Hours(long remainingDiffInMilliSeconds) {
        //get hours
        long hours = remainingDiffInMilliSeconds / DATE_UTIL_MILLIS_TO_HOUR;
        return hours <= 48 && remainingDiffInMilliSeconds > 0;
    }

    public static String getRemainingTimeValue(long remainingDiffInMilliSeconds) {
        HashMap<String, String> map = timeDivision(remainingDiffInMilliSeconds);
        String remainingTextViewValue = "";
        boolean isGreaterThanTwoDays = !map.get(KEY_DAYS).isEmpty() && Integer.valueOf(map.get(KEY_DAYS)) > 2;
        boolean isLessThanADay = Integer.valueOf(map.get(KEY_DAYS)) < 1;
        if (!isGreaterThanTwoDays && !isLessThanADay) {
            remainingTextViewValue = map.get(KEY_DAYS) + "d " + map.get(DateUtils.KEY_HOURS) + 'h';
            return remainingTextViewValue;
        } else {
            return getRemainingTimeInTimeFormat(remainingDiffInMilliSeconds);
        }
    }

    public static String getRemainingTimeInWeeks(long remainingTimeInMilliSeconds) {
        HashMap<String, String> map = timeDivision(remainingTimeInMilliSeconds);
        String remainingTextViewValue = "";
        boolean isWeekGreaterThanZero = (map != null && !map.get(KEY_WEEKS).isEmpty() && Integer.valueOf(map.get(KEY_WEEKS)) > 0);
        boolean isDayLessThanOne = (map != null && !map.get(KEY_DAYS).isEmpty() && Integer.valueOf(map.get(KEY_DAYS)) < 1);
        if (!isWeekGreaterThanZero && !isDayLessThanOne) {
            remainingTextViewValue = map.get(KEY_DAYS) + "d " + map.get(DateUtils.KEY_HOURS) + 'h';
            return remainingTextViewValue;
        } else if (isWeekGreaterThanZero) {
            remainingTextViewValue = map.get(KEY_WEEKS) + "w " + map.get(DateUtils.KEY_DAYS) + 'd';
            return remainingTextViewValue;
        } else {
            return getRemainingTimeInTimeFormat(remainingTimeInMilliSeconds);
        }
    }

    public static long getRemainingTimeInMillis(String endDate) {
        TimeZone timeZone = TimeZone.getDefault();
        SimpleDateFormat sdf = new SimpleDateFormat(AppConstants.API_DATE_FORMAT, Locale.ENGLISH);
        sdf.setTimeZone(timeZone);
        Date date = new Date();

        try {
            long remainingDiffInMilliSeconds = sdf.parse(endDate).getTime() - date.getTime();
            return remainingDiffInMilliSeconds;
        } catch (ParseException | NullPointerException e) {
            return 0;
        }
    }

    public static String getRemainingTimeInTimeFormat(long milliSeconds) {
        long seconds = milliSeconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        return String.format("%02d:%02d:%02d", hours % 24, minutes % 60, seconds % 60);
    }

    public static boolean hasDayPassed(int pastDay) {
        Calendar calander = Calendar.getInstance();
        int currentDay = calander.get(Calendar.DAY_OF_YEAR);
        return pastDay != currentDay;
    }

    public static int yearsBetweenCalendar(Calendar start, Calendar end) {
        final long differenceInMilliseconds = end.getTimeInMillis() - start.getTimeInMillis();
        final long differenceInDays = TimeUnit.MILLISECONDS.toDays(differenceInMilliseconds);
        final int numberOfLeapYears = leapYearsBetween(start.get(Calendar.YEAR), end.get(Calendar.YEAR), true);
        final int numberOfYears = end.get(Calendar.YEAR) - start.get(Calendar.YEAR);
        final double ratio = (double) numberOfLeapYears / (double) numberOfYears;
        final double actualYears = ((differenceInDays / 365d) * (1 - ratio) + (differenceInDays / 366d) * ratio);
        return (int) Math.floor(actualYears);
    }

    public static int leapYearsBetween(int start, int end, boolean inclusive)
    {
        if (inclusive) {
            start--;
            end++;
        }
        if (start > end || start <= 0) {
            return -1;
        }
        // source: https://stackoverflow.com/a/4587611
        return totalNumberOfLeapYears(end) - totalNumberOfLeapYears(start + 1);
    }

    private static int totalNumberOfLeapYears(int year)
    {
        // source: https://stackoverflow.com/a/4587611
        year--;
        return (year / 4) - (year / 100) + (year / 400);
    }
}
