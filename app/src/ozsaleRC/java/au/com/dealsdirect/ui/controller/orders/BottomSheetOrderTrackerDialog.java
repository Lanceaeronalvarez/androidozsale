package au.com.dealsdirect.ui.controller.orders;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.orders.GetOrdersResponse.Order.Invoice.Delivery.Step;
import au.com.dealsdirect.utils.DateUtils;
import au.com.dealsdirect.utils.ImageUtils;

public class BottomSheetOrderTrackerDialog extends BottomSheetDialogFragment {

    private View mMainLayout = null;

    private TextView upperLabel;
    private ImageView upperNode;

    private ProgressBar progressBar;

    private TextView lowerLabel;
    private ImageView lowerNode;

    private Step upperStep;
    private Step lowerStep;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.BottomSheetDialogTheme);
    }

    @Override
    public void onStart() {
        super.onStart();
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from((View) mMainLayout.getParent());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_order_step, container, false);

        bindViews(v);

        mMainLayout = v;

        setupStep(upperStep, upperLabel, upperNode);
        setupStep(lowerStep, lowerLabel, lowerNode);
        setupProgressBar();

        return v;
    }

    private void bindViews(View v) {
        upperLabel = v.findViewById(R.id.order_step_upper_label);
        upperNode = v.findViewById(R.id.order_step_upper_node);

        progressBar = v.findViewById(R.id.order_step_progress_bar);

        lowerLabel = v.findViewById(R.id.order_step_lower_label);
        lowerNode = v.findViewById(R.id.order_step_lower_node);
    }

    private void setupStep(Step step, TextView label, ImageView node) {
        LabelSetupObject labelSetupObject = new LabelSetupObject();

        if (step.getStatus().equalsIgnoreCase("active")) {
            node.setBackgroundResource(R.drawable.bg_order_tracking_node_hollow);
            labelSetupObject.getTitle().setColorId(R.color.order_tracking_active);
        } else if (step.getStatus().equalsIgnoreCase("success")) {
            node.setBackgroundResource(R.drawable.bg_order_tracking_node_full);
            labelSetupObject.getTitle().setColorId(R.color.order_tracking_done);
        } else if (step.getStatus().equalsIgnoreCase("cancelled")) {
            node.setBackgroundResource(R.drawable.bg_order_tracking_node_cancelled);
            labelSetupObject.getTitle().setColorId(R.color.order_tracking_done);
        } else {
            node.setBackgroundResource(R.drawable.bg_order_tracking_node_inactive);
            labelSetupObject.getTitle().setColorId(R.color.order_tracking_inactive);
        }
        labelSetupObject.getTitle().setText(step.getTitle());
        labelSetupObject.getTitle().setBold(true);

        labelSetupObject.getDescription().setText(step.getText());
        labelSetupObject.getDescription().setBold(false);

        labelSetupObject.getDate().setText(DateUtils.getDateForOrderProgress(step.getDate()));
        labelSetupObject.getDate().setBold(false);

        label.setText(labelSetupObject.getSpannableString(label.getContext()));

        ImageUtils.loadImage(step.getIconUrl(), node);
    }

    private void setupProgressBar() {
        int progress = 0;
        if (lowerStep.getStatus().equalsIgnoreCase("success")) {
            progress = 100;
        } else {
            progress = (int) upperStep.getProgress();
        }
        progressBar.setProgress(progress);
    }

    public Step getUpperStep() {
        return upperStep;
    }

    public void setUpperStep(Step upperStep) {
        this.upperStep = upperStep;
    }

    public Step getLowerStep() {
        return lowerStep;
    }

    public void setLowerStep(Step lowerStep) {
        this.lowerStep = lowerStep;
    }

    private static class LabelSetupObject {
        private SubLabelObject title = new SubLabelObject();
        private SubLabelObject description = new SubLabelObject();
        private SubLabelObject date = new SubLabelObject();

        public SubLabelObject getTitle() {
            return title;
        }

        public void setTitle(SubLabelObject title) {
            this.title = title;
        }

        public SubLabelObject getDescription() {
            return description;
        }

        public void setDescription(SubLabelObject description) {
            this.description = description;
        }

        public SubLabelObject getDate() {
            return date;
        }

        public void setDate(SubLabelObject date) {
            this.date = date;
        }

        private Spannable getSpannableString(Context context) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();

            getTitle().appendTo(context, spannableStringBuilder);
            getDescription().appendTo(context, spannableStringBuilder);
            getDate().appendTo(context, spannableStringBuilder);

            return spannableStringBuilder;
        }
    }

    private static class SubLabelObject {
        private boolean isBold = false;
        private String text = "";
        private int colorId = 0;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text == null ? "" : text;
        }

        public int getColorId() {
            return colorId;
        }

        public void setColorId(int colorId) {
            this.colorId = colorId;
        }

        public boolean isBold() {
            return isBold;
        }

        public void setBold(boolean bold) {
            isBold = bold;
        }

        private void appendTo(Context context, SpannableStringBuilder spannableStringBuilder) {
            if (getText().isEmpty()) {
                return;
            }
            final int color = getColorId() > 0 ?
                    context.getResources().getColor(getColorId()) :
                    context.getResources().getColor(R.color.text_dark);

            if (spannableStringBuilder.length() > 0) {
                spannableStringBuilder.append("\n");
            }

            final int start = spannableStringBuilder.length();
            final int end = start + getText().length();
            spannableStringBuilder.append(getText());
            spannableStringBuilder.setSpan(
                    new ForegroundColorSpan(color),
                    start, end,
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            spannableStringBuilder.setSpan(
                    new StyleSpan(isBold() ? Typeface.BOLD : Typeface.NORMAL),
                    start, end,
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
    }
}
