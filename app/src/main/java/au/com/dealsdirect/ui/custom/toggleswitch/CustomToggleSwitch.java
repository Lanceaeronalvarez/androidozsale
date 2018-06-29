package au.com.dealsdirect.ui.custom.toggleswitch;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by Admin on 5/23/18.
 */

public class CustomToggleSwitch extends BaseToggleSwitch {
    private int mCheckedTogglePosition = -1;

    public CustomToggleSwitch(Context context) {
        this(context, null);
    }

    public CustomToggleSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public int getCheckedTogglePosition() {
        return mCheckedTogglePosition;
    }

    @Override
    protected void onClickOnToggleSwitch(int position) {
        setCheckedTogglePosition(position);
    }

    @Override
    public void setOnToggleSwitchChangeListener(OnToggleSwitchChangeListener onToggleSwitchChangeListener) {
        super.setOnToggleSwitchChangeListener(onToggleSwitchChangeListener);
    }

    public void setCheckedTogglePosition(int position) {
        setCheckedTogglePosition(position, true);
    }

    public void setCheckedTogglePosition(int position, boolean notifyListener) {
        disableAll();
        activate(position);
        setSeparatorVisibility(position);

        mCheckedTogglePosition = position;
        if (notifyListener)
            notifyOnToggleChange(position);
    }

    private void setSeparatorVisibility(int activeIndex) {
        for (int i = 0; i < getToggleSwitchesContainer().getChildCount() - 1; i++) {
            CustomToggleSwitchButton toggleSwitchButton = new CustomToggleSwitchButton(getToggleSwitchesContainer().getChildAt(i));
            if (i == activeIndex || i == (activeIndex - 1))
                toggleSwitchButton.hideSeparator();
            else
                toggleSwitchButton.showSeparator();
        }
    }

    @Override
    protected void buildToggleButtons() {
        super.buildToggleButtons();
    }

    @Override
    protected boolean isActive(int position) {
        return mCheckedTogglePosition == position;
    }
}
