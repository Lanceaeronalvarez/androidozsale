package au.com.dealsdirect.ui.controller.login;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;

/**
 * Created by smartwave on 11/06/2018.
 */

public class LoginHostController extends BaseController implements LoginHostMvpView {

    @BindView(R.id.login_frame_container)
    FrameLayout mLoginFrameContainer;

    //router to hold login/registration router
    private Router mAuthenticationRouter;

    public static LoginHostController newInstance() {
        return new LoginHostController(new BundleBuilder(new Bundle())
                .build());
    }

    public LoginHostController(Bundle args){
        super(args);
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_login_host,container,false);
        getControllerComponent().inject(this);
        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    @Override
    protected void setUp(View view) {
        mAuthenticationRouter = getChildRouter(mLoginFrameContainer);
        GateKeeper.setRoot(mAuthenticationRouter,GateKeeper.Destination.LOGIN, RouterTransaction.with(LoginController.newInstance()));
    }

    @OnClick(R.id.dialog_background)
    public void dismissDialog() {
        handleBack();
    }

    @Override
    public boolean handleBack() {
        mActivity.loginErrorHandler("");
        return super.handleBack();
    }
}
