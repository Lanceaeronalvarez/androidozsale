package au.com.dealsdirect.ui.controller.home;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter;
import com.aurelhubert.ahbottomnavigation.notification.AHNotification;
import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.ControllerChangeHandler;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.mysale.genie.utility.config.model.getappsettings.Checkout;

import java.util.HashMap;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.auth.AuthHandler;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.account.AccountController;
import au.com.dealsdirect.ui.controller.account.AccountMvpView;
import au.com.dealsdirect.ui.controller.categories.CategoriesController;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutController;
import au.com.dealsdirect.ui.controller.checkout.paymentsuccess.PaymentSuccessController;
import au.com.dealsdirect.ui.controller.contact.viewcontacts.ViewContactsController;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsController;
import au.com.dealsdirect.ui.controller.shops.ShopsController;
import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.CartUtil;
import au.com.dealsdirect.utils.module.ControllerFactory;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;

/**
 * dp Created by Admin on 6/6/17.
 */

public class HomeController extends BaseController implements HomeMvpView {

    public static final String TAG = "HomeController";

    private static final int TAB_SHOP_INDEX = 0;
    private static final int TAB_CATEGORIES_INDEX = 1;
    private static final int TAB_ACCOUNT_INDEX = 2;
    private static final int TAB_CONTACT_INDEX = 3;
    private static final int TAB_CHECKOUT_INDEX = 4;

    @Inject
    HomeMvpPresenter<HomeMvpView> mPresenter;

    @Inject
    MainActivity mActivity;

    @BindView(R.id.product_details_add_to_cart)
    ImageView mImageAddToCartAnimation;

    @BindView(R.id.controller_first_frame)
    ViewGroup mShopContainer;

    @BindView(R.id.controller_second_frame)
    ViewGroup mCategoriesContainer;

    @BindView(R.id.controller_third_frame)
    ViewGroup mContactContainer;

    @BindView(R.id.controller_fourth_frame)
    ViewGroup mAccountsContainer;

    @BindView(R.id.controller_fifth_frame)
    ViewGroup mCheckoutContainer;

    @BindView(R.id.login_host_frame)
    ViewGroup mLoginHostContainer;

    @BindView(R.id.controller_home_bottom_nav)
    AHBottomNavigation mBottomNavigationView;

    private HashMap<Integer, Pair<Router, ViewGroup>> mRouterContainerMapping;

    private Router mShopRouter;
    private Router mCategoriesRouter;
    private Router mContactRouter;
    private Router mAccountsRouter;
    private Router mCheckoutRouter;

    private Router mLoginHostRouter;

    public Router getAccountsRouter() {
        return mAccountsRouter;
    }

    private int mPreviousTab = R.id.action_shop;
    private int mCurrentTab = R.id.action_shop;
    private int currentVisibleIndex = 1;
    private int previousVisibleIndex = 0;

    private boolean isLoginVisible = false;

    public static HomeController newInstance() {

        return new HomeController(
                new BundleBuilder(new Bundle())
                        .build());
    }

    public HomeController(Bundle args) {
        super(args);
    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);
        mPresenter.onAttach(this);
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

        initControllers(true);

        mRouterContainerMapping = new HashMap<>();
        mRouterContainerMapping.put(TAB_SHOP_INDEX, new Pair<>(mShopRouter, mShopContainer));
        mRouterContainerMapping.put(TAB_CATEGORIES_INDEX, new Pair<>(mCategoriesRouter, mCategoriesContainer));
        mRouterContainerMapping.put(TAB_ACCOUNT_INDEX, new Pair<>(mAccountsRouter, mAccountsContainer));
        mRouterContainerMapping.put(TAB_CONTACT_INDEX, new Pair<>(mContactRouter, mContactContainer));
        mRouterContainerMapping.put(TAB_CHECKOUT_INDEX, new Pair<>(mCheckoutRouter, mCheckoutContainer));
        setVisibleContainer(TAB_SHOP_INDEX);

        mActivity.setHomeRouter(mShopRouter);
        AHBottomNavigationAdapter navigationAdapter = new AHBottomNavigationAdapter(getActivity(), R.menu.bottom_navigation_menu);
        navigationAdapter.setupWithBottomNavigation(mBottomNavigationView);
        mBottomNavigationView.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        mBottomNavigationView.setCurrentItem(TAB_SHOP_INDEX);
        mBottomNavigationView.setDefaultBackgroundColor(getResources().getColor(R.color.bottom_nav_background));
        mBottomNavigationView.setAccentColor(getResources().getColor(R.color.bottom_nav_accent));
        mBottomNavigationView.setInactiveColor(getResources().getColor(R.color.bottom_nav_inactive));
        setUp(view);
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    public HomeMvpPresenter<HomeMvpView> getPresenter() {
        return mPresenter;
    }

    @Override
    protected void setUp(View view) {
        if (mPresenter.isAuthorized()) {
            mPresenter.callGetBasketItemsQuantity();
        }

        mShopRouter.addChangeListener(new ControllerChangeHandler.ControllerChangeListener() {
            @Override
            public void onChangeStarted(@Nullable Controller to, @Nullable Controller from, boolean isPush, @NonNull ViewGroup container, @NonNull ControllerChangeHandler handler) {

            }

            @Override
            public void onChangeCompleted(@Nullable Controller to, @Nullable Controller from, boolean isPush, @NonNull ViewGroup container, @NonNull ControllerChangeHandler handler) {
                if (mBottomNavigationView == null) return;

                if (to instanceof HomeController || to instanceof ShopsController) {
                    mBottomNavigationView.setCurrentItem(TAB_SHOP_INDEX);
                } else if (to instanceof CategoriesController) {
                    mBottomNavigationView.setCurrentItem(TAB_CATEGORIES_INDEX);
                } else if (to instanceof AccountController) {
                    mBottomNavigationView.setCurrentItem(TAB_ACCOUNT_INDEX);
                } else if (to instanceof ViewContactsController) {
                    mBottomNavigationView.setCurrentItem(TAB_CONTACT_INDEX);
                } else if (to instanceof CheckoutController) {
                    mBottomNavigationView.setCurrentItem(TAB_CHECKOUT_INDEX);
                }
            }
        });

        mBottomNavigationView.setOnTabSelectedListener((position, wasSelected) -> {
            if (mCheckoutRouter != null) {
                Controller controller = getCurrentControllerOnRouter(mCheckoutRouter);
                if (controller instanceof CheckoutController) {
                    Log.d("ourpay", "home controller remove ourpay");
                    ((CheckoutController) controller).setIsGraphVisible(false);
                    ((CheckoutController) controller).removeOurpayView();
                    ((CheckoutController) controller).clearOurpayGraphBitmapsAndListeners();
                }
            }

            if (!wasSelected) {

                Controller checkoutController = getCurrentControllerOnRouter(mCheckoutRouter);
                if (position != TAB_CATEGORIES_INDEX && checkoutController instanceof PaymentSuccessController) {
                    mCheckoutRouter.popToRoot();
                }

                switch (position) {
                    case TAB_SHOP_INDEX:
                        showFirstTabController();
                        break;
                    case TAB_CATEGORIES_INDEX:
                        showSecondTabController();
                        break;
                    case TAB_ACCOUNT_INDEX:
                        showThirdTabController();
                        break;
                    case TAB_CONTACT_INDEX:
                        mActivity.setDraggableViewPager(false);
                        showFourthTabController();
                        break;
                    case TAB_CHECKOUT_INDEX:
                        mActivity.setDraggableViewPager(false);
                        showFifthTabController();
                        break;
                    default:
                        break;

                }
            } else {
                if (position == TAB_SHOP_INDEX) {
                    mShopRouter.popToRoot();
                } else if (position == TAB_ACCOUNT_INDEX) {
                    mAccountsRouter.popToRoot();
                }
            }
            return true;
        });

    }

    public void initControllers(boolean includeShop) {
        if (includeShop) {
            ShopsController shopsController = ShopsController.newInstance();
            mActivity.setShopController(shopsController);
            mShopRouter = getChildRouter(mShopContainer);
            mShopRouter.setRoot(RouterTransaction.with(shopsController).tag(ShopsController.TAG));
        }

        mCategoriesRouter = getChildRouter(mCategoriesContainer);
        mCategoriesRouter.setRoot(RouterTransaction.with(ControllerFactory.getInstance(GateKeeper.Destination.CATEGORIES)));

        mContactRouter = getChildRouter(mContactContainer);
        mContactRouter.setRoot(RouterTransaction.with(ViewContactsController.newInstance()));

        mAccountsRouter = getChildRouter(mAccountsContainer);
        mActivity.setAccountsRouter(mAccountsRouter);

        mAccountsRouter.setRoot(RouterTransaction.with(AccountController.newInstance()));


        mCheckoutRouter = getChildRouter(mCheckoutContainer);
        mCheckoutRouter.setRoot(RouterTransaction.with(CheckoutController.newInstance())
                .tag(getActivity().getResources().getString(R.string.checkout_controller)));
    }

    public void resetContactsRouter() {
        mContactRouter = getChildRouter(mContactContainer);
        mContactRouter.setRoot(RouterTransaction.with(ControllerFactory.getInstance(GateKeeper.Destination.CONTACT_US)));

    }

    public void resetCheckoutRouter() {
        mCheckoutRouter = getChildRouter(mCheckoutContainer);
        mCheckoutRouter.setRoot(RouterTransaction.with(CheckoutController.newInstance())
                .tag(getActivity().getResources().getString(R.string.checkout_controller)));
    }

    @Override
    public void showFirstTabController() {

        if (mShopRouter != null) {
            Controller controller = getCurrentControllerOnRouter(mShopRouter);
            if (controller instanceof ShopsController) {
                ((MainActivity) getActivity()).getMainController().setViewpagerDraggable(true);
            }

            if (controller instanceof SaleItemsController) {
                mActivity.onBackPressed();
            }
        }

        setVisibleContainer(TAB_SHOP_INDEX);
    }

    @Override
    public void showSecondTabController() {
        mActivity.getMainController().setViewpagerDraggable(false);
        setVisibleContainer(TAB_CATEGORIES_INDEX);
    }

    @Override
    public void showThirdTabController() {
        setVisibleContainer(TAB_ACCOUNT_INDEX);
        mActivity.getMainController().setViewpagerDraggable(false);

        int size = mAccountsRouter.getBackstack().size();
        if (mAccountsRouter.getBackstack().get(size - 1).controller() instanceof AccountMvpView)
            ((AccountMvpView) mAccountsRouter.getBackstack().get(size - 1).controller()).initLoginDrawable();
    }

    @Override
    public void showFourthTabController() {
        setVisibleContainer(TAB_CONTACT_INDEX);
        mActivity.getMainController().setViewpagerDraggable(false);
        if (!mActivity.isAuthorized()) {
            mActivity.showLoginController(mContactRouter, new AuthHandler() {
                @Override
                public void success() {
                    resetRouters();
                }

                @Override
                public void error() {

                }
            });
        }
    }

    @Override
    public void showFifthTabController() {
        setVisibleContainer(TAB_CHECKOUT_INDEX);
        mActivity.getMainController().setViewpagerDraggable(false);

        if (!mActivity.isAuthorized()) {
            mActivity.showLoginController(mCheckoutRouter, new AuthHandler() {
                @Override
                public void success() {
                    resetRouters();
                }

                @Override
                public void error() {

                }
            });
        } else { //should load cart everytime checkout is clicked on bottom nav
            Controller controller = getCurrentControllerOnRouter(mCheckoutRouter);
            if (controller instanceof CheckoutController) {
                ((CheckoutController) controller).loadCart();
                ((CheckoutController) controller).setIsGraphVisible(true);
            }
        }
    }

    public boolean isAccountsActive() {
        return mAccountsContainer != null && mAccountsContainer.isShown();
    }

    public boolean isShopActive() {
        return mShopContainer != null && mShopContainer.isShown();
    }

    @Override
    public void updateBasketItemCount() {
        if (CartUtil.getCartValue() == 0) {
            removeBasketItemCount();
        } else {
            AHNotification notification = new AHNotification.Builder()
                    .setText(CartUtil.getCartValue() + "")
                    .setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.bottom_nav_badge_color))
                    .setTextColor(ContextCompat.getColor(getActivity(), R.color.white))
                    .build();
            getBottomNavigationView().setNotification(notification, TAB_CHECKOUT_INDEX);
        }
    }

    public void removeBasketItemCount() {
        getBottomNavigationView().setNotification("", TAB_CHECKOUT_INDEX);
    }


    public void hideBottomNav() {
        if (mBottomNavigationView != null) {
            mBottomNavigationView.setVisibility(View.GONE);
        }
    }

    public void showBottomNav() {
        if (mBottomNavigationView != null && mBottomNavigationView.getVisibility() == View.GONE) {
            mBottomNavigationView.setVisibility(View.VISIBLE);
            mBottomNavigationView.bringToFront();
        }
    }

    public void setVisibleContainer(int index) {
        mRouterContainerMapping.get(currentVisibleIndex).second.setVisibility(View.GONE);
        previousVisibleIndex = currentVisibleIndex;
        mRouterContainerMapping.get(index).second.setVisibility(View.VISIBLE);
        mBottomNavigationView.setCurrentItem(index, false);
        currentVisibleIndex = index;
    }

    public void setShopRouterViewPagerDraggable() {
        if (mShopRouter != null) {
            Controller controller = getCurrentControllerOnRouter(mShopRouter);
            if (controller instanceof ShopsController) {
                ((MainActivity) getActivity()).getMainController().setViewpagerDraggable(true);
            }
        }
    }

    public void resetVisibleContainer() {

        if (!(getCurrentRouter() == mAccountsRouter || getCurrentRouter() == mShopRouter)) {
            mRouterContainerMapping.get(currentVisibleIndex).second.setVisibility(View.GONE);
            mRouterContainerMapping.get(previousVisibleIndex).second.setVisibility(View.VISIBLE);
            mBottomNavigationView.setCurrentItem(previousVisibleIndex, false);
            currentVisibleIndex = previousVisibleIndex;

        } else if (getCurrentRouter() == mShopRouter) {
            showBottomNav();
        }
    }

    @Override
    public void onDetach(View view) {
        super.onDetach(view);
    }

    public Router getCurrentRouter() {
        return mRouterContainerMapping.get(currentVisibleIndex).first;
    }

    public Controller getCurrentControllerOnRouter(Router router) {
        int topIndex = router.getBackstackSize() - 1;
        if (topIndex >= 0) {
            return router.getBackstack().get(topIndex).controller();
        }

        return null;
    }

    public ImageView getAddToCartImage() {
        return mImageAddToCartAnimation;
    }

    public Router getCheckoutRouter() {
        return mCheckoutRouter;
    }

    public void resetRouters() {
        resetContactsRouter();
        resetCheckoutRouter();
    }

    public int getSelectedBottomNavTab() {
        return currentVisibleIndex;
    }

    public boolean isCheckoutRouterVisible() {
        Log.d("ourpay", "current item = " + mBottomNavigationView.getCurrentItem());
        return mBottomNavigationView.getCurrentItem() == TAB_CHECKOUT_INDEX;
    }

    public AHBottomNavigation getBottomNavigationView() {
        return mBottomNavigationView;
    }

    public void animateBottomNav(int verticalAdjustmentPixels) {

        if (Math.abs(verticalAdjustmentPixels) > 0) {
            mActivity.getHomeController().showBottomNav();
            mActivity.getHomeController().getBottomNavigationView().restoreBottomNavigation(true);

        } else {
            mActivity.getHomeController().hideBottomNav();
            mActivity.getHomeController().getBottomNavigationView().hideBottomNavigation(true);
        }
    }

    public void initLoginHostController() {
        mLoginHostContainer.setVisibility(View.VISIBLE);
        mLoginHostRouter = getChildRouter(mLoginHostContainer);
        mLoginHostRouter.setPopsLastView(true);
    }
}
