package au.com.dealsdirect.utils;
/*
 * Created by CodeineBot on 2/8/17.
 */

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class GDeepLinkUtil {

    public static final String DEEP_LINK_INTENT_LAUNCHED = "deep_link_intent";
    public static final String KEY_DEEP_LINK_COUNTRY = "deep_link_country_id";
    public static final String KEY_DEEP_LINK_ACTION = "deep_link_action";
    public static final String KEY_DEEP_LINK_SEOIDENTIFIER = "deep_link_seoidentifier";

    public static Bundle generateExtraFromDeepLink(Uri uri) {

        String[] uriSplit = uri.toString().replace("http://", "").replace("//", "/").split("/");

        Bundle bundle = new Bundle();
        bundle.putBoolean(DEEP_LINK_INTENT_LAUNCHED, true);
        bundle.putString(KEY_DEEP_LINK_COUNTRY, uriSplit[1]);
        bundle.putString(KEY_DEEP_LINK_ACTION, uriSplit[2]);
        bundle.putString(KEY_DEEP_LINK_SEOIDENTIFIER, uriSplit[3]);

        Log.i(KEY_DEEP_LINK_COUNTRY, uriSplit[1]);
        Log.i(KEY_DEEP_LINK_ACTION, uriSplit[2]);
        Log.i(KEY_DEEP_LINK_SEOIDENTIFIER, uriSplit[3]);

        return bundle;
    }
}
