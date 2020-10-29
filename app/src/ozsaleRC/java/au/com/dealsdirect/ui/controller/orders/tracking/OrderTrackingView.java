package au.com.dealsdirect.ui.controller.orders.tracking;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.orders.GetOrdersResponse;
import au.com.dealsdirect.utils.DateUtils;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.StringUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.graphics.Typeface.BOLD;
import static android.text.Spanned.SPAN_EXCLUSIVE_INCLUSIVE;

public class OrderTrackingView extends LinearLayout {

    private final static int STEP_MAX_COUNT = 5;

    @BindView(R.id.trackHereButton)
    ViewGroup trackHereButton;

    @BindView(R.id.order_node_1)
    ImageView orderNode1;
    @BindView(R.id.order_node_2)
    ImageView orderNode2;
    @BindView(R.id.order_node_3)
    ImageView orderNode3;
    @BindView(R.id.order_node_4)
    ImageView orderNode4;
    @BindView(R.id.order_node_5)
    ImageView orderNode5;

    @BindView(R.id.order_node_title_1)
    TextView orderNodeTitle1;
    @BindView(R.id.order_node_title_2)
    TextView orderNodeTitle2;
    @BindView(R.id.order_node_title_3)
    TextView orderNodeTitle3;
    @BindView(R.id.order_node_title_4)
    TextView orderNodeTitle4;
    @BindView(R.id.order_node_title_5)
    TextView orderNodeTitle5;

    @BindView(R.id.order_node_date_1)
    TextView orderNodeDate1;
    @BindView(R.id.order_node_date_2)
    TextView orderNodeDate2;
    @BindView(R.id.order_node_date_3)
    TextView orderNodeDate3;
    @BindView(R.id.order_node_date_4)
    TextView orderNodeDate4;
    @BindView(R.id.order_node_date_5)
    TextView orderNodeDate5;

    @BindView(R.id.order_node_connector_1)
    ProgressBar orderNodeConnector1;
    @BindView(R.id.order_node_connector_2)
    ProgressBar orderNodeConnector2;
    @BindView(R.id.order_node_connector_3)
    ProgressBar orderNodeConnector3;
    @BindView(R.id.order_node_connector_4)
    ProgressBar orderNodeConnector4;

    @BindView(R.id.received_order_layout)
    LinearLayout receivedOrderLayout;
    @BindView(R.id.estimatedDeliveryTextView)
    TextView estimatedDeliveryText;
    @BindView(R.id.shipFromTextView)
    TextView shipFromTextView;
    @BindView(R.id.shipToTextView)
    TextView shipToTextView;
    @BindView(R.id.my_order_information_text_content)
    ViewGroup deliveryRouteView;

    Unbinder unbinder = null;

    boolean isBlinkingDisabledOnLastStep = false;

    public OrderTrackingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public OrderTrackingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public OrderTrackingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void bind() {
        if (unbinder == null) {
            unbinder = ButterKnife.bind(this, this);
        }
    }

    public OrderTrackingView createNew(ViewGroup parent) {
        return (OrderTrackingView) LayoutInflater
                .from(parent.getContext()).inflate(R.layout.component_order_tracking,
                        parent,
                        false);
    }

    public boolean isBlinkingDisabledOnLastStep() {
        return isBlinkingDisabledOnLastStep;
    }

    public void setBlinkingDisabledOnLastStep(boolean blinkingDisabledOnLastStep) {
        isBlinkingDisabledOnLastStep = blinkingDisabledOnLastStep;
    }

    public void setNumberOfSteps(int numberOfSteps) {
        // TODO
    }

    ImageView getNode(int index) {
        switch (index) {
            case 0:
                return orderNode1;
            case 1:
                return orderNode2;
            case 2:
                return orderNode3;
            case 3:
                return orderNode4;
            case 4:
                return orderNode5;
            default:
                return null;
        }
    }

    TextView getNodeTitle(int index) {
        switch (index) {
            case 0:
                return orderNodeTitle1;
            case 1:
                return orderNodeTitle2;
            case 2:
                return orderNodeTitle3;
            case 3:
                return orderNodeTitle4;
            case 4:
                return orderNodeTitle5;
            default:
                return null;
        }
    }

    TextView getNodeDate(int index) {
        switch (index) {
            case 0:
                return orderNodeDate1;
            case 1:
                return orderNodeDate2;
            case 2:
                return orderNodeDate3;
            case 3:
                return orderNodeDate4;
            case 4:
                return orderNodeDate5;
            default:
                return null;
        }
    }

    ProgressBar getNodeConnector(int index) {
        switch (index) {
            case 0:
                return orderNodeConnector1;
            case 1:
                return orderNodeConnector2;
            case 2:
                return orderNodeConnector3;
            case 3:
                return orderNodeConnector4;
            default:
                return null;
        }
    }

    public void setup(String invoiceId, int invoiceNumber, GetOrdersResponse.Order.Invoice.Delivery delivery, String trackingUrl, OrderTrackingClickListener onClickListener) {
        bind();

        Context context = getContext();

        if (trackingUrl == null || trackingUrl.isEmpty()) {
            trackHereButton.setVisibility(GONE);
            trackHereButton.setOnClickListener(null);
        } else {
            trackHereButton.setVisibility(View.VISIBLE);
            trackHereButton.setOnClickListener(v ->
                    onClickListener.onOrderItemTrackingButtonClick(
                            trackingUrl,
                            null));
        }

        if (delivery.getFrom() == null || delivery.getFrom().isEmpty() ||
                delivery.getTo() == null || delivery.getTo().isEmpty() ||
                delivery.getEstimate() == null || delivery.getEstimate().isEmpty()) {
            deliveryRouteView.setVisibility(View.GONE);
        } else {
            deliveryRouteView.setVisibility(View.VISIBLE);
            shipFromTextView.setText(StringUtils.twoPartStringWithStyles(
                    context.getResources().getString(R.string.ship_from_with_colon),
                    null,
                    delivery.getFrom(),
                    new StyleSpan(BOLD)
            ));
            estimatedDeliveryText.setText(StringUtils.applySpanToSubstringsMatching(
                    new SpannableStringBuilder(delivery.getEstimate()),
                    new StyleSpan(BOLD),
                    "(?!.*:).{1,}",
                    SPAN_EXCLUSIVE_INCLUSIVE));
            shipToTextView.setText(StringUtils.twoPartStringWithStyles(
                    context.getResources().getString(R.string.ship_to_with_colon),
                    null,
                    delivery.getTo(),
                    new StyleSpan(BOLD)
            ));
        }

        List<GetOrdersResponse.Order.Invoice.Delivery.Step> steps = delivery.getSteps();

        setNumberOfSteps(steps.size());

        int offset = STEP_MAX_COUNT - steps.size();

        for (int i = 0; i < offset; i++) {
            getNode(i).setVisibility(View.GONE);
            getNode(i).clearAnimation();
            getNodeConnector(i).setVisibility(View.GONE);
            getNodeTitle(i).setVisibility(View.GONE);
            getNodeDate(i).setVisibility(View.GONE);
        }

        Animation blink = AnimationUtils.loadAnimation(getContext(), R.anim.blinking);

        for (int i = offset; i < STEP_MAX_COUNT - 1; i++) {
            getNode(i).setVisibility(View.VISIBLE);
            getNodeConnector(i).setVisibility(View.VISIBLE);
            getNodeTitle(i).setVisibility(View.VISIBLE);
            getNodeDate(i).setVisibility(View.VISIBLE);

            GetOrdersResponse.Order.Invoice.Delivery.Step step = steps.get(i - offset);
            if (step.getStatus().equalsIgnoreCase("active")) {
                getNode(i).setBackgroundResource(R.drawable.bg_order_tracking_node_hollow);
                getNodeTitle(i).setTextColor(context.getResources().getColor(R.color.order_tracking_active));
                if (getNode(i).getAnimation() == null) {
                    getNode(i).startAnimation(blink);
                }
            } else if (step.getStatus().equalsIgnoreCase("success")) {
                getNode(i).setBackgroundResource(R.drawable.bg_order_tracking_node_full);
                getNodeTitle(i).setTextColor(context.getResources().getColor(R.color.order_tracking_done));
                if (i > 0) {
                    getNodeConnector(i - 1).setProgress(100);
                }
                getNode(i).clearAnimation();
            } else if (step.getStatus().equalsIgnoreCase("cancelled")) {
                getNode(i).setBackgroundResource(R.drawable.bg_order_tracking_node_cancelled);
                getNodeTitle(i).setTextColor(context.getResources().getColor(R.color.order_tracking_done));
                getNode(i).clearAnimation();
            } else {
                getNode(i).setBackgroundResource(R.drawable.bg_order_tracking_node_inactive);
                getNodeTitle(i).setTextColor(context.getResources().getColor(R.color.order_tracking_inactive));
                getNode(i).clearAnimation();
            }
            String title = step.getTitle() == null ? "" : step.getTitle().replace(' ', '\n');
            getNodeTitle(i).setText(title);
            getNodeDate(i).setText(DateUtils.getDateForOrderProgress(step.getDate()));
            getNodeConnector(i).setProgress((int) step.getProgress());
            ImageUtils.loadImage(step.getIconUrl(), getNode(i));

            final int upperStepIndex = Math.max(i - offset - 1, 0);
            final int lowerStepIndex = upperStepIndex + 1;

            final View.OnClickListener clickListener = v -> {
                if (onClickListener != null) {
                    onClickListener.onNodeTapped(
                            steps.get(upperStepIndex),
                            steps.get(lowerStepIndex));
                }
            };
            getNode(i).setOnClickListener(clickListener);
        }

        setupOrderReceivedNode(offset, invoiceId, invoiceNumber, delivery, onClickListener);
    }

    private void setupOrderReceivedNode(int offset, String invoiceId, int invoiceNumber, GetOrdersResponse.Order.Invoice.Delivery delivery, OrderTrackingClickListener onClickListener) {
        GetOrdersResponse.Order.Invoice.Delivery.Step step = delivery.getSteps().get(STEP_MAX_COUNT - 1 - offset);

        ImageView node = getNode(STEP_MAX_COUNT - 1);
        final Animation blink = AnimationUtils.loadAnimation(getContext(), R.anim.blinking);

        if (step.getStatus().equalsIgnoreCase("active")) {
            node.setBackgroundResource(R.drawable.bg_order_tracking_node_hollow);
            getNodeConnector(STEP_MAX_COUNT - 2).setProgress(0);
            if (isBlinkingDisabledOnLastStep) {
                node.clearAnimation();
            } else if (node.getAnimation() == null) {
                node.startAnimation(blink);
            }
            node.setOnClickListener(v -> onClickListener.onOrderReceivedToggle(invoiceId, invoiceNumber, true));
        } else if (step.getStatus().equalsIgnoreCase("success")) {
            node.setBackgroundResource(R.drawable.bg_order_tracking_node_full);
            getNodeConnector(STEP_MAX_COUNT - 2).setProgress(100);
            node.clearAnimation();
            node.setOnClickListener(v -> onClickListener.onOrderReceivedToggle(invoiceId, invoiceNumber, false));
        } else {
            node.setBackgroundResource(R.drawable.bg_order_tracking_node_not_received);
            getNodeConnector(STEP_MAX_COUNT - 2).setProgress(0);
            node.clearAnimation();
            node.setOnClickListener(null);
        }

        ImageUtils.loadImage(step.getIconUrl(), node);

        String title = step.getTitle() == null ? "" : step.getTitle().replace(' ', '\n');
        getNodeTitle(STEP_MAX_COUNT - 1).setText(title);
    }
}
