package au.com.dealsdirect.ui.controller.returns.returnsteps;

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
import au.com.dealsdirect.data.network.model.returns.step.Step;
import au.com.dealsdirect.utils.DateUtils;
import au.com.dealsdirect.utils.ImageUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ReturnTrackingView extends LinearLayout {

    private static final String STEP_STATUS_ACTIVE = "active";
    private static final String STEP_STATUS_SUCCESS = "success";
    private static final String STEP_STATUS_CANCELLED = "cancelled";

    public boolean addGlow = false;

    @BindView(R.id.my_order_graph_container)
    ConstraintLayout graphContainer;

    @BindView(R.id.left_spacer)
    View leftSpacerView;
    @BindView(R.id.right_spacer)
    View rightSpacerView;

    Unbinder unbinder = null;

    boolean isBlinkingDisabledOnLastStep = false;

    public ReturnTrackingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ReturnTrackingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ReturnTrackingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void bind() {
        if (unbinder == null) {
            unbinder = ButterKnife.bind(this, this);
        }
    }

    public ReturnTrackingView createNew(ViewGroup parent) {
        return (ReturnTrackingView) LayoutInflater
                .from(parent.getContext()).inflate(R.layout.component_return_tracking,
                        parent,
                        false);
    }

    public boolean isBlinkingDisabledOnLastStep() {
        return isBlinkingDisabledOnLastStep;
    }

    public void setBlinkingDisabledOnLastStep(boolean blinkingDisabledOnLastStep) {
        isBlinkingDisabledOnLastStep = blinkingDisabledOnLastStep;
    }

    public void setup(List<Step> steps, String returnId, ReturnTrackingClickListener listener) {
        bind();

        if (glowingView == null) {
            glowingView = createNodeGlowView(graphContainer);
        }

        glowingViewPosition = -1;
        if (addGlow) {
            for (int i = 0; i < steps.size(); i++) {
                final Step step = steps.get(i);
                if (step.getStatus().equalsIgnoreCase(STEP_STATUS_ACTIVE)) {
                    glowingViewPosition = i;
                    break;
                }
            }
        }
        glowingView.setVisibility(glowingViewPosition >= 0 ? VISIBLE : GONE);

        setNumberOfSteps(steps.size());

        final Animation blink = AnimationUtils.loadAnimation(getContext(), R.anim.blinking);

        if (glowingViewPosition >= 0) {
            if (glowingView.getAnimation() == null) {
                glowingView.startAnimation(blink);
            }
        } else {
            glowingView.clearAnimation();
        }

        for (int i = 0; i < steps.size() - 1; i++) {
            final Step step = steps.get(i);
            final View node = nodes.get(i);
            final ImageView nodeIcon = nodeIcons.get(i);
            final TextView nodeLabel = nodeLabels.get(i);
            final TextView nodeDateLabel = nodeDateLabels.get(i);
            final ProgressBar rightProgressBar = progressBars.get(new Pair<>(i, i + 1));
            final ProgressBar leftProgressBar = progressBars.get(new Pair<>(i, i + 1));
            final ReturnTrackingView.LabelSetupObject labelSetupObject = new ReturnTrackingView.LabelSetupObject();

            final int upperStepIndex = Math.max(0, i - 1);
            final int lowerStepIndex = upperStepIndex + 1;

            if (step.getStatus().equalsIgnoreCase(STEP_STATUS_ACTIVE)) {
                node.setBackgroundResource(R.drawable.bg_order_tracking_node_hollow);
                labelSetupObject.getTitle().setColorId(R.color.order_tracking_active);
                if (node.getAnimation() == null) {
                    node.startAnimation(blink);
                }
            } else if (step.getStatus().equalsIgnoreCase(STEP_STATUS_SUCCESS)) {
                node.setBackgroundResource(R.drawable.bg_order_tracking_node_full);
                labelSetupObject.getTitle().setColorId(R.color.order_tracking_done);
                if (leftProgressBar != null) {
                    leftProgressBar.setProgress(100);
                }
                node.clearAnimation();
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

            labelSetupObject.getTitle().setBold(true);
            labelSetupObject.getDate().setColorId(R.color.text_light);
            labelSetupObject.getDate().setBold(false);

            labelSetupObject.getTitle().setText(step.getTitle() == null ? "" : step.getTitle().replace(' ', '\n'));
            labelSetupObject.getDate().setText(null);
            nodeLabel.setText(labelSetupObject.getSpannableString(nodeLabel.getContext()));
            labelSetupObject.getTitle().setText(null);
            labelSetupObject.getDate().setText(DateUtils.getDateForOrderProgress(step.getDate()));
            nodeDateLabel.setText(labelSetupObject.getSpannableString(nodeLabel.getContext()));

            if (rightProgressBar != null) {
                rightProgressBar.setProgress((int) step.getProgress());
            }
            ImageUtils.loadImage(step.getIconUrl(), nodeIcon);
        }

        setupCloseNode(steps.get(steps.size() - 1), returnId, listener);
    }

    private void setupCloseNode(Step finalStep, String returnId, ReturnTrackingClickListener listener) {
        setupCloseNode(finalStep.getTitle(), finalStep.getIconUrl(), finalStep.getStatus(), getReceivedClickListener(returnId, finalStep, listener));
    }

    private void setupCloseNode(String title, String iconUrl, String closedStatus, OnClickListener listener) {
        View node = nodes.get(nodes.size() - 1);
        ImageView nodeIcon = nodeIcons.get(nodeIcons.size() - 1);
        TextView nodeLabel = nodeLabels.get(nodeLabels.size() - 1);
        TextView nodeDateLabel = nodeDateLabels.get(nodeDateLabels.size() - 1);
        final Animation blink = AnimationUtils.loadAnimation(getContext(), R.anim.blinking);

        LabelSetupObject labelSetupObject = new LabelSetupObject();

        ProgressBar progressBar = progressBars.get(new Pair<>(nodes.size() - 2, nodes.size() - 1));
        Integer drawableId;
        switch (closedStatus) {
            case STEP_STATUS_ACTIVE:
                node.setBackgroundResource(R.drawable.bg_order_tracking_node_hollow);
                if (progressBar != null) {
                    progressBar.setProgress(0);
                }
                if (isBlinkingDisabledOnLastStep) {
                    node.clearAnimation();
                } else if (node.getAnimation() == null) {
                    node.startAnimation(blink);
                }

                nodeIcon.setImageDrawable(null);
                break;
            case STEP_STATUS_SUCCESS:
                drawableId = R.drawable.order_step_received_success;
                node.setBackgroundResource(R.drawable.bg_order_tracking_node_full);
                if (progressBar != null) {
                    progressBar.setProgress(100);
                }
                node.clearAnimation();

                if (iconUrl == null || iconUrl.isEmpty()) {
                    if (drawableId == null) {
                        nodeIcon.setImageDrawable(null);
                    } else {
                        nodeIcon.setImageResource(drawableId);
                    }
                } else {
                    ImageUtils.loadImage(iconUrl, nodeIcon);
                }
                break;
            default:
                node.setBackgroundResource(R.drawable.bg_order_tracking_node_not_received);
                if (progressBar != null) {
                    progressBar.setProgress(0);
                }
                node.clearAnimation();

                nodeIcon.setImageDrawable(null);
                break;
        }
        node.setOnClickListener(listener);

        labelSetupObject.getTitle().setBold(true);
        labelSetupObject.getTitle().setColorId(R.color.order_tracking_inactive);
        labelSetupObject.getDate().setBold(false);

        labelSetupObject.getTitle().setText(title == null ? "" : title.replace(' ', '\n'));
        labelSetupObject.getDate().setText(null);
        nodeLabel.setText(labelSetupObject.getSpannableString(nodeLabel.getContext()));
        labelSetupObject.getTitle().setText(null);
        labelSetupObject.getDate().setText(null);
        nodeDateLabel.setText(labelSetupObject.getSpannableString(nodeLabel.getContext()));
    }

    private OnClickListener getReceivedClickListener(String returnId,
                                                     Step step,
                                                     ReturnTrackingClickListener onClickListener) {
        final String title = step.getTitle();
        final String orderReceivedStatus = step.getStatus().toLowerCase();
        switch (orderReceivedStatus) {
            case STEP_STATUS_ACTIVE:
                return v -> {
                    if (onClickListener != null) {
                        onClickListener.onReturnReceivedToggle(returnId, true);
                    }
                    step.setStatus(STEP_STATUS_SUCCESS);
                    step.setIconUrl(null);
                    setupCloseNode(
                            title,
                            null,
                            STEP_STATUS_SUCCESS,
                            getReceivedClickListener(
                                    returnId,
                                    step,
                                    onClickListener));
                };
            case STEP_STATUS_SUCCESS:
                return v -> {
                    if (onClickListener != null) {
                        onClickListener.onReturnReceivedToggle(returnId, false);
                    }
                    step.setStatus(STEP_STATUS_ACTIVE);
                    step.setIconUrl(null);
                    setupCloseNode(
                            title,
                            null,
                            STEP_STATUS_ACTIVE,
                            getReceivedClickListener(
                                    returnId,
                                    step,
                                    onClickListener));
                };
            default:
                return null;
        }
    }

    private final ArrayList<View> nodes = new ArrayList<>();
    private final ArrayList<ImageView> nodeIcons = new ArrayList<>();
    private final HashMap<Pair<Integer, Integer>, ProgressBar> progressBars = new HashMap<>();
    private final ArrayList<TextView> nodeLabels = new ArrayList<>();
    private final ArrayList<TextView> nodeDateLabels = new ArrayList<>();
    private View glowingView;
    private int glowingViewPosition = -1;

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
        nodeIcons.add(createNodeIcon(graphContainer));
        nodeLabels.add(createNodeLabel(graphContainer));
        nodeDateLabels.add(createNodeDateLabel(graphContainer));
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
        constraintSet.connect(leftSpacerId, ConstraintSet.BOTTOM, parentId, ConstraintSet.BOTTOM);

        constraintSet.connect(rightSpacerId, ConstraintSet.RIGHT, parentId, ConstraintSet.RIGHT);
        constraintSet.connect(rightSpacerId, ConstraintSet.BOTTOM, parentId, ConstraintSet.BOTTOM);

        final LinkedList<Integer> chainedViewIds = new LinkedList<>();
        final LinkedList<Float> weights = new LinkedList<>();
        chainedViewIds.add(leftSpacerId);
        weights.add(0.25f);
        for (int i = 0; i < nodes.size(); i++) {
            final ProgressBar leftProgressBar = progressBars.get(new Pair<>(i - 1, i));
            final int currentId = nodes.get(i).getId();
            final int iconId = nodeIcons.get(i).getId();
            final int labelId = nodeLabels.get(i).getId();
            final int dateLabelId = nodeDateLabels.get(i).getId();
            constraintSet.connect(labelId, ConstraintSet.LEFT, currentId, ConstraintSet.RIGHT);
            constraintSet.connect(labelId, ConstraintSet.RIGHT, currentId, ConstraintSet.LEFT);
            constraintSet.connect(labelId, ConstraintSet.BOTTOM, currentId, ConstraintSet.TOP);
            constraintSet.connect(dateLabelId, ConstraintSet.LEFT, currentId, ConstraintSet.RIGHT);
            constraintSet.connect(dateLabelId, ConstraintSet.RIGHT, currentId, ConstraintSet.LEFT);
            constraintSet.connect(dateLabelId, ConstraintSet.TOP, currentId, ConstraintSet.BOTTOM);
            if (leftProgressBar != null) {
                final int leftProgressBarId = leftProgressBar.getId();
                chainedViewIds.add(leftProgressBarId);
                weights.add(0.5f);
                constraintSet.connect(leftProgressBarId, ConstraintSet.TOP, currentId, ConstraintSet.TOP);
                constraintSet.connect(leftProgressBarId, ConstraintSet.BOTTOM, currentId, ConstraintSet.BOTTOM);
            }
            constraintSet.connect(currentId, ConstraintSet.TOP, parentId, ConstraintSet.TOP);
            chainedViewIds.add(currentId);
            weights.add(1.0f);

            constraintSet.connect(iconId, ConstraintSet.LEFT, currentId, ConstraintSet.LEFT);
            constraintSet.connect(iconId, ConstraintSet.RIGHT, currentId, ConstraintSet.RIGHT);
            constraintSet.connect(iconId, ConstraintSet.BOTTOM, currentId, ConstraintSet.BOTTOM);
            constraintSet.connect(iconId, ConstraintSet.TOP, currentId, ConstraintSet.TOP);


            if (i == glowingViewPosition) {
                final int glowingViewId = glowingView.getId();
                constraintSet.connect(glowingViewId, ConstraintSet.LEFT, currentId, ConstraintSet.LEFT);
                constraintSet.connect(glowingViewId, ConstraintSet.RIGHT, currentId, ConstraintSet.RIGHT);
                constraintSet.connect(glowingViewId, ConstraintSet.BOTTOM, currentId, ConstraintSet.BOTTOM);
                constraintSet.connect(glowingViewId, ConstraintSet.TOP, currentId, ConstraintSet.TOP);
            }
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

    private View createNodeView(ViewGroup parent) {
        final Context context = parent.getContext();
        final View view = new ImageView(context);
        final int nodeSize = (int) context.getResources().getDimension(R.dimen.order_tracking_node_size);
        final ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(nodeSize, nodeSize);
        view.setLayoutParams(layoutParams);
        final int padding = dpToPixels(context, 5);
        view.setPadding(padding, padding, padding, padding);
        parent.addView(view);
        setupViewIdForView(view);
        view.setBackgroundResource(R.drawable.bg_order_tracking_node_full);
        return view;
    }

    private ImageView createNodeIcon(ViewGroup parent) {
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
        return imageView;
    }

    private View createNodeGlowView(ViewGroup parent) {
        final Context context = parent.getContext();
        final View view = new View(context);
        final int nodeSize = (int) context.getResources().getDimension(R.dimen.order_tracking_node_size) * 2;
        final ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(nodeSize, nodeSize);
        view.setLayoutParams(layoutParams);
        view.setBackground(context.getResources().getDrawable(R.drawable.bg_order_tracking_node_glow));
        parent.addView(view);
        setupViewIdForView(view);
        return view;
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
        layoutParams.bottomMargin = dpToPixels(context, 3);
        textView.setLayoutParams(layoutParams);
        parent.addView(textView);
        setupViewIdForView(textView);
        textView.setTypeface(getNodeLabelTypeFace(context));
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(context.getResources().getDimension(R.dimen.text_size_caption2) / context.getResources().getDisplayMetrics().density);
        return textView;
    }

    private TextView createNodeDateLabel(ViewGroup parent) {
        final Context context = parent.getContext();
        final TextView textView = new TextView(context);
        final ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.bottomMargin = dpToPixels(context, 3);
        textView.setLayoutParams(layoutParams);
        parent.addView(textView);
        setupViewIdForView(textView);
        textView.setTypeface(getNodeLabelTypeFace(context));
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(context.getResources().getDimension(R.dimen.text_size_caption1) / context.getResources().getDisplayMetrics().density);
        return textView;
    }

    private static class LabelSetupObject {
        private ReturnTrackingView.SubLabelObject title = new ReturnTrackingView.SubLabelObject();
        private ReturnTrackingView.SubLabelObject date = new ReturnTrackingView.SubLabelObject();

        public ReturnTrackingView.SubLabelObject getTitle() {
            return title;
        }

        public void setTitle(ReturnTrackingView.SubLabelObject title) {
            this.title = title;
        }

        public ReturnTrackingView.SubLabelObject getDate() {
            return date;
        }

        public void setDate(ReturnTrackingView.SubLabelObject date) {
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
