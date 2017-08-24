package au.com.dealsdirect.service.ourpay;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;

import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.MyPayDetails;
import au.com.dealsdirect.ui.base.BaseActivity;
import au.com.dealsdirect.ui.controller.legalities.LegalitiesController;
import au.com.dealsdirect.utils.PriceUtils;

/**
 *dp  Created on 8/4/17.
 */

public class OurpayPanel {

    Activity mBaseActivity;
    private LinearLayout mPanelHolder;
    LinearLayout mHolderInBorder;
    Router mRouter;

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

    public View generatePanel(Ourpay ourpay){
        if (0 != (ourpay.getState() & OurpayState.PRECART)){

            if (0 != (ourpay.getState() & OurpayState.ERROR)){
                mHolderInBorder.addView(getTemplateText(ourpay.getDetails()));
            }else{


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
                });

            }
        }else if (0 != (ourpay.getState() & OurpayState.ONCART)) {

            if (0 != (ourpay.getState() & OurpayState.ERROR)) {

                mHolderInBorder.addView(getTemplateText(
                        OurpayTemplateText.getTemplateText(mBaseActivity, ourpay)));
                mPanelHolder.addView(getCartAmountHeader(""), 0);
            } else {

                mPanelHolder.addView(getButton(), 0);

                View header = getPanelHeader(ourpay);
                mHolderInBorder.addView(header);

                if (ourpay.getTermsAndConditionsCheckboxState() != 0) {
                    mPanelHolder.addView(getTermsAndConditions(ourpay), 1);
                }
                mPanelHolder.addView(getTemplateText(
                        OurpayTemplateText.getTemplateText(mBaseActivity, ourpay)), 0);

                View panelRows = getPanelRows(ourpay.getPlannedTransactions());
                View panelTotalRow = getPanelTotalRow(PriceUtils.getPriceStringValue(ourpay.getUserAmount()));
                mPanelHolder.addView(getCartAmountHeader(PriceUtils.getPriceStringValue(ourpay.getAmount())), 0);
                mHolderInBorder.addView(panelRows);
                mHolderInBorder.addView(panelTotalRow);

                header.setOnClickListener(view -> {

                    panelRows.setVisibility(panelRows.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                    panelTotalRow.setVisibility(panelTotalRow.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
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
        }else{
            textView.setText(Html.fromHtml(message));
        }

        return view;
    }

    private View getPanelHeader(Ourpay ourpay) {

        View header = mBaseActivity.getLayoutInflater().inflate(R.layout.ourpay_panel_header, null, false);

        TextView textViewPrice = (TextView) header.findViewById(R.id.ourpay_panel_header_price);
        TextView textViewCount = (TextView) header.findViewById(R.id.ourpay_panel_header_transaction);

        textViewPrice.setText(PriceUtils.getProductRpStringValue(ourpay.getAmount()));
        textViewCount.setText(Integer.toString(ourpay.getTransactionCount()));

        return header;
    }

    private View getPanelRows(List<MyPayDetails.PlannedTransaction> transactions) {

        OurpayGraph ourpayGraph = new OurpayGraph();
        return ourpayGraph.generateGraph(mBaseActivity, transactions);
    }

    private View getCartAmountHeader(String amount) {
        View view = mBaseActivity.getLayoutInflater().inflate(R.layout.ourpay_cart_amount_layout, null, false);

        TextView textViewAmount = (TextView) view.findViewById(R.id.textView_amount);
        if (amount.length() > 0) {

            textViewAmount.setText(amount);
        } else {
            textViewAmount.setVisibility(View.GONE);
        }

        return view;
    }

    private View getButton() {
        View view = mBaseActivity.getLayoutInflater().inflate(R.layout.ourpay_button, null, false);

        return view;
    }

    private View getTermsAndConditions(Ourpay ourpay) {
        View view = mBaseActivity.getLayoutInflater().inflate(R.layout.ourpay_terms_and_conditions, null, false);

        CheckBox cb = (CheckBox) view.findViewById(R.id.ourpay_checkbox_tc);
        TextView textViewTC = (TextView) view.findViewById(R.id.ourpay_text_tc);

        textViewTC.setText(Html.fromHtml(OurpayTemplateText.getText(mBaseActivity, ourpay.getTermsAndConditionsText())));
        textViewTC.setOnClickListener(view1 -> {

            String ourpayTermsAndConditionKey = "OurPayTermsAndConditions_Text";
            Bundle bundle = new Bundle();
            bundle.putString("templateKey", ourpayTermsAndConditionKey);

            mRouter.pushController(RouterTransaction.with(new LegalitiesController(ourpayTermsAndConditionKey,"My Basket"))
                    .pushChangeHandler(new HorizontalChangeHandler(false))
                    .popChangeHandler(new HorizontalChangeHandler(false)));

//                HTMLViewFragment fragment = new HTMLViewFragment();
//                fragment.setArguments(bundle);
//
//                baseActivity.switchContent(fragment, R.id.contentBody);

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

    private View getSuccessHeaderRow(){

        View header = mBaseActivity.getLayoutInflater().inflate(R.layout.ourpay_post_cart_panel_header, null, false);
        return header;
    }

    private View getThankYouFooter() {
        View view = mBaseActivity.getLayoutInflater().inflate(R.layout.ourpay_thankyou, null, false);

        return view;
    }

}
