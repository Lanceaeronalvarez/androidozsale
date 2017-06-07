package au.com.dealsdirect.utils;

import android.os.Handler;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import au.com.dealsdirect.data.network.model.banner.BannerResponse;

/**
 * Created by Admin on 6/7/17.
 */

public class LegacyStringImageUtils {

    private static String OEngineSaleImageBaseURL = "https://c1.mysalec.com/sales";
    private static String OEngineProductImageBaseURL = "https://cdn1.apacsale.com/brands";

    private static Handler h = new Handler();

    public static String saleImageURLString(Object sale) {

        BannerResponse.Sale mSale = (BannerResponse.Sale) sale;
        String saleId = "";
        String imageFilename = "";
        String imageId = "";
        String encodedImageFilename = "";

        saleId = mSale.getID();

        //Image ID
        imageId = ((BannerResponse.Sale) sale).getImageID();

        imageFilename = ((BannerResponse.Sale) sale).getFile();


        try {
            encodedImageFilename = URLEncoder.encode(imageFilename, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            encodedImageFilename = "";
        }

        encodedImageFilename = encodedImageFilename.replace("+", "%20");


        String urlString = OEngineSaleImageBaseURL + "/"
                           + saleId + "/"
                           + imageId + "/"
                           + encodedImageFilename;

        Log.d("LegacyString", urlString);

        return urlString;

    }

    public static String productImageURLString(JSONObject item) {
        String brandId = "";
        String imageFilename = "";
        String imageId = "";
        String encodedImageFilename = "";

        try {
            brandId = item.getString("BrandID");
            imageId = item.getString("ImageID");

            imageFilename = item.getString("File");
            try {
                encodedImageFilename = URLEncoder.encode(imageFilename, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                encodedImageFilename = "";
            }
            //			encodedImageFilename = URIEncoder.encodeURI(imageFilename);
            encodedImageFilename = encodedImageFilename.replace("+", "%20");
            //			encodedImageFilename = encodedImageFilename.replace("%0B", "%5B");
            //			encodedImageFilename = encodedImageFilename.replace("%0D", "%5D");

        } catch (JSONException e) {

            try {
                imageFilename = item.getString("FileName");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            try {
                encodedImageFilename = URLEncoder.encode(imageFilename, "UTF-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
                encodedImageFilename = "";
            }

            //			encodedImageFilename = URIEncoder.encodeURI(imageFilename);
            encodedImageFilename = encodedImageFilename.replace("+", "%20");
            //			encodedImageFilename = encodedImageFilename.replace("%0B", "%5B");
            //			encodedImageFilename = encodedImageFilename.replace("%0D", "%5D");
        }

        String urlString = OEngineProductImageBaseURL + "/"
                           + brandId + "/"
                           + imageId + "/"
                           + encodedImageFilename;

        //System.out.println("print image url");
        //Log.d("ImageURL", urlString.toString());

        return urlString;
    }
}
