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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

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
import au.com.dealsdirect.ui.controller.categories.NewSaleCategoriesController;
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

    public static final int SHOP_INDEX = 2;
    public static final int CATEGORY_INDEX = 0;
    public static final int ACCOUNT_INDEX = 4;
    public static final int WISHLIST_INDEX = 3;
    public static final int BRANDS_INDEX = 1;

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

    @BindView(R.id.controller_main_line)
    RelativeLayout mBeigeLine;

    @BindView(R.id.controller_nav_indicator)
    LinearLayout mNavIndicatorView;

    @BindView(R.id.popup_host_frame)
    ViewGroup mPopupHostContainer;

    @BindView(R.id.indicator_1)
    View mIndicator1;

    @BindView(R.id.indicator_2)
    View mIndicator2;

    @BindView(R.id.indicator_3)
    View mIndicator3;

    @BindView(R.id.indicator_4)
    View mIndicator4;

    @BindView(R.id.indicator_5)
    View mIndicator5;

    @BindView(R.id.controller_home_button)
    ImageView mHomeButton;

    private CheckoutMvpView mCheckoutHostController;

    private View mLastSelectedSubCategoryItem;

    private View mPreviousSubcategoryItem;

    private View mLastSelectedCategory;

    private String mChosenSubCategoryItemKey = "";

    private int previousPagerPosition = 2;

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
        mBottomNavigationView.setAccentColor(mActivity.getResources().getColor(R.color.nav_dark_blue));
        mBottomNavigationView.setInactiveColor(mActivity.getResources().getColor(R.color.beige));
        mBottomNavigationView.setCurrentItem(2);
        mBottomNavigationView.getItem(1).setTitle("Brands");
        mBottomNavigationView.getItem(2).setTitle("");
        mBottomNavigationView.getItem(2).setColor(mActivity.getResources().getColor(R.color.nav_dark_blue));

        mHomeButton.setOnClickListener(it -> {
            mBottomNavigationView.setCurrentItem(2);
        });

        if(mPresenter.isTablet()){
            mNavIndicatorView.setVisibility(View.GONE);
        }

        mBottomNavigationView.setOnTabSelectedListener((position, wasSelected) -> {
            switch (position) {
                case SHOP_INDEX:
                    hideIndicators();
                    mIndicator3.setVisibility(View.VISIBLE);
                    showShopController();
                    return true;
                case CATEGORY_INDEX:
                    hideIndicators();
                    mIndicator1.setVisibility(View.VISIBLE);
                    showCategoryController();
                    return true;
                case ACCOUNT_INDEX:
                    hideIndicators();
                    mIndicator5.setVisibility(View.VISIBLE);
                    showAccountController();
                    return true;
                case WISHLIST_INDEX:
                    hideIndicators();
                    mIndicator4.setVisibility(View.VISIBLE);
                    showWishlistController();
                    return true;
                case BRANDS_INDEX:
                    hideIndicators();
                    mIndicator2.setVisibility(View.VISIBLE);
                    showBrandsController();
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

    private void hideIndicators(){
        mIndicator1.setVisibility(View.INVISIBLE);
        mIndicator2.setVisibility(View.INVISIBLE);
        mIndicator3.setVisibility(View.INVISIBLE);
        mIndicator4.setVisibility(View.INVISIBLE);
        mIndicator5.setVisibility(View.INVISIBLE);
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

    private void setupBrandsRouter(Router router) {
        setupBrandsRouter(router, false);
    }

    private void setupBrandsRouter(Router router, boolean willReset) {
        if (!router.hasRootController() || willReset) {
            ShopsController shopsController = ShopsController.instanceWithBrandsOnlyFilter();
            router.setRoot(RouterTransaction.with(shopsController)
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

    public void resetAccountRouter() {
        setupAccountRouter(routers.get(ACCOUNT_INDEX), true);
    }

    public void resetBrandsRouter() {
        setupBrandsRouter(routers.get(BRANDS_INDEX), true);
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
            case BRANDS_INDEX:
                setupBrandsRouter(router);
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

    public Router getBrandsRouter() {
        return routers.get(BRANDS_INDEX);
    }

    public Router getCheckoutRouter() {
        return routers.get(previousPagerPosition);
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
            mBeigeLine.setVisibility(View.GONE);
            mNavIndicatorView.setVisibility(View.GONE);
            mHomeButton.setVisibility(View.GONE);
        }
    }

    public void showBottomNav() {
        if (mBottomNavigationView != null && mBottomNavigationView.getVisibility() == View.GONE) {
            mBottomNavigationView.setVisibility(View.VISIBLE);
            mBottomNavigationView.bringToFront();
            mBeigeLine.setVisibility(View.VISIBLE);
            mHomeButton.setVisibility(View.VISIBLE);
            mHomeButton.bringToFront();
            if(mPresenter.isTablet()){
                mNavIndicatorView.setVisibility(View.GONE);
            }else{
                mNavIndicatorView.setVisibility(View.VISIBLE);
            }
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
        hideIndicators();
        mIndicator3.setVisibility(View.VISIBLE);
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
            if (!mIsInitialSavedInstanceLoad && !(controller instanceof NewSaleCategoriesController)) {
                Router router = routers.get(CATEGORY_INDEX);
                if (router != null) {
                    categoryBackStack();
                }
            }else{
                categoryBackStack();
            }
            if (controller instanceof BaseController) {
                ((BaseController) controller).refreshContents();
            }
        } else {
            setViewPagerItem(CATEGORY_INDEX);
        }

        showNewTagOnCategory(false);

        mIsInitialSavedInstanceLoad = false;
    }

    private void categoryBackStack(){
        Router router = routers.get(CATEGORY_INDEX);
        if (router != null) {
            ArrayList<RouterTransaction> backstack = new ArrayList<>();
            mActivity.getNewSaleCategoriesController().handleBack();
            mActivity.getNewSaleCategoriesController().handleBack();
            backstack.add(router.getBackstack().get(0));
            router.setBackstack(backstack, new HorizontalChangeHandler());
        }
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
    public void showBrandsController() {
        mBottomNavigationView.setCurrentItem(BRANDS_INDEX, false);
        if (previousPagerPosition == BRANDS_INDEX) {
            Controller controller = getCurrentViewPagerController();
            if (!mIsInitialSavedInstanceLoad && !(controller instanceof ShopsController)) {
                Router router = routers.get(BRANDS_INDEX);
                if (router != null) {
                    ArrayList<RouterTransaction> backstack = new ArrayList<>();
                    backstack.add(router.getBackstack().get(0));
                    router.setBackstack(backstack, new HorizontalChangeHandler());
                }
            }
            if (controller instanceof BaseController) {
                ((BaseController) controller).refreshContents();
            }
        } else {
            setViewPagerItem(BRANDS_INDEX);
        }

        mIsInitialSavedInstanceLoad = false;
    }

    @Override
    public void showBasketItemCount() {
        mActivity.getBrandsController().updateBasketItemsQuantity(CartUtil.getCartValue());
        mActivity.getShopController().updateBasketItemsQuantity(CartUtil.getCartValue());
        mActivity.getNewSaleCategoriesController().updateBasketItemsQuantity(CartUtil.getCartValue());
        mActivity.getWishlistController().updateBasketItemsQuantity(CartUtil.getCartValue());
        mActivity.getSaleItemsControllerFromShop().updateBasketItemsQuantity(CartUtil.getCartValue());
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
                .setBackgroundColor(ContextCompat.getColor(mActivity, R.color.fluorescent_blue))
                .setTextColor(ContextCompat.getColor(mActivity, R.color.black))
                .build();
        mBottomNavigationView.setNotification(notification, WISHLIST_INDEX);

        wishlistCount = count;
    }

    public void updateBasketItemsQuantity() {
        mPresenter.callGetBasketItemsQuantity();
    }

    public void removeBasketItemCount() {
        mActivity.getBrandsController().updateBasketItemsQuantity(0);
        mActivity.getShopController().updateBasketItemsQuantity(0);
        mActivity.getNewSaleCategoriesController().updateBasketItemsQuantity(0);
        mActivity.getWishlistController().updateBasketItemsQuantity(0);
        mBottomNavigationView.setNotification("", BRANDS_INDEX);
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
        return mHomeViewPager.getCurrentItem() == previousPagerPosition;
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
                case BRANDS_INDEX:
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
                .setBackgroundColor(ContextCompat.getColor(mActivity, R.color.fluorescent_blue))
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

    public void showHomePage(){
        mBottomNavigationView.setCurrentItem(2);
    }

    public void showBrands(){
        mBottomNavigationView.setCurrentItem(1);
    }
}
