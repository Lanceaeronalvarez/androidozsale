package au.com.dealsdirect.utils;
/*
 * Created by CodeineBot on 5/23/17.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.integration.webp.decoder.WebpDrawable;
import com.bumptech.glide.integration.webp.decoder.WebpDrawableTransformation;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ImageUtils {

    public static final String TAG = ImageUtils.class.getSimpleName();
    public static String IMAGE_SERVER_URL = "server_image_server_url";
    private static final int NO_MAX_COLUMN = -1;

    public static abstract class ImageLoadedCallback {

        public void onImageResourceReady(Bitmap resource) {

        }
    }

    static class Headers {

        static GlideUrl applyHeadersForWebPContent(String url){
            return new GlideUrl(url, new LazyHeaders.Builder()
                    .addHeader("Accept", "image/webp")
                    .addHeader("Accept-Encoding", "gzip")
                    .build());
        }
    }

    static Transformation<Bitmap> centerInside = new CenterInside();

    public static void loadImage(String url, ImageView imageView) {
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .format(DecodeFormat.PREFER_ARGB_8888);

        Glide.with(imageView)
                .asBitmap()
                .apply(options)
                .load(url)
                .transform(WebpDrawable.class, new WebpDrawableTransformation(centerInside))
                .into(imageView);
    }

    public static void loadImageDontAnimate(String url, ImageView imageView) {
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .skipMemoryCache(true)
                .format(DecodeFormat.PREFER_ARGB_8888)
                .dontAnimate();

        Glide.with(imageView)
                .asBitmap()
                .load(url)
                .apply(options)
                .transform(WebpDrawable.class, new WebpDrawableTransformation(centerInside))
                .into(new SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        imageView.setImageBitmap(resource);
                    }
                });
    }

    public static void loadImageWithPlaceholder(String url, ImageView imageView, Drawable placeholder,
                                                ImageLoadedCallback callback) {
        RequestOptions options = new RequestOptions()
                .placeholder(placeholder)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .skipMemoryCache(true)
                .format(DecodeFormat.PREFER_ARGB_8888);

        if (callback != null) {
            Glide.with(imageView)
                    .asBitmap()
                    .apply(options)
                    .load(url)
                    .transform(WebpDrawable.class, new WebpDrawableTransformation(centerInside))
                    .listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            callback.onImageResourceReady(resource);
                            return false;
                        }
                    })
                    .into(imageView);
        } else {
            Glide.with(imageView)
                    .asBitmap()
                    .apply(options)
                    .load(url)
                    .transform(WebpDrawable.class, new WebpDrawableTransformation(centerInside))
                    .into(imageView);
        }
    }

    public static void loadImageImmediate(String url, ImageView imageView, ImageLoadedCallback callback) {
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .skipMemoryCache(true)
                .format(DecodeFormat.PREFER_ARGB_8888)
                .priority(Priority.IMMEDIATE);

        if (callback != null) {
            Glide.with(imageView)
                    .asBitmap()
                    .apply(options)
                    .load(url)
                    .transform(WebpDrawable.class, new WebpDrawableTransformation(centerInside))
                    .listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            callback.onImageResourceReady(resource);
                            return false;
                        }
                    })
                    .into(imageView);
        } else if (imageView != null) {
            Glide.with(imageView)
                    .asBitmap()
                    .apply(options)
                    .load(url)
                    .transform(WebpDrawable.class, new WebpDrawableTransformation(centerInside))
                    .into(imageView);
        }
    }

    public static void loadImage(String url, ImageView imageView, int width,
                                 int height) {
        if (url != null && !url.equals("")) {
            @SuppressLint("DefaultLocale") String sizeFormat =
                    String.format("?width=%d&height=%d", width, height);
            url = url + sizeFormat;

            loadImage(url, imageView);
        }
    }

    public static void loadImageWithImageViewDimens(final Context context, final String url,
                                                    final ImageView imageView) {
        if (imageView.getMeasuredWidth() != 0 && imageView.getMeasuredHeight() != 0) {

            loadImage(url,
                    imageView,
                    imageView.getMeasuredWidth(),
                    imageView.getMeasuredHeight());

        } else {

            imageView.getViewTreeObserver()
                    .addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                        public boolean onPreDraw() {
                            imageView.getViewTreeObserver()
                                    .removeOnPreDrawListener(this);
                            loadImage(url,
                                    imageView,
                                    imageView.getMeasuredWidth(),
                                    imageView.getMeasuredHeight());
                            return true;
                        }
                    });

        }
    }

    public static void clearImage(ImageView imageView) {
        Glide.with(imageView).clear(imageView);
    }

    public static String appendBannerSizeUrl(String url, int width, int height) {

        String bannerSize = String.format("_%dx%d", width, height);

        if (!url.contains(".")) return url;

        String removedExtension = url.substring(0, url.lastIndexOf('.'));

        String extension = "";
        int i = url.lastIndexOf('.');
        if (i > 0) {
            extension = url.substring(i + 1);
        }

        url = String.format("%s%s.%s", removedExtension, bannerSize, extension);

        return url;
    }

    public static int getComputedBannerHeight(int width, int height, int screenWidth) {

        float scale = (float) screenWidth / width;
        int computedHeight = (int) (height * scale);
        AppLogger.d("IMG " + String.format("width: %d height: %d screenWidth: %d scale: %f computedHeight: %d", width, height, screenWidth, scale, computedHeight));

        return computedHeight;
    }

    /**
     * Returns the bitmap position inside an imageView.
     *
     * @param imageView source ImageView
     * @return 0: left, 1: top, 2: width, 3: height
     */
    public static int[] getDisplayedImageLocation(ImageView imageView) {
        int[] ret = new int[4];

        if (imageView == null || imageView.getDrawable() == null)
            return ret;

        // Get image dimensions
        // Get image matrix values and place them in an array
        float[] f = new float[9];
        imageView.getImageMatrix().getValues(f);

        // Extract the scale values using the constants (if aspect ratio maintained, scaleX == scaleY)
        final float scaleX = f[Matrix.MSCALE_X];
        final float scaleY = f[Matrix.MSCALE_Y];

        // Get the drawable (could also get the bitmap behind the drawable and getWidth/getHeight)
        final Drawable d = imageView.getDrawable();
        final int origW = d.getIntrinsicWidth();
        final int origH = d.getIntrinsicHeight();

        // Calculate the actual dimensions
        final int actW = Math.round(origW * scaleX);
        final int actH = Math.round(origH * scaleY);

        ret[2] = actW;
        ret[3] = actH;

        // Get image position
        // We assume that the image is centered into ImageView
        int imgViewW = imageView.getWidth();
        int imgViewH = imageView.getHeight();

        int[] imgViewScreenLoc = new int[2];
        imageView.getLocationOnScreen(imgViewScreenLoc);

        // get the actual image location inside its image view
        int left = imgViewScreenLoc[0] + (imgViewW - actW) / 2;
        int top = imgViewScreenLoc[1] + (imgViewH - actH) / 2;

        ret[0] = left;
        ret[1] = top;

        return ret;
    }

    public static class Grid {
        private int mColumn;
        private float mItemWidth;
        private float mItemHeight;

        public Grid(int column, float width, float height) {
            mColumn = column;
            mItemWidth = width;
            mItemHeight = height;
        }

        public int getColumn() {
            return mColumn;
        }

        public float getItemWidth() {
            return mItemWidth;
        }

        public float getItemHeight() {
            return mItemHeight;
        }
    }

    public static Grid getExactGridDefinition(int columnCount, float ratio, float canvasWidth) {
        float width = canvasWidth / columnCount;
        float height = width * ratio;
        return new Grid(columnCount, width, height);
    }

    public static Grid getRangedGridDefinition(int proposedWidth, int proposedHeight, float canvasWidth, int minColumn) {
        return getRangedGridDefinition(proposedWidth, proposedHeight, canvasWidth, minColumn, NO_MAX_COLUMN);
    }

    public static Grid getRangedGridDefinition(int proposedWidth, int proposedHeight,
                                               float canvasWidth, int minColumn, int maxColumn) {
        int computedColumn = Math.max((int) canvasWidth / Math.max(1, proposedWidth), minColumn);
        int actualMaxColumn = maxColumn == NO_MAX_COLUMN ? computedColumn : maxColumn;
        int finalColumnCount = Math.min(actualMaxColumn, computedColumn);
        float ratio = (float) proposedHeight / Math.max(1, proposedWidth);
        return getExactGridDefinition(finalColumnCount, ratio, canvasWidth);
    }

    public static String removeResolutionModifierInImageUrl(String sourceUrl) {
        String newString = sourceUrl;
        final String[] extensions = new String[]{
                "jpg", "jpeg", "png", "webp"
        };

        for (String extension : extensions) {
            // This regex matches for "_NUMBERxNUMBER" followed by an extension, and selects only
            // the "_NUMBERxNUMBER" to remove from the URL.
            // e.g. in "https://www.itsallogrenow.com/img_200x200/getoutofmyswamp_200x200.jpg",
            // only, the second "_200x200" will be matched.
            String regex = "_[0-9]+x[0-9]+(?=\\." + extension + ")";
            Matcher matcher = Pattern.compile(regex).matcher(sourceUrl);
            if (matcher.find()) {
                newString = sourceUrl.replace(matcher.group(), "");
                break;
            }
        }

        return newString;
    }

    public static class ImageLink {
        String link;
        boolean isURL;

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public boolean isURL() {
            return isURL;
        }

        public void setIsURL(boolean isURL) {
            this.isURL = isURL;
        }

    }
}
