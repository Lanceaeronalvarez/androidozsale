package au.com.dealsdirect.utils;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.ReplacementSpan;

/**
 * Created by MTC on 2019-10-18.
 */
public class SlashSpan extends ReplacementSpan {

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        float padding = paint.measureText(" ", 0, 1) * 2;
        float slash = paint.measureText("/", 0, 1);
        float textSize = paint.measureText(text, start, end);
        return (int) (padding + slash + textSize);
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y,
                     int bottom, Paint paint) {
        canvas.drawText(text.subSequence(start, end) + " / ", x, y, paint);
    }
}