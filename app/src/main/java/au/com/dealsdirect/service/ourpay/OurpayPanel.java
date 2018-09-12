package au.com.dealsdirect.service.ourpay;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler;

import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.GetCurrentOrderOurpay;
import au.com.dealsdirect.ui.base.BaseActivity;
import au.com.dealsdirect.ui.custom.toggleswitch.CustomToggleSwitch;
import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.PriceUtils;
import au.com.dealsdirect.utils.module.GateKeeper;

/**
 * dp  Created on 8/4/17.
 */

public class OurpayPanel {

    Activity mBaseActivity;
    private LinearLayout mPanelHolder;
    LinearLayout mHolderInBorder;
    Router mRouter;
    OurpayGraph mOurpayGraph = new OurpayGraph();

    public View getCartAmountHeader() {
        return mCartAmountHeader;
    }

    View mCartAmountHeader;


    public OurpayPanel(BaseActivity activity) {
        this.mBaseActivity = activity;
        View view = activity.getLayoutInflater().inflate(R.layout.ourpay_panel_holder, null, false);
        mPanelHolder = (LinearLayout) view.findViewById(R.id.linearLayout_placeholder);
        mHolderInBorder = (LinearLayout) view.findViewById(R.id.linearlayout_placeholder_bordered);
    }

    public OurpayPanel(BaseActivity activity, Router router) {
        this(activity);
        this.mRouter = router;
    }

    public View generatePanel(Ourpay ourpay) {
        return generatePanel(ourpay, null);
    }

    public View generatePanel(Ourpay ourpay, OurpayCallback callback) {
        if (0 != (ourpay.getState() & OurpayState.PRECART)) {

            if (0 != (ourpay.getState() & OurpayState.ERROR)) {
                mHolderInBorder.addView(getTemplateText(ourpay.getDetails()));
                mHolderInBorder.setBackground(mBaseActivity.getDrawable(R.drawable.ourpay_layout_border));

            } else {

                View header = getPanelHeader(ourpay);
                mHolderInBorder.addView(header);

                LinearLayout headerHolder = (LinearLayout) header.findViewById(R.id.linearLayout_header);

                String templateTexts = ourpay.getDescription();
                final View templateView = getTemplateText(templateTexts);
                headerHolder.addView(templateView, headerHolder.getChildCount() - 1);

                if (!templateTexts.equals("")) templateView.setVisibility(View.GONE);
                templateView.setTag(templateTexts);

                final View panelRows = getPanelRows(ourpay.getPlannedTransactions());
                mHolderInBorder.addView(panelRows);

                templateView.setVisibility(View.GONE);
                panelRows.setVisibility(View.GONE);

                header.setOnClickListener(view -> {
                    panelRows.setVisibility(panelRows.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                    if (!templateView.getTag().equals("")) {
                        templateView.setVisibility(templateView.getVisibility() == View.GONE ?
                                View.VISIBLE : View.GONE);
                    }
                    if (callback != null)
                        callback.onHeaderClick(panelRows.getVisibility() == View.VISIBLE);
                });

            }
        } else if (0 != (ourpay.getState() & OurpayState.ONCART)) {

            if (0 != (ourpay.getState() & OurpayState.ERROR)) {

                mHolderInBorder.addView(getTemplateText(
                        OurpayTemplateText.getTemplateText(mBaseActivity, ourpay)));
                mHolderInBorder.setBackground(mBaseActivity.getDrawable(R.drawable.ourpay_layout_border));
                mPanelHolder.addView(getCartAmountHeader(""), 0);
            } else {

                mPanelHolder.addView(getButton(), 0);

                View header = getPanelHeader(ourpay);
                mHolderInBorder.addView(header);

                mPanelHolder.addView(getTemplateText(ourpay.getDescription()), 1);

                if (ourpay.getTermsAndConditionsCheckboxState() != 0) {
                    mPanelHolder.addView(getTermsAndConditions(ourpay), 2);
                }
                View panelRows = getPanelRows(ourpay.getPlannedTransactions());
                View panelTotalRow = getPanelTotalRow(PriceUtils.getPriceStringValue(ourpay.getTotalAmount()));
                mPanelHolder.addView(getCartAmountHeader(PriceUtils.getPriceStringValue(ourpay.getInitialAmount())), 0);
                mHolderInBorder.addView(panelRows);
                mHolderInBorder.addView(panelTotalRow);

                header.setOnClickListener(view -> {
                    panelRows.setVisibility(panelRows.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                    panelTotalRow.setVisibility(panelTotalRow.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                    if (callback != null)
                        callback.onHeaderClick(panelRows.getVisibility() == View.VISIBLE);
                });
            }
        } else if (0 != (ourpay.getState() & OurpayState.POSTCART)) {

            String templateText = OurpayTemplateText.getTemplateText(mBaseActivity, ourpay);
            if (!templateText.isEmpty()) {
                TextView tv = (TextView) getTemplateText(templateText);
                tv.setGravity(Gravity.CENTER_HORIZONTAL);
                mPanelHolder.addView(tv, 0);
            }
            mHolderInBorder.addView(getSuccessHeaderRow());
            mHolderInBorder.addView(getPanelRows(ourpay.getPlannedTransactions()));
            mHolderInBorder.addView(getPanelRemainingRow(PriceUtils.getPriceStringValue(ourpay.getInitialAmount())));

            if (!mBaseActivity.getResources().getBoolean(R.bool.is_ozsale_app)) {
                mPanelHolder.addView(getThankYouFooter());
            }

        }

        return mPanelHolder;
    }

    private View getTemplateText(String message) {

        View view = mBaseActivity.getLayoutInflater().inflate(R.layout.ourpay_tempate_layout, null, false);
        TextView textView = (TextView) view.findViewById(R.id.textView_template);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textView.setText(Html.fromHtml(message, Html.FROM_HTML_MODE_COMPACT));
        } else {
            textView.setText(Html.fromHtml(message));
        }

        return view;
    }

    private View getPanelHeader(Ourpay ourpay) {

        View header = mBaseActivity.getLayoutInflater().inflate(R.layout.ourpay_panel_header, null, false);

        TextView textViewFirstPrice = (TextView) header.findViewById(R.id.ourpay_panel_header_first_price);
        TextView textViewFirstMultiplier = (TextView) header.findViewById(R.id.ourpay_panel_header_first_text);
        TextView textViewPlannedPrice = (TextView) header.findViewById(R.id.ourpay_panel_header_planned_price);
        TextView textViewPlannedMultiplier = (TextView) header.findViewById(R.id.ourpay_panel_header_planned_text);

        textViewFirstPrice.setText(PriceUtils.getRpStringValue(ourpay.getFirstTransactionAmount()));
        textViewFirstMultiplier.setText(ourpay.getFirstTransactionText());
        textViewPlannedPrice.setText(PriceUtils.getRpStringValue(ourpay.getPlannedTransactionAmount()));
        textViewPlannedMultiplier.setText(ourpay.getPlannedTransactionText());
        textViewPlannedMultiplier.setVisibility(ourpay.getPlannedTransactionText() == null ? View.GONE : View.VISIBLE);
        textViewPlannedPrice.setVisibility(ourpay.getPlannedTransactionAmount() == 0 ? View.GONE : View.VISIBLE);

        return header;
    }

    private View getPanelRows(List<GetCurrentOrderOurpay.PlannedTransaction> transactions) {
        return mOurpayGraph.generateGraph(mBaseActivity, transactions);
    }

    private View getCartAmountHeader(String amount) {
        mCartAmountHeader = mBaseActivity.getLayoutInflater().inflate(R.layout.ourpay_cart_amount_layout, null, false);

        TextView textViewAmount = (TextView) mCartAmountHeader.findViewById(R.id.textView_amount);
        if (amount.length() > 0) {
            textViewAmount.setText(amount);
        } else {
            textViewAmount.setVisibility(View.GONE);
        }

        return mCartAmountHeader;
    }

    private View getButton() {
        View view = mBaseActivity.getLayoutInflater().inflate(R.layout.ourpay_button, null, false);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) mBaseActivity.getResources().getDimension(R.dimen.button_height_regular));
        view.setLayoutParams(layoutParams);

        return view;
    }

    private View getTermsAndConditions(Ourpay ourpay) {
        View view = mBaseActivity.getLayoutInflater().inflate(R.layout.ourpay_terms_and_conditions, null, false);

        CustomToggleSwitch toggleSwitch = (CustomToggleSwitch) view.findViewById(R.id.ourpay_toggle_switch_tc);
        TextView textViewTC = (TextView) view.findViewById(R.id.ourpay_text_tc);

        textViewTC.setText(Html.fromHtml(OurpayTemplateText.getText(mBaseActivity, ourpay.getTermsAndConditionsText())));
        textViewTC.setOnClickListener(view1 -> {
            ((MainActivity) mBaseActivity).setDraggableViewPager(false);
            GateKeeper.push(mRouter, GateKeeper.Destination.LEGALITIES,
                    new BundleBuilder(new Bundle())
                            .putString(BundleKeys.TEMPLATE_KEY, OurpayTemplateText.KEY_OPS_TNC_FULL_TEXT)
                            .putString(BundleKeys.LEGALITIES_TITLE, mBaseActivity.getString(R.string.my_basket))
                            .build(),
                    new VerticalChangeHandler(false),
                    new VerticalChangeHandler());

        });

        // TODO: Check ourpay.getTermsAndConditionsCheckboxState() for default state for toggleSwitch
        // This behavior is TBD so for now we will copy legacy which has no default state

        return view;
    }

    private View getPanelTotalRow(String amount) {

        View footer = mBaseActivity.getLayoutInflater().inflate(R.layout.ourpay_panel_row_footer, null, false);
        TextView textViewAmount = (TextView) footer.findViewById(R.id.textView_amount_total);
        TextView textTotalAmountLabel = (TextView) footer.findViewById(R.id.textView_amount_label);
        textTotalAmountLabel.setTextColor(Color.parseColor("#FF6F7070"));
        textViewAmount.setText(amount);

        return footer;
    }

    private View getPanelRemainingRow(String amount) {

        View footer = mBaseActivity.getLayoutInflater().inflate(R.layout.ourpay_panel_row_footer, null, false);
        footer.setBackground(mBaseActivity.getDrawable(R.drawable.ourpay_layout_remaining_balance_footer));
        TextView textViewAmount = (TextView) footer.findViewById(R.id.textView_amount_total);
        TextView textViewLabel = (TextView) footer.findViewById(R.id.textView_amount_label);
        textViewAmount.setText(amount);
        textViewAmount.setTextColor(Color.parseColor("#FFE47C0B"));
        textViewLabel.setText("Remaining Balance");
        return footer;
    }

    private View getSuccessHeaderRow() {

        View header = mBaseActivity.getLayoutInflater().inflate(R.layout.ourpay_post_cart_panel_header, null, false);
        return header;
    }

    private View getThankYouFooter() {
        View view = mBaseActivity.getLayoutInflater().inflate(R.layout.ourpay_thankyou, null, false);

        return view;
    }

    public void clearOurpayGraphBitmapsAndListeners() {
//        mOurpayGraph.clearOurpayGraphBitmapsAndListeners();
    }

    public void setIsGraphVisible(boolean isVisible) {
//        mOurpayGraph.setIsGraphVisible(isVisible);
    }
}
