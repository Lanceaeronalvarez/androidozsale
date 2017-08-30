package au.com.dealsdirect.ui.custom;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.KeyEvent;

/**
 * Created by Ayi on 23/06/2017.
 */

public class ClearFocusEditText extends AppCompatEditText {
    public ClearFocusEditText(Context context) {
        super(context);
    }

    public ClearFocusEditText(Context context, AttributeSet attribute_set) {
        super(context, attribute_set);
    }

    public ClearFocusEditText(Context context, AttributeSet attribute_set, int def_style_attribute) {
        super(context, attribute_set, def_style_attribute);
    }

    @Override
    public boolean onKeyPreIme(int key_code, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP)
            this.clearFocus();

        return super.onKeyPreIme(key_code, event);
    }
}
