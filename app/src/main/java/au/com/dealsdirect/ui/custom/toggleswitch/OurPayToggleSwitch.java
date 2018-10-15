package au.com.dealsdirect.ui.custom.toggleswitch;

import android.content.Context;
import android.util.AttributeSet;

import au.com.dealsdirect.service.ourpay.OurpayPanel;

public class OurPayToggleSwitch extends CustomToggleSwitch {

    public OurPayToggleSwitch(Context context) {
        super(context);
    }

    public OurPayToggleSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOurPayToggleSwitch(OurpayPanel.TermsAndConditionStates termsAndConditionStates) {
        switch (termsAndConditionStates) {
            case DISABLED:
                setVisibility(GONE);
                break;
            case CHECKED:
                setCheckedTogglePosition(0);
                break;
            case UNCHECKED:
                setCheckedTogglePosition(1);
                break;
        }
    }
}
