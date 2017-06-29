package com.mysale.genie.views.custom;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.mysale.genie.views.R;

/**
 * Created by SmartwaveDev on 30/05/16.
 */
public class GenieTextView extends TextView {
    public static final String ANDROID_SCHEMA = "http://schemas.android.com/apk/res/android";
    public AttributeSet attrSet;
    public GenieTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyFont(context,attrs);
    }

    public GenieTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyFont(context,attrs);
    }

    private void applyFont(Context context, AttributeSet attrs) {
        attrSet=attrs;
        TypedArray attributeArray = context.obtainStyledAttributes(attrs, R.styleable.GenieTextView);
        String fontName = attributeArray.getString(R.styleable.GenieTextView_font);
        int textStyle = attrs.getAttributeIntValue(ANDROID_SCHEMA, "textStyle", Typeface.NORMAL);

        Typeface customFont = selectTypeface(context, fontName, textStyle);
        setTypeface(customFont);
        attributeArray.recycle();
    }

    @Override
    public void setTypeface(Typeface tf) {
        super.setTypeface(tf);
    }

    public Typeface selectTypeface(Context context, String fontName, int textStyle) {

        if(fontName.contentEquals(context.getString(R.string.fontname_lato))) {
            return FontCache.getTypeface("Lato.ttf", context);
        }else if(fontName.contentEquals(context.getString(R.string.fontname_lato_bold))) {
            return FontCache.getTypeface("Lato Bold.ttf", context);
        }else if(fontName.contentEquals(context.getString(R.string.fontname_lato_bold_italic))) {
            return FontCache.getTypeface("Lato Bold Italic.ttf", context);
        }else if(fontName.contentEquals(context.getString(R.string.fontname_lato_light))) {
            return FontCache.getTypeface("Lato Light.ttf", context);
        }else if(fontName.contentEquals(context.getString(R.string.fontname_lato_light_italic))) {
            return FontCache.getTypeface("Lato Light Italic.ttf", context);
        }else if(fontName.contentEquals(context.getString(R.string.fontname_lato_italic))) {
            return FontCache.getTypeface("Lato Italic.ttf", context);
        }
        return null;
    }
}
