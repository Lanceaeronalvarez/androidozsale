package au.com.dealsdirect.ui.controller.home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.base.BaseController;

import javax.inject.Inject;

import au.com.dealsdirect.ui.categories.CategoriesController;
import au.com.dealsdirect.ui.controller.shops.ShopsController;
import au.com.dealsdirect.ui.custom.BottomNavigationViewHelper;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;

/**
 * dp Created by Admin on 6/6/17.
 */

public class HomeController extends BaseController implements HomeMvpView {

    public static final String TAG = "HomeController";

    private static final String KEY_TEXT = "HomeController.KEY_TEXT";

    @Inject
    HomeMvpPresenter<HomeMvpView> mPresenter;

    @BindView(R.id.controller_home_frame)
    FrameLayout mFrameLayout;

    @BindView(R.id.controller_home_bottom_nav)
    BottomNavigationView mBottomNavigationView;

    private int mPreviousTab = R.id.action_shop;
    private int mCurrentTab = R.id.action_shop;

    private Router mChildRouter;


    public static HomeController newInstance() {

        return new HomeController(
                new BundleBuilder(new Bundle())
                        .build());
    }

    public HomeController(Bundle args) {
        super(args);
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {

        View view = inflater.inflate(R.layout.controller_home, container, false);
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
    protected void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @Override
    protected void setUp(View view) {

        mChildRouter = getChildRouter(mFrameLayout).setPopsLastView(false);
        if (!mChildRouter.hasRootController()) {
            mChildRouter.setRoot(RouterTransaction.with(new ShopsController()));
        }

        BottomNavigationViewHelper.disableShiftMode(mBottomNavigationView);

        mBottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        if (mBottomNavigationView.getSelectedItemId() == item.getItemId()) {
                            return true;
                        }

                        mPreviousTab = mCurrentTab;
                        mCurrentTab = item.getItemId();

                        switch (item.getItemId()) {

                            case R.id.action_shop:
                                showShopController();
                                break;

                            case R.id.action_account:
                                showAccountController();
                                break;

                            case R.id.action_contact:
                                showContactController();
                                break;

                            case R.id.action_invite:
                                showInviteController();
                                break;

                            case R.id.action_checkout:
                                showCheckoutController();
                                break;
                        }
                        return true;
                    }
                });
    }


    @Override
    public void showCategoryController() {
        getChildRouter(mFrameLayout).pushController(RouterTransaction.with(CategoriesController.newInstance())
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));
    }

    @Override
    public void showShopController() {
        getChildRouter(mFrameLayout).pushController(RouterTransaction.with(new ShopsController())
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));
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
}
