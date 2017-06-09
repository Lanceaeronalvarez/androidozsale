package au.com.dealsdirect.utils;

import android.os.Handler;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import au.com.dealsdirect.data.network.model.banner.GetPublicSalesBannerResponse;
import au.com.dealsdirect.data.network.model.productdetails.GetPublicItemDetailsResponse;
import au.com.dealsdirect.data.network.model.saleitems.GetPublicSaleItemsResponse;

/**
 * Created by Admin on 6/7/17.
 */

public class LegacyStringImageUtils {

    private static String OEngineSaleImageBaseURL = "https://c1.mysalec.com/sales";
    private static String OEngineProductImageBaseURL = "https://cdn1.apacsale.com/brands";
    private static String OEngineProductDetailsImageBaseURL = "https://c1.mysalec.com/brands";

    private static Handler h = new Handler();

    public static String saleImageURLString(Object sale) {

        GetPublicSalesBannerResponse.Sale mSale = (GetPublicSalesBannerResponse.Sale) sale;
        String saleId = "";
        String imageFilename = "";
        String imageId = "";
        String encodedImageFilename = "";

        saleId = mSale.getID();

        //Image ID
        imageId = ((GetPublicSalesBannerResponse.Sale) sale).getImageID();

        imageFilename = ((GetPublicSalesBannerResponse.Sale) sale).getFile();


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

    public static String itemImageURLString(GetPublicSaleItemsResponse.Item item) {
        String brandId = "";
        String imageFilename = "";
        String imageId = "";
        String encodedImageFilename = "";

        brandId = item.getBrandID();
        imageId = item.getImageID();

        imageFilename = item.getFile();
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

        String urlString = OEngineProductImageBaseURL + "/"
                           + brandId + "/"
                           + imageId + "/"
                           + encodedImageFilename;

        //System.out.println("print image url");
        //Log.d("ImageURL", urlString.toString());

        return urlString;
    }

    public static String productDetailsImageURLString(String brandId, String imageId, String previewPath) {

        String encodedImageFilename = "";

        try {
            encodedImageFilename = URLEncoder.encode(previewPath, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            encodedImageFilename = "";
        }

        encodedImageFilename = encodedImageFilename.replace("+", "%20");


        String urlString = OEngineProductDetailsImageBaseURL + "/"
                + brandId + "/"
                + imageId + "/"
                + encodedImageFilename;

        Log.d("LegacyString", urlString);

        return urlString;

    }
}
