package au.com.dealsdirect.ui.controller.splash;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.utils.AppLogger;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.CommonUtils;
import au.com.dealsdirect.utils.IntrospectionUtils;
import au.com.dealsdirect.utils.ScreenUtils;
import butterknife.BindView;

public class SplashScreenController extends BaseController implements SplashScreenMvpView {

    @BindView(R.id.controller_splash_welcome_layout)
    @Nullable
    ViewGroup mWelcomeLayout;

    @BindView(R.id.controller_splash_continue_button)
    ViewGroup mContinueButton;

    @BindView(R.id.controller_splash_layout)
    ViewGroup mSplashLayout;

    @BindView(R.id.controller_splash_logo_layout)
    @Nullable
    ViewGroup mLogoLayout;

    @BindView(R.id.controller_splash_logo_image)
    @Nullable
    View mLogoImage;

    @BindView(R.id.controller_splash_logo_description)
    @Nullable
    View mLogoDescription;

    @Inject
    SplashScreenMvpPresenter<SplashScreenMvpView> mPresenter;

    private Handler delayedProceedHandler = null;

    private final Runnable delayedProceedRunnable = new Runnable() {
        @Override
        public void run() {
            if (isAttached() && isViewAttached() &&
                    mWelcomeLayout != null &&
                    (mPresenter.isInitialLaunch() ||
                            !IntrospectionUtils.verifyIsAppUpdated(getApplicationContext()))) {
                mPresenter.setIsInitialLaunch(false);
                mSplashLayout.setVisibility(View.GONE);
                mWelcomeLayout.setVisibility(View.VISIBLE);

                mSplashLayout.setOnClickListener(null);
                mContinueButton.setOnClickListener(v -> proceed());
            } else {
                proceed();
            }
        }
    };

    // delay is intentional. actual amount is not specified in documentation.
    // this is meant to allow enough time to read the tagline.
    private static final long DELAY = 4500;
    private static final long DELAY_MINIMUM = 1000;

    public SplashScreenController(Bundle args) {
        super(args);
    }

    public static SplashScreenController newInstance() {
        return new SplashScreenController(new BundleBuilder(new Bundle()).build());
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        return inflater.inflate(R.layout.controller_splash_screen, container, false);
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        ScreenUtils.setStatusBarColor(mActivity, R.color.status_bar_splash);

        setUp(view);
    }

    @Override
    protected void setUp(View view) {
        AppLogger.d("splash" + "setup");

        fadeInLogo();
    }

    private void fadeInLogo() {
        if (mLogoImage != null && mLogoDescription != null) {
            fadeInLogoAndDescription();
        } else {
            fadeInLogoLayout();
        }
    }

    private void fadeInLogoLayout() {
        if (mLogoLayout == null) {
            delayedProceed(DELAY);
            return;
        }
        CommonUtils.fadeInView(mLogoLayout, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                final long delay = Math.max(DELAY_MINIMUM, DELAY - animation.getDuration());
                delayedProceed(delay);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                final long delay = Math.max(DELAY_MINIMUM, DELAY - animation.getDuration());
                delayedProceed(delay);
            }
        });
    }

    private void fadeInLogoAndDescription() {
        if (mLogoImage == null) {
            delayedProceed(DELAY);
            return;
        }
        if (mLogoDescription != null) {
            mLogoDescription.setAlpha(0f);
        }
        CommonUtils.fadeInView(mLogoImage, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                if (mLogoDescription != null) {
                    mLogoDescription.setAlpha(1f);
                }
                final long delay = Math.max(DELAY_MINIMUM, DELAY - animation.getDuration());
                delayedProceed(delay);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                fadeInLogoDescription();
            }
        });
    }

    private void fadeInLogoDescription() {
        if (mLogoDescription == null) {
            delayedProceed(DELAY);
            return;
        }
        CommonUtils.fadeInView(mLogoDescription, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                final long delay = Math.max(DELAY_MINIMUM, DELAY - animation.getDuration());
                delayedProceed(delay);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                final long delay = Math.max(DELAY_MINIMUM, DELAY - animation.getDuration());
                delayedProceed(delay);
            }
        });
    }

    private void delayedProceed(long delay) {
        setupSkip();
        if (delayedProceedHandler != null) {
            delayedProceedHandler.removeCallbacks(delayedProceedRunnable);
        }
        delayedProceedHandler = new Handler();
        delayedProceedHandler.postDelayed(delayedProceedRunnable, delay);
    }

    private void proceed() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).splashShownCallback();
        }
    }

    private void setupSkip() {
        if (mSplashLayout == null || mPresenter.isInitialLaunch()) {
            return;
        }
        mSplashLayout.setOnClickListener(v -> {
            if (delayedProceedHandler != null) {
                delayedProceedHandler.removeCallbacks(delayedProceedRunnable);
                delayedProceedHandler = null;
            }
            proceed();
        });
    }
}