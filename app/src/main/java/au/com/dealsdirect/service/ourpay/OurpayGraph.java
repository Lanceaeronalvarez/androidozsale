package au.com.dealsdirect.service.ourpay;

/*
 * Created by CodeineBot on 9/28/16.
 */

import android.content.Context;

import androidx.core.widget.TextViewCompat;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.GetCurrentOrderOurpay;
import au.com.dealsdirect.ui.controller.main.Settings;

@SuppressWarnings({"ResourceType"})
public class OurpayGraph {

    /**
     * @param context            -  Get context to identify
     * @param ourpayTransactions - Get Arraylist for processing information
     * @return - return view as ui object
     */

    public View generateGraph(final Context context, final List<GetCurrentOrderOurpay.PlannedTransaction> ourpayTransactions, final boolean hasCheckMark) {
        final LinearLayout rootViewLayout;

        rootViewLayout = new LinearLayout(context);
        rootViewLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams LLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        rootViewLayout.setLayoutParams(LLParams);

        generateRows(ourpayTransactions, context, rootViewLayout, hasCheckMark);

        return rootViewLayout;
    }

    private void generateRows(List<GetCurrentOrderOurpay.PlannedTransaction> ourpayTransactions, Context context, ViewGroup viewGroup, boolean hasCheckMark) {
        LayoutInflater inflater = LayoutInflater.from(context);
        int transactionsCount = ourpayTransactions.size();
        for (int i = 0; i < ourpayTransactions.size(); i++) {
            View ourpayPanelRow = inflater.inflate(R.layout.ourpay_panel_row, viewGroup, false);
            ViewGroup circlesContainer = (ViewGroup) ourpayPanelRow.findViewById(R.id.circlesContainer);
            circlesContainer.setTag(i);
            TextView tempDate = (TextView) ourpayPanelRow.findViewById(R.id.dateTextView);
            TextView tempPay = (TextView) ourpayPanelRow.findViewById(R.id.dollarValue);
            ImageView checkImage = (ImageView) ourpayPanelRow.findViewById(R.id.checkImage);

            tempDate.setText(OurpayUtils.convertDateToTrimmedString(ourpayTransactions.get(i).getPlannedDate()));

            if (hasCheckMark) {
                if (ourpayTransactions.get(i).getState() == 2) {
                    tempPay.setText(R.string.paid);
                    checkImage.setVisibility(View.VISIBLE);
                } else {
                    String formattedPriceString = Settings.getSelectedCountry().currencySign + String.format(Locale.ENGLISH, "%.2f", ourpayTransactions.get(i).getAmount());
                    tempPay.setText(formattedPriceString);
                    checkImage.setVisibility(View.INVISIBLE);
                }
            } else {
                String formattedPriceString = Settings.getSelectedCountry().currencySign + String.format(Locale.ENGLISH, "%.2f", ourpayTransactions.get(i).getAmount());
                tempPay.setText(formattedPriceString);
                checkImage.setVisibility(View.GONE);
            }

            TextViewCompat.setAutoSizeTextTypeWithDefaults(tempPay, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
            viewGroup.addView(generateLineView(context));
            viewGroup.addView(ourpayPanelRow);

            generateProgressCircles(circlesContainer, context, transactionsCount);
        }
    }

    private void generateProgressCircles(ViewGroup circlesContainer, Context context, int transactionsSize) {
        for (int j = 0; j < transactionsSize; j++) {
            TextView panelCircleState = (TextView) LayoutInflater.from(context).inflate(R.layout.ourpay_panel_circle, circlesContainer, false);
            float size = panelCircleState.getContext().getResources().getDimension(R.dimen.text_size_body);
            panelCircleState.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);

            panelCircleState.setText(String.valueOf(j + 1));

            if (j <= (int) circlesContainer.getTag()) {
                panelCircleState.setBackground(context.getResources().getDrawable(R.drawable.ourpay_circle_state_active));
            } else {
                panelCircleState.setBackground(context.getResources().getDrawable(R.drawable.ourpay_circle_state_default));
            }

            circlesContainer.addView(panelCircleState);

            if (j < transactionsSize - 1) {
                Space space = new Space(context);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1.0f
                );
                space.setLayoutParams(params);

                circlesContainer.addView(space);
            }
        }

    }

    private View generateLineView(Context context) {
        View lineView = new View(context);
        lineView.setBackgroundColor(context.getResources().getColor(R.color.border_regular));
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.height = 2;
        lineView.setLayoutParams(layoutParams);
        return lineView;
    }
}
