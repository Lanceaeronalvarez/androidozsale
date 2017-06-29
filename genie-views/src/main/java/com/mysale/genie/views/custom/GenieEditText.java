package com.mysale.genie.views.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.widget.EditText;

import com.mysale.genie.views.R;

/**
 * Created by smartwave on 10/01/2017.
 */

public class GenieEditText extends EditText {

    public static final String ANDROID_SCHEMA = "http://schemas.android.com/apk/res/android";
    public AttributeSet attrSet;

    private BackPressedListener mOnImeBack;
    private DeleteListener mDeleteListener;

    public GenieEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyFont(context,attrs);
    }

    public GenieEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyFont(context,attrs);
    }

    private void applyFont(Context context, AttributeSet attrs) {
        attrSet=attrs;
        TypedArray attributeArray = context.obtainStyledAttributes(attrs, R.styleable.GenieEditText);
        String fontName = attributeArray.getString(R.styleable.GenieEditText_font);
        int textStyle = attrs.getAttributeIntValue(ANDROID_SCHEMA, "textStyle", Typeface.NORMAL);

        Typeface customFont = selectTypeface(context, fontName, textStyle);
        setTypeface(customFont);
        attributeArray.recycle();
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return new GenieEditTextInputConnection(super.onCreateInputConnection(outAttrs),
                true,mDeleteListener);
    }

    private class GenieEditTextInputConnection extends InputConnectionWrapper {

        DeleteListener mListener;
        public GenieEditTextInputConnection(InputConnection target, boolean mutable, DeleteListener listener) {
            super(target, mutable);
            mListener = listener;
        }

        @Override
        public boolean sendKeyEvent(KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {

                if(GenieEditText.this.getText().toString().isEmpty() && mListener!=null){
                    mListener.onDeletePressed();
                }
            }
            return super.sendKeyEvent(event);
        }

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

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            if (mOnImeBack != null) mOnImeBack.onImeBack(this);
        }
        return super.dispatchKeyEvent(event);
    }

    public void setBackPressedListener(BackPressedListener listener) {
        mOnImeBack = listener;
    }

    public void setDeleteListener(DeleteListener listener) { mDeleteListener = listener; }
    public interface BackPressedListener {
        void onImeBack(GenieEditText editText);
    }

    public interface DeleteListener {
        void onDeletePressed();
    }

}
