package au.com.dealsdirect.utils;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.Range;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsRequest;

import static android.text.Spanned.SPAN_EXCLUSIVE_INCLUSIVE;

public class StringUtils {

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

        if (categoryKey == null) {
            return newString;
        }
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
        String key = category.getKey();
        if (key != null) {
            key = key.replace(">>>" + category.getName(), "");
        }
        return key;
    }

    public static String loadAssetTextAsString(Context context, String name) {
        BufferedReader in = null;
        try {
            StringBuilder buf = new StringBuilder();
            InputStream is = context.getAssets().open(name);
            in = new BufferedReader(new InputStreamReader(is));

            String str;
            boolean isFirst = true;
            while ((str = in.readLine()) != null) {
                if (isFirst)
                    isFirst = false;
                else
                    buf.append('\n');
                buf.append(str);
            }
            return buf.toString();
        } catch (IOException e) {
            Log.e(StringUtils.class.toString(), "Error opening asset " + name);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.e(StringUtils.class.toString(), "Error closing asset " + name);
                }
            }
        }

        return null;
    }

    // Probably runs faster? uses a delimiter
    public static List<Range<Integer>> rangesOfSubstringsMatching(String source,
                                                                  char delimiter,
                                                                  String regex) {
        ArrayList<Range<Integer>> ranges = new ArrayList<>();

        int i = -1;
        int length = source.length();
        while (i < length) {
            if ((i < 0 || source.charAt(i) == delimiter) &&
                    (i + 1 < length && source.charAt(i + 1) != delimiter)) {
                int start = i + 1;

                // initialize chr as anything other than the delimiter
                char chr = delimiter == (char) 0 ? (char) 1 : (char) 0;

                // find index of next instance of the delimiter
                do {
                    i++;
                    if (i < length) {
                        chr = source.charAt(i);
                    }
                } while (i < length && chr != delimiter);

                int end = Math.min(chr == delimiter ? i : i + 1, length);

                if (start <= end && source.substring(start, end).matches(regex)) {
                    ranges.add(new Range<>(start, end));
                }
            } else {
                i++;
            }
        }

        return ranges;
    }

    // More readable version
    public static List<Range<Integer>> rangesOfSubstringsMatching(String source,
                                                                  String regex) {
        ArrayList<Range<Integer>> ranges = new ArrayList<>();

        Matcher matcher = Pattern.compile(regex).matcher(source);

        while (matcher.find()) {
            String match = matcher.group();
            int start = source.indexOf(match);
            int end = start + match.length();
            ranges.add(new Range<>(start, end));
        }

        return ranges;
    }

    public static SpannableStringBuilder applySpanToRange(SpannableStringBuilder source,
                                                          Object what,
                                                          Range<Integer> range,
                                                          int flags) {
        source.setSpan(what, range.getLower(), range.getUpper(), flags);
        return source;
    }

    public static SpannableStringBuilder applySpanToRanges(SpannableStringBuilder source,
                                                           Object what,
                                                           List<Range<Integer>> ranges,
                                                           int flags) {
        for (int i = 0; i < ranges.size(); i++) {
            applySpanToRange(source, what, ranges.get(i), flags);
        }
        return source;
    }

    public static SpannableStringBuilder applySpanToSubstringsMatching(SpannableStringBuilder source,
                                                                       Object what,
                                                                       char delimiter,
                                                                       String regex,
                                                                       int flags) {
        return applySpanToRanges(source, what, rangesOfSubstringsMatching(source.toString(), delimiter, regex), flags);
    }

    public static SpannableStringBuilder applySpanToSubstringsMatching(SpannableStringBuilder source,
                                                                       Object what,
                                                                       String regex,
                                                                       int flags) {
        return applySpanToRanges(source, what, rangesOfSubstringsMatching(source.toString(), regex), flags);
    }

    public interface CSSStyle {
        String getBodyFontName();

        String getBodyFontColor();

        String getBoldFontName();

        String getBoldFontColor();
    }

    public static String applyStyleToCSS(CSSStyle style, String sourceCSS) {
        final String regexBodyFontName = "__BODY_FONT_NAME__";
        final String regexBodyFontColor = "__BODY_FONT_COLOR__";
        final String regexBoldFontName = "__BOLD_FONT_NAME__";
        final String regexBoldFontColor = "__BOLD_FONT_COLOR__";

        return sourceCSS
                .replaceAll(regexBodyFontName, style.getBodyFontName())
                .replaceAll(regexBodyFontColor, style.getBodyFontColor())
                .replaceAll(regexBoldFontName, style.getBoldFontName())
                .replaceAll(regexBoldFontColor, style.getBoldFontColor());
    }

    public static String typeFaceFamilyFromFilename(String filename) {
        // This may not work if the filename does not represent the family name
        final String regex = "(?!.*\\/).*(?=-.*\\.ttf)";

        String familyName = null;
        Matcher matcher = Pattern.compile(regex).matcher(filename);
        if (matcher.find()) {
            familyName = matcher.group();
        }

        if (familyName == null || familyName.isEmpty()) {
            return null;
        }

        // Assumes CamelCasing
        familyName = insertSpaceBetweenLowerCaseAndUppercaseLetters(familyName);

        return familyName;
    }

    public static String insertSpaceBetweenLowerCaseAndUppercaseLetters(String source) {
        final String regex = "[a-z](?=[A-Z])";
        return source.replaceAll(regex, "$0 ");
    }

    public static SpannableStringBuilder twoPartStringWithStyles(String firstString,
                                                                 StyleSpan firstStyle,
                                                                 String secondString,
                                                                 StyleSpan secondStyle) {
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(firstString + secondString);
        if (firstStyle != null) {
            stringBuilder.setSpan(firstStyle, 0, firstString.length(), SPAN_EXCLUSIVE_INCLUSIVE);
        }
        if (secondString != null) {
            stringBuilder.setSpan(secondStyle, firstString.length(), stringBuilder.length(), SPAN_EXCLUSIVE_INCLUSIVE);
        }
        return stringBuilder;
    }

    public static boolean isNumeric(String string) {
        return string.matches("-?\\d+(\\.\\d+)?");
    }


    public static String addQueryParameter(String url, String key, String value) {
        return addQueryParameter(url, key, value, false);
    }

    public static String addQueryParameter(String url, String key, String value, boolean includeWhenValueNull) {
        if (key == null || key.isEmpty() || (value == null && !includeWhenValueNull)) {
            return url;
        }

        String output = url;
        if (!output.contains("?")) {
            output += "?";
        } else if (output.charAt(output.length() - 1) != '&') {
            output += "&";
        }

        try {
            output += URLEncoder.encode(key, null) + "=" + URLEncoder.encode(value, null);
        } catch (Exception e) {
            Log.e("StringUtils", "addQueryParameter exception: " + e.getMessage());
            return url;
        }

        return output;
    }

    public static String addEscapeCharactersForRegex(String source) {
        final String regexReservedCharacters = ".^$*+?()[{\\|^-]\\";
        final StringBuilder output = new StringBuilder();
        for (int i = 0; i < source.length(); i++) {
            final char character = source.charAt(i);
            if (regexReservedCharacters.contains(Character.toString(character))) {
                output.append('\\');
            }
            output.append(character);
        }
        return output.toString();
    }
}
