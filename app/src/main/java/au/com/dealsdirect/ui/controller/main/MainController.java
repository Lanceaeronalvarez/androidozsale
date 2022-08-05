package au.com.dealsdirect.ui.controller.main;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter;
import com.aurelhubert.ahbottomnavigation.notification.AHNotification;
import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.bluelinelabs.conductor.support.RouterPagerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.auth.AuthHandler;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.account.AccountController;
import au.com.dealsdirect.ui.controller.bannerfilter.BannerFiltersController;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutDetailsMapper;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutMvpView;
import au.com.dealsdirect.ui.controller.checkout.checkouthost.CheckoutHostController;
import au.com.dealsdirect.ui.controller.saleitemdetails.SaleItemDetailsController;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsController;
import au.com.dealsdirect.ui.controller.shops.ShopsController;
import au.com.dealsdirect.ui.custom.transitions.SharedArcFadePopChangeHandler;
import au.com.dealsdirect.ui.custom.transitions.SharedArcFadePushChangeHandler;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.CartUtil;
import au.com.dealsdirect.utils.CommonUtils;
import au.com.dealsdirect.utils.DelayedMethodExecutionManager;
import au.com.dealsdirect.utils.module.ControllerFactory;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;

/**
 * dp Created by Admin on 6/6/17.
 */

public class MainController extends BaseController implements MainMvpView {

    public static final String TAG = "Home";

    public static final int SHOP_INDEX = 0;
    public static final int CATEGORY_INDEX = 1;
    public static final int ACCOUNT_INDEX = 2;
    public static final int WISHLIST_INDEX = 3;
    public static final int CHECKOUT_INDEX = 4;

    private static final int VIEWPAGER_SIZE = 5;

    private static final String KEY_HAS_SAVED_INSTANCE = "KEY_HAS_SAVED_INSTANCE";

    private int wishlistCount = 0;

    @SuppressLint("UseSparseArrays")
    private HashMap<Integer, Router> routers = new HashMap<>();

    private Router mPopUpHostRouter;

    @Inject
    MainMvpPresenter<MainMvpView> mPresenter;

    @BindView(R.id.home_viewpager)
    MainCustomViewPager mHomeViewPager;

    @BindView(R.id.controller_home_bottom_nav)
    AHBottomNavigation mBottomNavigationView;

    @BindView(R.id.popup_host_frame)
    ViewGroup mPopupHostContainer;

    private CheckoutMvpView mCheckoutHostController;

    private View mLastSelectedSubCategoryItem;

    private View mPreviousSubcategoryItem;

    private View mLastSelectedCategory;

    private String mChosenSubCategoryItemKey = "";

    private int previousPagerPosition = 0;

    private boolean mHasSavedStateInstance;
    public static boolean mIsInitialSavedInstanceLoad;

    private boolean mShouldBottomNavigationViewEnabled = true;

    public static MainController newInstance() {
        return new MainController(
                new BundleBuilder(new Bundle())
                        .build());
    }

    public static MainController newInstance(Bundle bundle) {
        return new MainController(bundle);
    }

    public MainController(Bundle args) {
        super(args);
    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);
        mPresenter.onAttach(this);

        if (mHasSavedStateInstance) {
            refreshAllTopControllers();
        }

        mActivity.fetchCachedResponses();

        DelayedMethodExecutionManager.getInstance()
                .executeDelayedMethodCalls(this.getClass().getName());
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {

        View view = inflater.inflate(R.layout.controller_main, container, false);
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);

        DelayedMethodExecutionManager.getInstance()
                .executeDelayedMethodCalls(this.getClass().getName());

        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);

        AHBottomNavigationAdapter navigationAdapter = new AHBottomNavigationAdapter(mActivity, R.menu.bottom_navigation_menu);
        navigationAdapter.setupWithBottomNavigation(mBottomNavigationView);
        mBottomNavigationView.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        mBottomNavigationView.setDefaultBackgroundColor(mActivity.getResources().getColor(R.color.bottom_nav_background));
        mBottomNavigationView.setAccentColor(mActivity.getResources().getColor(R.color.bottom_nav_accent));
        mBottomNavigationView.setInactiveColor(mActivity.getResources().getColor(R.color.bottom_nav_inactive));

        mBottomNavigationView.setOnTabSelectedListener((position, wasSelected) -> {
            switch (position) {
                case SHOP_INDEX:
                    showShopController();
                    return true;
                case CATEGORY_INDEX:
                    showCategoryController();
                    return true;
                case ACCOUNT_INDEX:
                    showAccountController();
                    return true;
                case WISHLIST_INDEX:
                    showWishlistController();
                    return true;
                case CHECKOUT_INDEX:
                    showCheckoutController();
                    return true;
                default:
                    return false;
            }
        });

//        ADD "NEW" Badge to categories

        if (mPresenter.isInitialLaunch()) {
            mPresenter.setInitialLaunchFalse();
        }

        setUp(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        mHomeViewPager.setOnTouchListener(null);
        super.onDestroyView(view);
    }

    @Override
    protected void setUp(View view) {

        mActivity.setMainController(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

        mPopUpHostRouter = getChildRouter(mPopupHostContainer);
        CommonControllerChangeListener.addToRouter(mPopUpHostRouter);
        mPopUpHostRouter.setPopsLastView(true);

        setupViewPager();
    }

    private void setupShopRouter(Router router) {
        setupShopRouter(router, false);
    }

    private void setupShopRouter(Router router, boolean willReset) {
        if (!router.hasRootController() || willReset) {
            router.setBackstack(
                    Arrays.asList(
                            RouterTransaction.with(BannerFiltersController.newInstance())
                                    .popChangeHandler(new HorizontalChangeHandler())
                                    .pushChangeHandler(new HorizontalChangeHandler()),
                            RouterTransaction.with(ShopsController.newInstance()).tag(ShopsController.TAG)
                                    .popChangeHandler(new HorizontalChangeHandler())
                                    .pushChangeHandler(new HorizontalChangeHandler())),
                    null);
        }
    }

    private void setupCategoryRouter(Router router) {
        setupCategoryRouter(router, false);
    }

    private void setupCategoryRouter(Router router, boolean willReset) {
        if (!router.hasRootController() || willReset) {
            Controller categoryController = mActivity.getResources().getBoolean(R.bool.should_use_old_category_layout) ?
                    ControllerFactory.getInstance(GateKeeper.Destination.CATEGORIES) : ControllerFactory.getInstance(GateKeeper.Destination.SALECATEGORY);

            router.setRoot(RouterTransaction.with(categoryController)
                    .popChangeHandler(new HorizontalChangeHandler())
                    .pushChangeHandler(new HorizontalChangeHandler()));
        }
    }

    private void setupCheckoutRouter(Router router) {
        setupCheckoutRouter(router, false);
    }

    private void setupCheckoutRouter(Router router, boolean willReset) {
        Controller controller;

        if (!mHasSavedStateInstance || mActivity.getCheckoutController() == null || willReset) {
            controller = mPresenter.isTablet() ?
                    ControllerFactory.getInstance(GateKeeper.Destination.CHECKOUT_HOST) :
                    ControllerFactory.getInstance(GateKeeper.Destination.CHECKOUT);
        } else {
            controller = mActivity.getCheckoutController();
        }

        if (controller instanceof CheckoutMvpView) {
            mCheckoutHostController = (CheckoutMvpView) controller;
        }

        if (!router.hasRootController() || willReset) {
            router.setRoot(RouterTransaction
                    .with(controller).tag(controller.getClass().getName()));
        }
    }

    private void setupAccountRouter(Router router) {
        setupAccountRouter(router, false);
    }

    private void setupAccountRouter(Router router, boolean willReset) {
        if (!router.hasRootController() || willReset) {
            Controller accountController = ControllerFactory.getInstance(GateKeeper.Destination.ACCOUNT);
            router.setRoot(RouterTransaction.with(accountController)
                    .popChangeHandler(new HorizontalChangeHandler())
                    .pushChangeHandler(new HorizontalChangeHandler()));
        }
    }

    private void setupWishlistRouter(Router router) {
        setupWishlistRouter(router, false);
    }

    private void setupWishlistRouter(Router router, boolean willReset) {
        if (!router.hasRootController() || willReset) {
            SaleItemsController wishlistController = (SaleItemsController) ControllerFactory.getInstance(GateKeeper.Destination.SALEITEMS);
            wishlistController.setSourceMode(SaleItemsController.SourceMode.WISHLIST);
            router.setRoot(RouterTransaction.with(wishlistController)
                    .popChangeHandler(new HorizontalChangeHandler()));
        }
    }

    public void resetShopRouter() {
        setupShopRouter(routers.get(SHOP_INDEX), true);
    }

    public void resetCategoriesRouter() {
        setupCategoryRouter(routers.get(CATEGORY_INDEX), true);
    }

    public void resetWishlistRouter() {
        setupWishlistRouter(routers.get(WISHLIST_INDEX), true);
    }

    public void resetCheckoutRouter() {
        setupCheckoutRouter(routers.get(CHECKOUT_INDEX), true);
    }

    public void resetAccountRouter() {
        setupAccountRouter(routers.get(ACCOUNT_INDEX), true);
    }

    private void setupRouterAtPosition(Router router, int position) {
        routers.put(position, router);

        switch (position) {
            case SHOP_INDEX:
                setupShopRouter(router);
                break;
            case CATEGORY_INDEX:
                setupCategoryRouter(router);
                break;
            case ACCOUNT_INDEX:
                setupAccountRouter(router);
                break;
            case WISHLIST_INDEX:
                setupWishlistRouter(router);
                break;
            case CHECKOUT_INDEX:
                setupCheckoutRouter(router);
                break;
            default:
                return;
        }

        CommonControllerChangeListener.addToRouter(router);
    }

    public Router getShopRouter() {
        return routers.get(SHOP_INDEX);
    }

    public Router getCategoriesRouter() {
        return routers.get(CATEGORY_INDEX);
    }

    public Router getWishlistRouter() {
        return routers.get(WISHLIST_INDEX);
    }

    public Router getAccountRouter() {
        return routers.get(ACCOUNT_INDEX);
    }

    public Router getCheckoutRouter() {
        return routers.get(CHECKOUT_INDEX);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupViewPager() {

        RouterPagerAdapter mViewPagerAdapter = new RouterPagerAdapter(this) {
            @Override
            public void configureRouter(@NonNull Router router, int position) {
                setupRouterAtPosition(router, position);
            }

            @Override
            public int getCount() {
                return VIEWPAGER_SIZE;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return "Page " + position;
            }
        };


        mHomeViewPager.setAdapter(mViewPagerAdapter);
        mHomeViewPager.setCurrentItem(SHOP_INDEX, false);
        mHomeViewPager.setMyScroller();

        mHomeViewPager.setOffscreenPageLimit(VIEWPAGER_SIZE);

        mHomeViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == previousPagerPosition) {
                    return;
                }
                onPageSwitch(previousPagerPosition, false);
                onPageSwitch(position, true);
                previousPagerPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mHomeViewPager.setIsSwipeable(false);
    }

    private void onPageSwitch(int position, boolean isCurrent) {
        Router router = routers.get(position);
        if (router == null) {
            return;
        }
        Controller controller = getCurrentControllerOnRouter(router);
        if (controller instanceof BaseController &&
                ((BaseController) controller).isViewAttached()) {
            if (isCurrent) {
                ((BaseController) controller).refreshContents();
            }
            ((BaseController) controller).onTabSwitch(isCurrent);
        }
    }

    public boolean shouldBottomNavigationViewEnabled() {
        return mShouldBottomNavigationViewEnabled;
    }

    public void setShouldBottomNavigationViewEnabled(boolean enabled) {
        mShouldBottomNavigationViewEnabled = enabled;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(BundleKeys.KEY_HAS_SAVED_INSTANCE, true);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mHasSavedStateInstance = savedInstanceState.getBoolean(BundleKeys.KEY_HAS_SAVED_INSTANCE);
        mIsInitialSavedInstanceLoad = savedInstanceState.getBoolean(KEY_HAS_SAVED_INSTANCE);
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

    public void setChosenCategoryItemKey(String key) {
        mChosenSubCategoryItemKey = key;
    }

    public void setSelectedSubCategoryItem(View view) {
        mLastSelectedSubCategoryItem = view;
    }

    public View getSelectedSubCategoryItem() {
        return mLastSelectedSubCategoryItem;
    }

    public void setPreviousSubcategoryItem(View view) {
        mPreviousSubcategoryItem = view;
    }

    public View getPreviousSubcategoryItem() {
        return mPreviousSubcategoryItem;
    }

    public View getLastSelectedCategory() {
        return mLastSelectedCategory;
    }

    public void setLastSelectedCategory(View lastSelectedCategory) {
        this.mLastSelectedCategory = lastSelectedCategory;
    }

    public String getChosenCategoryItemKey() {
        return mChosenSubCategoryItemKey;
    }

    public MainCustomViewPager getHomeViewPager() {
        return mHomeViewPager;
    }

    private void setViewPagerItem(int position) {
        if (mHomeViewPager.getCurrentItem() == position) {
            return;
        }

        CommonUtils.fadeOutView(mHomeViewPager, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                mHomeViewPager.setCurrentItem(position, false);
                CommonUtils.fadeInView(mHomeViewPager, null);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mHomeViewPager.setCurrentItem(position, false);
                CommonUtils.fadeInView(mHomeViewPager, null);
            }
        });
    }

    @Override
    public void showShopController() {
        mBottomNavigationView.setCurrentItem(SHOP_INDEX, false);
        if (previousPagerPosition == SHOP_INDEX) {
            Controller controller = getCurrentViewPagerController();
            if (!mIsInitialSavedInstanceLoad && !(controller instanceof ShopsController)) {
                Router router = routers.get(SHOP_INDEX);
                if (router != null) {
                    ArrayList<RouterTransaction> backstack = new ArrayList<>();
                    backstack.add(router.getBackstack().get(0));
                    if (router.getBackstackSize() == 1) {
                        backstack.add(RouterTransaction.with(ShopsController.newInstance())
                                .pushChangeHandler(new HorizontalChangeHandler())
                                .popChangeHandler(new HorizontalChangeHandler()));
                    } else {
                        backstack.add(router.getBackstack().get(1));
                    }
                    router.setBackstack(backstack, new HorizontalChangeHandler());
                }
            }
            if (controller instanceof BaseController) {
                ((BaseController) controller).refreshContents();
            }
        } else {
            setViewPagerItem(SHOP_INDEX);
        }

        mIsInitialSavedInstanceLoad = false;
    }

    @Override
    public void showCategoryController() {
        mBottomNavigationView.setCurrentItem(CATEGORY_INDEX, false);
        if (previousPagerPosition == CATEGORY_INDEX) {
            Controller controller = getCurrentViewPagerController();
            if (controller instanceof BaseController) {
                ((BaseController) controller).refreshContents();
            }
        } else {
            setViewPagerItem(CATEGORY_INDEX);
        }

        showNewTagOnCategory(false);

        mIsInitialSavedInstanceLoad = false;
    }

    @Override
    public void showAccountController() {
        mBottomNavigationView.setCurrentItem(ACCOUNT_INDEX, false);
        if (previousPagerPosition == ACCOUNT_INDEX) {
            Controller controller = getCurrentViewPagerController();
            if (controller instanceof BaseController) {
                ((BaseController) controller).refreshContents();
            }
        } else {
            setViewPagerItem(ACCOUNT_INDEX);
        }

        mIsInitialSavedInstanceLoad = false;
    }

    @Override
    public void showContactUsController() {
        // do nothing
    }

    @Override
    public void showCheckoutController() {
        mBottomNavigationView.setCurrentItem(CHECKOUT_INDEX, false);
        if (previousPagerPosition != CHECKOUT_INDEX) {
            setViewPagerItem(CHECKOUT_INDEX);
        }

        if (!mActivity.isAuthorized() && !mIsInitialSavedInstanceLoad) {
            mActivity.showLoginController(routers.get(CHECKOUT_INDEX), new AuthHandler() {
                @Override
                public void success() {
                    resetCheckoutRouter();
                    setViewPagerItem(CHECKOUT_INDEX);
                }

                @Override
                public void error() {

                }
            });
        } else if (mActivity.isAuthorized() &&
                mCheckoutHostController != null && !mCheckoutHostController.isCartLoading()) {
            mCheckoutHostController.loadCart();
        }

        mIsInitialSavedInstanceLoad = false;
    }

    @Override
    public void showWishlistController() {
        mBottomNavigationView.setCurrentItem(WISHLIST_INDEX, false);
        if (previousPagerPosition == WISHLIST_INDEX) {
            Controller controller = getCurrentViewPagerController();
            if (controller instanceof BaseController) {
                ((BaseController) controller).refreshContents();
            }
        } else {
            setViewPagerItem(WISHLIST_INDEX);
        }

        if (!mPresenter.hasWishlistBeenAccessed()) {
            mPresenter.setHasWishlistBeenAccessed(true);
            showWishlistItemCount(wishlistCount);
        }

        mIsInitialSavedInstanceLoad = false;
    }

    @Override
    public void showBasketItemCount() {
        if (CartUtil.getCartValue() == 0) {
            removeBasketItemCount();
        } else {
            AHNotification notification = new AHNotification.Builder()
                    .setText(Integer.toString(CartUtil.getCartValue()))
                    .setBackgroundColor(ContextCompat.getColor(mActivity, R.color.bottom_nav_badge))
                    .setTextColor(ContextCompat.getColor(mActivity, R.color.white))
                    .build();
            mBottomNavigationView.setNotification(notification, CHECKOUT_INDEX);
        }
    }

    @Override
    public void showWishlistItemCount(int count) {
        if (mPresenter == null || mBottomNavigationView == null) {
            DelayedMethodExecutionManager.getInstance()
                    .queueDelayedMethodCall(
                            this.getClass().getName(),
                            "showWishlistItemCount",
                            () -> showWishlistItemCount(count));
            return;
        }

        String text;
        if (count == 0) {
            text = "";
        } else {
            text = Integer.toString(count);
        }
        AHNotification notification = new AHNotification.Builder()
                .setText(text)
                .setBackgroundColor(ContextCompat.getColor(mActivity, R.color.bottom_nav_badge))
                .setTextColor(ContextCompat.getColor(mActivity, R.color.white))
                .build();
        mBottomNavigationView.setNotification(notification, WISHLIST_INDEX);

        wishlistCount = count;
    }

    public void updateBasketItemsQuantity() {
        mPresenter.callGetBasketItemsQuantity();
    }

    public void removeBasketItemCount() {
        mBottomNavigationView.setNotification("", CHECKOUT_INDEX);
    }

    @Override
    public boolean isPopUpControllerVisible() {
        return mPopUpHostRouter != null && mPopUpHostRouter.getBackstackSize() >= 1;
    }

    @Override
    public void backClick() {
        mActivity.onBackPressed();
    }

    public void setSavedCurrentItem() {
        if (mIsInitialSavedInstanceLoad)
            mBottomNavigationView.setCurrentItem(mHomeViewPager.getCurrentItem(), false);
    }

    public AHBottomNavigation getBottomNav() {
        return mBottomNavigationView;
    }

    public boolean isCheckoutPageVisible() {
        return mHomeViewPager.getCurrentItem() == CHECKOUT_INDEX;
    }

    public CheckoutHostController getCheckoutHostController() {
        if (mCheckoutHostController instanceof CheckoutHostController) {
            return (CheckoutHostController) mCheckoutHostController;
        } else {
            return null;
        }
    }

    public Controller getCurrentViewPagerController() {
        return getCurrentControllerOnRouter(routers.get(mHomeViewPager.getCurrentItem()));
    }

    public Router getCurrentRouter() {
        if (mPresenter == null) {
            return null;
        }
        if (mPresenter.isTablet() && isPopUpControllerVisible()) {
            return mPopUpHostRouter;
        } else {
            return routers.get(mHomeViewPager.getCurrentItem());
        }
    }

    public Controller getCurrentControllerOnRouter(Router router) {
        if (router == null) {
            return null;
        }
        int topIndex = router.getBackstackSize() - 1;
        if (topIndex >= 0) {
            return router.getBackstack().get(topIndex).controller();
        }

        return null;
    }

    public Router getPopUpHostRouter() {
        return mPopUpHostRouter;
    }

    private void refreshAllTopControllers() {
        for (int i = 0; i < VIEWPAGER_SIZE; i++) {
            Router router = routers.get(i);
            Controller controller = getCurrentControllerOnRouter(router);
            if (controller instanceof BaseController) {
                ((BaseController) controller).refreshContents();
            }
        }
    }

    public void goToPreviousContainerFromLogin(boolean isAuthorized) {
        int newIndex = mHomeViewPager.getCurrentItem();
        if (!isAuthorized) {
            switch (mHomeViewPager.getCurrentItem()) {
                case CHECKOUT_INDEX:
                    newIndex = previousPagerPosition;
                    break;
                default:
                    break;
            }
        }

        setViewPagerItem(newIndex);

        showBottomNav();
    }

    private void addControllerOnShopRouterBackStack(RouterTransaction routerTransaction) {
        Router router = routers.get(SHOP_INDEX);

        if (router == null) {
            return;
        }

        ArrayList<RouterTransaction> backstack = new ArrayList<>();
        backstack.add(router.getBackstack().get(0));
        backstack.add(RouterTransaction.with(ShopsController.newInstance())
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));
        backstack.add(routerTransaction);
        router.setBackstack(backstack, new HorizontalChangeHandler());
    }

    //----------------

    public void deepLinkSaleItemDetails(String seoIdentifierId, String skuId, boolean isWithSale) {
        SaleItemDetailsController.Parameters.FromDeepLink parameters = new SaleItemDetailsController.Parameters
                .FromDeepLink(seoIdentifierId, skuId);

        mHomeViewPager.setCurrentItem(SHOP_INDEX, false);

        RouterTransaction routerTransaction = RouterTransaction.with(
                SaleItemDetailsController.newInstance(parameters));

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            routerTransaction = routerTransaction
                    .pushChangeHandler(new FadeChangeHandler(false))
                    .popChangeHandler(new FadeChangeHandler());
        } else {
            routerTransaction = routerTransaction
                    .pushChangeHandler(new SharedArcFadePushChangeHandler())
                    .popChangeHandler(new SharedArcFadePopChangeHandler());
        }

        addControllerOnShopRouterBackStack(routerTransaction);
        showShopController();
    }

    public void openLocationFilterHash(String locationFilterHash) {

        SaleItemsController.Parameters.FromLocationFilterHash parameters = new SaleItemsController
                .Parameters.FromLocationFilterHash(locationFilterHash);

        SaleItemsController controller = SaleItemsController.newInstance(parameters);

        RouterTransaction routerTransaction = RouterTransaction.with(controller)
                .tag(mActivity.getString(R.string.sale_items_controller_tag))
                .popChangeHandler(new HorizontalChangeHandler());

        addControllerOnShopRouterBackStack(routerTransaction);
        showShopController();
    }

    public void deepLinkSaleItems(String bannerTitle, String saleId, String bannerId) {

        SaleItemsController.Parameters.FromSaleItemDeepLink parameters = new SaleItemsController
                .Parameters.FromSaleItemDeepLink(bannerTitle, saleId, bannerId);

        SaleItemsController controller = SaleItemsController.newInstance(parameters);

        RouterTransaction routerTransaction = RouterTransaction.with(controller)
                .tag(mActivity.getString(R.string.sale_items_controller_tag))
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler());

        addControllerOnShopRouterBackStack(routerTransaction);
        showShopController();
    }

    public void deepLinkSaleCategory(String categoryName, String categoryIdentifier) {

        SaleItemsController.Parameters.FromCategoryDeepLink parameters = new SaleItemsController
                .Parameters.FromCategoryDeepLink(categoryName, categoryIdentifier);

        SaleItemsController controller = SaleItemsController.newInstance(parameters);

        RouterTransaction routerTransaction = RouterTransaction.with(controller)
                .tag(mActivity.getString(R.string.sale_items_controller_tag))
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler());

        addControllerOnShopRouterBackStack(routerTransaction);
        showShopController();
    }

    public void sendSaleItemToCheckout(CheckoutDetailsMapper getCurrentOrder) {
        mActivity.getCheckoutController().getPresenter().updateCartValues(getCurrentOrder);
    }


    public void setNavigationBarEnabled(boolean enabled) {
        for (int i = 0; i < VIEWPAGER_SIZE; i++) {
            if (enabled) {
                mBottomNavigationView.enableItemAtPosition(i);
            } else {
                mBottomNavigationView.disableItemAtPosition(i);
            }
        }
    }

    private void showNewTagOnCategory(boolean show) {
        AHNotification notification = new AHNotification.Builder()
                .setText(show ? "NEW" : "")
                .setBackgroundColor(ContextCompat.getColor(mActivity, R.color.bottom_nav_badge))
                .setTextColor(ContextCompat.getColor(mActivity, R.color.white))
                .build();
        mBottomNavigationView.setNotification(notification, CATEGORY_INDEX);
    }

    public void showReturnPolicy() {
        showAccountController();
        getAccountRouter().popToRoot();
        final Controller controller = getAccountRouter().getBackstack().get(0).controller();
        if (controller instanceof AccountController) {
            ((AccountController) controller).showReturnsPolicy();
        }
    }
}
