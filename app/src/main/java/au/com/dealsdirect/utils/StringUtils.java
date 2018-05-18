package au.com.dealsdirect.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import com.mysale.genie.utility.Prefs;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Set;

import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsRequest;


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

    public static String getInitials(String text) {
        String initialLetters = "";
        text = text.replaceAll("[.,]", " "); // Replace dots, etc (optional)
        for (String s : text.split(" ")) {
            if (!s.equals("")) initialLetters += s.charAt(0);
        }
        return initialLetters;
    }

    public static String buildCategoryToolbarTitle(String categoryKey) {
        char c = '>';
        int charCount = 0;
        String newString = "";
        for (int i = 0; i < categoryKey.length(); i++) {
            String getChar = String.valueOf(categoryKey.charAt(i));
            if (!getChar.equals(String.valueOf(c))) {
                newString = newString + categoryKey.charAt(i);

            } else {
                if (charCount == 2) {
                    newString = newString + " • ";
                    charCount = 0;
                }
                charCount++;
            }
        }
        return newString;
    }

    public static String getCategoryInitials(GetCategoryTreeResponse response) {
        String initials = response.getName().charAt(0) + "" + response.getName().charAt(1);
        return initials.toUpperCase();
    }

    public static String generateConcatenatedCategories(Set<String> categoryKeys) {
        String concatCategoryKey = "";
        for (String key : categoryKeys) {
            concatCategoryKey += "\"" + key + "\"" + ',';
        }
        return concatCategoryKey;
    }

    public static GetSaleItemsRequest updateSaleItemRequest(String categoryKey, GetSaleItemsRequest getSaleItemsRequest) {
        if (!categoryKey.isEmpty()) {
            if (categoryKey.contains("\"")) {
                getSaleItemsRequest.setCategoryKey("[" + categoryKey + "]");
            } else {
                getSaleItemsRequest.setCategoryKey("[\"" + categoryKey + "\"]");
            }
        } else {
            getSaleItemsRequest.setCategoryKey("[]");
        }
        return getSaleItemsRequest;
    }

    public static String getParentKey(GetCategoryTreeResponse category) {
        return category.getKey().replace(">>>" + category.getName(), "");
    }

}
