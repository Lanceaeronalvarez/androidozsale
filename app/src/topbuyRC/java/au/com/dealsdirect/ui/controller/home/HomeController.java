package au.com.dealsdirect.ui.controller.home;

import androidx.annotation.NonNull;
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
    public void showFirstTabController() {

    }

    @Override
    public void showSecondTabController() {

    }

    @Override
    public void showThirdTabController() {

    }

    @Override
    public void showFourthTabController() {

    }

    @Override
    public void showFifthTabController() {

    }

    @Override
    public void updateBasketItemCount() {

    }

    @Override
    public boolean isPopUpControllerVisible() {
        return false;
    }

    public HomeMvpPresenter<HomeMvpView> getPresenter() {
        return null;
    }

    public boolean isCheckoutRouterVisible() {
        return false;
    }
}
