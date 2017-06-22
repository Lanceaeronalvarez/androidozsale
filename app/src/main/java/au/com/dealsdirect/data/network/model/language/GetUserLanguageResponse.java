package au.com.dealsdirect.data.network.model.language;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mysale.genie.utility.LegacyBaseResponseValue;
import com.mysale.genie.utility.config.model.getserversettings.Language;

import java.util.ArrayList;

/**
 * Created by Paul on 6/22/17.
 */

public class GetUserLanguageResponse {

    public Response d;

    public static class Response extends LegacyBaseResponseValue {
        @SerializedName("List")
        @Expose
        public ArrayList<String> languages;
    }
}
