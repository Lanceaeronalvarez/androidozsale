package au.com.dealsdirect.service.afterpay;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import au.com.dealsdirect.R;

public class AfterpayPanelViewHolder {

    private View mView;
    private TextView mDescription;
    private ImageButton mInfoButton;

    public AfterpayPanelViewHolder(Context context) {
        mView = LayoutInflater.from(context).inflate(R.layout.afterpay_panel_layout, null, false);
        mDescription = mView.findViewById(R.id.afterpay_panel_description);
        mInfoButton = mView.findViewById(R.id.afterpay_panel_info_button);
    }

    public View getView() {
        return mView;
    }

    public CharSequence getDescription() {
        return mDescription.getText();
    }

    public void setDescription(CharSequence description) {
        mDescription.setText(description);
        mDescription.setTypeface(Typeface
                .createFromAsset(getContext().getAssets(),
                        getContext().getResources().getString(R.string.font_raleway_regular)));
    }

    public void setInfoButtonOnClickListener(View.OnClickListener listener) {
        mInfoButton.setOnClickListener(listener);
    }

    public Context getContext() {
        return mView.getContext();
    }
}
