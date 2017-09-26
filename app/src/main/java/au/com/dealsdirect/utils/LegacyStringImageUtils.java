package au.com.dealsdirect.utils;

import com.mysale.genie.utility.Prefs;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/*
 * Created by Admin on 6/7/17.
 */

public class LegacyStringImageUtils {

    private final static String IMAGE_SERVER_URL = "server_image_server_url";
    private final static String DEFAULT_IMAGE_SERVER_URL = "https://c1.mysalec.com/";


    public static String generateImageUrl(String brandId, String imageId, String imageFilename) {
        String encodedImageFilename;

        try {
            encodedImageFilename = URLEncoder.encode(imageFilename, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            encodedImageFilename = "";
        }
        encodedImageFilename = encodedImageFilename.replace("+", "%20");

        return Prefs.getString(IMAGE_SERVER_URL, DEFAULT_IMAGE_SERVER_URL)
                + "brands/"
                + brandId + "/"
                + imageId + "/"
                + encodedImageFilename;
    }
}
