package au.com.dealsdirect.ui.custom;

import android.animation.Animator;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

public class BottomPopupView {

    protected final ViewGroup parent;
    protected final RelativeLayout container;
    protected final View content;
    protected final BottomPopupViewAdapter adapter;

    protected BottomPopupViewListener listener = null;

    public BottomPopupView(ViewGroup parent, BottomPopupViewAdapter adapter) {
        this.parent = parent;
        container = new RelativeLayout(parent.getContext());
        this.adapter = adapter;
        this.content = adapter.onCreate(container);
        setup();

        container.setTag("BottomPopupView.container");
        content.setTag("BottomPopupView.content");
    }

    private void setup() {
        container.setLayoutParams(new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        RelativeLayout.LayoutParams layoutParams;
        if (content.getLayoutParams() != null) {
            layoutParams = new RelativeLayout.LayoutParams(content.getLayoutParams());
        } else {
            layoutParams = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        content.setLayoutParams(layoutParams);

        container.addView(content);
    }

    protected void addViewToParent() {
        if (container.getParent() != null) {
            return;
        }
        parent.addView(container);
        parent.invalidate();
    }

    protected void removeViewFromParent() {
        if (container.getParent() == null) {
            return;
        }
        parent.removeView(container);
        parent.invalidate();
    }

    public void show(boolean isAnimated) {
        if (listener != null) {
            listener.willShow();
        }
        addViewToParent();
        container.setVisibility(View.VISIBLE);
        if (isAnimated) {
            content.measure(0, 0);
            content.setTranslationY(content.getMeasuredHeight());
            content.animate()
                    .translationY(0)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(@NonNull Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(@NonNull Animator animation) {
                            show();
                        }

                        @Override
                        public void onAnimationCancel(@NonNull Animator animation) {
                            show();
                        }

                        @Override
                        public void onAnimationRepeat(@NonNull Animator animation) {

                        }
                    });
        } else {
            show();
        }
    }

    private void show() {
        content.setTranslationY(0);
        if (listener != null) {
            listener.onShow();
        }
    }

    public void dismiss(boolean isAnimated) {
        if (listener != null) {
            listener.willDismiss();
        }
        if (isAnimated) {
            content.setTranslationY(0);
            content.animate()
                    .translationY(content.getMeasuredHeight())
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(@NonNull Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(@NonNull Animator animation) {
                            dismiss();
                        }

                        @Override
                        public void onAnimationCancel(@NonNull Animator animation) {
                            dismiss();
                        }

                        @Override
                        public void onAnimationRepeat(@NonNull Animator animation) {

                        }
                    });
        } else {
            dismiss();
        }
    }

    private void dismiss() {
        content.setTranslationY(content.getMeasuredHeight());
        container.setVisibility(View.GONE);
        removeViewFromParent();

        if (listener != null) {
            listener.onDismiss();
        }
    }

    public void setListener(BottomPopupViewListener listener) {
        this.listener = listener;
    }

    public BottomPopupViewAdapter getAdapter() {
        return adapter;
    }

    public interface BottomPopupViewListener {
        void willShow();

        void onShow();

        void willDismiss();

        void onDismiss();
    }

    public interface BottomPopupViewAdapter {
        View onCreate(ViewGroup parent);
    }
}
