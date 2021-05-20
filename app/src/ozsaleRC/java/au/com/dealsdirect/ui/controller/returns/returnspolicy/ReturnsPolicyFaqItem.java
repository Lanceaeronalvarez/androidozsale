package au.com.dealsdirect.ui.controller.returns.returnspolicy;

import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;

public class ReturnsPolicyFaqItem {
    private CharSequence title;
    private SpannableString content;

    public CharSequence getTitle() {
        return title;
    }

    public void setTitle(CharSequence title) {
        this.title = title;
    }

    public CharSequence getContent() {
        return content;
    }

    public void setContent(CharSequence content) {
        if (content instanceof SpannableString) {
            this.content = (SpannableString) content;
        } else {
            this.content = new SpannableString(content);
        }

        int startSpan = 0, endSpan = 0;
        final String target = "My Returns";

        while (true) {
            startSpan = content.toString().indexOf(target, endSpan);
            StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);
            if (startSpan < 0)
                break;
            endSpan = startSpan + target.length();
            this.content.setSpan(styleSpan, startSpan, endSpan,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }
}