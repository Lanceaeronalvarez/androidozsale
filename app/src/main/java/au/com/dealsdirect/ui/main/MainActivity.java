package au.com.dealsdirect.ui.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.view.ViewGroup;

import com.bluelinelabs.conductor.Conductor;
import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.ControllerChangeHandler;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.auth.AuthHandler;
import au.com.dealsdirect.ui.base.BaseActivity;
import au.com.dealsdirect.ui.controller.account.AccountController;
import au.com.dealsdirect.ui.controller.categories.CategoriesController;
import au.com.dealsdirect.ui.controller.checkout.CheckoutController;
import au.com.dealsdirect.ui.controller.contact.viewcontacts.ViewContactsController;
import au.com.dealsdirect.ui.controller.home.HomeController;
import au.com.dealsdirect.ui.controller.invite.InviteController;
import au.com.dealsdirect.ui.controller.login.LoginController;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

    @Override
    protected void setUp() {


        mPresenter.initServerSettings(this,
                ((MainPresenter) mPresenter).getDataManager()
                        .getCountryId());
        mPresenter.callGetAppSettingsSection(this,
                ((MainPresenter) mPresenter).getDataManager()
                        .getCountryId());

        BottomNavigationViewHelper.disableShiftMode(mBottomNavigationView);
        mRouter.addChangeListener(new ControllerChangeHandler.ControllerChangeListener() {
            @Override
            public void onChangeStarted(@Nullable Controller to, @Nullable Controller from, boolean isPush, @NonNull ViewGroup container, @NonNull ControllerChangeHandler handler) {

            }

            @Override
            public void onChangeCompleted(@Nullable Controller to, @Nullable Controller from, boolean isPush, @NonNull ViewGroup container, @NonNull ControllerChangeHandler handler) {
                if (to instanceof HomeController || to instanceof ShopsController) {
                    mBottomNavigationView.setSelectedItemId(R.id.action_shop);
                } else if (to instanceof AccountController) {
                    mBottomNavigationView.setSelectedItemId(R.id.action_account);
                }else if (to instanceof ViewContactsController) {
                    mBottomNavigationView.setSelectedItemId(R.id.action_contact);
                }else if (to instanceof InviteController) {
                    mBottomNavigationView.setSelectedItemId(R.id.action_invite);
                }else if (to instanceof CheckoutController) {
                    mBottomNavigationView.setSelectedItemId(R.id.action_checkout);
                }
            }
        });

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


    @Override
    public void showCategoryController() {

        mRouter.pushController(RouterTransaction.with(CategoriesController.newInstance())
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));

    }

    @Override
    public void showShopController() {
        mRouter.setRoot(RouterTransaction.with(new ShopsController())
                .pushChangeHandler(new FadeChangeHandler())
                .popChangeHandler(new FadeChangeHandler()));
    }

    @Override
    public void showAccountController() {
        mRouter.pushController(RouterTransaction.with(AccountController.newInstance())
                .pushChangeHandler(new FadeChangeHandler())
                .popChangeHandler(new FadeChangeHandler()));


    }

    @Override
    public void showContactController() {

        showLoginController(mRouter,new AuthHandler() {
            @Override
            public void success() {

                mRouter.setRoot(RouterTransaction.with(ViewContactsController.newInstance())
                        .tag("Login")
                        .pushChangeHandler(new FadeChangeHandler())
                        .popChangeHandler(new FadeChangeHandler()));

            }

            @Override
            public void error() {

            }
        });

    }

    @Override
    public void showInviteController() {

    }

    @Override
    public void showCheckoutController() {
        mRouter.pushController(RouterTransaction.with(new CheckoutController())
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));
    }

    @Override
    public void showLoginController(Router router, AuthHandler handler) {
        //pinapasa yung router, para kahit childe router man siya ng kung ano mang view, pwedeng siya ang tumawag.

        router.pushController(RouterTransaction.with(LoginController.newInstance(handler))
                .tag("Login")
                .pushChangeHandler(new VerticalChangeHandler(false)) //false, para hindi mag onDestroyView yung view na nag trigger ng login
                .popChangeHandler(new VerticalChangeHandler()));
    }


}
