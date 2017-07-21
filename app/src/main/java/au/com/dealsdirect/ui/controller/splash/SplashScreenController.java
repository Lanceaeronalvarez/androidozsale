package au.com.dealsdirect.ui.controller.splash;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bluelinelabs.conductor.RouterTransaction;
import com.bumptech.glide.Glide;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.home.HomeController;
import au.com.dealsdirect.ui.controller.main.MainController;
import au.com.dealsdirect.utils.AppLogger;
import au.com.dealsdirect.utils.BundleBuilder;
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

        setUp(view);
    }

    @Override
    protected void setUp(View view) {

        AppLogger.d("splash"+"setup");

        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                AppLogger.d("splash"+"popcontroller");
                getRouter().popController(SplashScreenController.this);

            }
        }, 5000);
    }
}
