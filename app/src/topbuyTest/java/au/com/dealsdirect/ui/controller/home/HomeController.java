package au.com.dealsdirect.ui.controller.home;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import au.com.dealsdirect.ui.base.BaseController;

/**
 * Created by smartwave on 30/10/2017.
 */

public class HomeController extends BaseController implements HomeMvpView {
    @Override
    protected void setUp(View view) {

    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        return null;
    }

    @Override
    public void showShopController() {

    }

    @Override
    public void showAccountController() {

    }

    @Override
    public void showContactController() {

    }

    @Override
    public void showInviteController() {

    }

    @Override
    public void showCheckoutController() {

    }

    @Override
    public void updateBasketItemCount() {

    }

    public HomeMvpPresenter<HomeMvpView> getPresenter() {
        return null;
    }

    public boolean isCheckoutRouterVisible() {
        return false;
    }

}
