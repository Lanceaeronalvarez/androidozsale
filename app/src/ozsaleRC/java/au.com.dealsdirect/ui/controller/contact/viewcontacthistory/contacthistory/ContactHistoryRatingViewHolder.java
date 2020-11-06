package au.com.dealsdirect.ui.controller.contact.viewcontacthistory.contacthistory;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import au.com.dealsdirect.R;
import au.com.dealsdirect.utils.CommonUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * dp Created by Admin on 6/22/17.
 */

public class ContactHistoryRatingViewHolder extends RecyclerView.ViewHolder {

    private final static int THANK_YOU_HIDE_DELAY = 3000;

    private boolean isAnimating = false;

    @BindView(R.id.contact_history_user_satisfaction)
    LinearLayout contactHistoryUserSatisfactionContainer;
    @BindView(R.id.contact_history_thank_you_feedback)
    LinearLayout contactHistoryThankYouContainer;
    @BindView(R.id.contact_history_smile)
    ImageButton contactHistorySmileButton;
    @BindView(R.id.contact_history_neutral)
    ImageButton contactHistoryNeutralBUtton;
    @BindView(R.id.contact_history_sad)
    ImageButton contactHistorySadButton;

    public ContactHistoryRatingViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }

    public void setup(boolean shouldShow, ContactHistoryOnClickRatingListener listener) {
        if (shouldShow) {
            if (!isAnimating) {
                contactHistoryUserSatisfactionContainer.setVisibility(View.VISIBLE);
                contactHistoryThankYouContainer.setVisibility(View.GONE);
            }

            contactHistorySmileButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onClickSmile();
                }
                transitionToThankYou();
            });

            contactHistoryNeutralBUtton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onClickNeutral();
                }
                transitionToThankYou();
            });

            contactHistorySadButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onClickSad();
                }
                transitionToThankYou();
            });
        } else {
            contactHistorySmileButton.setOnClickListener(null);
            contactHistoryNeutralBUtton.setOnClickListener(null);
            contactHistorySadButton.setOnClickListener(null);

            if (!isAnimating) {
                contactHistoryUserSatisfactionContainer.setVisibility(View.GONE);
                contactHistoryThankYouContainer.setVisibility(View.GONE);
            }
        }
    }

    private void transitionToThankYou() {
        isAnimating = true;
        contactHistoryThankYouContainer.setVisibility(View.GONE);
        contactHistoryUserSatisfactionContainer.setVisibility(View.VISIBLE);
        CommonUtils.fadeOutView(contactHistoryUserSatisfactionContainer, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                showThankYou();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                showThankYou();
            }
        });
    }

    private void showThankYou() {
        isAnimating = true;
        contactHistoryThankYouContainer.setVisibility(View.VISIBLE);
        contactHistoryUserSatisfactionContainer.setVisibility(View.GONE);
        CommonUtils.fadeInView(contactHistoryThankYouContainer, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                hideThankYouAfterDelay();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                hideThankYouAfterDelay();
            }
        });
    }

    private void hideThankYouAfterDelay() {
        isAnimating = true;
        contactHistoryThankYouContainer.setVisibility(View.VISIBLE);
        contactHistoryUserSatisfactionContainer.setVisibility(View.GONE);
        new Handler(itemView.getContext().getMainLooper()).postDelayed(() -> {
            CommonUtils.fadeOutView(contactHistoryThankYouContainer, new AnimatorListenerAdapter() {
                @Override
                public void onAnimationCancel(Animator animation) {
                    super.onAnimationCancel(animation);
                    isAnimating = false;
                    setup(false, null);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    isAnimating = false;
                    setup(false, null);
                }
            });
        }, THANK_YOU_HIDE_DELAY);
    }
}
