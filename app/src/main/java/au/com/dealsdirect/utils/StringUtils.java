package au.com.dealsdirect.utils;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import com.mysale.genie.utility.Prefs;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


/*
 * Created by Ayi on 02/06/2017.
 */

public class StringUtils {

//    public static void appendGrayTextToTeal(Context context, TextView textView, String grayText, String tealText) {
//        Spannable text1 = new SpannableString(grayText);
//        text1.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.gray_description_text)), 0, text1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        textView.setText(text1);
//        Spannable text2 = new SpannableString(tealText);
//        text2.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.teal_text)), 0, text2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        textView.append(text2);
//    }

    public static String toTitleCase(String str) {

        if (str == null) {
            return null;
        }

        boolean space = true;
        StringBuilder builder = new StringBuilder(str);
        final int len = builder.length();

        for (int i = 0; i < len; ++i) {
            char c = builder.charAt(i);
            if (space) {
                if (!Character.isWhitespace(c)) {
                    // Convert to title case and switch out of whitespace mode.
                    builder.setCharAt(i, Character.toTitleCase(c));
                    space = false;
                }
            } else if (Character.isWhitespace(c)) {
                space = true;
            } else {
                builder.setCharAt(i, Character.toLowerCase(c));
            }
        }

        return builder.toString();
    }


}
