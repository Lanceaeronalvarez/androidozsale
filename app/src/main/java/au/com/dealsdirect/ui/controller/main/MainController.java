package au.com.dealsdirect.ui.controller.main;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

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
import com.bluelinelabs.conductor.viewpager.RouterPagerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.base.MvpView;
import au.com.dealsdirect.ui.controller.account.AccountController;
import au.com.dealsdirect.ui.controller.account.model.AccountOption;
import au.com.dealsdirect.ui.controller.brands.TopBrandsController;
import au.com.dealsdirect.ui.controller.categories.NewSaleCategoriesController;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutController;
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
import au.com.dealsdirect.utils.ScreenUtils;
import au.com.dealsdirect.utils.StringUtils;
import au.com.dealsdirect.utils.module.ControllerFactory;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;

public class MainController extends BaseController implements MainMvpView {

    public static final String TAG = "Home";

    public static final int SHOP_INDEX = 5;
    public static final int CATEGORY_INDEX = 0;
    public static final int ACCOUNT_INDEX = 4;
    public static final int WISHLIST_INDEX = 3;
    public static final int BRANDS_INDEX = 1;
    public static final int CHECKOUT_INDEX = 2;

    private static final int VIEWPAGER_SIZE = 6;

    private static final String KEY_HAS_SAVED_INSTANCE = "KEY_HAS_SAVED_INSTANCE";

    private static final long INDICATOR_ANIMATION_DURATION = 250;
    private static final long BOTTOM_NAVIGATION_ANIMATION_DURATION = 250;

    private static final boolean WILL_ANIMATE_INDICATOR = true;

    private final Set<ObjectAnimator> indicatorAnimators = new HashSet<>();
    private ValueAnimator bottomNavAnimator = null;

    private boolean isBottomNavHidden = false;

    private int wishlistCount = 0;

    @SuppressLint("UseSparseArrays")
    private HashMap<Integer, Router> routers = new HashMap<>();

    private Router mPopUpHostRouter;

    @Inject
    MainMvpPresenter<MainMvpView> mPresenter;

    @BindView(R.id.home_viewpager)
    MainCustomViewPager mHomeViewPager;

    @BindView(R.id.controller_nav_container)
    ViewGroup mBottomNavigationContainer;

    @BindView(R.id.controller_home_bottom_nav)
    AHBottomNavigation mBottomNavigationView;

    @BindView(R.id.controller_main_line)
    View mBottomNavigationUpperLine;

    @BindView(R.id.controller_nav_indicator)
    View mNavIndicatorView;

    @BindView(R.id.popup_host_frame)
    ViewGroup mPopupHostContainer;

    @BindView(R.id.indicator_slidng)
    View mIndicatorSliding;
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

    private View mLastSelectedSubCategoryItem;

    private View mPreviousSubcategoryItem;

    private View mLastSelectedCategory;

    private String mChosenSubCategoryItemKey = "";

    private int previousPagerPosition = -1;

    private boolean mHasSavedStateInstance;
    public static boolean mIsInitialSavedInstanceLoad;

    private final Map<Integer, View> mIndicators = new HashMap<>();

    private boolean mShouldBottomNavigationViewEnabled = true;

    private Uri deeplinkUriToProcess = null;

    boolean willGoToShopInsteadOfCategories = false;

    private Map<Integer, List<RouterTransaction>> deeplinkBackstack = new HashMap<>();

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
    protected void onRestoreViewState(@NonNull View view, @NonNull Bundle savedViewState) {
        super.onRestoreViewState(view, savedViewState);
        if (mBottomNavigationView != null) {
            resetIndicators(mBottomNavigationView.getCurrentItem());
        }
        if (deeplinkUriToProcess != null) {
            processDeeplinkUri(deeplinkUriToProcess);
            deeplinkUriToProcess = null;
        }
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);

        mIndicators.put(CATEGORY_INDEX, mIndicator1);
        mIndicators.put(BRANDS_INDEX, mIndicator2);
        mIndicators.put(CHECKOUT_INDEX, mIndicator3);
        mIndicators.put(WISHLIST_INDEX, mIndicator4);
        mIndicators.put(ACCOUNT_INDEX, mIndicator5);

        AHBottomNavigationAdapter navigationAdapter = new AHBottomNavigationAdapter(mActivity, R.menu.bottom_navigation_menu);
        navigationAdapter.setupWithBottomNavigation(mBottomNavigationView);
        mBottomNavigationView.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        mBottomNavigationView.setDefaultBackgroundColor(mActivity.getResources().getColor(R.color.bottom_nav_background));
        mBottomNavigationView.setAccentColor(mActivity.getResources().getColor(R.color.bottom_nav_accent));
        mBottomNavigationView.setInactiveColor(mActivity.getResources().getColor(R.color.bottom_nav_inactive));
        readjustBottomNavigationViewLayoutWidth();

        if (!mHasSavedStateInstance) {
            view.setVisibility(View.INVISIBLE);
            // post showHomePage so that it is called after the host controller for the
            // ControllerHostedRouter is set
            (new Handler(view.getContext().getMainLooper()))
                    .post(() -> {
                        view.setVisibility(View.VISIBLE);
                        showHomePage(false);
                    });
        }
        previousPagerPosition = mHomeViewPager.getCurrentItem();

        mHomeButton.setOnClickListener(it -> {
            showHomePage();
        });

        mBottomNavigationView.setOnTabSelectedListener((position, wasSelected) -> {
            switch (position) {
                case SHOP_INDEX:
                    showShopController();
                    return true;
                case CATEGORY_INDEX:
                    if (willGoToShopInsteadOfCategories) {
                        showShopController();
                    } else {
                        showCategoryController();
                    }
                    return true;
                case ACCOUNT_INDEX:
                    showAccountController();
                    return true;
                case WISHLIST_INDEX:
                    showWishlistController();
                    return true;
                case BRANDS_INDEX:
                    showBrandsController();
                    return true;
                case CHECKOUT_INDEX:
                    showCheckoutController();
                    return true;
                default:
                    return false;
            }
        });

        if (mPresenter.isInitialLaunch()) {
            mPresenter.setInitialLaunchFalse();
        }

        setUp(view);

        if (deeplinkUriToProcess != null && !mHasSavedStateInstance) {
            processDeeplinkUri(deeplinkUriToProcess);
            deeplinkUriToProcess = null;
        }
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

    @Override
    public void onOrientationChanged(Configuration newConfiguration) {
        super.onOrientationChanged(newConfiguration);
        if (!isViewAttached()) {
            return;
        }
        readjustBottomNavigationViewLayoutWidth();
    }

    // workaround to exactly wrap content in bottom navigation view
    private void readjustBottomNavigationViewLayoutWidth() {
        if (mActivity == null) {
            return;
        }
        float minWidth = mActivity.getResources().getDimension(R.dimen.bottom_navigation_min_width);
        float maxWidth = mActivity.getResources().getDimension(R.dimen.bottom_navigation_max_width);
        if (mBottomNavigationView.getTitleState() == AHBottomNavigation.TitleState.ALWAYS_SHOW &&
                mBottomNavigationView.getItemsCount() > 3) {
            minWidth = mActivity.getResources().getDimension(R.dimen.bottom_navigation_small_inactive_min_width);
            maxWidth = mActivity.getResources().getDimension(R.dimen.bottom_navigation_small_inactive_max_width);
        }
        final int layoutWidth = ScreenUtils.getScreenWidth(mActivity) - mBottomNavigationContainer.getPaddingLeft() - mBottomNavigationContainer.getPaddingRight();
        float itemWidth = layoutWidth / (float) mBottomNavigationView.getItemsCount();
        if (itemWidth < minWidth) {
            itemWidth = minWidth;
        } else if (itemWidth > maxWidth) {
            itemWidth = maxWidth;
        }
        mBottomNavigationView.getLayoutParams().width = (int) (itemWidth * mBottomNavigationView.getItemsCount());
    }

    private void resetIndicators(int index) {
        if (!isViewBound() || !indicatorAnimators.isEmpty()) {
            return;
        }
        mIndicator1.setVisibility(View.INVISIBLE);
        mIndicator2.setVisibility(View.INVISIBLE);
        mIndicator3.setVisibility(View.INVISIBLE);
        mIndicator4.setVisibility(View.INVISIBLE);
        mIndicator5.setVisibility(View.INVISIBLE);
        View view = mIndicators.get(index);
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }
    }

    private void cancelIndicatorAnimators() {
        final List<ObjectAnimator> arrayCopy = new ArrayList<>(indicatorAnimators);
        for (ObjectAnimator objectAnimator : arrayCopy) {
            objectAnimator.cancel();
        }
    }

    private void animateIndicator(int oldIndex, int newIndex) {
        if (!isViewBound()) {
            return;
        }
        cancelIndicatorAnimators();
        indicatorAnimators.clear();
        mIndicator1.setVisibility(View.INVISIBLE);
        mIndicator2.setVisibility(View.INVISIBLE);
        mIndicator3.setVisibility(View.INVISIBLE);
        mIndicator4.setVisibility(View.INVISIBLE);
        mIndicator5.setVisibility(View.INVISIBLE);
        mIndicatorSliding.setVisibility(View.VISIBLE);
        final View oldIndicator = mIndicators.get(oldIndex);
        final View newIndicator = mIndicators.get(newIndex);

        final float scaleBase = 1f / (float) mIndicators.size();
        final float scaleMax = (Math.abs(newIndex - oldIndex) + 1) * scaleBase;

        final float medianIndex = ((float) mIndicators.size() + 1f) / 2f - 1f;
        final float moveStartX = ((oldIndex - medianIndex) / (float) mIndicators.size()) * (float) mIndicatorSliding.getWidth();
        final float moveEndX = ((newIndex - medianIndex) / (float) mIndicators.size()) * (float) mIndicatorSliding.getWidth();

        if (newIndicator != null) {
            if (oldIndicator == newIndicator) {
                mIndicatorSliding.setVisibility(View.INVISIBLE);
                newIndicator.setVisibility(View.VISIBLE);
            } else if (oldIndicator != null) {
                mIndicatorSliding.setAlpha(1f);

                final ObjectAnimator animateScale = ObjectAnimator.ofFloat(mIndicatorSliding, View.SCALE_X, scaleBase, scaleMax, scaleBase);
                animateScale.setDuration(INDICATOR_ANIMATION_DURATION);
                animateScale.setInterpolator(new AccelerateDecelerateInterpolator());
                animateScale.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(@NonNull Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(@NonNull Animator animator) {
                        mIndicatorSliding.setScaleX(scaleBase);
                        indicatorAnimators.remove(animateScale);
                    }

                    @Override
                    public void onAnimationCancel(@NonNull Animator animator) {
                        mIndicatorSliding.setScaleX(scaleBase);
                        indicatorAnimators.remove(animateScale);
                    }

                    @Override
                    public void onAnimationRepeat(@NonNull Animator animator) {

                    }
                });
                indicatorAnimators.add(animateScale);
                animateScale.start();

                final ObjectAnimator animateMove = ObjectAnimator.ofFloat(mIndicatorSliding, View.X, moveStartX, moveEndX);
                animateMove.setDuration(INDICATOR_ANIMATION_DURATION);
                animateMove.setInterpolator(new AccelerateDecelerateInterpolator());
                animateMove.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(@NonNull Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(@NonNull Animator animator) {
                        mIndicatorSliding.setX(moveEndX);
                        mIndicatorSliding.setVisibility(View.INVISIBLE);
                        newIndicator.setVisibility(View.VISIBLE);
                        indicatorAnimators.remove(animateMove);
                    }

                    @Override
                    public void onAnimationCancel(@NonNull Animator animator) {
                        mIndicatorSliding.setX(moveEndX);
                        mIndicatorSliding.setVisibility(View.INVISIBLE);
                        newIndicator.setVisibility(View.VISIBLE);
                        indicatorAnimators.remove(animateMove);
                    }

                    @Override
                    public void onAnimationRepeat(@NonNull Animator animator) {

                    }
                });
                indicatorAnimators.add(animateMove);
                animateMove.start();
            } else {
                mIndicatorSliding.setX(moveEndX);
                mIndicatorSliding.setScaleX(scaleBase);
                mIndicatorSliding.setAlpha(0f);
                final ObjectAnimator fadeIn = ObjectAnimator.ofFloat(mIndicatorSliding, View.ALPHA, 1f);
                fadeIn.setDuration(INDICATOR_ANIMATION_DURATION);
                fadeIn.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(@NonNull Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(@NonNull Animator animator) {
                        mIndicatorSliding.setAlpha(1f);
                        mIndicatorSliding.setVisibility(View.INVISIBLE);
                        newIndicator.setVisibility(View.VISIBLE);
                        indicatorAnimators.remove(fadeIn);
                    }

                    @Override
                    public void onAnimationCancel(@NonNull Animator animator) {
                        mIndicatorSliding.setAlpha(1f);
                        mIndicatorSliding.setVisibility(View.INVISIBLE);
                        newIndicator.setVisibility(View.VISIBLE);
                        indicatorAnimators.remove(fadeIn);
                    }

                    @Override
                    public void onAnimationRepeat(@NonNull Animator animator) {

                    }
                });
                indicatorAnimators.add(fadeIn);
                fadeIn.start();
            }
        } else {
            if (oldIndicator != null) {
                mIndicatorSliding.setX(moveStartX);
                mIndicatorSliding.setScaleX(scaleBase);
                mIndicatorSliding.setAlpha(1f);
                final ObjectAnimator fadeOut = ObjectAnimator.ofFloat(mIndicatorSliding, View.ALPHA, 0f);
                fadeOut.setDuration(INDICATOR_ANIMATION_DURATION);
                fadeOut.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(@NonNull Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(@NonNull Animator animator) {
                        mIndicatorSliding.setAlpha(0f);
                        mIndicatorSliding.setVisibility(View.INVISIBLE);
                        indicatorAnimators.remove(fadeOut);
                    }

                    @Override
                    public void onAnimationCancel(@NonNull Animator animator) {
                        mIndicatorSliding.setAlpha(0f);
                        mIndicatorSliding.setVisibility(View.INVISIBLE);
                        indicatorAnimators.remove(fadeOut);
                    }

                    @Override
                    public void onAnimationRepeat(@NonNull Animator animator) {

                    }
                });
                indicatorAnimators.add(fadeOut);
                fadeOut.start();
            } else {
                mIndicatorSliding.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void setupShopRouter(Router router) {
        setupShopRouter(router, false);
    }

    private void setupShopRouter(Router router, boolean willReset) {
        if (router == null || (router.hasRootController() && !willReset)) {
            return;
        }

        router.setBackstack(
                Arrays.asList(
                        RouterTransaction.with(ShopsController.newInstance()).tag(ShopsController.TAG)
                                .popChangeHandler(new HorizontalChangeHandler())
                                .pushChangeHandler(new HorizontalChangeHandler())),
                null);
    }

    private void setupCategoryRouter(Router router) {
        setupCategoryRouter(router, false);
    }

    private void setupCategoryRouter(Router router, boolean willReset) {
        if (router == null || (router.hasRootController() && !willReset)) {
            return;
        }

        Controller categoryController = mActivity.getResources().getBoolean(R.bool.should_use_old_category_layout) ?
                ControllerFactory.getInstance(GateKeeper.Destination.CATEGORIES) : ControllerFactory.getInstance(GateKeeper.Destination.SALECATEGORY);
        router.setRoot(RouterTransaction.with(categoryController)
                .popChangeHandler(new HorizontalChangeHandler())
                .pushChangeHandler(new HorizontalChangeHandler()));
    }

    private void setupAccountRouter(Router router) {
        setupAccountRouter(router, false);
    }

    private void setupAccountRouter(Router router, boolean willReset) {
        if (router == null || (router.hasRootController() && !willReset)) {
            return;
        }

        Controller accountController = ControllerFactory.getInstance(GateKeeper.Destination.ACCOUNT);
        router.setRoot(RouterTransaction.with(accountController)
                .tag(AccountController.TAG)
                .popChangeHandler(new HorizontalChangeHandler())
                .pushChangeHandler(new HorizontalChangeHandler()));
    }

    private void setupWishlistRouter(Router router) {
        setupWishlistRouter(router, false);
    }

    private void setupWishlistRouter(Router router, boolean willReset) {
        if (router == null || (router.hasRootController() && !willReset)) {
            return;
        }

        SaleItemsController wishlistController = (SaleItemsController) ControllerFactory.getInstance(GateKeeper.Destination.SALEITEMS);
        wishlistController.setSourceMode(SaleItemsController.SourceMode.WISHLIST);
        router.setRoot(RouterTransaction.with(wishlistController)
                .popChangeHandler(new HorizontalChangeHandler()));
    }

    private void setupBrandsRouter(Router router) {
        setupBrandsRouter(router, false);
    }

    private void setupBrandsRouter(Router router, boolean willReset) {
        if (router == null || (router.hasRootController() && !willReset)) {
            return;
        }

        TopBrandsController brandsController = TopBrandsController.newInstance();
        router.setRoot(RouterTransaction.with(brandsController)
                .popChangeHandler(new HorizontalChangeHandler()));
    }

    private void setupCheckoutRouter(Router router) {
        setupCheckoutRouter(router, false);
    }

    private void setupCheckoutRouter(Router router, boolean willReset) {
        if (router == null || (router.hasRootController() && !willReset)) {
            return;
        }

        Controller controller;

        if (mPresenter.isTablet()) {
            controller = CheckoutHostController.newInstance();
        } else {
            controller = CheckoutController.newInstance();
        }

        router.setRoot(RouterTransaction.with(controller)
                .tag(CheckoutController.TAG)
                .popChangeHandler(new HorizontalChangeHandler()));
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

    public void resetCheckoutRouter() {
        setupCheckoutRouter(routers.get(CHECKOUT_INDEX), true);
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
            case CHECKOUT_INDEX:
                setupCheckoutRouter(router);
                break;
            default:
                return;
        }

        CommonControllerChangeListener.addToRouter(router);

        List<RouterTransaction> backstack = deeplinkBackstack.get(position);
        if (backstack != null) {
            backstack = new ArrayList<>(backstack);
            backstack.add(0, router.getBackstack().get(0));
            router.setBackstack(backstack, new HorizontalChangeHandler());
            deeplinkBackstack.put(position, null);
        }
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
        return routers.get(CHECKOUT_INDEX);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupViewPager() {

        RouterPagerAdapter viewPagerAdapter = new RouterPagerAdapter(this) {
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


        mHomeViewPager.setAdapter(viewPagerAdapter);
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

        mHomeViewPager.setCurrentItem(SHOP_INDEX, false);
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
            mBottomNavigationUpperLine.setVisibility(View.GONE);
            mBottomNavigationContainer.setVisibility(View.GONE);
            mNavIndicatorView.setVisibility(View.GONE);
            mHomeButton.setVisibility(View.GONE);
            isBottomNavHidden = true;
        }
    }

    public void hideBottomNav(boolean isAnimated) {
        if (!isAnimated) {
            hideBottomNav();
        }

        if (bottomNavAnimator != null) {
            bottomNavAnimator.cancel();
            bottomNavAnimator = null;
        }

        if (isBottomNavHidden) {
            return;
        }

        final int height = (int) mActivity.getResources().getDimension(R.dimen.bottom_nav_height);

        final ValueAnimator animator = ValueAnimator.ofInt(height, 1);
        animator.setDuration(BOTTOM_NAVIGATION_ANIMATION_DURATION);
        animator.addUpdateListener(animation -> {
            final int value = (int) animation.getAnimatedValue();
            ViewGroup.LayoutParams layoutParams = mBottomNavigationView.getLayoutParams();
            layoutParams.height = value;
            mBottomNavigationView.setLayoutParams(layoutParams);
        });
        animator.addListener(new Animator.AnimatorListener() {
            private void resetHeight() {
                ViewGroup.LayoutParams layoutParams = mBottomNavigationView.getLayoutParams();
                layoutParams.height = height;
                mBottomNavigationView.setLayoutParams(layoutParams);
            }

            @Override
            public void onAnimationStart(@NonNull Animator animator) {

            }

            @Override
            public void onAnimationEnd(@NonNull Animator animator) {
                hideBottomNav();
                resetHeight();
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animator) {
                hideBottomNav();
                resetHeight();
            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animator) {

            }
        });
        bottomNavAnimator = animator;
        animator.start();
    }

    public void showBottomNav() {
        if (mBottomNavigationView != null) {
            mBottomNavigationView.setVisibility(View.VISIBLE);
            mBottomNavigationView.bringToFront();
            mBottomNavigationUpperLine.setVisibility(View.VISIBLE);
            mBottomNavigationContainer.setVisibility(View.VISIBLE);
            mNavIndicatorView.setVisibility(View.VISIBLE);
            mHomeButton.setVisibility(View.VISIBLE);
            resetIndicators(mBottomNavigationView.getCurrentItem());
            isBottomNavHidden = false;
        }
    }

    public void showBottomNav(boolean isAnimated) {
        if (!isAnimated) {
            showBottomNav();
        }

        if (bottomNavAnimator != null) {
            bottomNavAnimator.cancel();
            bottomNavAnimator = null;
        }

        if (!isBottomNavHidden) {
            return;
        }

        showBottomNav();

        final int height = (int) mActivity.getResources().getDimension(R.dimen.bottom_nav_height);

        final ValueAnimator animator = ValueAnimator.ofInt(1, height);
        animator.setDuration(BOTTOM_NAVIGATION_ANIMATION_DURATION);
        animator.addUpdateListener(animation -> {
            final int value = (int) animation.getAnimatedValue();
            ViewGroup.LayoutParams layoutParams = mBottomNavigationView.getLayoutParams();
            layoutParams.height = value;
            mBottomNavigationView.setLayoutParams(layoutParams);
        });
        animator.addListener(new Animator.AnimatorListener() {
            private void resetHeight() {
                ViewGroup.LayoutParams layoutParams = mBottomNavigationView.getLayoutParams();
                layoutParams.height = height;
                mBottomNavigationView.setLayoutParams(layoutParams);
            }

            @Override
            public void onAnimationStart(@NonNull Animator animator) {

            }

            @Override
            public void onAnimationEnd(@NonNull Animator animator) {
                resetHeight();
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animator) {
                resetHeight();
            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animator) {

            }
        });
        bottomNavAnimator = animator;
        animator.start();
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
        setViewPagerItem(position, true);
    }

    private void setViewPagerItem(int position, boolean isAnimated) {
        if (mHomeViewPager.getCurrentItem() == position) {
            return;
        }

        if (isAnimated) {
            CommonUtils.fadeOutView(mHomeViewPager, new AnimatorListenerAdapter() {
                @Override
                public void onAnimationCancel(Animator animation) {
                    super.onAnimationCancel(animation);
                    mHomeViewPager.setCurrentItem(position, false);
                    CommonUtils.fadeInView(mHomeViewPager, null);
                    final Router previousRouter = routers.get(previousPagerPosition);
                    popCheckoutController(previousRouter);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mHomeViewPager.setCurrentItem(position, false);
                    CommonUtils.fadeInView(mHomeViewPager, null);
                    final Router previousRouter = routers.get(previousPagerPosition);
                    popCheckoutController(previousRouter);
                }
            });
        } else {
            mHomeViewPager.setCurrentItem(position, false);
        }
    }

    private void setBottomNavigationItem(int index) {
        final int oldPosition = mBottomNavigationView.getCurrentItem();
        final int newPosition = index == SHOP_INDEX ? CATEGORY_INDEX : index;
        if (WILL_ANIMATE_INDICATOR) {
            animateIndicator(oldPosition, newPosition);
        } else {
            resetIndicators(newPosition);
        }
        mBottomNavigationView.setCurrentItem(index, false);
    }

    @Override
    public void showShopController() {
        showShopController(false, true);
    }

    private void showShopController(boolean willNotNavigateFromAwayShop, boolean isAnimated) {
        setBottomNavigationItem(CATEGORY_INDEX);
        if (previousPagerPosition == SHOP_INDEX) {
            if (!willNotNavigateFromAwayShop) {
                if (getCategoriesRouter() != null) {
                    getCategoriesRouter().popToRoot();
                }
                setViewPagerItem(CATEGORY_INDEX);
            }
        } else {
            setViewPagerItem(SHOP_INDEX, isAnimated);
        }
        resetSecureFlag(getCurrentControllerOnRouter(getShopRouter()));

        mIsInitialSavedInstanceLoad = false;
    }

    @Override
    public void showCategoryController() {
        setWillGoToShopInsteadOfCategories(false);
        setBottomNavigationItem(CATEGORY_INDEX);

        if (previousPagerPosition == CATEGORY_INDEX &&
                mHomeViewPager.getCurrentItem() == CATEGORY_INDEX) {
            if (getCategoriesRouter() != null) {
                getCategoriesRouter().popToRoot();
            }
        } else {
            setViewPagerItem(CATEGORY_INDEX);
        }

        showNewTagOnCategory(false);

        resetSecureFlag(getCurrentControllerOnRouter(getCategoriesRouter()));

        mIsInitialSavedInstanceLoad = false;
    }

    @Override
    public void showAccountController() {
        setBottomNavigationItem(ACCOUNT_INDEX);
        if (previousPagerPosition == ACCOUNT_INDEX) {
            Controller controller = getCurrentViewPagerController();
            if (controller instanceof BaseController &&
                    ((BaseController) controller).isViewAttached()) {
                ((BaseController) controller).refreshContents();
            }
        } else {
            setViewPagerItem(ACCOUNT_INDEX);
        }

        resetSecureFlag(getCurrentControllerOnRouter(getAccountRouter()));


        mIsInitialSavedInstanceLoad = false;
    }

    @Override
    public void showContactUsController() {
        // do nothing
    }

    @Override
    public void showWishlistController() {
        setBottomNavigationItem(WISHLIST_INDEX);
        if (previousPagerPosition == WISHLIST_INDEX) {
            Controller controller = getCurrentViewPagerController();
            if (controller instanceof BaseController &&
                    ((BaseController) controller).isViewAttached()) {
                ((BaseController) controller).refreshContents();
            }
        } else {
            setViewPagerItem(WISHLIST_INDEX);
        }

        if (!mPresenter.hasWishlistBeenAccessed()) {
            mPresenter.setHasWishlistBeenAccessed(true);
            showWishlistItemCount(wishlistCount);
        }

        resetSecureFlag(getCurrentControllerOnRouter(getWishlistRouter()));

        mIsInitialSavedInstanceLoad = false;
    }

    @Override
    public void showBrandsController() {
        setBottomNavigationItem(BRANDS_INDEX);
        if (previousPagerPosition == BRANDS_INDEX) {
            Controller controller = getCurrentViewPagerController();
            if (!mIsInitialSavedInstanceLoad && !(controller instanceof TopBrandsController)) {
                Router router = routers.get(BRANDS_INDEX);
                if (router != null) {
                    ArrayList<RouterTransaction> backstack = new ArrayList<>();
                    backstack.add(router.getBackstack().get(0));
                    router.setBackstack(backstack, new HorizontalChangeHandler());
                }
            }
            if (controller instanceof BaseController &&
                    ((BaseController) controller).isViewAttached()) {
                ((BaseController) controller).refreshContents();
            }
        } else {
            setViewPagerItem(BRANDS_INDEX);
        }

        resetSecureFlag(getCurrentControllerOnRouter(getBrandsRouter()));

        mIsInitialSavedInstanceLoad = false;
    }

    @Override
    public void showCheckoutController() {
        setBottomNavigationItem(CHECKOUT_INDEX);
        if (previousPagerPosition == CHECKOUT_INDEX) {
            Controller controller = getCurrentViewPagerController();
            if (!mIsInitialSavedInstanceLoad && !(controller instanceof CheckoutMvpView)) {
                Router router = routers.get(CHECKOUT_INDEX);
                if (router != null) {
                    ArrayList<RouterTransaction> backstack = new ArrayList<>();
                    backstack.add(router.getBackstack().get(0));
                    router.setBackstack(backstack, new HorizontalChangeHandler());
                }
            }
            if (controller instanceof BaseController &&
                    ((BaseController) controller).isViewAttached()) {
                ((BaseController) controller).refreshContents();
            }
        } else {
            setViewPagerItem(CHECKOUT_INDEX);
        }

        if (!mActivity.isAuthorized()) {
            mActivity.showLoginController(null);
        }

        resetSecureFlag(getCurrentControllerOnRouter(getBrandsRouter()));

        mIsInitialSavedInstanceLoad = false;
    }

    @Override
    public void showBasketItemCount() {
        final int count = CartUtil.getCartValue();
        updateBasketItemCount(count);
    }

    private void updateBasketItemCount(int count) {
        if (mPresenter == null || mBottomNavigationView == null) {
            DelayedMethodExecutionManager.getInstance()
                    .queueDelayedMethodCall(
                            this.getClass().getName(),
                            "showBasketItemCount",
                            this::showBasketItemCount);
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
                .setTextColor(ContextCompat.getColor(mActivity, R.color.bottom_nav_badge_text))
                .build();
        mBottomNavigationView.setNotification(notification, CHECKOUT_INDEX);
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
                .setTextColor(ContextCompat.getColor(mActivity, R.color.bottom_nav_badge_text))
                .build();
        mBottomNavigationView.setNotification(notification, WISHLIST_INDEX);

        wishlistCount = count;
    }

    public void updateBasketItemsQuantity() {
        mPresenter.callGetBasketItemsQuantity();
    }

    public void updateCheckoutWithCartDetails(CheckoutDetailsMapper cartDetails) {
        if (getCheckoutRouter() == null) {
            return;
        }
        Controller controller = getCheckoutRouter().getControllerWithTag(CheckoutController.TAG);
        if (controller instanceof CheckoutMvpView) {
            ((CheckoutMvpView) controller).updateCartWithMappedValues(cartDetails);
        }
    }

    public void removeBasketItemCount() {
        updateBasketItemCount(0);
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

    public Controller getCurrentController() {
        return getCurrentControllerOnRouter(getCurrentRouter());
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

    private static Controller getCurrentControllerOnRouter(Router router) {
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
            if (controller instanceof BaseController &&
                    ((BaseController) controller).isViewAttached()) {
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

    public void deepLinkSaleItemDetails(String saleId, String seoIdentifierId, String productName) {
        mBottomNavigationView.setCurrentItem(SHOP_INDEX, false);
        resetIndicators(SHOP_INDEX);
        mHomeViewPager.setCurrentItem(SHOP_INDEX);

        SaleItemDetailsController.Parameters.FromDeepLink parameters = new SaleItemDetailsController.Parameters
                .FromDeepLink(saleId, seoIdentifierId, productName);

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

        addAsSecondControllerOnBackstack(SHOP_INDEX, routerTransaction);
    }

    public void openLocationFilterHash(String locationFilterHash) {
        mBottomNavigationView.setCurrentItem(SHOP_INDEX, false);
        resetIndicators(SHOP_INDEX);
        mHomeViewPager.setCurrentItem(SHOP_INDEX);

        SaleItemsController.Parameters.FromLocationFilterHash parameters = new SaleItemsController
                .Parameters.FromLocationFilterHash(locationFilterHash);

        SaleItemsController controller = SaleItemsController.newInstance(parameters);

        RouterTransaction routerTransaction = RouterTransaction.with(controller)
                .tag(mActivity.getString(R.string.sale_items_controller_tag))
                .popChangeHandler(new HorizontalChangeHandler());

        addAsSecondControllerOnBackstack(SHOP_INDEX, routerTransaction);
    }

    private void deepLinkSaleItems(String bannerTitle, String saleId, String bannerId) {
        mBottomNavigationView.setCurrentItem(SHOP_INDEX, false);
        resetIndicators(SHOP_INDEX);
        mHomeViewPager.setCurrentItem(SHOP_INDEX);

        SaleItemsController.Parameters.FromSaleItemDeepLink parameters = new SaleItemsController
                .Parameters.FromSaleItemDeepLink(bannerTitle, saleId, bannerId);

        SaleItemsController controller = SaleItemsController.newInstance(parameters);

        RouterTransaction routerTransaction = RouterTransaction.with(controller)
                .tag(mActivity.getString(R.string.sale_items_controller_tag))
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler());

        addAsSecondControllerOnBackstack(SHOP_INDEX, routerTransaction);
    }

    private void deepLinkSearchQuery(String searchQuery) {
        mBottomNavigationView.setCurrentItem(SHOP_INDEX, false);
        resetIndicators(SHOP_INDEX);
        mHomeViewPager.setCurrentItem(SHOP_INDEX);

        SaleItemsController.Parameters.FromShopSearch parameters = new SaleItemsController
                .Parameters.FromShopSearch(null, searchQuery);

        SaleItemsController controller = SaleItemsController.newInstance(parameters);

        RouterTransaction routerTransaction = RouterTransaction.with(controller)
                .tag(mActivity.getString(R.string.sale_items_controller_tag))
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler());

        addAsSecondControllerOnBackstack(SHOP_INDEX, routerTransaction);
    }

    private void deepLinkSaleCategory(String categoryName, String categoryIdentifier) {
        mBottomNavigationView.setCurrentItem(CATEGORY_INDEX, false);
        resetIndicators(CATEGORY_INDEX);
        mHomeViewPager.setCurrentItem(CATEGORY_INDEX);

        ShopsController controller = ShopsController.instanceWithCategoryFilter(categoryIdentifier, categoryName);

        RouterTransaction routerTransaction = RouterTransaction.with(controller)
                .tag(mActivity.getString(R.string.shop_controller_tag))
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler());

        addAsSecondControllerOnBackstack(CATEGORY_INDEX, routerTransaction);
    }

    private void deepLinkSaleCategoryItems(String categoryName, String categoryIdentifier) {
        mBottomNavigationView.setCurrentItem(CATEGORY_INDEX, false);
        resetIndicators(CATEGORY_INDEX);
        mHomeViewPager.setCurrentItem(CATEGORY_INDEX);

        SaleItemsController.Parameters.FromCategoryDeepLink parameters = new SaleItemsController
                .Parameters.FromCategoryDeepLink(categoryName, categoryIdentifier);

        SaleItemsController controller = SaleItemsController.newInstance(parameters);

        RouterTransaction routerTransaction = RouterTransaction.with(controller)
                .tag(mActivity.getString(R.string.sale_items_controller_tag))
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler());

        addAsSecondControllerOnBackstack(CATEGORY_INDEX, routerTransaction);
    }

    private void deepLinkBrands() {
        mBottomNavigationView.setCurrentItem(BRANDS_INDEX, false);
        resetIndicators(BRANDS_INDEX);
        mHomeViewPager.setCurrentItem(BRANDS_INDEX);
    }

    private void deepLinkBrandProductList(String brandName, String brandId) {
        mBottomNavigationView.setCurrentItem(BRANDS_INDEX, false);
        resetIndicators(BRANDS_INDEX);
        mHomeViewPager.setCurrentItem(BRANDS_INDEX);

        SaleItemsController.Parameters.FromBrandClick parameters = new SaleItemsController.Parameters.FromBrandClick(brandName);

        SaleItemsController controller = SaleItemsController.newInstance(parameters);

        RouterTransaction routerTransaction = RouterTransaction.with(controller)
                .tag(mActivity.getString(R.string.sale_items_controller_tag))
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler());

        addAsSecondControllerOnBackstack(BRANDS_INDEX, routerTransaction);
    }

    private void addAsSecondControllerOnBackstack(int index, RouterTransaction routerTransaction) {

        ArrayList<RouterTransaction> backstack = new ArrayList<>();
        backstack.add(routerTransaction);

        Router router = routers.get(index);
        if (router != null) {
            backstack.add(0, router.getBackstack().get(0));
            router.setBackstack(backstack, new HorizontalChangeHandler());
        } else {
            deeplinkBackstack.put(index, backstack);
        }
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
                .setTextColor(ContextCompat.getColor(mActivity, R.color.bottom_nav_badge_text))
                .build();
        mBottomNavigationView.setNotification(notification, CATEGORY_INDEX);
    }

    public void showReturnPolicy() {
        showAccountController();
        if (getAccountRouter() != null) {
            getAccountRouter().popToRoot();
        }
        final Controller controller = getAccountRouter().getBackstack().get(0).controller();
        if (controller instanceof AccountController) {
            ((AccountController) controller).showReturnsPolicy();
        }
    }

    public void showHomePage() {
        showHomePage(true);
    }

    public void showHomePage(boolean isAnimated) {
        if (getShopRouter() != null) {
            getShopRouter().popToRoot();
        }
        if (getCategoriesRouter() != null && getCategoriesRouter().getBackstackSize() > 0) {
            Controller controller = getCategoriesRouter().getBackstack().get(0).controller();
            if (controller instanceof NewSaleCategoriesController) {
                ((NewSaleCategoriesController) controller).resetLevels();
            }
        }
        showShopController(true, isAnimated);
    }

    public void showBrands() {
        mBottomNavigationView.setCurrentItem(1);
    }

    private void popCheckoutController(Router router) {
        if (router == null) {
            return;
        }

        List<RouterTransaction> backstack = router.getBackstack();
        for (int i = backstack.size() - 1; i > 0; i--) {
            Controller controller = backstack.get(i).controller();
            if (controller instanceof CheckoutMvpView) {
                backstack = backstack.subList(0, i);
                break;
            }
        }
        router.setBackstack(backstack, null);
    }

    public void processLinkUri(Uri uri) {
        processDeeplinkUri(uri);
    }

    public void processDeeplinkUri(Uri uri) {
        if (!isViewBound()) {
            deeplinkUriToProcess = uri;
            return;
        }
        final String path = uri.getPath();
        if (path != null && !path.isEmpty()) {
            final ArrayList<String> directories = new ArrayList<>(Arrays.asList(path.split("/")));
            if (directories.isEmpty()) {
                showShopController();
                if (getShopRouter() != null) {
                    getShopRouter().popToRoot();
                }
                return;
            }
            if (directories.get(0).isEmpty()) {
                directories.remove(0);
                if (directories.isEmpty()) {
                    showShopController();
                    if (getShopRouter() != null) {
                        getShopRouter().popToRoot();
                    }
                    return;
                }
            }

            if (directories.get(0).equals("ItemsList.aspx")) {
                mPresenter.loadSaleBannerDetails(uri.getQueryParameter("saleID"));
            } else if (directories.get(0).equals("shop")) {
                if (directories.get(1).equals("sale")) {
                    // Specific Sale and Trending Now
                    if (directories.size() < 5) {
                        return;
                    }
                    String saleName = StringUtils.toTitleCase(
                            StringUtils.fixApostropheS(
                                    directories.get(2)
                                            .replace('-', ' ')
                                            .replace(" or ", " | ")
                                            .replace(" and ", " & ")));
                    if (!directories.get(3).equals("s")) {
                        return;
                    }
                    String saleId = directories.get(4);

                    if (directories.size() == 5) {
                        String saleIdParam = uri.getQueryParameter("saleID");
                        deepLinkSaleItems(saleName, saleId, saleIdParam);
                    } else {
                        deeplinkProductDetail(5, saleId, directories);
                    }
                } else if (directories.get(1).equals("sales")) {
                    // Main sale category
                    if (directories.size() != 4) {
                        return;
                    }
                    String categoryName = StringUtils.splitAndGetLastString(directories.get(2), "-");
                    categoryName = StringUtils.toTitleCase(categoryName);
                    String categoryId = directories.get(3);
                    deepLinkSaleCategory(categoryName, categoryId);
                } else if (directories.get(1).equals("brand")) {
                    // Brand product list
                    if (directories.size() != 4) {
                        return;
                    }
                    String brandName = StringUtils.toTitleCase(
                            StringUtils.fixApostropheS(
                                    directories.get(2)
                                            .replace('-', ' ')
                                            .replace(" or ", " | ")
                                            .replace(" and ", " & ")));
                    String brandId = directories.get(3);
                    deepLinkBrandProductList(brandName, brandId);
                } else if (directories.get(1).equals("brands")) {
                    // Brands category
                    if (directories.size() != 2) {
                        return;
                    }
                    deepLinkBrands();
                } else if (directories.get(1).equals("search")) {
                    // Search
                    if (directories.size() != 2) {
                        return;
                    }
                    String searchQuery = uri.getQueryParameter("query");
                    deepLinkSearchQuery(searchQuery);
                } else {
                    if (directories.size() == 3) {
                        // Product list for subcategory
                        String categoryId = StringUtils.capitalizeCategoryKey(
                                directories.get(1)
                                        .replace("-and-", " & ")
                                        .replace("-", ">>>"));
                        String categoryName = StringUtils.splitAndGetLastString(categoryId, ">>>");
                        categoryName = StringUtils.toTitleCase(categoryName);
                        deepLinkSaleCategoryItems(categoryName, categoryId);
                    }
                }
            } else if (directories.get(0).equals("product")) {
                deeplinkProductDetail(0, null, directories);
            } else if (directories.get(0).equals("brands")) {
                // Brands category
                if (directories.size() != 1) {
                    return;
                }
                deepLinkBrands();
            } else {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
                mActivity.startActivity(browserIntent);
            }
        }
    }

    private void deeplinkProductDetail(int index, String saleId, List<String> directories) {
        if (!((directories.size() - index == 4) &&
                directories.get(index).equals("product") &&
                directories.get(index + 2).equals("s"))) {
            return;
        }
        String productName = StringUtils.toTitleCase(
                StringUtils.fixApostropheS(
                        directories.get(index + 1)
                                .replace('-', ' ')
                                .replace(" or ", " | ")
                                .replace(" and ", " & ")));
        String productId = directories.get(index + 3);
        deepLinkSaleItemDetails(saleId, productId, productName);
    }

    @Override
    public void receiveSaleBannerDetails(String saleName, String encodedId, String externalId) {
        deepLinkSaleItems(saleName, encodedId, externalId);
    }

    private void resetSecureFlag(Controller controller) {
        if (controller instanceof MvpView) {
            final Window window = mActivity.getWindow();
            if (((MvpView) controller).isSecurePage()) {
                window.addFlags(WindowManager.LayoutParams.FLAG_SECURE);
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
            }
        }
    }

    public boolean isWillGoToShopInsteadOfCategories() {
        return willGoToShopInsteadOfCategories;
    }

    public void setWillGoToShopInsteadOfCategories(boolean willGoToShopInsteadOfCategories) {
        this.willGoToShopInsteadOfCategories = willGoToShopInsteadOfCategories;
    }

    public AccountController getAccountController() {
        if (getAccountRouter() == null) {
            return null;
        }
        Controller controller = getAccountRouter().getControllerWithTag(AccountController.TAG);
        if (controller instanceof AccountController) {
            return (AccountController) controller;
        }
        return null;
    }

    public void showTNC() {
        setBottomNavigationItem(ACCOUNT_INDEX);
        setViewPagerItem(ACCOUNT_INDEX);
        final Router router = getAccountRouter();
        router.popToTag(AccountController.TAG);
        final Controller controller = router.getControllerWithTag(AccountController.TAG);
        if (controller instanceof AccountController) {
            if (!mPresenter.isTablet()) {
                router.popToTag(AccountController.TAG);
            }
            ((AccountController) controller).showLegalities(BundleKeys.TEMPLATE_KEY_TNC, AccountOption.TERMSANDCONDITIONS);
        }
        resetSecureFlag(getCurrentControllerOnRouter(router));
    }
}
