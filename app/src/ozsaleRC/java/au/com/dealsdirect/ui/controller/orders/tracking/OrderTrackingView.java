package au.com.dealsdirect.ui.controller.orders.tracking;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Gravity;
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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
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

    private static final String STEP_STATUS_ACTIVE = "active";
    private static final String STEP_STATUS_SUCCESS = "success";
    private static final String STEP_STATUS_CANCELLED = "cancelled";

    @BindView(R.id.trackHereButton)
    ViewGroup trackHereButton;

    @BindView(R.id.my_order_graph_container)
    ConstraintLayout graphContainer;

    @BindView(R.id.left_spacer)
    View leftSpacerView;
    @BindView(R.id.right_spacer)
    View rightSpacerView;

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

        Animation blink = AnimationUtils.loadAnimation(getContext(), R.anim.blinking);

        for (int i = 0; i < steps.size() - 1; i++) {
            final GetOrdersResponse.Order.Invoice.Delivery.Step step = steps.get(i);
            final ImageView node = nodes.get(i);
            final TextView nodeLabel = nodeLabels.get(i);
            final ProgressBar rightProgressBar = progressBars.get(new Pair<>(i, i + 1));
            final ProgressBar leftProgressBar = progressBars.get(new Pair<>(i, i + 1));
            final LabelSetupObject labelSetupObject = new LabelSetupObject();

            final int upperStepIndex = Math.max(0, i - 1);
            final int lowerStepIndex = upperStepIndex + 1;

            final View.OnClickListener clickListener = v -> {
                if (onClickListener != null) {
                    onClickListener.onNodeTapped(
                            steps.get(upperStepIndex),
                            steps.get(lowerStepIndex));
                }
            };

            if (step.getStatus().equalsIgnoreCase(STEP_STATUS_ACTIVE)) {
                node.setBackgroundResource(R.drawable.bg_order_tracking_node_hollow);
                labelSetupObject.getTitle().setColorId(R.color.order_tracking_active);
                if (node.getAnimation() == null) {
                    node.startAnimation(blink);
                }
                node.setOnClickListener(clickListener);
            } else if (step.getStatus().equalsIgnoreCase(STEP_STATUS_SUCCESS)) {
                node.setBackgroundResource(R.drawable.bg_order_tracking_node_full);
                labelSetupObject.getTitle().setColorId(R.color.order_tracking_done);
                if (leftProgressBar != null) {
                    leftProgressBar.setProgress(100);
                }
                node.clearAnimation();
                node.setOnClickListener(clickListener);
            } else if (step.getStatus().equalsIgnoreCase(STEP_STATUS_CANCELLED)) {
                node.setBackgroundResource(R.drawable.bg_order_tracking_node_cancelled);
                labelSetupObject.getTitle().setColorId(R.color.order_tracking_done);
                node.clearAnimation();
                node.setOnClickListener(null);
            } else {
                node.setBackgroundResource(R.drawable.bg_order_tracking_node_inactive);
                labelSetupObject.getTitle().setColorId(R.color.order_tracking_inactive);
                node.clearAnimation();
                node.setOnClickListener(null);
            }

            labelSetupObject.getTitle().setText(step.getTitle() == null ? "" : step.getTitle().replace(' ', '\n'));
            labelSetupObject.getTitle().setBold(true);
            labelSetupObject.getDate().setText(DateUtils.getDateForOrderProgress(step.getDate()));
            labelSetupObject.getDate().setColorId(R.color.text_light);
            labelSetupObject.getDate().setBold(false);
            nodeLabel.setText(labelSetupObject.getSpannableString(nodeLabel.getContext()));

            if (rightProgressBar != null) {
                rightProgressBar.setProgress((int) step.getProgress());
            }
            ImageUtils.loadImage(step.getIconUrl(), node);
        }

        setupOrderReceivedNode(invoiceId, invoiceNumber, delivery, onClickListener);
    }

    private GetOrdersResponse.Order.Invoice.Delivery.Step getFinalStep(GetOrdersResponse.Order.Invoice.Delivery delivery) {
        return delivery.getSteps().get(delivery.getSteps().size() - 1);
    }

    private OnClickListener getReceivedClickListener(String invoiceId,
                                                     int invoiceNumber,
                                                     GetOrdersResponse.Order.Invoice.Delivery.Step step,
                                                     OrderTrackingClickListener onClickListener) {
        final String title = step.getTitle();
        final String orderReceivedStatus = step.getStatus().toLowerCase();
        switch (orderReceivedStatus) {
            case STEP_STATUS_ACTIVE:
                return v -> {
                    if (onClickListener != null) {
                        onClickListener.onOrderReceivedToggle(invoiceId, invoiceNumber, true);
                    }
                    step.setStatus(STEP_STATUS_SUCCESS);
                    step.setIconUrl(null);
                    setupOrderReceivedNode(
                            title,
                            null,
                            STEP_STATUS_SUCCESS,
                            getReceivedClickListener(
                                    invoiceId,
                                    invoiceNumber,
                                    step,
                                    onClickListener));
                };
            case STEP_STATUS_SUCCESS:
                return v -> {
                    if (onClickListener != null) {
                        onClickListener.onOrderReceivedToggle(invoiceId, invoiceNumber, false);
                    }
                    step.setStatus(STEP_STATUS_ACTIVE);
                    step.setIconUrl(null);
                    setupOrderReceivedNode(
                            title,
                            null,
                            STEP_STATUS_ACTIVE,
                            getReceivedClickListener(
                                    invoiceId,
                                    invoiceNumber,
                                    step,
                                    onClickListener));
                };
            default:
                return null;
        }
    }

    private void setupOrderReceivedNode(String invoiceId,
                                        int invoiceNumber,
                                        GetOrdersResponse.Order.Invoice.Delivery delivery,
                                        OrderTrackingClickListener onClickListener) {
        GetOrdersResponse.Order.Invoice.Delivery.Step step = getFinalStep(delivery);
        setupOrderReceivedNode(
                step.getTitle(),
                step.getIconUrl(),
                step.getStatus().toLowerCase(),
                getReceivedClickListener(
                        invoiceId,
                        invoiceNumber,
                        step,
                        onClickListener));
    }

    private void setupOrderReceivedNode(String title, String iconUrl, String orderReceivedStatus, OnClickListener onClickListener) {
        ImageView node = nodes.get(nodes.size() - 1);
        TextView nodeLabel = nodeLabels.get(nodeLabels.size() - 1);
        final Animation blink = AnimationUtils.loadAnimation(getContext(), R.anim.blinking);

        LabelSetupObject labelSetupObject = new LabelSetupObject();

        ProgressBar progressBar = progressBars.get(new Pair<>(nodes.size() - 2, nodes.size() - 1));
        Integer drawableId = null;
        switch (orderReceivedStatus) {
            case STEP_STATUS_ACTIVE:
                drawableId = R.drawable.order_step_received_active;
                node.setBackgroundResource(R.drawable.bg_order_tracking_node_hollow);
                if (progressBar != null) {
                    progressBar.setProgress(0);
                }
                if (isBlinkingDisabledOnLastStep) {
                    node.clearAnimation();
                } else if (node.getAnimation() == null) {
                    node.startAnimation(blink);
                }
                break;
            case STEP_STATUS_SUCCESS:
                drawableId = R.drawable.order_step_received_success;
                node.setBackgroundResource(R.drawable.bg_order_tracking_node_full);
                if (progressBar != null) {
                    progressBar.setProgress(100);
                }
                node.clearAnimation();
                break;
            default:
                node.setBackgroundResource(R.drawable.bg_order_tracking_node_not_received);
                if (progressBar != null) {
                    progressBar.setProgress(0);
                }
                node.clearAnimation();
                break;
        }
        node.setOnClickListener(onClickListener);

        if (iconUrl == null || iconUrl.isEmpty()) {
            if (drawableId == null) {
                node.setImageDrawable(null);
            } else {
                node.setImageResource(drawableId);
            }
        } else {
            ImageUtils.loadImage(iconUrl, node);
        }

        labelSetupObject.getTitle().setColorId(R.color.order_tracking_inactive);
        labelSetupObject.getTitle().setBold(true);
        labelSetupObject.getTitle().setText(title == null ? "" : title.replace(' ', '\n'));

        nodeLabel.setText(labelSetupObject.getSpannableString(nodeLabel.getContext()));
    }

    private final ArrayList<ImageView> nodes = new ArrayList<>();
    private final HashMap<Pair<Integer, Integer>, ProgressBar> progressBars = new HashMap<>();
    private final ArrayList<TextView> nodeLabels = new ArrayList<>();

    public void setNumberOfSteps(int steps) {
        if (steps > nodes.size()) {
            for (int i = nodes.size(); i < steps; i++) {
                addNewNode();
            }
            resetNodeConstraints();
        }
        for (int i = 0; i < nodes.size(); i++) {
            nodes.get(i).setVisibility(i < steps ? VISIBLE : GONE);
            nodes.get(i).clearAnimation();
            final ProgressBar progressBar = progressBars.get(new Pair<>(i, i + 1));
            if (progressBar != null) {
                progressBar.setVisibility(i + 1 < steps ? VISIBLE : GONE);
            }
            nodeLabels.get(i).setVisibility(i < steps ? VISIBLE : GONE);
        }

    }

    private void addNewNode() {
        int index = nodes.size();
        nodes.add(createNodeView(graphContainer));
        nodeLabels.add(createNodeLabel(graphContainer));
        if (nodes.size() > 1) {
            progressBars.put(new Pair<>(index - 1, index), createProgressBar(graphContainer));
        }
    }

    private void resetNodeConstraints() {
        final int parentId = graphContainer.getId();
        final ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(graphContainer);

        final int leftSpacerId = leftSpacerView.getId();
        final int rightSpacerId = rightSpacerView.getId();

        constraintSet.connect(leftSpacerId, ConstraintSet.LEFT, parentId, ConstraintSet.LEFT);
        constraintSet.connect(leftSpacerId, ConstraintSet.TOP, parentId, ConstraintSet.TOP);

        constraintSet.connect(rightSpacerId, ConstraintSet.RIGHT, parentId, ConstraintSet.RIGHT);
        constraintSet.connect(rightSpacerId, ConstraintSet.TOP, parentId, ConstraintSet.TOP);

        final LinkedList<Integer> chainedViewIds = new LinkedList<>();
        final LinkedList<Float> weights = new LinkedList<>();
        chainedViewIds.add(leftSpacerId);
        weights.add(0.25f);
        for (int i = 0; i < nodes.size(); i++) {
            final ProgressBar leftProgressBar = progressBars.get(new Pair<>(i - 1, i));
            final int currentId = nodes.get(i).getId();
            final int labelId = nodeLabels.get(i).getId();
            constraintSet.connect(labelId, ConstraintSet.LEFT, currentId, ConstraintSet.RIGHT);
            constraintSet.connect(labelId, ConstraintSet.RIGHT, currentId, ConstraintSet.LEFT);
            constraintSet.connect(labelId, ConstraintSet.TOP, currentId, ConstraintSet.BOTTOM);
            if (leftProgressBar != null) {
                final int leftProgressBarId = leftProgressBar.getId();
                chainedViewIds.add(leftProgressBarId);
                weights.add(0.5f);
                constraintSet.connect(leftProgressBarId, ConstraintSet.TOP, currentId, ConstraintSet.TOP);
                constraintSet.connect(leftProgressBarId, ConstraintSet.BOTTOM, currentId, ConstraintSet.BOTTOM);
            }
            chainedViewIds.add(currentId);
            weights.add(1.0f);
        }
        chainedViewIds.add(rightSpacerId);
        weights.add(0.25f);

        constraintSet.createHorizontalChain(
                parentId, ConstraintSet.LEFT,
                parentId, ConstraintSet.RIGHT,
                Ints.toArray(chainedViewIds),
                Floats.toArray(weights),
                ConstraintSet.CHAIN_SPREAD
        );

        constraintSet.applyTo(graphContainer);
    }

    private int dpToPixels(Context context, float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    private void setupViewIdForView(View view) {
        final ViewGroup parent = (ViewGroup) view.getParent();
        int candidateId = ((ViewGroup) view.getParent()).getChildCount();
        while (parent.findViewById(candidateId) != null) {
            candidateId += 1;
        }
        view.setId(candidateId);
    }

    private ProgressBar createProgressBar(ViewGroup parent) {
        final Context context = parent.getContext();
        final ProgressBar progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        final ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(0, dpToPixels(context, 5));
        progressBar.setLayoutParams(layoutParams);
        parent.addView(progressBar);
        setupViewIdForView(progressBar);
        progressBar.setProgressDrawable(context.getDrawable(R.drawable.bg_order_tracking_line_active));
        return progressBar;
    }

    private ImageView createNodeView(ViewGroup parent) {
        final Context context = parent.getContext();
        final ImageView imageView = new ImageView(context);
        final int nodeSize = (int) context.getResources().getDimension(R.dimen.order_tracking_node_size);
        final ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(nodeSize, nodeSize);
        imageView.setLayoutParams(layoutParams);
        final int padding = dpToPixels(context, 5);
        imageView.setPadding(padding, padding, padding, padding);
        parent.addView(imageView);
        setupViewIdForView(imageView);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setBackgroundResource(R.drawable.bg_order_tracking_node_full);
        return imageView;
    }

    private Typeface nodeLabelTypeFace = null;

    private Typeface getNodeLabelTypeFace(Context context) {
        if (nodeLabelTypeFace == null) {
            nodeLabelTypeFace = Typeface.createFromAsset(context.getAssets(), context.getResources().getString(R.string.font_app_regular));
        }
        return nodeLabelTypeFace;
    }

    private TextView createNodeLabel(ViewGroup parent) {
        final Context context = parent.getContext();
        final TextView textView = new TextView(context);
        final ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(layoutParams);
        parent.addView(textView);
        setupViewIdForView(textView);
        textView.setTypeface(getNodeLabelTypeFace(context));
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(context.getResources().getDimension(R.dimen.text_size_caption1) / context.getResources().getDisplayMetrics().density);
        return textView;
    }

    private static class LabelSetupObject {
        private SubLabelObject title = new SubLabelObject();
        private SubLabelObject date = new SubLabelObject();

        public SubLabelObject getTitle() {
            return title;
        }

        public void setTitle(SubLabelObject title) {
            this.title = title;
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
