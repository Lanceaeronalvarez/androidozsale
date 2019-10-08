package au.com.dealsdirect.ui.custom.toggleswitch;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import androidx.core.content.ContextCompat;
import androidx.core.widget.CompoundButtonCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.braintreepayments.cardform.utils.ViewUtils;

import java.util.ArrayList;

import au.com.dealsdirect.R;

/**
 * dp Created by Admin on 5/23/18.
 */

public class ToggleCheckbox extends LinearLayout {

    public interface OnToggleSwitchChangeListener {
        void onToggleSwitchChangeListener(int position, boolean isChecked);
    }

    protected static class Default {

        protected static final int ACTIVE_BG_COLOR = R.color.toggle_switch_positive;
        protected static final int ACTIVE_TEXT_COLOR = android.R.color.white;
        protected static final int INACTIVE_BG_COLOR = R.color.toggle_switch_gray_light;
        protected static final int INACTIVE_TEXT_COLOR = R.color.toggle_switch_gray;
        protected static final int SEPARATOR_COLOR = R.color.toggle_switch_gray_light;
        protected static final int NEGATIVE_ACTIVE_BG_COLOR = R.color.toggle_switch_negative;

        protected static final int CORNER_RADIUS_DP = 4;
        protected static final float TEXT_SIZE = 12;
        protected static final float TOGGLE_WIDTH = 44;
    }

    private OnToggleSwitchChangeListener mOnToggleSwitchChangeListener = null;

    private int activeBgColor;
    private int activeTextColor;
    private int inactiveBgColor;
    private int inactiveTextColor;
    private int separatorColor;
    private int activeNegativeBgColor;

    private int textSize;
    private float cornerRadius;
    private float toggleWidth;

    private LayoutInflater mInflater;
    private CheckBox checkboxStart;
    private CheckBox checkboxEnd;
    private ArrayList<String> mLabels;
    private Context mContext;

    public ToggleCheckbox(Context context) {
        this(context, null);
    }

    public ToggleCheckbox(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (attrs != null) {
            TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.ToggleSwitchOptions, 0, 0);

            try {
                mContext = context;

                mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                mInflater.inflate(R.layout.widget_toggle_checkbox, this, true);

                checkboxStart = (CheckBox) findViewById(R.id.checkbox_toggle_start);
                checkboxEnd = (CheckBox) findViewById(R.id.checkbox_toggle_end);

                String leftToggleText = attributes.getString(R.styleable.ToggleSwitchOptions_textToggleLeft);
                String rightToggleText = attributes.getString(R.styleable.ToggleSwitchOptions_textToggleRight);

                checkboxStart.setText(leftToggleText);
                checkboxEnd.setText(rightToggleText);

                this.activeBgColor = attributes.getColor(R.styleable.ToggleSwitchOptions_activeBgColor, ContextCompat.getColor(context, ToggleCheckbox.Default.ACTIVE_BG_COLOR));
                this.activeTextColor = attributes.getColor(R.styleable.ToggleSwitchOptions_activeTextColor, ContextCompat.getColor(context, ToggleCheckbox.Default.ACTIVE_TEXT_COLOR));
                this.inactiveBgColor = attributes.getColor(R.styleable.ToggleSwitchOptions_inactiveBgColor, ContextCompat.getColor(context, ToggleCheckbox.Default.INACTIVE_BG_COLOR));
                this.inactiveTextColor = attributes.getColor(R.styleable.ToggleSwitchOptions_inactiveTextColor, ContextCompat.getColor(context, ToggleCheckbox.Default.INACTIVE_TEXT_COLOR));
                this.activeNegativeBgColor = attributes.getColor(R.styleable.ToggleSwitchOptions_activeNegativeBgColor, ContextCompat.getColor(context, Default.NEGATIVE_ACTIVE_BG_COLOR));

                checkboxStart.setTextColor(activeTextColor);
                checkboxEnd.setTextColor(activeTextColor);

                int states[][] = {{}};
                int colors[] = {activeTextColor};
                CompoundButtonCompat.setButtonTintList(checkboxStart, new ColorStateList(states, colors));
                CompoundButtonCompat.setButtonTintList(checkboxEnd, new ColorStateList(states, colors));

                this.separatorColor = attributes.getColor(R.styleable.ToggleSwitchOptions_separatorColor, ContextCompat.getColor(context, ToggleCheckbox.Default.SEPARATOR_COLOR));
                this.textSize = attributes.getDimensionPixelSize(R.styleable.ToggleSwitchOptions_android_textSize, (int) ViewUtils.dp2px(context, ToggleCheckbox.Default.TEXT_SIZE));
                this.toggleWidth = attributes.getDimension(R.styleable.ToggleSwitchOptions_toggleWidth, ViewUtils.dp2px(getContext(), ToggleCheckbox.Default.TOGGLE_WIDTH));
                this.cornerRadius = attributes.getDimensionPixelSize(R.styleable.ToggleSwitchOptions_cornerRadius, (int) ViewUtils.dp2px(context, ToggleCheckbox.Default.CORNER_RADIUS_DP));

                checkboxStart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        checkboxEnd.setChecked(!isChecked);
                    }
                });

                checkboxEnd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        checkboxStart.setChecked(!isChecked);
                    }
                });

            } finally {
                attributes.recycle();
            }
        }
    }

    public boolean isStartCheckboxSelected() {
        return checkboxStart.isSelected();
    }

    public boolean isEndCheckboxSelected() {
        return checkboxEnd.isSelected();
    }
}
