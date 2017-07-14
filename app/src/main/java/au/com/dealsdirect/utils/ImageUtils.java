package au.com.dealsdirect.utils;
/*
 * Created by CodeineBot on 5/23/17.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;


public class ImageUtils {

    public static final String TAG = ImageUtils.class.getSimpleName();

    public static abstract class ImageLoadedCallback {

        public void onImageResourceReady() {

        }
    }

    public static void loadImage(Context context, String url, ImageView imageView) {
        RequestOptions options = new RequestOptions().encodeQuality(50)
                                                     .encodeFormat(Bitmap.CompressFormat.JPEG)
                                                     .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                                     .skipMemoryCache(true)
                                                     .format(DecodeFormat.PREFER_RGB_565);

        Glide.with(context)
             .load(url)
             .apply(options)
             .into(imageView);
    }

    public static void loadImageImmediate(Context context, String url, ImageView imageView, ImageLoadedCallback callback) {
        RequestOptions options = new RequestOptions().encodeQuality(50)
                .encodeFormat(Bitmap.CompressFormat.JPEG)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.IMMEDIATE)
                .format(DecodeFormat.PREFER_RGB_565);

        if (callback!=null){
            Glide.with(context)
                    .load(url)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            callback.onImageResourceReady();
                            return false;
                        }
                    })
                    .apply(options)
                    .into(imageView);
        }else{

            Glide.with(context)
                    .load(url)
                    .apply(options)
                    .into(imageView);
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

    public static void clearImage(Context context, ImageView imageView) {
        Glide.with(context)
             .clear(imageView);
    }

    private static String appendBannerSizeUrl(String url, String bannerSize) {

        String removedExtension = url.substring(0, url.lastIndexOf('.'));

        String extension = "";
        int i = url.lastIndexOf('.');
        if (i > 0) {
            extension = url.substring(i+1);
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

        return (int) (height * scale);
    }
}
