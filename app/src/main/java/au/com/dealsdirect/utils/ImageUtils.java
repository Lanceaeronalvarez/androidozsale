package au.com.dealsdirect.utils;
/*
 * Created by CodeineBot on 5/23/17.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.ResourceEncoder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.bitmap.BitmapEncoder;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class ImageUtils {

    public static final String TAG = ImageUtils.class.getSimpleName();
    public static String IMAGE_SERVER_URL = "server_image_server_url";

    public static abstract class ImageLoadedCallback {

        public void onImageResourceReady() {

        }
    }

    public static void loadImage(Context context, String url, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .asBitmap()
                .encoder(new BitmapEncoder(Bitmap.CompressFormat.JPEG, 50))
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .skipMemoryCache(true)
                .format(DecodeFormat.PREFER_RGB_565)
                .into(imageView);
    }

    public static void loadImageImmediate(Context context, String url, ImageView imageView, ImageLoadedCallback callback) {
        if (callback != null) {
            Glide.with(context)
                    .load(url)
                    .asBitmap()
                    .listener(new RequestListener<String, Bitmap>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            callback.onImageResourceReady();
                            return false;
                        }
                    })
                    .encoder(new BitmapEncoder(Bitmap.CompressFormat.JPEG, 50))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.IMMEDIATE)
                    .format(DecodeFormat.PREFER_RGB_565)
                    .into(imageView);
        } else {

            if (imageView != null) {
                Glide.with(context)
                        .load(url)
                        .asBitmap()
                        .encoder(new BitmapEncoder(Bitmap.CompressFormat.JPEG, 50))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .priority(Priority.IMMEDIATE)
                        .format(DecodeFormat.PREFER_RGB_565)
                        .into(imageView);
            }
        }

    }

    public static void loadImage(Context context, String url, ImageView imageView, int width,
                                 int height) {
        if (url != null && !url.equals("")) {
            @SuppressLint("DefaultLocale") String sizeFormat =
                    String.format("?width=%d&height=%d", width, height);
            url = url + sizeFormat;

            loadImage(context, url, imageView);
        }
    }

    public static void loadImageWithImageViewDimens(final Context context, final String url,
                                                    final ImageView imageView) {
        if (imageView.getMeasuredWidth() != 0 && imageView.getMeasuredHeight() != 0) {

            loadImage(context,
                    url,
                    imageView,
                    imageView.getMeasuredWidth(),
                    imageView.getMeasuredHeight());

        } else {

            imageView.getViewTreeObserver()
                    .addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                        public boolean onPreDraw() {
                            imageView.getViewTreeObserver()
                                    .removeOnPreDrawListener(this);
                            loadImage(context,
                                    url,
                                    imageView,
                                    imageView.getMeasuredWidth(),
                                    imageView.getMeasuredHeight());
                            return true;
                        }
                    });

        }
    }

    public static void clearImage(ImageView imageView) {
        Glide.clear(imageView);
    }

    private static String appendBannerSizeUrl(String url, String bannerSize) {

        String removedExtension = url.substring(0, url.lastIndexOf('.'));

        String extension = "";
        int i = url.lastIndexOf('.');
        if (i > 0) {
            extension = url.substring(i + 1);
        }

        url = String.format("%s%s.%s", removedExtension, bannerSize, extension);

        return url;
    }

    public static String getBannerMobileSize(String url) {
        return appendBannerSizeUrl(url, AppConstants.BANNER_SIZE_MOBILE);
    }

    public static String getBannerTabletSize(String url) {
        return appendBannerSizeUrl(url, AppConstants.BANNER_SIZE_TABLET);
    }

    public static int getComputedBannerHeight(int width, int height, int screenWidth) {

        float scale = (float) screenWidth / width;
        int computedHeight = (int) (height * scale);
        AppLogger.d("IMG " + String.format("width: %d height: %d screenWidth: %d scale: %f computedHeight: %d", width, height, screenWidth, scale, computedHeight));

        return computedHeight;
    }

    public static String generateImageUrl(String brandId, String imageId, String imageFilename) {
        String encodedImageFilename;

        try {
            encodedImageFilename = URLEncoder.encode(imageFilename, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            encodedImageFilename = "";
        }
        encodedImageFilename = encodedImageFilename.replace("+", "%20");

//        String urlString =  Prefs.getString(IMAGE_SERVER_URL, "https://c1.mysalec.com/brands/")
        String urlString = "https://c1.mysalec.com/"
                + "brands/"
                + brandId + "/"
                + imageId + "/"
                + encodedImageFilename;

        return urlString;
    }
}
