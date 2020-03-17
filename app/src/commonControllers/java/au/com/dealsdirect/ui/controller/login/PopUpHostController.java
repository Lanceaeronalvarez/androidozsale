package au.com.dealsdirect.ui.controller.login;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.afterpay.AfterpayViewController;
import au.com.dealsdirect.ui.controller.checkout.ourpay.OurpaySMSVerificationController;
import au.com.dealsdirect.ui.controller.checkout.paymentsuccess.PaymentSuccessController;
import au.com.dealsdirect.ui.controller.gdpr.StrictConsentController;
import au.com.dealsdirect.ui.controller.saleitemdetails.SaleItemDetailsController;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by smartwave on 11/06/2018.
 */

public class PopUpHostController extends BaseController implements PopUpHostMvpView {

    @Nullable
    @BindView(R.id.child_frame_container)
    FrameLayout mChildFrameContainer;

    //router to hold login/registration router
    private Router mPopUpHostChildRouter;

    GateKeeper.Destination mDestination;

    public static PopUpHostController newInstance() {
        return new PopUpHostController(new BundleBuilder(new Bundle()).build());
    }


    public PopUpHostController(Bundle args) {
        super(args);
        mDestination = (GateKeeper.Destination) args.getSerializable(BundleKeys.KEY_POP_UP_HOST_DESTINATION);
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_pop_up_host, container, false);
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
        mPopUpHostChildRouter = getChildRouter(mChildFrameContainer);
        switch (mDestination) {
            case LOGIN:
                GateKeeper.setRoot(mPopUpHostChildRouter, GateKeeper.Destination.LOGIN, RouterTransaction.with(LoginController.newInstance()));
                break;
            case PAYMENT_SUCCESS:
                GateKeeper.setRoot(mPopUpHostChildRouter, GateKeeper.Destination.PAYMENT_SUCCESS, RouterTransaction.with(new PaymentSuccessController(getArgs())));
                break;
            case SMS_VERIFICATION:
                GateKeeper.setRoot(mPopUpHostChildRouter, GateKeeper.Destination.SMS_VERIFICATION, RouterTransaction.with(new OurpaySMSVerificationController(getArgs())));
                break;
            case STRICT_CONSENT_UI:
                GateKeeper.setRoot(mPopUpHostChildRouter, GateKeeper.Destination.STRICT_CONSENT_UI, RouterTransaction.with(StrictConsentController.newInstance()));
                break;
            case AFTERPAY:
                mPopUpHostChildRouter.setRoot(RouterTransaction.with(new AfterpayViewController(getArgs())));
            default:
                break;
        }
    }

    @Nullable
    @OnClick(R.id.dialog_background)
    void dismissDialog() {

        if (mDestination == GateKeeper.Destination.STRICT_CONSENT_UI) {
            return;
        }

        mPopUpHostChildRouter.handleBack();
        getRouter().popController(this);
        int routerStackSize = mActivity.getCurrentRouter().getBackstackSize();
        Controller previousController = mActivity.getCurrentRouter().getBackstack().get(routerStackSize - 1).controller();
        mActivity.setDraggableViewPager(mDestination == GateKeeper.Destination.LOGIN && !(previousController instanceof SaleItemDetailsController));
    }

    @Override
    public boolean handleBack() {
        if (mPopUpHostChildRouter.getBackstackSize() == 1) {
            dismissDialog();
            return true;
        }

        return super.handleBack();
    }

    public Router getPopUpHostChildRouter() {
        return mPopUpHostChildRouter;
    }
}
