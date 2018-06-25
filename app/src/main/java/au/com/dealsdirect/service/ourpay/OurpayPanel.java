package au.com.dealsdirect.service.ourpay;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler;

import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.MyPayDetails;
import au.com.dealsdirect.ui.base.BaseActivity;
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
        mHolderInBorder = (LinearLayout) view.findViewById(R.id.linearlayout_placeholder_boredered);
    }

    public OurpayPanel(BaseActivity activity, Router router) {
        this.mBaseActivity = activity;
        this.mRouter = router;

        View view = activity.getLayoutInflater().inflate(R.layout.ourpay_panel_holder, null, false);
        mPanelHolder = (LinearLayout) view.findViewById(R.id.linearLayout_placeholder);
        mHolderInBorder = (LinearLayout) view.findViewById(R.id.linearlayout_placeholder_boredered);
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
                final View panelRows = getPanelRows(ourpay.getPlannedTransactions());
                mHolderInBorder.addView(header);

                LinearLayout headerHolder = (LinearLayout) header.findViewById(R.id.linearLayout_header);
                final View templateView = getTemplateText(OurpayTemplateText
                        .getTemplateText(mBaseActivity, ourpay));

                headerHolder.addView(templateView, headerHolder.getChildCount() - 1);
                mHolderInBorder.addView(panelRows);

                templateView.setVisibility(View.GONE);
                panelRows.setVisibility(View.GONE);

                header.setOnClickListener(view -> {
                    panelRows.setVisibility(panelRows.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                    templateView.setVisibility(templateView.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
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

                mPanelHolder.addView(getTemplateText(
                        OurpayTemplateText.getTemplateText(mBaseActivity, ourpay)), 1);

                if (ourpay.getTermsAndConditionsCheckboxState() != 0) {
                    mPanelHolder.addView(getTermsAndConditions(ourpay), 2);
                }
                View panelRows = getPanelRows(ourpay.getPlannedTransactions());
                View panelTotalRow = getPanelTotalRow(PriceUtils.getPriceStringValue(ourpay.getUserAmount()));
                mPanelHolder.addView(getCartAmountHeader(PriceUtils.getPriceStringValue(ourpay.getAmount())), 0);
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

            TextView tv = (TextView) getTemplateText(
                    OurpayTemplateText.getTemplateText(mBaseActivity, ourpay));
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
            mPanelHolder.addView(tv, 0);
            mHolderInBorder.addView(getSuccessHeaderRow());
            mHolderInBorder.addView(getPanelRows(ourpay.getPlannedTransactions()));
            mHolderInBorder.addView(getPanelRemainingRow(PriceUtils.getPriceStringValue(ourpay.getAmount())));
            mPanelHolder.addView(getThankYouFooter());
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

        TextView textViewPrice = (TextView) header.findViewById(R.id.ourpay_panel_header_price);
        TextView textViewCount = (TextView) header.findViewById(R.id.ourpay_panel_header_transaction);

        textViewPrice.setText(PriceUtils.getRpStringValue(ourpay.getAmount()));
        textViewCount.setText(Integer.toString(ourpay.getTransactionCount()));

        return header;
    }

    private View getPanelRows(List<MyPayDetails.PlannedTransaction> transactions) {

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

        CheckBox cb = (CheckBox) view.findViewById(R.id.ourpay_checkbox_tc);
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


        if (ourpay.getTermsAndConditionsCheckboxState() == 1) {
            cb.setChecked(false);
        } else if (ourpay.getTermsAndConditionsCheckboxState() == 2) {
            cb.setChecked(true);
        }

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
        mOurpayGraph.clearOurpayGraphBitmapsAndListeners();
    }

    public void setIsGraphVisible(boolean isVisible) {
        mOurpayGraph.setIsGraphVisible(isVisible);
    }
}
