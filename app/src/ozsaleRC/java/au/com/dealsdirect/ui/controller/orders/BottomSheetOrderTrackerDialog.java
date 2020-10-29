package au.com.dealsdirect.ui.controller.orders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.common.collect.Sets;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.HashSet;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.orders.GetOrdersResponse;
import au.com.dealsdirect.utils.CommonUtils;
import au.com.dealsdirect.utils.DateUtils;
import au.com.dealsdirect.utils.ImageUtils;

public class BottomSheetOrderTrackerDialog extends BottomSheetDialogFragment {

    private ArrayList<Pair<String, String>> mProductSizes = new ArrayList<>();
    private HashSet<Integer> mIndicesOfSoldOutSizes = new HashSet<>();

    private View mMainLayout = null;

    private TextView upperTitle;
    private TextView upperDescription;
    private TextView upperDate;
    private ImageView upperNode;

    private ProgressBar progressBar;

    private TextView lowerTitle;
    private TextView lowerDescription;
    private TextView lowerDate;
    private ImageView lowerNode;

    private GetOrdersResponse.Order.Invoice.Delivery.Step upperStep;
    private GetOrdersResponse.Order.Invoice.Delivery.Step lowerStep;

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

        setupUpperStep();
        setupLowerStep();
        setupProgressBar();

        return v;
    }

    private void bindViews(View v) {
        upperTitle = v.findViewById(R.id.order_step_upper_title);
        upperDescription = v.findViewById(R.id.order_step_upper_description);
        upperDate = v.findViewById(R.id.order_step_upper_date);
        upperNode = v.findViewById(R.id.order_step_upper_node);

        progressBar = v.findViewById(R.id.order_step_progress_bar);

        lowerTitle = v.findViewById(R.id.order_step_lower_title);
        lowerDescription = v.findViewById(R.id.order_step_lower_description);
        lowerDate = v.findViewById(R.id.order_step_lower_date);
        lowerNode = v.findViewById(R.id.order_step_lower_node);
    }

    private void setupUpperStep() {
        GetOrdersResponse.Order.Invoice.Delivery.Step step = upperStep;

        if (step.getStatus().equalsIgnoreCase("active")) {
            upperNode.setBackgroundResource(R.drawable.bg_order_tracking_node_hollow);
            upperTitle.setTextColor(upperTitle.getContext().getResources().getColor(R.color.order_tracking_active));
        } else if (step.getStatus().equalsIgnoreCase("success")) {
            upperNode.setBackgroundResource(R.drawable.bg_order_tracking_node_full);
            upperTitle.setTextColor(upperTitle.getContext().getResources().getColor(R.color.order_tracking_done));
        } else if (step.getStatus().equalsIgnoreCase("cancelled")) {
            upperNode.setBackgroundResource(R.drawable.bg_order_tracking_node_cancelled);
            upperTitle.setTextColor(upperTitle.getContext().getResources().getColor(R.color.order_tracking_done));
        } else {
            upperNode.setBackgroundResource(R.drawable.bg_order_tracking_node_inactive);
            upperTitle.setTextColor(upperTitle.getContext().getResources().getColor(R.color.order_tracking_inactive));
        }
        upperTitle.setText(step.getTitle());

        if (upperStep.getText() == null || upperStep.getText().isEmpty()) {
            upperDescription.setVisibility(View.GONE);
        } else {
            upperDescription.setVisibility(View.VISIBLE);
            upperDescription.setText(upperStep.getText());
        }

        if (upperStep.getDate() == null || upperStep.getDate().isEmpty()) {
            upperDate.setVisibility(View.GONE);
        } else {
            upperDate.setVisibility(View.VISIBLE);
            upperDate.setText(DateUtils.getDateForOrderProgress(step.getDate()));
        }


        ImageUtils.loadImage(step.getIconUrl(), upperNode);
    }

    private void setupLowerStep() {
        GetOrdersResponse.Order.Invoice.Delivery.Step step = lowerStep;

        if (step.getStatus().equalsIgnoreCase("active")) {
            lowerNode.setBackgroundResource(R.drawable.bg_order_tracking_node_hollow);
            lowerTitle.setTextColor(lowerTitle.getContext().getResources().getColor(R.color.order_tracking_active));
        } else if (step.getStatus().equalsIgnoreCase("success")) {
            lowerNode.setBackgroundResource(R.drawable.bg_order_tracking_node_full);
            lowerTitle.setTextColor(lowerTitle.getContext().getResources().getColor(R.color.order_tracking_done));
        } else if (step.getStatus().equalsIgnoreCase("cancelled")) {
            lowerNode.setBackgroundResource(R.drawable.bg_order_tracking_node_cancelled);
            lowerTitle.setTextColor(lowerTitle.getContext().getResources().getColor(R.color.order_tracking_done));
        } else {
            lowerNode.setBackgroundResource(R.drawable.bg_order_tracking_node_inactive);
            lowerTitle.setTextColor(lowerTitle.getContext().getResources().getColor(R.color.order_tracking_inactive));
        }
        lowerTitle.setText(step.getTitle());
        lowerDate.setText(DateUtils.getDateForOrderProgress(step.getDate()));

        if (lowerStep.getText() == null) {
            lowerDescription.setVisibility(View.GONE);
        } else {
            lowerDescription.setVisibility(View.VISIBLE);
            lowerDescription.setText(lowerStep.getText());
        }

        ImageUtils.loadImage(step.getIconUrl(), lowerNode);
    }

    private void setupProgressBar() {
        int progress = 0;
        if (lowerStep.getStatus().equalsIgnoreCase("success")) {
            progress = 100;
        } else {
            upperStep.getProgress();
        }
        progressBar.setProgress(progress);
    }

    public GetOrdersResponse.Order.Invoice.Delivery.Step getUpperStep() {
        return upperStep;
    }

    public void setUpperStep(GetOrdersResponse.Order.Invoice.Delivery.Step upperStep) {
        this.upperStep = upperStep;
    }

    public GetOrdersResponse.Order.Invoice.Delivery.Step getLowerStep() {
        return lowerStep;
    }

    public void setLowerStep(GetOrdersResponse.Order.Invoice.Delivery.Step lowerStep) {
        this.lowerStep = lowerStep;
    }
}
