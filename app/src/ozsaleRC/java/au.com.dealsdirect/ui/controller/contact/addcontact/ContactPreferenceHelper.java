package au.com.dealsdirect.ui.controller.contact.addcontact;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * dp Created by Admin on 8/4/16.
 */


public class ContactPreferenceHelper {

    private static final String PREF_FILE_NAME = "android_contact_pref_file";

    private static final String PREF_KEY_IS_SUBJECT = "android_chosen_subject";

    private static final String PREF_KEY_IS_ORDER = "android_chosen_order";

    private static final String PREF_KEY_INVOICE = "invoice";

    public ContactPreferenceHelper() {}

    public static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
    }


    public static SharedPreferences.Editor getPreferencesEditor(Context context){
        return getPreferences(context).edit();
    }


    public static void setChosenInvoiceString(Context context, String invoice){
        SharedPreferences.Editor editor = getPreferencesEditor(context);
        editor.putString(PREF_KEY_INVOICE, invoice);
        editor.commit();
        editor.apply();
    }



    public static String getChosenInvoice(Context context){
        return getPreferences(context).getString(PREF_KEY_INVOICE, "");
    }


    public static void setChosenSubjectString(Context context, String subject){
        SharedPreferences.Editor editor = getPreferencesEditor(context);
        editor.putString(PREF_KEY_IS_SUBJECT, subject);
        editor.commit();
        editor.apply();
    }


    public static String getChosenSubject(Context context){
        return getPreferences(context).getString(PREF_KEY_IS_SUBJECT, "");
    }

    public static void setChosenOrderString(Context context, String order){
        SharedPreferences.Editor editor = getPreferencesEditor(context);
        editor.putString(PREF_KEY_IS_ORDER, order);
        editor.commit();
        editor.apply();
    }

    public static String getChosenOrder(Context context){
        return getPreferences(context).getString(PREF_KEY_IS_ORDER, "");
    }

    public static void clear(Context context){
        SharedPreferences.Editor editor = getPreferencesEditor(context);
        editor.remove(PREF_KEY_IS_SUBJECT);
        editor.remove(PREF_KEY_IS_ORDER);
        editor.remove(PREF_KEY_INVOICE);
        editor.commit();
        editor.apply();
    }


}

