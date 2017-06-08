package au.com.dealsdirect.ui.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bluelinelabs.conductor.Conductor;
import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.base.BaseActivity;
import au.com.dealsdirect.ui.categories.CategoriesController;
import au.com.dealsdirect.ui.controller.shops.ShopsController;
import au.com.dealsdirect.ui.controller.shops.changehandler.SharedElementTransitionChangehandler;
import au.com.dealsdirect.ui.custom.BottomNavigationViewHelper;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements MainMvpView {

    @Inject
    MainMvpPresenter<MainMvpView> mPresenter;

    @BindView(R.id.controller_container)
    ViewGroup mContainer;

    @BindView(R.id.partial_toolbar_header_text)
    TextView mHeaderTitle;

    @BindView(R.id.partial_toolbar_logo)
    ImageView mHeaderLogo;

    @BindView(R.id.controller_shop_bottom_navigation)
    BottomNavigationView mBottomNavigationView;

    private int mPreviousTab = R.id.action_shop;
    private int mCurrentTab = R.id.action_shop;

    private Router mRouter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        getActivityComponent().inject(this);

        setUnBinder(ButterKnife.bind(this));

        mPresenter.onAttach(this);

        mRouter = Conductor.attachRouter(this, mContainer, savedInstanceState);
        if (!mRouter.hasRootController()) {
            mRouter.setRoot(RouterTransaction.with(new ShopsController()));

        }

        setUp();
    }

    @Override
    protected void setUp() {
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
    protected void onDestroy() {
        mPresenter.onDetach();
        super.onDestroy();
    }


    @Override
    public void onBackPressed() {
        if (!mRouter.handleBack()) {
            super.onBackPressed();
        }
    }

    @Override public void showCategoryController() {

        mRouter.pushController(RouterTransaction.with(
                        new CategoriesController())
                                 .pushChangeHandler(new SharedElementTransitionChangehandler())
                                 .popChangeHandler(new FadeChangeHandler()));

    }

    @Override public void showShopController() {
        mRouter.setRoot(RouterTransaction.with(new ShopsController()));
    }

    @Override public void showAccountController() {
        mRouter.setRoot(RouterTransaction.with(new ShopsController()));
    }

    @Override public void showContactController() {
        mRouter.setRoot(RouterTransaction.with(new ShopsController()));
    }

    @Override public void showInviteController() {
        mRouter.setRoot(RouterTransaction.with(new ShopsController()));
    }

    @Override public void showCheckoutController() {
        mRouter.setRoot(RouterTransaction.with(new ShopsController()));
    }

    @Override public void showController(Controller controller) {
        mRouter.setRoot(RouterTransaction.with(new CategoriesController()));
    }

    @Override public void setHeaderTitle(String title) {
        mHeaderTitle.setText(title);
        mHeaderLogo.setVisibility(View.GONE);
    }

//    @OnClick(R.id.provide_toolbar_nav_icon)
//    void userClicked(){
//        showCategoryController();
//
//    }

}
