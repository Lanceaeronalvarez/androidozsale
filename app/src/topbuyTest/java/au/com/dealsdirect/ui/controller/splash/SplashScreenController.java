package au.com.dealsdirect.ui.controller.splash;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;

/**
 * Created by Paul on 7/21/17.
 */
public class SplashScreenController extends BaseController implements SplashScreenMvpView {

    @Inject
    SplashScreenMvpPresenter<SplashScreenMvpView> mPresenter;

    @BindView(R.id.controller_splash_app_logo)
    ImageView mSplashLogoImageView;

    public SplashScreenController(Bundle args) {
        super(args);
    }

    public static SplashScreenController newInstance() {
        return new SplashScreenController(new BundleBuilder(new Bundle()).build());
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_splash_screen, container, false);
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);

        setUp(view);
    }

    @Override
    protected void setUp(View view) {
        new Handler().postDelayed(() -> {
            if(!mActivity.isFinishing()) {
                if (mPresenter.isInitialLaunch()) {
                    GateKeeper.push(
                            getRouter(),
                            GateKeeper.Destination.TUTORIAL,
                            new BundleBuilder(new Bundle())
                                    .putBoolean(BundleKeys.FROM_MY_ACCOUNTS, false)
                                    .build(),
                            new FadeChangeHandler(false),
                            new VerticalChangeHandler());
                    mPresenter.setIsInitialLaunch(false);
                } else {
                    mActivity.splashShownCallback();
                }
            }
        }, 5000);
    }
}
