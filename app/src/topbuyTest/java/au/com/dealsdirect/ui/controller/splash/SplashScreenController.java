package au.com.dealsdirect.ui.controller.splash;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;

/**
 * Created by Paul on 7/21/17.
 */
public class SplashScreenController extends BaseController {

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
        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getActivity().getResources().getColor(R.color.activered));
        }

        setUp(view);
    }

    @Override
    protected void setUp(View view) {

        new Handler().postDelayed(() -> {
            if (getActivity() != null) mActivity.splashShownCallback();
//            GateKeeper.push(
//                    getRouter(),
//                    GateKeeper.Destination.TUTORIAL,
//                    new BundleBuilder(new Bundle())
//                            .putBoolean(BundleKeys.FROM_MY_ACCOUNTS, false)
//                            .build(),
//                    new VerticalChangeHandler(false),
//                    new VerticalChangeHandler());

        }, 5000);
    }
}
