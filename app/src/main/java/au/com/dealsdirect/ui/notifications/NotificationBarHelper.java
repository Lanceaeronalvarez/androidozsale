package au.com.dealsdirect.ui.notifications;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Map;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.notification.GetNotificationsResponse;
import au.com.dealsdirect.data.network.model.notification.GetNotificationsType;

public class NotificationBarHelper {

    private static final int ANIMATION_DURATION = 250;

    private ViewGroup layout = null;
    private TextView textView = null;
    private Button button = null;

    private ValueAnimator layoutValueAnimator = null;

    private List<GetNotificationsResponse> notifications = null;

    private Map<GetNotificationsType, NotificationDialogHelper.ButtonSelector> dialogButtonSelectors = null;

    public NotificationBarHelper(ViewGroup notificationBarView) {
        setup(notificationBarView);
    }

    private void setup(ViewGroup notificationBarView) {
        layout = notificationBarView;
        textView = notificationBarView.findViewById(R.id.notifications_bar_description);
        button = notificationBarView.findViewById(R.id.notifications_bar_button);

        button.setOnClickListener(v -> {
            HorizontalScrollingNotificationAdapter adapter = new HorizontalScrollingNotificationAdapter();
            adapter.setDataSource(notifications);
            NotificationDialogHelper helper = new NotificationDialogHelper(layout.getContext(), false, false);
            helper.setButtonSelectors(dialogButtonSelectors);
            helper.setAdapter(adapter);
            helper.show();
        });
    }

    public void setNotifications(List<GetNotificationsResponse> notifications, boolean isAnimated) {
        final Context context = textView.getContext();
        if (notifications.isEmpty()) {
            disappear(true);
        } else {
            final CharSequence text = context.getResources().getText(
                    notifications.size() == 1 ?
                            R.string.notifications_use_this_code :
                            R.string.notifications_use_these_codes);
            textView.setText(text);
            appear(true);
        }
        this.notifications = notifications;
    }

    public void setDialogButtonSelectors(Map<GetNotificationsType, NotificationDialogHelper.ButtonSelector> dialogButtonSelectors) {
        this.dialogButtonSelectors = dialogButtonSelectors;
    }

    private void appear(boolean isAnimated) {
        if (layout.getVisibility() == View.VISIBLE) {
            if (layoutValueAnimator != null) {
                layoutValueAnimator.cancel();
            }
            return;
        }

        final Context context = layout.getContext();
        final int height = (int) context.getResources().getDimension(R.dimen.notification_bar_height);

        layout.setVisibility(View.VISIBLE);

        if (!isAnimated) {
            if (layoutValueAnimator != null) {
                layoutValueAnimator.cancel();
            }
            ViewGroup.LayoutParams layoutParams = layout.getLayoutParams();
            layoutParams.height = height;
            layout.setLayoutParams(layoutParams);
            return;
        }

        layoutValueAnimator = ValueAnimator.ofInt(0, height);
        layoutValueAnimator.addUpdateListener(animation -> {
            if (layout == null) {
                animation.cancel();
                return;
            }

            int val = (int) animation.getAnimatedValue();
            ViewGroup.LayoutParams layoutParams = layout.getLayoutParams();
            layoutParams.height = val;
            layout.setLayoutParams(layoutParams);
        });
        layoutValueAnimator.setDuration(ANIMATION_DURATION);
        layoutValueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
                ViewGroup.LayoutParams layoutParams = layout.getLayoutParams();
                layoutParams.height = height;
                layout.setLayoutParams(layoutParams);
                layoutValueAnimator = null;
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animation) {
                ViewGroup.LayoutParams layoutParams = layout.getLayoutParams();
                layoutParams.height = height;
                layout.setLayoutParams(layoutParams);
                layoutValueAnimator = null;
            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {

            }
        });
        layoutValueAnimator.start();
    }

    private void disappear(boolean isAnimated) {
        if (layout.getVisibility() == View.GONE) {
            return;
        }

        final Context context = layout.getContext();
        final int height = (int) context.getResources().getDimension(R.dimen.notification_bar_height);

        if (!isAnimated) {
            if (layoutValueAnimator != null) {
                layoutValueAnimator.cancel();
            }
            layout.setVisibility(View.GONE);
            return;
        }

        layoutValueAnimator = ValueAnimator.ofInt(height, 0);
        layoutValueAnimator.addUpdateListener(animation -> {
            if (layout == null) {
                animation.cancel();
                return;
            }

            int val = (int) animation.getAnimatedValue();
            ViewGroup.LayoutParams layoutParams = layout.getLayoutParams();
            layoutParams.height = val;
            layout.setLayoutParams(layoutParams);
        });
        layoutValueAnimator.setDuration(ANIMATION_DURATION);
        layoutValueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
                layoutValueAnimator = null;
                layout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animation) {
                layoutValueAnimator = null;
                layout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {

            }
        });
        layoutValueAnimator.start();
    }
}
