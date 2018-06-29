package au.com.dealsdirect.ui.custom.toggleswitch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import au.com.dealsdirect.R;

/**
 * dp Created by Admin on 5/23/18.
 */

public class CustomToggleSwitchButton {

    private View view;
    private TextView textView;
    private View separator;

    public CustomToggleSwitchButton(Context context) {
        this(LayoutInflater.from(context).inflate(R.layout.widget_toggle_switch_item, null));
    }

    public CustomToggleSwitchButton(View view) {
        this.view = view;
        this.textView = (TextView) view.findViewById(R.id.text_view);
        this.separator = view.findViewById(R.id.separator);
    }

    public View getView() {
        return view;
    }

    public TextView getTextView() {
        return textView;
    }

    public View getSeparator() {
        return separator;
    }

    public void showSeparator(){
        getSeparator().setVisibility(View.VISIBLE);
    }

    public void hideSeparator(){
        getSeparator().setVisibility(View.INVISIBLE);
    }

}
