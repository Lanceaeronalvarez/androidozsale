package au.com.dealsdirect.ui.controller.returns.returnspolicy;

import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;

public class ReturnsPolicyStepItem {
    private int drawableId;
    private SpannableString description;

    public int getDrawableId() {
        return drawableId;
    }

    public void setDrawableId(int drawableId) {
        this.drawableId = drawableId;
    }

    public CharSequence getDescription() {
        return description;
    }

    public void setDescription(CharSequence description) {
        if (description instanceof SpannableString) {
            this.description = (SpannableString) description;
        } else {
            this.description = new SpannableString(description);
        }

        int startSpan = 0, endSpan = 0;
        final String target = "My Returns";

        while (true) {
            startSpan = description.toString().indexOf(target, endSpan);
            StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);
            if (startSpan < 0)
                break;
            endSpan = startSpan + target.length();
            this.description.setSpan(styleSpan, startSpan, endSpan,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }
}
