package au.com.dealsdirect.utils;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/*
 * Created by Ayi on 18/05/2017.
 */

public class DateUtils {

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

    public static Calendar convertApiDateToDateObject(String dateString) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat apiDateFormat = new SimpleDateFormat(AppConstants.API_DATE_FORMAT);

        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(apiDateFormat.parse(dateString));
            return calendar;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        } catch (NullPointerException e) {
            return null;
        }
    }

    public static String convertApiDateToDateString(String dateString) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat apiDateFormat = new SimpleDateFormat(AppConstants.API_DATE_FORMAT);
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat displayDateFormat = new SimpleDateFormat(AppConstants.MP_DATE_FORMAT);

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
        int hh = hour > 12 ? hour-12 : hour;
        hh = hh == 0 ? 12 : hh;
        String a = hour > 12 ? "PM" : "AM";
        String HH = hh > 10? hh+"" : "0"+hh;
        String mm = min > 10? min+"" : "0"+min;
        return HH+":"+mm+" "+a;
    }

}
