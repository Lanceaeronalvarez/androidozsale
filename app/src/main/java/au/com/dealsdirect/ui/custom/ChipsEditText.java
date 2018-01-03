package au.com.dealsdirect.ui.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.widget.EditText;

import com.mysale.genie.views.custom.GenieEditText;

/**
 * Created by smartwave on 21/07/2017.
 */

public class ChipsEditText extends EditText {

    private DeleteListener mDeleteListener;
    private BackPressedListener mOnImeBack;

    public ChipsEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChipsEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return new ChipsEditTextInputConnection(super.onCreateInputConnection(outAttrs),
                true,mDeleteListener);
    }

    private class ChipsEditTextInputConnection extends InputConnectionWrapper {

        DeleteListener mListener;
        public ChipsEditTextInputConnection(InputConnection target, boolean mutable, DeleteListener listener) {
            super(target, mutable);
            mListener = listener;
        }

        @Override
        public boolean sendKeyEvent(KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {

                if(ChipsEditText.this.getText().toString().isEmpty() && mListener!=null){
                    mListener.onDeletePressed();
                }
            }
            return super.sendKeyEvent(event);
        }

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

    public interface DeleteListener {
        void onDeletePressed();
    }

    public interface BackPressedListener {
        void onImeBack(ChipsEditText editText);
    }
}
