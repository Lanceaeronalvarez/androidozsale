package au.com.dealsdirect.ui.main;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.view.ViewGroup;

import com.bluelinelabs.conductor.Conductor;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.base.BaseActivity;
import au.com.dealsdirect.ui.controller.account.AccountController;
import au.com.dealsdirect.ui.controller.categories.CategoriesController;
import au.com.dealsdirect.ui.controller.home.HomeController;
import au.com.dealsdirect.ui.controller.shops.ShopsController;
import au.com.dealsdirect.ui.custom.BottomNavigationViewHelper;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements MainMvpView {

    @Inject
    MainMvpPresenter<MainMvpView> mPresenter;

    @BindView(R.id.activity_main_frame)
    ViewGroup mContainer;

    @BindView(R.id.controller_home_bottom_nav)
    BottomNavigationView mBottomNavigationView;

    private int mPreviousTab = R.id.action_shop;
    private int mCurrentTab = R.id.action_shop;


    private Router mRouter;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        getActivityComponent().inject(this);

        setUnBinder(ButterKnife.bind(this));

        mPresenter.onAttach(this);

        mRouter = Conductor.attachRouter(this, mContainer, savedInstanceState);
        if (!mRouter.hasRootController()) {
            mRouter.setRoot(RouterTransaction.with(HomeController.newInstance())
                                             .tag("Home"));
        }

        setUp();
    }

    @Override protected void setUp() {


        mPresenter.initServerSettings(this,
                                      ((MainPresenter) mPresenter).getDataManager()
                                                                  .getCountryId());
        mPresenter.callGetAppSettingsSection(this,
                                             ((MainPresenter) mPresenter).getDataManager()
                                                                         .getCountryId());

        BottomNavigationViewHelper.disableShiftMode(mBottomNavigationView);

        mBottomNavigationView.setOnNavigationItemSelectedListener(item -> {

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
        });
    }

    @Override protected void onDestroy() {
        mPresenter.onDetach();
        super.onDestroy();
    }


    @Override public void onBackPressed() {
        if (!mRouter.handleBack()) {
            super.onBackPressed();
        }
    }


    @Override public void showCategoryController() {

        mRouter.pushController(RouterTransaction.with(CategoriesController.newInstance())
                                                .pushChangeHandler(new HorizontalChangeHandler())
                                                .popChangeHandler(new HorizontalChangeHandler()));

    }

    @Override public void showShopController() {
        mRouter.setRoot(RouterTransaction.with(new ShopsController())
                                                .pushChangeHandler(new FadeChangeHandler())
                                                .popChangeHandler(new FadeChangeHandler()));
    }

    @Override public void showAccountController() {
        mRouter.setRoot(RouterTransaction.with(AccountController.newInstance())
                .pushChangeHandler(new FadeChangeHandler())
                .popChangeHandler(new FadeChangeHandler()));

    }

    @Override public void showContactController() {

    }

    @Override public void showInviteController() {

    }

    @Override public void showCheckoutController() {

    }

}
