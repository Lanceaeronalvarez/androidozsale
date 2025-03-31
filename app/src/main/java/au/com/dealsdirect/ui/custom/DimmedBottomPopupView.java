package au.com.dealsdirect.ui.custom;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import au.com.dealsdirect.utils.CommonUtils;

public class DimmedBottomPopupView extends BottomPopupView {
    private final DimmedBottomPopupViewAdapter dimmingAdapter;

    private OnBackgroundClickedListener onBackgroundClickedListener;

    public DimmedBottomPopupView(ViewGroup parent, BottomPopupViewAdapter adapter, DimmedBottomPopupViewAdapter dimmingAdapter) {
        super(parent, adapter);
        this.dimmingAdapter = dimmingAdapter;

        dimmingAdapter.getDimmingView().setOnClickListener(v -> {
            if (onBackgroundClickedListener != null) {
                onBackgroundClickedListener.onClick();
            }
        });
    }

    @Override
    public void show(boolean isAnimated) {
        if (listener != null) {
            listener.willShow();
        }
        addViewToParent();
        container.setVisibility(View.VISIBLE);

        showDimming(isAnimated);

        View bottomView = dimmingAdapter.getBottomView();

        if (isAnimated) {
            bottomView.measure(0, 0);
            bottomView.setTranslationY(bottomView.getMeasuredHeight());
            bottomView.animate()
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
        View bottomView = dimmingAdapter.getBottomView();
        bottomView.setTranslationY(0);
        if (listener != null) {
            listener.onShow();
        }
    }

    @Override
    public void dismiss(boolean isAnimated) {
        if (listener != null) {
            listener.willDismiss();
        }

        hideDimming(isAnimated);

        View bottomView = dimmingAdapter.getBottomView();

        if (isAnimated) {
            bottomView.setTranslationY(0);
            bottomView.animate()
                    .translationY(bottomView.getMeasuredHeight())
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

    public void showDimming(boolean isAnimated) {
        showDimming();

        if (isAnimated) {
            CommonUtils.fadeInView(dimmingAdapter.getDimmingView(), null);
        }
    }

    private void showDimming() {
        dimmingAdapter.getDimmingView().setVisibility(View.VISIBLE);
    }

    public void hideDimming(boolean isAnimated) {
        if (dimmingAdapter.getDimmingView().getVisibility() == View.GONE) {
            return;
        }

        if (!isAnimated) {
            hideDimming();
            return;
        }

        CommonUtils.fadeOutView(dimmingAdapter.getDimmingView(), new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                hideDimming();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                hideDimming();
            }
        });
    }

    private void hideDimming() {
        dimmingAdapter.getDimmingView().setVisibility(View.GONE);
    }

    public void setOnBackgroundClickedListener(OnBackgroundClickedListener onBackgroundClickedListener) {
        this.onBackgroundClickedListener = onBackgroundClickedListener;
    }

    public interface DimmedBottomPopupViewAdapter {
        View getBottomView();

        View getDimmingView();
    }

    public interface OnBackgroundClickedListener {
        void onClick();
    }
}
