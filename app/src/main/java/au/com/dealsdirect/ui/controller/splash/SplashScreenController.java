package au.com.dealsdirect.ui.controller.splash;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import javax.inject.Inject;

import au.com.dealsdirect.BuildConfig;
import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.main.Settings;
import au.com.dealsdirect.ui.controller.searchfilter.SearchFilterMvpPresenter;
import au.com.dealsdirect.ui.controller.searchfilter.SearchFilterMvpView;
import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.utils.AppLogger;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.IntrospectionUtils;
import au.com.dealsdirect.utils.ScreenUtils;
import butterknife.BindView;

/**
 * Created by Paul on 7/21/17.
 */
public class SplashScreenController extends BaseController implements SplashScreenMvpView {

    @BindView(R.id.controller_splash_app_logo)
    ImageView mSplashLogoImageView;

    @BindView(R.id.controller_splash_logo_layout)
    RelativeLayout mLogoLayout;
    @BindView(R.id.controller_splash_welcome_layout)
    LinearLayout mWelcomeLayout;

    @BindView(R.id.controller_splash_continue_button)
    RelativeLayout mContinueButton;

    @BindView(R.id.controller_splash_description)
    TextView mSplashDescription;
    @BindView(R.id.controller_splash_welcome)
    TextView mSplashWelcomeText;
    @BindView(R.id.controller_splash_description_1)
    TextView mSplashDescription1;
    @BindView(R.id.controller_splash_description_2)
    TextView mSplashDescription2;
    @BindView(R.id.controller_splash_description_3)
    TextView mSplashDescription3;

    @BindView(R.id.controller_splash_layout)
    RelativeLayout mSplashLayout;

    @Inject
    SplashScreenMvpPresenter<SplashScreenMvpView> mPresenter;

    static int delayMillis = 4500;

    public SplashScreenController(Bundle args) {
        super(args);
    }

    public static SplashScreenController newInstance() {
        return new SplashScreenController(new BundleBuilder(new Bundle()).build());
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_splash_screen, container, false);
        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        ScreenUtils.setStatusBarColor(mActivity,R.color.status_bar_splash);

        setUp(view);
    }

    @Override
    protected void setUp(View view) {
        AppLogger.d("splash" + "setup");

        if (mPresenter.isInitialLaunch() || !IntrospectionUtils.verifyIsAppUpdated(getApplicationContext())) {

            mPresenter.setIsInitialLaunch(false);
            new Handler().postDelayed(() -> {
                mLogoLayout.setVisibility(View.GONE);
                mWelcomeLayout.setVisibility(View.VISIBLE);

                mContinueButton.setOnClickListener(v -> {
                    if (getActivity() != null)
                        ((MainActivity) getActivity()).splashShownCallback();
                });
            }, delayMillis);

        } else {

            new Handler().postDelayed(() -> {
                if (getActivity() != null)
                    ((MainActivity) getActivity()).splashShownCallback();
            }, delayMillis);

        }

    }
}