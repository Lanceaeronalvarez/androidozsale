package au.com.dealsdirect.service.ourpay;

/*
 * Created by CodeineBot on 9/28/16.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.MyPayDetails;
import au.com.dealsdirect.utils.PriceUtils;

import static android.view.ViewTreeObserver.OnGlobalLayoutListener;

@SuppressWarnings("deprecation")
public class OurpayGraph {

    private int circlesLayoutWidth;
    private int indexTracker;
    private Bitmap bitmap;
    private Bitmap[] bitmapState = new Bitmap[6];


    /**
     * @param scaleBitmapImage - Contains Bitmap Image to be masked
     * @param circleSize       - int parameter for size of circle
     * @return - Round version of Bitmap
     */

    public static Bitmap getRoundedShape(Bitmap scaleBitmapImage, int circleSize) {
        Bitmap targetBitmap = Bitmap.createBitmap(circleSize,
                circleSize, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) circleSize - 1) / 2,
                ((float) circleSize - 1) / 2,
                (Math.min(((float) circleSize),
                        ((float) circleSize)) / 2),
                Path.Direction.CCW);

        canvas.clipPath(path);
        canvas.drawBitmap(scaleBitmapImage,
                new Rect(0, 0, scaleBitmapImage.getWidth(),
                        scaleBitmapImage.getHeight()),
                new Rect(0, 0, circleSize, circleSize), null);
        return targetBitmap;
    }

    /**
     * @param bitmap    - bitmap to be cropped
     * @param startFrom - int where to start the crop
     * @return - cropped bitmap
     */
    public static Bitmap cropImage(Bitmap bitmap, int startFrom) {
        return Bitmap.createBitmap(bitmap, startFrom, 0, 1, 1);
    }


    /**
     * @param context - get context for identification
     * @param v       - Get view where the bitmap is be acuired
     * @return - View as bitmap for processing masked cirles
     */
    private static Bitmap getBitmapFromView(Context context, View v) {
        DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
        v.measure(View.MeasureSpec.makeMeasureSpec(dm.widthPixels, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(dm.heightPixels, View.MeasureSpec.EXACTLY));

        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());


        Bitmap returnedBitmap = Bitmap.createBitmap(v.getMeasuredWidth(),
                v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(returnedBitmap);
        v.draw(c);
        return returnedBitmap;
    }


    /**
     * @param bitmap     - Bitmap to be processed
     * @param circleSize - Size for the new processed bitmap
     * @return - processed bitmap (Cropped and masked)
     */
    public Bitmap getCircle(Bitmap bitmap, int circleSize) {
        Bitmap croppedProgressBitmap = cropImage(bitmap, 0);
        return getRoundedShape(croppedProgressBitmap, circleSize);
    }


    /**
     * @param context - to identify current view for acquiring resolution
     * @return - int[] that contains width and height
     */
    private static int[] getScreenResolution(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int[] dimension = new int[2];
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        dimension[0] = width;
        dimension[1] = height;

        return dimension;
    }


    /**
     * Returns a valid id that isn't in use
     *
     * @param context - Get context to identify views
     * @param id      - random number given where to start iterating search
     * @return - a valid (int) id that is not yet used
     */

    private int findId(Context context, int id) {

        View v = ((Activity) context).findViewById(id);
        while (v != null) {
            v = ((Activity) context).findViewById(++id);
        }
        int result = id;
        ++id;
        return result;
    }

    //TODO: ROWS

    /**
     * @param context            -  Get context to identify
     * @param ourpayTransactions - Get Arraylist for processing information
     * @return - return view as ui object
     */

    public View generateGraph(final Context context,
                              final List<MyPayDetails.PlannedTransaction> ourpayTransactions) {
        final LinearLayout layPayViewId;
        int circleTempSize;
        int circleTempTextSize;

        Bitmap progressBitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ourpay_image_gradient);
        Bitmap grayBitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ourpay_image_gray);


        DisplayMetrics dm = context.getResources().getDisplayMetrics();

        int[] dimension = getScreenResolution(context);
        final int deviceWidth = (int) (dimension[0] - (25 * dm.density));
        //final int deviceHeight = dimension[1];

        final boolean tabletSize = context.getResources().getBoolean(R.bool.is_tablet);

        if (tabletSize) {
            circleTempSize = (int) (27 * dm.density);
            circleTempTextSize = (int) (15 - dm.density);
        } else {
            circleTempSize = (int) (19 * dm.density);
            circleTempTextSize = (int) (15 - dm.density);
        }

        final int circleSize = circleTempSize;
        final int circleTextSize = circleTempTextSize;

        Bitmap drawProgressCircle = getRoundedShape(progressBitmap, circleSize);
        Bitmap drawGrayCircle = getCircle(grayBitmap, circleSize);


        layPayViewId = new LinearLayout(context);
        layPayViewId.setBackgroundColor(Color.WHITE);
        layPayViewId.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams LLParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        layPayViewId.setWeightSum(6f);
        layPayViewId.setLayoutParams(LLParams);


        View[] tempView = new View[ourpayTransactions.size() + 1];
        final int max = ourpayTransactions.size() - 1;


        for (int i = 0; i < ourpayTransactions.size(); i++) {

            final int loopIndex = max - (i + 1);
            final int state = i;
            final LinearLayout a = new LinearLayout(context);
            a.setOrientation(LinearLayout.HORIZONTAL);

            LayoutInflater inflater = LayoutInflater.from(context);
            final View yourView = inflater.inflate(R.layout.ourpay_panel_row,
                    layPayViewId, false);


            final RelativeLayout circleView =
                    (RelativeLayout) yourView.findViewById(R.id.imagelayout);

            final RelativeLayout circlesLayout =
                    (RelativeLayout) yourView.findViewById(R.id.circlesContainer);

            circleView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {

                    Log.d("Ourpaygraph", "onGlobalLayout");

                    circlesLayoutWidth = deviceWidth - (deviceWidth / 3);
                    ImageView foregroundBar =
                            (ImageView) yourView.findViewById(R.id.ourpay_fg_bar);


                    ImageView backgroundBar =
                            (ImageView) yourView.findViewById(R.id.ourpay_bg_bar);

                    if (tabletSize) {
                        if (deviceWidth <= 900) {
                            backgroundBar.getLayoutParams().height = 10;
                        } else if (deviceWidth <= 1300) {

                            backgroundBar.getLayoutParams().height = 12;
                        } else {

                            backgroundBar.getLayoutParams().height = 18;
                        }
                    }
//                    Bitmap progressBitmap = BitmapFactory.decodeResource(context.getResources(),
//                            R.drawable.ourpay_image_gradient);
//                    Bitmap grayBitmap = BitmapFactory.decodeResource(context.getResources(),
//                            R.drawable.ourpay_image_gray);

//                    Bitmap drawProgressCircle = getRoundedShape(progressBitmap, circleSize);
//                    Bitmap drawGrayCircle = getCircle(grayBitmap, circleSize);


                    if (state == 0) {
                        if (max == 1 && deviceWidth <= 600) {
                            foregroundBar.getLayoutParams().width =
                                    ((circlesLayoutWidth / max) * loopIndex) - (circleSize + max);
                            foregroundBar.setPadding(0, 0, -150, 0);

                        } else if (max == 1 && deviceWidth >= 600) {
                            foregroundBar.getLayoutParams().width = (circleSize * 2);
                            foregroundBar.setPadding(0, 0, -400, 0);

                        } else {
                            foregroundBar.getLayoutParams().width =
                                    ((circlesLayoutWidth / (max + 1)) * loopIndex);
                        }

                        for (int x = 0; x <= max; x++) {
                            if (x == 0) {
                                foregroundBar.getLayoutParams().width =
                                        ((circlesLayoutWidth / (max + 1)) * (loopIndex)) + (circleSize);
                            } else if (max == 2 && x == 2) {
                                foregroundBar.getLayoutParams().width =
                                        ((circlesLayoutWidth / (max + 1)) * loopIndex) +
                                                ((circleSize * 2) + max);
                            }

                            indexTracker = x + 1;

                            //
                            bitmap = getBitmapFromView(context, circleView);
                            int startsAt = (((circlesLayoutWidth / max) * x));

                            Bitmap croppedBitmap = cropImage(bitmap, startsAt);
                            Bitmap roundBitmap = getRoundedShape(croppedBitmap, circleSize);

                            ImageView iv = new ImageView(context);
                            iv.setId(findId(context, 555));


                            TextView tv = new TextView(context);
                            TextView tvStartEnd = new TextView(context);

                            tv.setTextSize(circleTextSize);
                            tv.setTextColor(Color.parseColor("#FFFFFF"));

                            tvStartEnd.setTextSize(circleTextSize);
                            tvStartEnd.setTextColor(Color.parseColor("#FFFFFF"));

                            RelativeLayout.LayoutParams imageParams;
                            RelativeLayout.LayoutParams inBetweenTextParams;
                            RelativeLayout.LayoutParams startEndTextParams;


                            imageParams = new RelativeLayout
                                    .LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT);
                            inBetweenTextParams = new RelativeLayout
                                    .LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT);
                            startEndTextParams = new RelativeLayout
                                    .LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT);


                            inBetweenTextParams.addRule(RelativeLayout.ALIGN_LEFT, iv.getId());
                            inBetweenTextParams.addRule(RelativeLayout.ALIGN_BOTTOM, iv.getId());
                            inBetweenTextParams.addRule(RelativeLayout.ALIGN_RIGHT, iv.getId());
                            inBetweenTextParams.addRule(RelativeLayout.ALIGN_TOP, iv.getId());

                            startEndTextParams.addRule(RelativeLayout.ALIGN_LEFT, iv.getId());
                            startEndTextParams.addRule(RelativeLayout.ALIGN_RIGHT, iv.getId());
                            startEndTextParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                            startEndTextParams.addRule(RelativeLayout.CENTER_VERTICAL);
                            //startEndTextParams.rightMargin = ((circleSize/3));


                            iv.setLayoutParams(imageParams);
                            //iv.requestLayout();
                            iv.getLayoutParams().width = circleSize;
                            iv.getLayoutParams().height = circleSize;


                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                inBetweenTextParams.addRule(RelativeLayout.TEXT_ALIGNMENT_GRAVITY,
                                        Gravity.CENTER);
                            }

                            tv.setGravity(Gravity.CENTER);
                            tvStartEnd.setLayoutParams(startEndTextParams);
                            tv.setLayoutParams(inBetweenTextParams);

                            String indexTrackerString = String.valueOf(indexTracker);
                            tv.setText(indexTrackerString);
                            tvStartEnd.setText(indexTrackerString);
                            tvStartEnd.setGravity(Gravity.CENTER);
                            if (max + 1 == 6) {
                                if (x == 0) {

                                    iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                            R.id.dateTextView);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                            R.id.dateTextView);
                                    iv.setImageBitmap(roundBitmap);
                                    iv.setLayoutParams(imageParams);

                                    circlesLayout.addView(iv);
                                    circlesLayout.addView(tv);
                                } else if (x == 2) {

                                    imageParams.leftMargin =
                                            ((((circlesLayoutWidth) / (max + 1)) * x) -
                                                    circleSize);

                                    iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                            R.id.dateTextView);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                            R.id.dateTextView);
                                    iv.setImageBitmap(drawGrayCircle);
                                    iv.setLayoutParams(imageParams);

                                    circlesLayout.addView(iv);
                                    circlesLayout.addView(tv);
                                } else if (x == 1 && x != max) {

                                    imageParams.leftMargin =
                                            ((((circlesLayoutWidth) / (max + 1)) - (circleSize / 2)));
                                    iv.setImageBitmap(drawProgressCircle);
                                    iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                            R.id.dateTextView);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                            R.id.dateTextView);
                                    iv.setLayoutParams(imageParams);

                                    circlesLayout.addView(iv);
                                    circlesLayout.addView(tv);

                                } else if (x > state && x < max) {

                                    if (x == 3) {
                                        imageParams.leftMargin =
                                                ((((circlesLayoutWidth) / (max + 1)) * x) -
                                                        (circleSize + (circleSize / 2)));
                                        iv.setImageBitmap(drawGrayCircle);
                                        iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                                R.id.dateTextView);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                                R.id.dateTextView);
                                        iv.setLayoutParams(imageParams);

                                        circlesLayout.addView(iv);
                                        circlesLayout.addView(tv);
                                    } else if (x == 4) {
                                        imageParams.leftMargin =
                                                ((((circlesLayoutWidth) / (max + 1)) * x) -
                                                        (circleSize + (circleSize)));
                                        iv.setImageBitmap(drawGrayCircle);
                                        iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                                R.id.dateTextView);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                                R.id.dateTextView);
                                        iv.setLayoutParams(imageParams);

                                        circlesLayout.addView(iv);
                                        circlesLayout.addView(tv);
                                    } else {

                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

                                        imageParams.rightMargin =
                                                ((((circlesLayoutWidth) / (max * 2))));
                                        iv.setImageBitmap(drawGrayCircle);
                                        iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                                R.id.dateTextView);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                                R.id.dateTextView);
                                        iv.setLayoutParams(imageParams);

                                        circlesLayout.addView(iv);
                                        circlesLayout.addView(tv);
                                    }


                                } else if (x == max && (state == max - 1)) {

                                    foregroundBar.getLayoutParams().width = 0;
                                    foregroundBar.setPadding(0, 0, -100, 0);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                            R.id.dateTextView);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                            R.id.dateTextView);
                                    iv.setScaleType(ImageView.ScaleType.FIT_XY);

                                    iv.setImageBitmap(drawProgressCircle);
                                    iv.setLayoutParams(imageParams);

                                    circlesLayout.addView(iv);
                                    circlesLayout.addView(tv);
                                } else if (x == max && (state + 1) != max) {

                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                            R.id.dateTextView);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                            R.id.dateTextView);
                                    iv.setScaleType(ImageView.ScaleType.FIT_XY);

                                    iv.setImageBitmap(drawGrayCircle);
                                    iv.setLayoutParams(imageParams);

                                    circlesLayout.addView(iv);
                                    circlesLayout.addView(tv);
                                } else if (x == max && ((state + 1) == max)) {

                                    foregroundBar.getLayoutParams().width = 0;
                                    foregroundBar.setPadding(0, 0, -100, 0);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                            R.id.dateTextView);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                            R.id.dateTextView);
                                    iv.setScaleType(ImageView.ScaleType.FIT_XY);

                                    iv.setImageBitmap(drawProgressCircle);
                                    iv.setLayoutParams(imageParams);

                                    circlesLayout.addView(iv);
                                    circlesLayout.addView(tv);
                                }

                            } else if (max + 1 == 5) {
                                if (x == 0) {
                                    iv.setImageBitmap(roundBitmap);
                                    iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                            R.id.dateTextView);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                            R.id.dateTextView);
                                    iv.setLayoutParams(imageParams);

                                    circlesLayout.addView(iv);
                                    circlesLayout.addView(tvStartEnd);

                                } else if (x == state + 1 && x != max) {

                                    imageParams.leftMargin =
                                            ((((circlesLayoutWidth) / (max + 1)) * x) - (circleSize / 2));
                                    iv.setImageBitmap(drawProgressCircle);
                                    iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                            R.id.dateTextView);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                            R.id.dateTextView);
                                    iv.setLayoutParams(imageParams);

                                    circlesLayout.addView(iv);
                                    circlesLayout.addView(tv);

                                } else if (x == max) {
                                    iv.requestLayout();
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                    iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                            R.id.dateTextView);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                            R.id.dateTextView);

                                    iv.setImageBitmap(drawGrayCircle);
                                    iv.setLayoutParams(imageParams);
                                    circlesLayout.addView(iv);
                                    circlesLayout.addView(tv);

                                } else {
                                    if (x == 3) {
                                        imageParams.leftMargin =
                                                ((((circlesLayoutWidth) / (max + 1)) * x) -
                                                        (circleSize + (circleSize / 2)));
                                        iv.setImageBitmap(drawGrayCircle);
                                        iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                                R.id.dateTextView);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                                R.id.dateTextView);
                                        iv.setLayoutParams(imageParams);

                                        circlesLayout.addView(iv);
                                        circlesLayout.addView(tv);
                                    } else {
                                        imageParams.leftMargin =
                                                ((((circlesLayoutWidth) / (max + 1)) * x) - (circleSize));
                                        iv.setImageBitmap(drawGrayCircle);
                                        iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                                R.id.dateTextView);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                                R.id.dateTextView);
                                        iv.setLayoutParams(imageParams);

                                        circlesLayout.addView(iv);
                                        circlesLayout.addView(tv);
                                    }
                                }
                            } else {

                                if (x == 0) {
                                    iv.setImageBitmap(roundBitmap);
                                    iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                            R.id.dateTextView);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                            R.id.dateTextView);
                                    iv.setLayoutParams(imageParams);

                                    circlesLayout.addView(iv);
                                    circlesLayout.addView(tvStartEnd);


                                } else if (x == 1 && max == 2) {

                                    foregroundBar.getLayoutParams().width =
                                            (circlesLayoutWidth / 3) + (circleSize);
                                    foregroundBar.setPadding(0, 0, -100, 0);
                                    imageParams.leftMargin =
                                            ((((circlesLayoutWidth) / 3)));

                                    iv.setImageBitmap(drawProgressCircle);
                                    iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                            R.id.dateTextView);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                            R.id.dateTextView);
                                    iv.setLayoutParams(imageParams);

                                    circlesLayout.addView(iv);
                                    circlesLayout.addView(tv);

                                } else if (x == state + 1 && x != max) {

                                    imageParams.leftMargin =
                                            ((((circlesLayoutWidth) / (max + 1)))
                                                    - (circleSize / 2));
                                    iv.setImageBitmap(drawProgressCircle);
                                    iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                            R.id.dateTextView);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                            R.id.dateTextView);
                                    iv.setLayoutParams(imageParams);

                                    circlesLayout.addView(iv);
                                    circlesLayout.addView(tv);

                                } else if (x == max && (state + 1) != max) {

                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                    iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                            R.id.dateTextView);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                            R.id.dateTextView);

                                    iv.setImageBitmap(drawGrayCircle);
                                    iv.setLayoutParams(imageParams);

                                    circlesLayout.addView(iv);
                                    circlesLayout.addView(tv);

                                } else if (x == max && (state + 1) == max) {

                                    iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                    iv.setImageBitmap(drawProgressCircle);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                            R.id.dateTextView);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                            R.id.dateTextView);
                                    iv.setLayoutParams(imageParams);

                                    circlesLayout.addView(iv);
                                    circlesLayout.addView(tvStartEnd);

                                } else {
                                    if (x < (((max + 1) / 2))) {
                                        imageParams.leftMargin = 0;

                                        iv.setImageBitmap(drawGrayCircle);
                                        iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                                R.id.dateTextView);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                                R.id.dateTextView);
                                        iv.setLayoutParams(imageParams);
                                        circlesLayout.addView(iv);
                                        circlesLayout.addView(tv);

                                    } else {
                                        imageParams.leftMargin =
                                                ((((circlesLayoutWidth) /
                                                        (max + 1)) * x) - (circleSize));
                                        iv.setImageBitmap(drawGrayCircle);
                                        iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                                R.id.dateTextView);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                                R.id.dateTextView);
                                        iv.setLayoutParams(imageParams);
                                        circlesLayout.addView(iv);
                                        circlesLayout.addView(tv);
                                    }
                                }
                            }
                        }
                    } else if (state > 0 && state < max) {
                        for (int x = 0; x <= max; x++) {

                            indexTracker = x + 1;
                            if (x == 1 && max == 2) {
                                foregroundBar.getLayoutParams().width = 0;
                                foregroundBar.setPadding(0, 0, -100, 0);
                            } else if (max == 4 && state == 2) {
                                foregroundBar.getLayoutParams().width = (
                                        (circlesLayoutWidth / max) * loopIndex);
                                foregroundBar.setPadding(0, 0, -100, 0);
                            } else if (max == 4 && state == 3) {
                                foregroundBar.getLayoutParams().width = circleSize + (circleSize / 2);
                                foregroundBar.setPadding(0, 0, -100, 0);
                            } else if (max == 5 && state == 4) {
                                foregroundBar.getLayoutParams().width = 0;
                                foregroundBar.setPadding(0, 0, -100, 0);
                            } else if (state == 4 && x == 4) {
                                foregroundBar.getLayoutParams().width = 0;
                                foregroundBar.setPadding(0, 0, -100, 0);
                            } else if (max == 5 && state == 3) {
                                foregroundBar.getLayoutParams().width = circleSize * 3;
                                foregroundBar.setPadding(0, 0, -100, 0);
                            } else if (max == 3 && state == 2) {
                                foregroundBar.getLayoutParams().width = circleSize;
                                foregroundBar.setPadding(0, 0, -100, 0);

                            } else if (max == 2 && state == 1) {
                                foregroundBar.getLayoutParams().width = circleSize;
                                foregroundBar.setPadding(0, 0, -100, 0);
                            } else {
                                foregroundBar.getLayoutParams().width =
                                        ((circlesLayoutWidth / max) * loopIndex) - circleSize;
                                foregroundBar.setPadding(0, 0, -100, 0);
                            }
                            bitmap = getBitmapFromView(context, circleView);

                            int startsAt = (((circlesLayoutWidth / max) * x));


                            Bitmap croppedBitmap = cropImage(bitmap, startsAt);
                            Bitmap roundBitmap = getRoundedShape(croppedBitmap, circleSize);
                            bitmapState[x] = roundBitmap;

                            ImageView iv = new ImageView(context);
                            TextView tv = new TextView(context);

                            iv.setId(findId(context, 555));
                            RelativeLayout.LayoutParams imageParams;

                            RelativeLayout.LayoutParams textParams;

                            imageParams = new RelativeLayout
                                    .LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT);
                            textParams = new RelativeLayout
                                    .LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT);

                            iv.setLayoutParams(imageParams);
                            iv.getLayoutParams().width = circleSize;
                            iv.getLayoutParams().height = circleSize;


                            textParams.addRule(RelativeLayout.ALIGN_LEFT, iv.getId());
                            textParams.addRule(RelativeLayout.ALIGN_BOTTOM, iv.getId());
                            textParams.addRule(RelativeLayout.ALIGN_RIGHT, iv.getId());
                            textParams.addRule(RelativeLayout.ALIGN_TOP, iv.getId());

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                textParams.addRule(RelativeLayout.TEXT_ALIGNMENT_GRAVITY,
                                        Gravity.CENTER);
                            }

                            tv.setGravity(Gravity.CENTER);

                            tv.setLayoutParams(textParams);
                            String indexTrackerString = String.valueOf(indexTracker);
                            tv.setText(indexTrackerString);

                            tv.setTextSize(circleTextSize);
                            tv.setTextColor(Color.parseColor("#FFFFFF"));

                            if (max + 1 == 5) {
                                if (x == 0) {
                                    imageParams.setMargins(0, 0, 0, 0);
                                    iv.setImageBitmap(roundBitmap);

                                    iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                            R.id.dateTextView);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                            R.id.dateTextView);

                                    iv.setLayoutParams(imageParams);
                                    circlesLayout.addView(iv);
                                    circlesLayout.addView(tv);
                                } else if (state > x) {
                                    if (x == 1) {
                                        imageParams.leftMargin =
                                                ((((circlesLayoutWidth) / (max + 1)) * x)) -
                                                        (circleSize / 2);

                                        iv.setImageBitmap(roundBitmap);
                                        iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                                R.id.dateTextView);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                                R.id.dateTextView);
                                        iv.setLayoutParams(imageParams);

                                        circlesLayout.addView(iv);
                                        circlesLayout.addView(tv);

                                    } else {
                                        imageParams.leftMargin =
                                                ((((circlesLayoutWidth) / (max + 1)) * x)
                                                        - (circleSize));

                                        iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                                R.id.dateTextView);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                                R.id.dateTextView);


                                        iv.setImageBitmap(roundBitmap);
                                        iv.setLayoutParams(imageParams);
                                        circlesLayout.addView(iv);
                                        circlesLayout.addView(tv);
                                    }
                                }
//
                                else if (x + 1 == max) {
                                    if (state < x - 1) {
                                        imageParams.leftMargin =
                                                ((((circlesLayoutWidth) / (max + 1)) * x)) -
                                                        (circleSize + (circleSize / 2));

                                        iv.setImageBitmap(drawGrayCircle);
                                        iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                                R.id.dateTextView);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                                R.id.dateTextView);
                                        iv.setLayoutParams(imageParams);

                                        circlesLayout.addView(iv);
                                        circlesLayout.addView(tv);
                                    } else if (state == x) {
                                        imageParams.leftMargin =
                                                ((((circlesLayoutWidth) / (max + 1)) * x)) -
                                                        (circleSize + (circleSize / 2));

                                        iv.setImageBitmap(roundBitmap);
                                        iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                                R.id.dateTextView);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                                R.id.dateTextView);
                                        iv.setLayoutParams(imageParams);

                                        circlesLayout.addView(iv);
                                        circlesLayout.addView(tv);
                                    } else {
                                        imageParams.leftMargin =
                                                ((((circlesLayoutWidth) / (max + 1)) * x)) -
                                                        (circleSize + (circleSize / 2));

                                        iv.setImageBitmap(drawProgressCircle);
                                        iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                                R.id.dateTextView);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                                R.id.dateTextView);
                                        iv.setLayoutParams(imageParams);

                                        circlesLayout.addView(iv);
                                        circlesLayout.addView(tv);
                                    }
                                } else if (x == max) {
                                    if (x - 1 > state) {
                                        iv.requestLayout();
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                        iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                                R.id.dateTextView);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                                R.id.dateTextView);

                                        iv.setImageBitmap(drawGrayCircle);
                                        iv.setLayoutParams(imageParams);
                                        circlesLayout.addView(iv);
                                        circlesLayout.addView(tv);
                                    } else if (x - 1 == state) {
                                        iv.requestLayout();
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                        iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                                R.id.dateTextView);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                                R.id.dateTextView);

                                        iv.setImageBitmap(drawProgressCircle);
                                        iv.setLayoutParams(imageParams);
                                        circlesLayout.addView(iv);
                                        circlesLayout.addView(tv);
                                    } else {
                                        iv.requestLayout();
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                        iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                                R.id.dateTextView);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                                R.id.dateTextView);

                                        iv.setImageBitmap(roundBitmap);
                                        iv.setLayoutParams(imageParams);
                                        circlesLayout.addView(iv);
                                        circlesLayout.addView(tv);

                                    }

                                } else if (x == state) {
                                    if (x == 1) {
                                        imageParams.leftMargin =
                                                ((((circlesLayoutWidth) / (max + 1)) * x)) -
                                                        (circleSize / 2);

                                        iv.setImageBitmap(roundBitmap);
                                        iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                                R.id.dateTextView);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                                R.id.dateTextView);
                                        iv.setLayoutParams(imageParams);

                                        circlesLayout.addView(iv);
                                        circlesLayout.addView(tv);

                                    } else if (x == 2) {
                                        imageParams.leftMargin =
                                                ((((circlesLayoutWidth) / (max + 1)) * x)) -
                                                        (circleSize - 2);

                                        iv.setImageBitmap(roundBitmap);
                                        iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                                R.id.dateTextView);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                                R.id.dateTextView);
                                        iv.setLayoutParams(imageParams);

                                        circlesLayout.addView(iv);
                                        circlesLayout.addView(tv);

                                    } else {
                                        imageParams.leftMargin =
                                                ((((circlesLayoutWidth) / (max + 1)) * x)) -
                                                        (circleSize + (circleSize / 2));

                                        iv.setImageBitmap(roundBitmap);
                                        iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                                R.id.dateTextView);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                                R.id.dateTextView);
                                        iv.setLayoutParams(imageParams);

                                        circlesLayout.addView(iv);
                                        circlesLayout.addView(tv);

                                    }
                                } else if (x == state + 1 && x != max) {
                                    imageParams.leftMargin =
                                            ((((circlesLayoutWidth) / (max + 1)) * x) - (circleSize));

                                    iv.setImageBitmap(drawProgressCircle);
                                    iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                            R.id.dateTextView);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                            R.id.dateTextView);
                                    iv.setLayoutParams(imageParams);

                                    circlesLayout.addView(iv);
                                    circlesLayout.addView(tv);

                                } else if (x == max && (state == max - 1)) {
                                    foregroundBar.getLayoutParams().width = circleSize * 2;
                                    foregroundBar.setPadding(0, 0, -300, 0);

                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                            R.id.dateTextView);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                            R.id.dateTextView);
                                    iv.setScaleType(ImageView.ScaleType.FIT_XY);

                                    iv.setImageBitmap(drawProgressCircle);
                                    iv.setLayoutParams(imageParams);

                                    circlesLayout.addView(iv);
                                    circlesLayout.addView(tv);


                                } else if (x == max && (state + 1) != max) {
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                            R.id.dateTextView);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                            R.id.dateTextView);
                                    iv.setScaleType(ImageView.ScaleType.FIT_XY);

                                    iv.setImageBitmap(drawGrayCircle);
                                    iv.setLayoutParams(imageParams);

                                    circlesLayout.addView(iv);
                                    circlesLayout.addView(tv);
                                } else if (x == max && ((state + 1) == max)) {
                                    foregroundBar.getLayoutParams().width = 0;
                                    foregroundBar.setPadding(0, 0, -100, 0);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                            R.id.dateTextView);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                            R.id.dateTextView);
                                    iv.setScaleType(ImageView.ScaleType.FIT_XY);

                                    iv.setImageBitmap(drawProgressCircle);
                                    iv.setLayoutParams(imageParams);

                                    circlesLayout.addView(iv);
                                    circlesLayout.addView(tv);
                                }
                            } else if (max + 1 == 6) {

                                if (state == 4 && x == 4) {
                                    imageParams.leftMargin =
                                            ((((circlesLayoutWidth) / (max + 1)) * x)
                                                    - (circleSize + circleSize));

                                    iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                            R.id.dateTextView);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                            R.id.dateTextView);

                                    iv.setImageBitmap(roundBitmap);
                                    iv.setLayoutParams(imageParams);

                                    circlesLayout.addView(iv);
                                    circlesLayout.addView(tv);

                                } else if (x == 3 && state < x - 1) {
                                    imageParams.leftMargin =
                                            ((((circlesLayoutWidth) / (max + 1)) * x) -
                                                    (circleSize + (circleSize / 2)));

                                    iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                            R.id.dateTextView);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                            R.id.dateTextView);

                                    iv.setImageBitmap(drawGrayCircle);
                                    iv.setLayoutParams(imageParams);

                                    circlesLayout.addView(iv);
                                    circlesLayout.addView(tv);
                                } else if (x == 4) {
                                    if (state == x - 1) {
                                        imageParams.leftMargin =
                                                ((((circlesLayoutWidth) / (max + 1)) * x) -
                                                        (circleSize + (circleSize)));

                                        iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                                R.id.dateTextView);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                                R.id.dateTextView);

                                        iv.setImageBitmap(drawProgressCircle);
                                        iv.setLayoutParams(imageParams);

                                        circlesLayout.addView(iv);
                                        circlesLayout.addView(tv);
                                    } else {
                                        imageParams.leftMargin =
                                                ((((circlesLayoutWidth) / (max + 1)) * x) -
                                                        (circleSize * 2));

                                        iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                                R.id.dateTextView);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                                R.id.dateTextView);

                                        iv.setImageBitmap(drawGrayCircle);
                                        iv.setLayoutParams(imageParams);

                                        circlesLayout.addView(iv);
                                        circlesLayout.addView(tv);
                                    }

                                } else if (x == 0) {
                                    iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                            R.id.dateTextView);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                            R.id.dateTextView);
                                    iv.setImageBitmap(roundBitmap);
                                    iv.setLayoutParams(imageParams);

                                    circlesLayout.addView(iv);
                                    circlesLayout.addView(tv);

                                } else if (x == 1 && x != max) {

                                    imageParams.leftMargin = ((((circlesLayoutWidth) / (max + 1)) -
                                            (circleSize / 2)));
                                    iv.setImageBitmap(roundBitmap);
                                    iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                            R.id.dateTextView);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                            R.id.dateTextView);
                                    iv.setLayoutParams(imageParams);

                                    circlesLayout.addView(iv);
                                    circlesLayout.addView(tv);

                                } else if (state >= x) {
                                    if (x == 4) {

                                        imageParams.leftMargin =
                                                ((((circlesLayoutWidth) / (max + 1)) * x) -
                                                        (circleSize + (circleSize)));

                                        iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                                R.id.dateTextView);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                                R.id.dateTextView);

                                        iv.setImageBitmap(roundBitmap);
                                        iv.setLayoutParams(imageParams);

                                        circlesLayout.addView(iv);
                                        circlesLayout.addView(tv);

                                    } else if (x != 3) {
                                        imageParams.leftMargin =
                                                ((((circlesLayoutWidth) / (max + 1)) * x) -
                                                        circleSize);
                                        iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                                R.id.dateTextView);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                                R.id.dateTextView);

                                        iv.setImageBitmap(roundBitmap);
                                        iv.setLayoutParams(imageParams);

                                        circlesLayout.addView(iv);
                                        circlesLayout.addView(tv);

                                    } else if (x == 3) {
                                        imageParams.leftMargin =
                                                ((((circlesLayoutWidth) / (max + 1)) * x) -
                                                        (circleSize + (circleSize / 2)));

                                        iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                                R.id.dateTextView);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                                R.id.dateTextView);

                                        iv.setImageBitmap(roundBitmap);
                                        iv.setLayoutParams(imageParams);

                                        circlesLayout.addView(iv);
                                        circlesLayout.addView(tv);

                                    }

                                } else if (x == state + 1 && x != max) {

                                    if (x == 4) {
                                        imageParams.leftMargin =
                                                ((((circlesLayoutWidth) / (max + 1)) * x) -
                                                        (circleSize));

                                        iv.setImageBitmap(drawProgressCircle);
                                        iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                                R.id.dateTextView);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                                R.id.dateTextView);
                                        iv.setLayoutParams(imageParams);

                                        circlesLayout.addView(iv);
                                        circlesLayout.addView(tv);
                                    } else if (x == 3) {
                                        imageParams.leftMargin =
                                                ((((circlesLayoutWidth) / (max + 1)) * x) -
                                                        (circleSize + circleSize / 2));

                                        iv.setImageBitmap(drawProgressCircle);
                                        iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                                R.id.dateTextView);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                                R.id.dateTextView);
                                        iv.setLayoutParams(imageParams);

                                        circlesLayout.addView(iv);
                                        circlesLayout.addView(tv);

                                    } else {
                                        imageParams.leftMargin =
                                                ((((circlesLayoutWidth) / (max + 1)) * x) -
                                                        circleSize);

                                        iv.setImageBitmap(drawProgressCircle);
                                        iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                                R.id.dateTextView);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                                R.id.dateTextView);
                                        iv.setLayoutParams(imageParams);

                                        circlesLayout.addView(iv);
                                        circlesLayout.addView(tv);

                                    }
                                } else if (x > state && x < max) {

                                    if (x == 3) {
                                        imageParams.leftMargin =
                                                ((((circlesLayoutWidth) / (max + 1)) * x) -
                                                        (circleSize));

                                        iv.setImageBitmap(drawGrayCircle);
                                        iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                                R.id.dateTextView);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                                R.id.dateTextView);
                                        iv.setLayoutParams(imageParams);

                                        circlesLayout.addView(iv);
                                        circlesLayout.addView(tv);

                                    } else {
                                        imageParams.leftMargin =
                                                ((((circlesLayoutWidth) / (max + 1)) * x) -
                                                        (circleSize));
                                        iv.setImageBitmap(drawGrayCircle);
                                        iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                                R.id.dateTextView);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                                R.id.dateTextView);
                                        iv.setLayoutParams(imageParams);

                                        circlesLayout.addView(iv);
                                        circlesLayout.addView(tv);
                                    }


                                } else if (x == max && (state == max - 1)) {
                                    foregroundBar.getLayoutParams().width = 0;
                                    foregroundBar.setPadding(0, 0, -100, 0);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                            R.id.dateTextView);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                            R.id.dateTextView);
                                    iv.setScaleType(ImageView.ScaleType.FIT_XY);

                                    iv.setImageBitmap(drawProgressCircle);
                                    iv.setLayoutParams(imageParams);
                                    circlesLayout.addView(iv);
                                    circlesLayout.addView(tv);

                                } else if (x == max && (state + 1) != max) {
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                            R.id.dateTextView);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                            R.id.dateTextView);
                                    iv.setScaleType(ImageView.ScaleType.FIT_XY);

                                    iv.setImageBitmap(drawGrayCircle);
                                    iv.setLayoutParams(imageParams);
                                    circlesLayout.addView(iv);
                                    circlesLayout.addView(tv);

                                } else if (x == max && ((state + 1) == max)) {
                                    foregroundBar.getLayoutParams().width = 0;
                                    foregroundBar.setPadding(0, 0, -100, 0);

                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                            R.id.dateTextView);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                            R.id.dateTextView);
                                    iv.setScaleType(ImageView.ScaleType.FIT_XY);

                                    iv.setImageBitmap(drawProgressCircle);
                                    iv.setLayoutParams(imageParams);
                                    circlesLayout.addView(iv);
                                    circlesLayout.addView(tv);

                                }

                            } else {

                                if (x == 0) {
                                    iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                            R.id.dateTextView);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                            R.id.dateTextView);
                                    iv.setImageBitmap(bitmapState[x]);
                                    iv.setLayoutParams(imageParams);

                                    circlesLayout.addView(iv);
                                    circlesLayout.addView(tv);

                                } else if (x == 1 && max == 2) {

                                    imageParams.leftMargin = circlesLayoutWidth / 3;
                                    iv.setImageBitmap(roundBitmap);
                                    iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                            R.id.dateTextView);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                            R.id.dateTextView);
                                    iv.setLayoutParams(imageParams);

                                    circlesLayout.addView(iv);
                                    circlesLayout.addView(tv);

                                } else if (x == 1 && x != max) {
                                    //axv
                                    imageParams.leftMargin =
                                            ((((circlesLayoutWidth) / (max + 1)) * x) - (circleSize / 2));
                                    iv.setImageBitmap(bitmapState[x]);
                                    iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                            R.id.dateTextView);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                            R.id.dateTextView);
                                    iv.setLayoutParams(imageParams);

                                    circlesLayout.addView(iv);
                                    circlesLayout.addView(tv);

                                } else if (state >= x) {
                                    imageParams.leftMargin =
                                            ((((circlesLayoutWidth) / (max + 1)) * x) - (circleSize));
                                    iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                            R.id.dateTextView);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                            R.id.dateTextView);

                                    iv.setImageBitmap(bitmapState[x]);
                                    iv.setLayoutParams(imageParams);

                                    circlesLayout.addView(iv);
                                    circlesLayout.addView(tv);


                                } else if (x == state + 1 && x != max) {
                                    imageParams.leftMargin =
                                            ((((circlesLayoutWidth) / (max + 1)) * x) - (circleSize));

                                    iv.setImageBitmap(drawProgressCircle);
                                    iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                            R.id.dateTextView);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                            R.id.dateTextView);
                                    iv.setLayoutParams(imageParams);

                                    circlesLayout.addView(iv);
                                    circlesLayout.addView(tv);

                                } else if (x > state && x < max) {
                                    imageParams.leftMargin =
                                            ((((circlesLayoutWidth) / (max + 1)) * x) - (circleSize));
                                    iv.setImageBitmap(drawGrayCircle);
                                    iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                            R.id.dateTextView);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                            R.id.dateTextView);
                                    iv.setLayoutParams(imageParams);

                                    circlesLayout.addView(iv);
                                    circlesLayout.addView(tv);

                                } else if (x == max && (state == max - 1)) {
                                    foregroundBar.getLayoutParams().width = circleSize * 2;
                                    foregroundBar.setPadding(0, 0, -300, 0);

                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                            R.id.dateTextView);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                            R.id.dateTextView);
                                    iv.setScaleType(ImageView.ScaleType.FIT_XY);

                                    iv.setImageBitmap(drawProgressCircle);
                                    iv.setLayoutParams(imageParams);

                                    circlesLayout.addView(iv);
                                    circlesLayout.addView(tv);
                                } else if (x == max && (state + 1) != max) {
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                            R.id.dateTextView);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                            R.id.dateTextView);
                                    iv.setScaleType(ImageView.ScaleType.FIT_XY);

                                    iv.setImageBitmap(drawGrayCircle);
                                    iv.setLayoutParams(imageParams);

                                    circlesLayout.addView(iv);
                                    circlesLayout.addView(tv);
                                } else if (x == max && ((state + 1) == max)) {
                                    foregroundBar.getLayoutParams().width = 0;
                                    foregroundBar.setPadding(0, 0, -100, 0);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                            R.id.dateTextView);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                            R.id.dateTextView);
                                    iv.setScaleType(ImageView.ScaleType.FIT_XY);

                                    iv.setImageBitmap(drawProgressCircle);
                                    iv.setLayoutParams(imageParams);

                                    circlesLayout.addView(iv);
                                    circlesLayout.addView(tv);
                                }

                            }
                        }
                    } else if (state == max) {
                        for (int x = 0; x <= max; x++) {

                            indexTracker = x + 1;
                            foregroundBar.getLayoutParams().width = 0;
                            bitmap = getBitmapFromView(context, circleView);

                            int startsAt = (((circlesLayoutWidth / max) * x));

                            Bitmap croppedBitmap = cropImage(bitmap, startsAt);
                            Bitmap roundBitmap = getRoundedShape(croppedBitmap, circleSize);


                            ImageView iv = new ImageView(context);
                            TextView tv = new TextView(context);


                            iv.setId(findId(context, 666));

                            RelativeLayout.LayoutParams imageParams;
                            RelativeLayout.LayoutParams textParams;


                            imageParams = new RelativeLayout
                                    .LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT);
                            textParams = new RelativeLayout
                                    .LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT);

                            iv.setLayoutParams(imageParams);
                            iv.requestLayout();
                            iv.getLayoutParams().width = circleSize;
                            iv.getLayoutParams().height = circleSize;


                            textParams.addRule(RelativeLayout.ALIGN_LEFT, iv.getId());
                            textParams.addRule(RelativeLayout.ALIGN_BOTTOM, iv.getId());
                            textParams.addRule(RelativeLayout.ALIGN_RIGHT, iv.getId());
                            textParams.addRule(RelativeLayout.ALIGN_TOP, iv.getId());

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                textParams.addRule(RelativeLayout.TEXT_ALIGNMENT_GRAVITY,
                                        Gravity.CENTER);
                            }

                            tv.setGravity(Gravity.CENTER);
                            tv.setLayoutParams(textParams);
                            String indexTrackerString = String.valueOf(indexTracker);
                            tv.setText(indexTrackerString);

                            tv.setTextSize(circleTextSize);
                            tv.setTextColor(Color.parseColor("#FFFFFF"));

                            if (max + 1 == 5) {
                                if (x == 0) {
                                    imageParams.setMargins(0, 0, 0, 0);
                                    iv.setImageBitmap(roundBitmap);

                                    iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                            R.id.dateTextView);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                            R.id.dateTextView);

                                    iv.setLayoutParams(imageParams);
                                    circlesLayout.addView(iv);
                                    circlesLayout.addView(tv);
                                } else if (state > x) {
                                    if (x == 1) {
                                        imageParams.leftMargin =
                                                ((((circlesLayoutWidth) / (max + 1)) * x)) -
                                                        (circleSize / 2);

                                        iv.setImageBitmap(roundBitmap);
                                        iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                                R.id.dateTextView);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                                R.id.dateTextView);
                                        iv.setLayoutParams(imageParams);

                                        circlesLayout.addView(iv);
                                        circlesLayout.addView(tv);

                                    } else if (x == 2) {
                                        imageParams.leftMargin =
                                                ((((circlesLayoutWidth) / (max + 1)) * x)) -
                                                        (circleSize);

                                        iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                                R.id.dateTextView);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                                R.id.dateTextView);


                                        iv.setImageBitmap(roundBitmap);
                                        iv.setLayoutParams(imageParams);
                                        circlesLayout.addView(iv);
                                        circlesLayout.addView(tv);
                                    } else {
                                        imageParams.leftMargin =
                                                ((((circlesLayoutWidth) / (max + 1)) * x)) -
                                                        (circleSize + (circleSize / 2));

                                        iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                                R.id.dateTextView);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                                R.id.dateTextView);


                                        iv.setImageBitmap(roundBitmap);
                                        iv.setLayoutParams(imageParams);
                                        circlesLayout.addView(iv);
                                        circlesLayout.addView(tv);
                                    }
                                }
                                //
                                else if (x + 1 == max) {
                                    if (state < x - 1) {
                                        imageParams.leftMargin =
                                                ((((circlesLayoutWidth) / (max + 1)) * x)) -
                                                        (circleSize + (circleSize / 2));

                                        iv.setImageBitmap(drawGrayCircle);
                                        iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                                R.id.dateTextView);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                                R.id.dateTextView);
                                        iv.setLayoutParams(imageParams);

                                        circlesLayout.addView(iv);
                                        circlesLayout.addView(tv);
                                    } else if (state == x) {
                                        imageParams.leftMargin =
                                                ((((circlesLayoutWidth) / (max + 1)) * x)) -
                                                        (circleSize + (circleSize / 2));

                                        iv.setImageBitmap(roundBitmap);
                                        iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                                R.id.dateTextView);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                                R.id.dateTextView);
                                        iv.setLayoutParams(imageParams);

                                        circlesLayout.addView(iv);
                                        circlesLayout.addView(tv);
                                    } else {
                                        imageParams.leftMargin =
                                                ((((circlesLayoutWidth) / (max + 1)) * x)) -
                                                        (circleSize);

                                        iv.setImageBitmap(drawProgressCircle);
                                        iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                                R.id.dateTextView);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                                R.id.dateTextView);
                                        iv.setLayoutParams(imageParams);

                                        circlesLayout.addView(iv);
                                        circlesLayout.addView(tv);
                                    }
                                } else if (x == max) {
                                    if (x - 1 > state) {
                                        iv.requestLayout();
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                        iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                                R.id.dateTextView);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                                R.id.dateTextView);

                                        iv.setImageBitmap(drawGrayCircle);
                                        iv.setLayoutParams(imageParams);
                                        circlesLayout.addView(iv);
                                        circlesLayout.addView(tv);
                                    } else if (x - 1 == state) {
                                        iv.requestLayout();
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                        iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                                R.id.dateTextView);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                                R.id.dateTextView);

                                        iv.setImageBitmap(drawProgressCircle);
                                        iv.setLayoutParams(imageParams);
                                        circlesLayout.addView(iv);
                                        circlesLayout.addView(tv);
                                    } else {
                                        iv.requestLayout();
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                        iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                                R.id.dateTextView);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                                R.id.dateTextView);

                                        iv.setImageBitmap(roundBitmap);
                                        iv.setLayoutParams(imageParams);
                                        circlesLayout.addView(iv);
                                        circlesLayout.addView(tv);

                                    }

                                } else if (x == state) {
                                    if (x == 1) {
                                        imageParams.leftMargin =
                                                ((((circlesLayoutWidth) / (max + 1)) * x)) -
                                                        (circleSize / 2);

                                        iv.setImageBitmap(roundBitmap);
                                        iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                                R.id.dateTextView);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                                R.id.dateTextView);
                                        iv.setLayoutParams(imageParams);

                                        circlesLayout.addView(iv);
                                        circlesLayout.addView(tv);

                                    } else if (x == 2) {
                                        imageParams.leftMargin =
                                                ((((circlesLayoutWidth) / (max + 1)) * x)) -
                                                        (circleSize - 2);

                                        iv.setImageBitmap(roundBitmap);
                                        iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                                R.id.dateTextView);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                                R.id.dateTextView);
                                        iv.setLayoutParams(imageParams);

                                        circlesLayout.addView(iv);
                                        circlesLayout.addView(tv);

                                    } else {
                                        imageParams.leftMargin =
                                                ((((circlesLayoutWidth) / (max + 1)) * x)) -
                                                        (circleSize);

                                        iv.setImageBitmap(roundBitmap);
                                        iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                                R.id.dateTextView);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                                R.id.dateTextView);
                                        iv.setLayoutParams(imageParams);

                                        circlesLayout.addView(iv);
                                        circlesLayout.addView(tv);

                                    }
                                } else if (x == state + 1 && x != max) {
                                    imageParams.leftMargin =
                                            ((((circlesLayoutWidth) / (max + 1)) * x) - (circleSize));

                                    iv.setImageBitmap(drawProgressCircle);
                                    iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                            R.id.dateTextView);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                            R.id.dateTextView);
                                    iv.setLayoutParams(imageParams);

                                    circlesLayout.addView(iv);
                                    circlesLayout.addView(tv);

                                } else if (x == max && (state == max - 1)) {
                                    foregroundBar.getLayoutParams().width = circleSize * 2;
                                    foregroundBar.setPadding(0, 0, -300, 0);

                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                            R.id.dateTextView);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                            R.id.dateTextView);
                                    iv.setScaleType(ImageView.ScaleType.FIT_XY);

                                    iv.setImageBitmap(drawProgressCircle);
                                    iv.setLayoutParams(imageParams);

                                    circlesLayout.addView(iv);
                                    circlesLayout.addView(tv);


                                } else if (x == max && (state + 1) != max) {
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                            R.id.dateTextView);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                            R.id.dateTextView);
                                    iv.setScaleType(ImageView.ScaleType.FIT_XY);

                                    iv.setImageBitmap(drawGrayCircle);
                                    iv.setLayoutParams(imageParams);

                                    circlesLayout.addView(iv);
                                    circlesLayout.addView(tv);
                                } else if (x == max && ((state + 1) == max)) {
                                    foregroundBar.getLayoutParams().width = 0;
                                    foregroundBar.setPadding(0, 0, -100, 0);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                            R.id.dateTextView);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                            R.id.dateTextView);
                                    iv.setScaleType(ImageView.ScaleType.FIT_XY);

                                    iv.setImageBitmap(drawProgressCircle);
                                    iv.setLayoutParams(imageParams);

                                    circlesLayout.addView(iv);
                                    circlesLayout.addView(tv);
                                }
                            } else if (max + 1 == 6) {
                                if (x == 5) {
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                    iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                            R.id.dateTextView);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                            R.id.dateTextView);
                                    iv.setImageBitmap(roundBitmap);
                                    iv.setLayoutParams(imageParams);

                                    circlesLayout.addView(iv);
                                    circlesLayout.addView(tv);
                                } else if (x == 4) {
                                    imageParams.leftMargin =
                                            ((((circlesLayoutWidth) / (max + 1)) * x) - (circleSize +
                                                    circleSize));

                                    iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                            R.id.dateTextView);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                            R.id.dateTextView);
                                    iv.setImageBitmap(roundBitmap);
                                    iv.setLayoutParams(imageParams);

                                    circlesLayout.addView(iv);
                                    circlesLayout.addView(tv);
                                } else if (x == 0) {

                                    iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                            R.id.dateTextView);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                            R.id.dateTextView);
                                    iv.setImageBitmap(roundBitmap);
                                    iv.setLayoutParams(imageParams);

                                    circlesLayout.addView(iv);
                                    circlesLayout.addView(tv);

                                } else if (x == 2) {

                                    imageParams.leftMargin =
                                            ((((circlesLayoutWidth) / (max + 1)) * x) - circleSize);

                                    iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                            R.id.dateTextView);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                            R.id.dateTextView);
                                    iv.setImageBitmap(roundBitmap);
                                    iv.setLayoutParams(imageParams);

                                    circlesLayout.addView(iv);
                                    circlesLayout.addView(tv);

                                } else if (x == 1 && x != max) {

                                    imageParams.leftMargin =
                                            ((((circlesLayoutWidth) / (max + 1)) - (circleSize / 2)));
                                    iv.setImageBitmap(roundBitmap);
                                    iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                            R.id.dateTextView);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                            R.id.dateTextView);
                                    iv.setLayoutParams(imageParams);

                                    circlesLayout.addView(iv);
                                    circlesLayout.addView(tv);

                                } else if (state >= x) {
                                    if (x != 3) {
                                        imageParams.leftMargin =
                                                ((((circlesLayoutWidth) / (max + 1)) * x) - circleSize);
                                        iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                                R.id.dateTextView);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                                R.id.dateTextView);

                                        iv.setImageBitmap(roundBitmap);
                                        iv.setLayoutParams(imageParams);

                                        circlesLayout.addView(iv);
                                        circlesLayout.addView(tv);

                                    } else if (x == 3) {
                                        imageParams.leftMargin =
                                                ((((circlesLayoutWidth) / (max + 1)) * x) -
                                                        (circleSize + (circleSize / 2)));


                                        iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                                R.id.dateTextView);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                                R.id.dateTextView);

                                        iv.setImageBitmap(roundBitmap);
                                        iv.setLayoutParams(imageParams);

                                        circlesLayout.addView(iv);
                                        circlesLayout.addView(tv);

                                    }

                                } else if (x == state + 1 && x != max) {

                                    if (x == 4) {
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                        imageParams.rightMargin =
                                                ((((circlesLayoutWidth) / (max * 2))));

                                        iv.setImageBitmap(drawProgressCircle);
                                        iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                                R.id.dateTextView);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                                R.id.dateTextView);
                                        iv.setLayoutParams(imageParams);

                                        circlesLayout.addView(iv);
                                        circlesLayout.addView(tv);

                                    } else if (x == 3) {
                                        imageParams.leftMargin =
                                                ((((circlesLayoutWidth) / (max + 1)) * x) - (circleSize));

                                        iv.setImageBitmap(drawProgressCircle);
                                        iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                                R.id.dateTextView);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                                R.id.dateTextView);
                                        iv.setLayoutParams(imageParams);

                                        circlesLayout.addView(iv);
                                        circlesLayout.addView(tv);

                                    } else {
                                        imageParams.leftMargin = ((((circlesLayoutWidth) /
                                                (max + 1)) * x) - circleSize);

                                        iv.setImageBitmap(drawProgressCircle);
                                        iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                                R.id.dateTextView);
                                        imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                                R.id.dateTextView);
                                        iv.setLayoutParams(imageParams);

                                        circlesLayout.addView(iv);
                                        circlesLayout.addView(tv);

                                    }
                                }

                            } else if (max + 1 == 5) {
                                if (x == 0) {
                                    imageParams.setMargins(0, 0, 0, 0);
                                    iv.setImageBitmap(roundBitmap);

                                    iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                            R.id.dateTextView);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                            R.id.dateTextView);

                                    iv.setLayoutParams(imageParams);
                                    circlesLayout.addView(iv);
                                    circlesLayout.addView(tv);
                                } else if (state > x) {
                                    imageParams.leftMargin
                                            = ((((circlesLayoutWidth) / (max + 1)) * x) -
                                            (circleSize));

                                    iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                            R.id.dateTextView);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                            R.id.dateTextView);

                                    iv.setImageBitmap(roundBitmap);
                                    iv.setLayoutParams(imageParams);
                                    circlesLayout.addView(iv);
                                    circlesLayout.addView(tv);

                                } else if (x == max) {
                                    iv.requestLayout();
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                    iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                            R.id.dateTextView);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                            R.id.dateTextView);

                                    iv.setImageBitmap(roundBitmap);
                                    iv.setLayoutParams(imageParams);
                                    circlesLayout.addView(iv);
                                    circlesLayout.addView(tv);

                                }
                            } else {
                                if (x == 0) {
                                    imageParams.setMargins(0, 0, 0, 0);
                                    iv.setImageBitmap(roundBitmap);

                                    iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                            R.id.dateTextView);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                            R.id.dateTextView);

                                    iv.setLayoutParams(imageParams);
                                    circlesLayout.addView(iv);
                                    circlesLayout.addView(tv);
                                } else if (x == 1 && max == 2) {

                                    imageParams.leftMargin = ((circlesLayoutWidth) / 3);
                                    iv.setImageBitmap(roundBitmap);
                                    iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                            R.id.dateTextView);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                            R.id.dateTextView);
                                    iv.setLayoutParams(imageParams);

                                    circlesLayout.addView(iv);
                                    circlesLayout.addView(tv);

                                } else if (x + 1 == max) {
                                    imageParams.leftMargin =
                                            ((((circlesLayoutWidth) /
                                                    (max + 1)) * x) - (circleSize));
                                    iv.setImageBitmap(roundBitmap);
                                    iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                            R.id.dateTextView);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                            R.id.dateTextView);
                                    iv.setLayoutParams(imageParams);
                                    circlesLayout.addView(iv);
                                    circlesLayout.addView(tv);
                                } else if (state > x) {
                                    imageParams.leftMargin
                                            = ((((circlesLayoutWidth) / (max + 1)) * x) -
                                            (circleSize / 2));

                                    iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                            R.id.dateTextView);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                            R.id.dateTextView);

                                    iv.setImageBitmap(roundBitmap);
                                    iv.setLayoutParams(imageParams);
                                    circlesLayout.addView(iv);
                                    circlesLayout.addView(tv);

                                } else if (x == max) {
                                    iv.requestLayout();
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                    iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
                                            R.id.dateTextView);
                                    imageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
                                            R.id.dateTextView);

                                    iv.setImageBitmap(roundBitmap);
                                    iv.setLayoutParams(imageParams);
                                    circlesLayout.addView(iv);
                                    circlesLayout.addView(tv);

                                }
                            }
                        }
                    }
                    circleView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });



            TextView tempDate = (TextView) yourView.findViewById(R.id.dateTextView);
            TextView tempPay = (TextView) yourView.findViewById(R.id.dollarValue);
            ImageView checkImage = (ImageView) yourView.findViewById(R.id.checkImage);


            if (ourpayTransactions.get(i).getState() == 2) {
                tempPay.setText(R.string.paid);
                checkImage.setVisibility(View.VISIBLE);

            } else{
                tempPay.setText(PriceUtils.getPriceStringValue(ourpayTransactions.get(i).getAmount()));
                checkImage.setVisibility(View.GONE);
            }

            tempDate.setText(OurpayUtils.convertDateToTrimmedString(ourpayTransactions.get(i).getPlannedDate()));

            if (i != tempView.length) {
                a.setId(i);
                a.addView(yourView);
                tempView[i] = yourView;
                layPayViewId.addView(a);
            }

        }


        return layPayViewId;
    }

}
