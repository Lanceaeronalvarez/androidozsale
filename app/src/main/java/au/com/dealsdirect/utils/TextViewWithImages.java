package au.com.dealsdirect.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import au.com.dealsdirect.R;

/**
 * dd Created by Admin on 8/8/17.
 */

public class TextViewWithImages extends TextView {


    public TextViewWithImages(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    public TextViewWithImages(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public TextViewWithImages(Context context) {
        super(context);
    }
    @Override
    public void setText(CharSequence text, BufferType type) {
        Spannable s = getTextWithImages(getContext(), text);
        super.setText(s, BufferType.SPANNABLE);
    }

    private static final Spannable.Factory spannableFactory = Spannable.Factory.getInstance();

    private boolean addImages(Context context, Spannable spannable) {
        Pattern refImg = Pattern.compile("\\Q[img src=\\E([a-zA-Z0-9_]+?)\\Q/]\\E");
        boolean hasChanges = false;

        Matcher matcher = refImg.matcher(spannable);
        while (matcher.find()) {
            boolean set = true;
            for (ImageSpan span : spannable.getSpans(matcher.start(), matcher.end(), ImageSpan.class)) {
                if (spannable.getSpanStart(span) >= matcher.start()
                        && spannable.getSpanEnd(span) <= matcher.end()
                        ) {
                    spannable.removeSpan(span);
                } else {
                    set = false;
                    break;
                }
            }
            String resname = spannable.subSequence(matcher.start(1), matcher.end(1)).toString().trim();
            int id = context.getResources().getIdentifier(resname, "drawable", context.getPackageName());

            final boolean isTablet = context.getResources().getBoolean(R.bool.is_tablet);
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            int tempRight;
            int tempBottom;

            if (isTablet) {
                tempRight = (int) (100 * dm.density);
                tempBottom = (int) (20 * dm.density);
            } else {
                tempRight = (int) (67 * dm.density);
                tempBottom = (int) (13 * dm.density);
            }


            Drawable myIcon = context.getResources().getDrawable(id);
            myIcon.setBounds(0, 0, tempRight, tempBottom);
            if (set) {
                hasChanges = true;
                spannable.setSpan(  new ImageSpan(myIcon, ImageSpan.ALIGN_BASELINE),
                        matcher.start(),
                        matcher.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                );
            }
        }

        return hasChanges;
    }
    private Spannable getTextWithImages(Context context, CharSequence text) {
        Spannable spannable = spannableFactory.newSpannable(text);
        addImages(context, spannable);
        return spannable;
    }
}
