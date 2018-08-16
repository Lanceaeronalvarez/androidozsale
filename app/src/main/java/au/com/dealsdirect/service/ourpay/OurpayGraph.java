package au.com.dealsdirect.service.ourpay;

/*
 * Created by CodeineBot on 9/28/16.
 */

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.GetCurrentOrderOurpay;
import au.com.dealsdirect.utils.PriceUtils;

import static android.view.ViewTreeObserver.OnGlobalLayoutListener;

@SuppressWarnings({"ResourceType"})
public class OurpayGraph {

    private OnGlobalLayoutListener mOnGlobalLayoutListener;

    /**
     * @param context            -  Get context to identify
     * @param ourpayTransactions - Get Arraylist for processing information
     * @return - return view as ui object
     */

    public View generateGraph(final Context context, final List<GetCurrentOrderOurpay.PlannedTransaction> ourpayTransactions) {
        final LinearLayout rootViewLayout;

        rootViewLayout = new LinearLayout(context);
        rootViewLayout.setBackgroundColor(Color.WHITE);
        rootViewLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams LLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        rootViewLayout.setLayoutParams(LLParams);

        generateRows(ourpayTransactions, context, rootViewLayout);

        return rootViewLayout;
    }

    private void generateRows(List<GetCurrentOrderOurpay.PlannedTransaction> ourpayTransactions, Context context, ViewGroup viewGroup) {
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

            if (ourpayTransactions.get(i).getState() == 2) {
                tempPay.setText(R.string.paid);
                checkImage.setVisibility(View.VISIBLE);
            } else {
                tempPay.setText(PriceUtils.getPriceStringValue(ourpayTransactions.get(i).getAmount()));
                checkImage.setVisibility(View.INVISIBLE);
            }

            mOnGlobalLayoutListener = new OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (circlesContainer.getMeasuredWidth() != 0) {
                        circlesContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        generateProgressCircles(circlesContainer, context, circlesContainer.getWidth(), transactionsCount);
                    }
                }
            };

            circlesContainer.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);

            viewGroup.addView(ourpayPanelRow);
        }
    }

    private void generateProgressCircles(ViewGroup circlesContainer, Context context, int margin, int transactionsSize) {
        int initialMargin = margin - (context.getResources().getDimensionPixelSize(R.dimen.ourpay_circle_state_size) * transactionsSize);
        int finalMargin = initialMargin / (transactionsSize - 1);

        for (int j = 0; j < transactionsSize; j++) {
            TextView panelCircleState = (TextView) LayoutInflater.from(context).inflate(R.layout.ourpay_panel_circle, circlesContainer, false);
            panelCircleState.setText(String.valueOf(j + 1));

            if (j <= (int) circlesContainer.getTag()) {
                panelCircleState.setBackground(context.getResources().getDrawable(R.drawable.ourpay_circle_state_active));
            } else {
                panelCircleState.setBackground(context.getResources().getDrawable(R.drawable.ourpay_circle_state_default));
            }

            if (j > 0) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) panelCircleState.getLayoutParams();
                lp.setMargins(finalMargin, 0, 0, 0);
            }

            circlesContainer.addView(panelCircleState);

        }

    }

}
