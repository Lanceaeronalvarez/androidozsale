package au.com.dealsdirect.ui.controller.splash;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.utils.AppLogger;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.IntrospectionUtils;
import au.com.dealsdirect.utils.ScreenUtils;
import butterknife.BindView;

public class SplashScreenController extends BaseController implements SplashScreenMvpView {

    @BindView(R.id.controller_splash_welcome_layout)
    RelativeLayout mWelcomeLayout;

    @BindView(R.id.controller_splash_continue_button)
    RelativeLayout mContinueButton;

    @BindView(R.id.controller_splash_layout)
    RelativeLayout mSplashLayout;

    @Inject
    SplashScreenMvpPresenter<SplashScreenMvpView> mPresenter;

    // delay is intentional. actual amount is not specified in documentation.
    // this is meant to allow enough time to read the tagline.
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
        ScreenUtils.setStatusBarColor(mActivity, R.color.status_bar_splash);

        setUp(view);
    }

    @Override
    protected void setUp(View view) {
        AppLogger.d("splash" + "setup");

        if (mPresenter.isInitialLaunch() || !IntrospectionUtils.verifyIsAppUpdated(getApplicationContext())) {

            mPresenter.setIsInitialLaunch(false);
            new Handler().postDelayed(() -> {
                mSplashLayout.setVisibility(View.GONE);
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