package au.com.dealsdirect.ui.controller.home;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter;
import com.aurelhubert.ahbottomnavigation.notification.AHNotification;
import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;

import java.util.HashMap;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.auth.AuthHandler;
import au.com.dealsdirect.data.network.model.orders.CreateRefundRequest;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.account.AccountMvpView;
import au.com.dealsdirect.ui.controller.address.viewaddress.ViewAddressController;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutDetailsMapper;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutMvpView;
import au.com.dealsdirect.ui.controller.checkout.paymentsuccess.PaymentSuccessController;
import au.com.dealsdirect.ui.controller.contact.viewcontacts.ViewContactsMvpView;
import au.com.dealsdirect.ui.controller.main.MainController;
import au.com.dealsdirect.ui.controller.orders.orders.BottomDialogCancelOrders;
import au.com.dealsdirect.ui.controller.saleitemdetails.SaleItemDetailsController;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsController;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsMvpView;
import au.com.dealsdirect.ui.controller.shops.ShopsController;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.ui.custom.transitions.SharedArcFadePopChangeHandler;
import au.com.dealsdirect.ui.custom.transitions.SharedArcFadePushChangeHandler;
import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.utils.ActionConstants;
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
    private static final int TAB_WISHLIST_INDEX = 3;
    private static final int TAB_CHECKOUT_INDEX = 4;
    private static final int TAB_CONTACT_INDEX = 5; //unused
    private static final int[] TAB_ALL_INDICES = new int[]{0, 1, 2, 3, 4};
    private static final String KEY_CURRENT_INDEX = "KEY_CURRENT_INDEX";
    private static final String KEY_HAS_SAVED_INSTANCE = "KEY_HAS_SAVED_INSTANCE";

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
    ViewGroup mWishlistContainer;

    @BindView(R.id.controller_fourth_frame)
    ViewGroup mAccountsContainer;

    @BindView(R.id.controller_fifth_frame)
    ViewGroup mCheckoutContainer;

    @BindView(R.id.controller_sixth_frame)
    ViewGroup mContactContainer;


    @BindView(R.id.popup_host_frame)
    ViewGroup mPopupHostContainer;

    private HashMap<Integer, Pair<Router, ViewGroup>> mRouterContainerMapping;

    private Router mShopRouter;
    private Router mCategoriesRouter;
    private Router mContactRouter;
    private Router mAccountsRouter;
    private Router mCheckoutRouter;
    private Router mPopUpHostRouter;
    private Router mWishlistRouter;

    private AccountMvpView mAccountMvpView;
    private CheckoutMvpView mCheckoutMvpView;
    private SaleItemsMvpView mWishlistMvpView;
    public ViewContactsMvpView mViewContactsMvpView;
    private AHBottomNavigation mBottomNavigationView;
    private RelativeLayout mFooter;
    private View mAdView;

    private int currentVisibleIndex = 0;
    private int previousVisibleIndex = 0;

    public int mSavedIndex;
    private boolean mHasSavedStateInstance;
    public static boolean mIsInitialSavedInstanceLoad;
    public ViewContactsMvpView mViewContactsController;
    private int mDefaultTab;
    private int mShopViewpagerIndex = 1;

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

        if (mHasSavedStateInstance) {
            refreshAllTopControllers();
        }
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {

        View view = inflater.inflate(R.layout.controller_home, container, false);
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);

        return view;
    }

    @SuppressLint("UseSparseArrays")
    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);

        mDefaultTab = mHasSavedStateInstance ? mSavedIndex : TAB_SHOP_INDEX;
        currentVisibleIndex = mDefaultTab;

        mActivity.getMainController().setHomeController(this);
        mBottomNavigationView = mActivity.getMainController().getBottomNav();

        if (!mHasSavedStateInstance) {
            initControllers(true);
        } else {
            setInitialSavedInstanceControllers();
        }

        mRouterContainerMapping = new HashMap<>();
        mRouterContainerMapping.put(TAB_SHOP_INDEX, new Pair<>(mShopRouter, mShopContainer));
        mRouterContainerMapping.put(TAB_CATEGORIES_INDEX, new Pair<>(mCategoriesRouter, mCategoriesContainer));
        mRouterContainerMapping.put(TAB_ACCOUNT_INDEX, new Pair<>(mAccountsRouter, mAccountsContainer));
        mRouterContainerMapping.put(TAB_CONTACT_INDEX, new Pair<>(mContactRouter, mContactContainer));
        mRouterContainerMapping.put(TAB_CHECKOUT_INDEX, new Pair<>(mCheckoutRouter, mCheckoutContainer));
        mRouterContainerMapping.put(TAB_WISHLIST_INDEX, new Pair<>(mWishlistRouter, mWishlistContainer));
        setAllContainersVisibility(View.GONE);
        setVisibleContainer(mDefaultTab);

        AHBottomNavigationAdapter navigationAdapter = new AHBottomNavigationAdapter(getActivity(), R.menu.bottom_navigation_menu);
        navigationAdapter.setupWithBottomNavigation(mBottomNavigationView);
        mBottomNavigationView.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        mBottomNavigationView.setCurrentItem(mDefaultTab);
        mBottomNavigationView.setDefaultBackgroundColor(getResources().getColor(R.color.bottom_nav_background));
        mBottomNavigationView.setAccentColor(getResources().getColor(R.color.bottom_nav_accent));
        mBottomNavigationView.setInactiveColor(getResources().getColor(R.color.bottom_nav_inactive));

//        ADD "NEW" Badge to categories

        if (mPresenter.isInitialLaunch()) {
            AHNotification notification = new AHNotification.Builder()
                    .setText("NEW")
                    .setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.bottom_nav_badge))
                    .setTextColor(ContextCompat.getColor(getActivity(), R.color.white))
                    .build();
            getBottomNavigationView().setNotification(notification, TAB_CATEGORIES_INDEX);
        }
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

        mBottomNavigationView.setCurrentItem(mDefaultTab);

        mBottomNavigationView.setOnTabSelectedListener((position, wasSelected) -> {
            MainController mainController = mActivity.getMainController();

            if (mainController == null) {
                return false;
            }

            if (!mainController.shouldBottomNavigationViewEnabled() ||
                    mainController.isHomeViewPagerDragging()) {
                return false;
            }

            if (!wasSelected || mIsInitialSavedInstanceLoad) {

                if (!mIsInitialSavedInstanceLoad) {
                    mainController.goToPage(getViewPagerScreen());
                }

                Controller checkoutController = getCurrentControllerOnRouter(mCheckoutRouter);
                if (position != TAB_CATEGORIES_INDEX && checkoutController instanceof PaymentSuccessController) {
                    mCheckoutRouter.popToRoot();
                }

                switch (position) {
                    case TAB_SHOP_INDEX:
                        showShopController();
                        break;
                    case TAB_CATEGORIES_INDEX:
                        if (mPresenter.isInitialLaunch()) {
                            //remove "new" badge by assigning a black notification on Categories Tan
                            getBottomNavigationView().setNotification(new AHNotification(), TAB_CATEGORIES_INDEX);
                            mPresenter.setInitialLaunchFalse();
                        }
                        mActivity.getMainController().getHomeViewPager().setCurrentItem(position);
                        showCategoryController();
                        break;
                    case TAB_ACCOUNT_INDEX:
                        showAccountController();
                        mActivity.getMainController().getHomeViewPager().setCurrentItem(position);
                        break;
                    case TAB_CONTACT_INDEX:
                        showContactUsController();
                        mActivity.getMainController().getHomeViewPager().setCurrentItem(position);
                        break;
                    case TAB_CHECKOUT_INDEX:
                        showCheckoutControllerController();
                        mActivity.getMainController().getHomeViewPager().setCurrentItem(position);
                        break;
                    case TAB_WISHLIST_INDEX:
                        showWishlistController();
                        mActivity.getMainController().getHomeViewPager().setCurrentItem(position);

                    default:
                        break;

                }
            } else {
                switch (position) {
                    case TAB_SHOP_INDEX:
                        if (getViewPagerScreen() == MainController.BANNER_FILTER_INDEX) {
                            mActivity.onBackPressed();
                        } else {
                            mShopRouter.popToRoot();
                        }
                        break;
                    case TAB_ACCOUNT_INDEX:
                        mAccountsRouter.popToRoot();
                        break;
                    case TAB_CATEGORIES_INDEX:
                        mCategoriesRouter.popToRoot();
                        break;
                    case TAB_CONTACT_INDEX:
                        mContactRouter.popToRoot();
                        break;
                    case TAB_CHECKOUT_INDEX:
                        mCheckoutRouter.popToRoot();
                        break;
                    case TAB_WISHLIST_INDEX:
                        mWishlistRouter.popToRoot();
                }
            }
            return true;
        });

    }

    public void initControllers(boolean includeShop) {
        if (includeShop) {
            mShopRouter = getChildRouter(mShopContainer);
            CommonControllerChangeListener.addToRouter(mShopRouter);
            ShopsController shopsController = ShopsController.newInstance();
            mShopRouter.setRoot(RouterTransaction.with(shopsController).tag(ShopsController.TAG));
            mActivity.setShopController(shopsController);
        }

        mCategoriesRouter = getChildRouter(mCategoriesContainer);
        CommonControllerChangeListener.addToRouter(mCategoriesRouter);

        Controller categoryController = mActivity.getResources().getBoolean(R.bool.should_use_old_category_layout) ?
                ControllerFactory.getInstance(GateKeeper.Destination.CATEGORIES) : ControllerFactory.getInstance(GateKeeper.Destination.SALECATEGORY);

        mCategoriesRouter.setRoot(RouterTransaction.with(categoryController));


        mPopUpHostRouter = getChildRouter(mPopupHostContainer);
        CommonControllerChangeListener.addToRouter(mPopUpHostRouter);
        mPopUpHostRouter.setPopsLastView(true);

        resetContactsRouter();

        resetAccountRouter();

        resetCheckoutRouter();

        resetWishlistRouter();

        mActivity.setHomeRouter(mShopRouter);

        CommonControllerChangeListener.addToRouter(getRouter());
    }

    public void setInitialSavedInstanceControllers() {
        mShopRouter = getChildRouter(mShopContainer);
        mCategoriesRouter = getChildRouter(mCategoriesContainer);
        mContactRouter = getChildRouter(mContactContainer);
        mActivity.setContactRouter(mContactRouter);
        mAccountsRouter = getChildRouter(mAccountsContainer);
        mActivity.setAccountsRouter(mAccountsRouter);
        mCheckoutRouter = getChildRouter(mCheckoutContainer);
        mActivity.setCheckoutRouter(mCheckoutRouter);
        mWishlistRouter = getChildRouter(mWishlistContainer);

        CommonControllerChangeListener.addToRouter(getRouter(),
                mShopRouter, mCategoriesRouter, mContactRouter, mAccountsRouter, mCheckoutRouter);

        if (mPresenter.isTablet()) {
            mPopUpHostRouter = getChildRouter(mPopupHostContainer);
            mPopUpHostRouter.setPopsLastView(true);

            CommonControllerChangeListener.addToRouter(mPopUpHostRouter);
        }

        mViewContactsMvpView = mActivity.getContactsController();
        mCheckoutMvpView = mActivity.getCheckoutController();
        mAccountMvpView = mActivity.getAccountController();
        mActivity.setHomeRouter(getRouter());
    }

    public void setSavedCurrentItem() {
        if (mIsInitialSavedInstanceLoad) mBottomNavigationView.setCurrentItem(currentVisibleIndex);
    }

    public void setViewpagerScreen(int index) {
        mShopViewpagerIndex = index;
    }

    public int getViewPagerScreen() {
        return mShopViewpagerIndex;
    }

    public void showSplashSavedInstance(Router router) {
        if (!mActivity.hasShownSplash) {
            if (mActivity.getHomeRouter() == null) {
                mActivity.setHomeRouter(router);
            }
            mActivity.showSplashScreen();
        }
    }

    public void resetContactsRouter() {
        mContactRouter = getChildRouter(mContactContainer);
        CommonControllerChangeListener.addToRouter(mContactRouter);
        mActivity.setContactRouter(mContactRouter);
        if (!mHasSavedStateInstance) {
            Controller contactsController = ControllerFactory.getInstance(GateKeeper.Destination.CONTACT_US);
            mViewContactsMvpView = (ViewContactsMvpView) contactsController;
            mContactRouter.setRoot(RouterTransaction.with(contactsController).tag(ViewContactsMvpView.TAG));
        }
    }


    public void resetCheckoutRouter() {
        mCheckoutRouter = getChildRouter(mCheckoutContainer);
        CommonControllerChangeListener.addToRouter(mCheckoutRouter);
        mActivity.setCheckoutRouter(mCheckoutRouter);
        Controller controller;
        String tag;

        if (!mHasSavedStateInstance || mActivity.getCheckoutController() == null) {
            if (!mPresenter.isTablet()) {
                controller = ControllerFactory.getInstance(GateKeeper.Destination.CHECKOUT);
                tag = getActivity().getResources().getString(R.string.checkout_controller);
            } else {
                controller = ControllerFactory.getInstance(GateKeeper.Destination.CHECKOUT_HOST);
                tag = getActivity().getResources().getString(R.string.checkout_host_controller);
            }
            mCheckoutRouter.setRoot(RouterTransaction.with(controller).tag(tag));
            mCheckoutMvpView = (CheckoutMvpView) controller;
        } else {
            mCheckoutMvpView = mActivity.getCheckoutController();
        }
    }

    public void resetAccountRouter() {
        mAccountsRouter = getChildRouter(mAccountsContainer);
        CommonControllerChangeListener.addToRouter(mAccountsRouter);
        mActivity.setAccountsRouter(mAccountsRouter);
        Controller accountController = ControllerFactory.getInstance(GateKeeper.Destination.ACCOUNT);
        mAccountsRouter.setRoot(RouterTransaction.with(accountController));
        mAccountMvpView = (AccountMvpView) accountController;
    }

    public void resetWishlistRouter() {
        mWishlistRouter = getChildRouter(mWishlistContainer);
        CommonControllerChangeListener.addToRouter(mWishlistRouter);
        mActivity.setWishlistRouter(mWishlistRouter);
        SaleItemsController wishlistController = (SaleItemsController) ControllerFactory.getInstance(GateKeeper.Destination.SALEITEMS);
        wishlistController.setSourceMode(SaleItemsController.SourceMode.WISHLIST);
        mWishlistRouter.setRoot(RouterTransaction.with(wishlistController));
        mWishlistMvpView = (SaleItemsMvpView) wishlistController;

    }

    public ViewContactsMvpView getContactsController() {
        return mViewContactsController;
    }

    public void setContactsController(ViewContactsMvpView viewContactsMvpView) {
        mViewContactsController = viewContactsMvpView;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CURRENT_INDEX, currentVisibleIndex);
        outState.putBoolean(KEY_HAS_SAVED_INSTANCE, true);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mSavedIndex = savedInstanceState.getInt(KEY_CURRENT_INDEX);
        mHasSavedStateInstance = savedInstanceState.getBoolean(KEY_HAS_SAVED_INSTANCE);
        mIsInitialSavedInstanceLoad = savedInstanceState.getBoolean(KEY_HAS_SAVED_INSTANCE);
    }

    private void onTabSwitch() {
        Controller controller = null;
        switch (currentVisibleIndex) {
            case TAB_SHOP_INDEX:
                controller = getCurrentControllerOnRouter(mShopRouter);
                break;
            case TAB_ACCOUNT_INDEX:
                controller = getCurrentControllerOnRouter(mAccountsRouter);
                break;
            case TAB_CATEGORIES_INDEX:
                controller = getCurrentControllerOnRouter(mCategoriesRouter);
                break;
            case TAB_CONTACT_INDEX:
                controller = getCurrentControllerOnRouter(mContactRouter);
                break;
            case TAB_CHECKOUT_INDEX:
                controller = getCurrentControllerOnRouter(mCheckoutRouter);
                break;
            case TAB_WISHLIST_INDEX:
                controller = getCurrentControllerOnRouter(mWishlistRouter);
                break;
            default:
                break;
        }
        if (controller instanceof BaseController) {
            ((BaseController) controller).onTabSwitch(false);
        }
    }

    @Override
    public void showShopController() {
        onTabSwitch();

        if (mShopRouter != null) {
            Controller controller = getCurrentControllerOnRouter(mShopRouter);
            mActivity.setDraggableViewPager(controller instanceof ShopsController);

            if (!mIsInitialSavedInstanceLoad) {
                if (controller instanceof SaleItemsController && currentVisibleIndex == TAB_SHOP_INDEX) {
                    mShopRouter.popToRoot();
                }
            }

            if (controller instanceof BaseController) {
                BaseController baseController = ((BaseController) controller);
                baseController.onTabSwitch(true);
                baseController.refreshContents();
            }

        }

        mIsInitialSavedInstanceLoad = false;
        setVisibleContainer(TAB_SHOP_INDEX);
        containerWillBeDisplayed(mShopContainer);
    }

    @Override
    public void showCategoryController() {
        onTabSwitch();

        if (mCategoriesRouter != null) {
            Controller controller = getCurrentControllerOnRouter(mCategoriesRouter);
            if (controller instanceof BaseController) {
                BaseController baseController = ((BaseController) controller);
                baseController.onTabSwitch(true);
                baseController.refreshContents();
            }
        }

        mActivity.setDraggableViewPager(false);
        mIsInitialSavedInstanceLoad = false;
        setVisibleContainer(TAB_CATEGORIES_INDEX);
        containerWillBeDisplayed(mCategoriesContainer);
    }

    @Override
    public void showAccountController() {
        onTabSwitch();

        mIsInitialSavedInstanceLoad = false;
        setVisibleContainer(TAB_ACCOUNT_INDEX);
        mActivity.setDraggableViewPager(false);

        if (mAccountsRouter != null) {
            Controller controller = getCurrentControllerOnRouter(mAccountsRouter);
            if (controller instanceof AccountMvpView) {
                ((AccountMvpView) controller).initLoginDrawable();
            }
            if (controller instanceof BaseController) {
                BaseController baseController = ((BaseController) controller);
                baseController.onTabSwitch(true);
                baseController.refreshContents();
            }
        }

        containerWillBeDisplayed(mAccountsContainer);
    }

    @Override
    public void showContactUsController() {
        onTabSwitch();

        mActivity.setDraggableViewPager(false);
        setVisibleContainer(TAB_CONTACT_INDEX);
        if (!mActivity.isAuthorized() && !mIsInitialSavedInstanceLoad) {
            mActivity.showLoginController(getCurrentRouter(), new AuthHandler() {
                @Override
                public void success() {
                    resetRouters();
                    setVisibleContainer(TAB_CONTACT_INDEX);
                }

                @Override
                public void error() {

                }
            });
        } else if (mActivity.isAuthorized() && mContactRouter != null) {
            Controller controller = getCurrentControllerOnRouter(mContactRouter);
            if (controller instanceof ViewContactsMvpView) {
                if (mViewContactsMvpView == null) {
                    mViewContactsMvpView = mActivity.getContactsController();
                }

                mViewContactsMvpView.getPresenter().loadContacts();
            }
            if (controller instanceof BaseController) {
                BaseController baseController = ((BaseController) controller);
                baseController.onTabSwitch(true);
                baseController.refreshContents();
            }
        }

        mIsInitialSavedInstanceLoad = false;
        containerWillBeDisplayed(mContactContainer);
    }

    @Override
    public void showCheckoutControllerController() {
        onTabSwitch();

        if (mCheckoutMvpView == null) {
            resetCheckoutRouter();
        }
        mActivity.setDraggableViewPager(false);
        setVisibleContainer(TAB_CHECKOUT_INDEX);
        if (!mActivity.isAuthorized() && !mIsInitialSavedInstanceLoad) {
            mActivity.showLoginController(getCurrentRouter(), new AuthHandler() {
                @Override
                public void success() {
                    resetRouters();
                    setVisibleContainer(TAB_CHECKOUT_INDEX);
                }

                @Override
                public void error() {

                }
            });
        } else if (mActivity.isAuthorized()) {
            if (!mCheckoutMvpView.isCartLoading()) {
                mCheckoutMvpView.loadCart();
            }
            if (mCheckoutRouter != null) {
                Controller controller = getCurrentControllerOnRouter(mCheckoutRouter);
                if (controller instanceof BaseController) {
                    BaseController baseController = ((BaseController) controller);
                    baseController.onTabSwitch(true);
                    baseController.refreshContents();
                }
            }
        }
        mIsInitialSavedInstanceLoad = false;
        containerWillBeDisplayed(mCheckoutContainer);
    }

    @Override
    public void showWishlistController() {
        onTabSwitch();

        mPresenter.setHasWishlistBeenAccessed(true);

        if (mWishlistRouter != null) {
            Controller controller = getCurrentControllerOnRouter(mWishlistRouter);
            mActivity.setDraggableViewPager(false);

            if (controller instanceof BaseController) {
                BaseController baseController = ((BaseController) controller);
                baseController.onTabSwitch(true);
                baseController.refreshContents();
            }

        }

        mIsInitialSavedInstanceLoad = false;
        setVisibleContainer(TAB_WISHLIST_INDEX);
        containerWillBeDisplayed(mWishlistContainer);
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
                    .setText(Integer.toString(CartUtil.getCartValue()))
                    .setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.bottom_nav_badge))
                    .setTextColor(ContextCompat.getColor(getActivity(), R.color.white))
                    .build();
            getBottomNavigationView().setNotification(notification, TAB_CHECKOUT_INDEX);
        }
    }

    @Override
    public void updateWishlistItemCount(int count) {
        String text;
        if (count == 0) {
            text = mPresenter.hasWishlistBeenAccessed() ? "" : "NEW";
        } else {
            text = Integer.toString(count);
        }
        AHNotification notification = new AHNotification.Builder()
                .setText(text)
                .setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.bottom_nav_badge))
                .setTextColor(ContextCompat.getColor(getActivity(), R.color.white))
                .build();
        getBottomNavigationView().setNotification(notification, TAB_WISHLIST_INDEX);
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
        Pair<Router, ViewGroup> previousPair = mRouterContainerMapping.get(currentVisibleIndex);
        Pair<Router, ViewGroup> nextPair = mRouterContainerMapping.get(index);

        if (nextPair == null || nextPair.second == null) {
            throw new AssertionError("Something is wrong with the mRouterContainerMapping!");
        }

        if (previousPair != null && previousPair.second != null && !previousPair.equals(nextPair)) {
            previousPair.second.setVisibility(View.GONE);
        }
        previousVisibleIndex = currentVisibleIndex;
        nextPair.second.setVisibility(View.VISIBLE);
        mBottomNavigationView.setCurrentItem(index, false);
        currentVisibleIndex = index;
    }

    public void refreshAllTopControllers() {
        for (int index : TAB_ALL_INDICES) {
            Pair<Router, ViewGroup> pair = mRouterContainerMapping.get(index);
            if (pair != null) {
                Router router = pair.first;
                if (router != null && router.getBackstack().size() > 0) {
                    Controller controller = router.getBackstack()
                            .get(router.getBackstack().size() - 1).controller();
                    if (controller instanceof BaseController) {
                        ((BaseController) controller).refreshContents();
                    }
                }
            }
        }
    }

    public void setAllContainersVisibility(int visibility) {
        for (int index : TAB_ALL_INDICES) {
            Pair<Router, ViewGroup> pair = mRouterContainerMapping.get(index);
            if (pair != null) {
                ViewGroup container = pair.second;
                if (container != null) {
                    container.setVisibility(visibility);
                }
            }
        }
    }

    public void setShopRouterViewPagerDraggable() {
        if (mShopRouter != null) {
            Controller controller = getCurrentControllerOnRouter(mShopRouter);
            if (controller instanceof ShopsController) {
                mActivity.setDraggableViewPager(true);
            }
        }
    }

    public void goToPreviousContainerFromLogin(boolean isAuthorized) {
        int newIndex;
        if (isAuthorized) {
            newIndex = currentVisibleIndex;
        } else {
            switch (currentVisibleIndex) {
                case TAB_CHECKOUT_INDEX:
                case TAB_CONTACT_INDEX:
                    newIndex = previousVisibleIndex;
                    break;
                default:
                    newIndex = currentVisibleIndex;
                    break;
            }
        }

        setVisibleContainer(newIndex);

        showBottomNav();
    }

    public void goBackToHomePage() {
        showShopController();
    }

    @Override
    public void onDetach(View view) {
        super.onDetach(view);
    }

    public Router getCurrentRouter() {
        if (mPresenter == null) {
            return null;
        }
        if (mPresenter.isTablet() && isPopUpControllerVisible()) {
            return mPopUpHostRouter;
        } else {
            return mRouterContainerMapping.get(currentVisibleIndex).first;
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

    @Override
    public boolean isPopUpControllerVisible() {
        return mPopUpHostRouter != null && mPopUpHostRouter.getBackstackSize() >= 1;
    }

    @Override
    public void backClick() {
        mActivity.onBackPressed();
    }

    public Router getPopUpHostRouter() {
        return mPopUpHostRouter;
    }


    public void deepLinkSaleItemDetails(String seoIdentifierId, String skuId, boolean isWithSale) {

        SaleItemDetailsController.Parameters.FromDeepLink parameters = new SaleItemDetailsController.Parameters
                .FromDeepLink(seoIdentifierId, skuId);

        if (mShopRouter != null) {
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

            mShopRouter.pushController(routerTransaction);
        }
    }

    public void openLocationFilterHash(String locationFilterHash) {
        if (mShopRouter == null) {
            return;
        }

        SaleItemsController.Parameters.FromLocationFilterHash parameters = new SaleItemsController
                .Parameters.FromLocationFilterHash(locationFilterHash);

        mShopRouter.popToRoot();

        SaleItemsController controller = SaleItemsController.newInstance(parameters);

        mShopRouter.pushController(RouterTransaction.with(controller)
                .tag(mActivity.getString(R.string.sale_items_controller_tag))
                .popChangeHandler(new HorizontalChangeHandler()));

        showShopController();

    }

    public void deepLinkSaleItems(String bannerTitle, String saleId, String bannerId) {

        SaleItemsController.Parameters.FromSaleItemDeepLink parameters = new SaleItemsController
                .Parameters.FromSaleItemDeepLink(bannerTitle, saleId, bannerId);

        SaleItemsController controller = SaleItemsController.newInstance(parameters);

        if (mShopRouter != null) {
            mShopRouter.pushController(RouterTransaction.with(controller)
                    .tag(mActivity.getString(R.string.sale_items_controller_tag))
                    .pushChangeHandler(new HorizontalChangeHandler())
                    .popChangeHandler(new HorizontalChangeHandler()));
        }
    }

    public void sendSaleItemToCheckout(CheckoutDetailsMapper getCurrentOrder) {
        mCheckoutMvpView.getPresenter().updateCartValues(getCurrentOrder);
    }

    public void deepLinkSaleCategory(String categoryName, String categoryIdentifier) {

        SaleItemsController.Parameters.FromCategoryDeepLink parameters = new SaleItemsController
                .Parameters.FromCategoryDeepLink(categoryName, categoryIdentifier);

        SaleItemsController controller = SaleItemsController.newInstance(parameters);

        if (mShopRouter != null) {
            mShopRouter.pushController(RouterTransaction.with(controller)
                    .tag(mActivity.getString(R.string.sale_items_controller_tag))
                    .pushChangeHandler(new HorizontalChangeHandler())
                    .popChangeHandler(new HorizontalChangeHandler()));
        }
    }

    private void containerWillBeDisplayed(ViewGroup container) {
        if (container != null) {
            Animation fadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.splash_fade_in);
            container.startAnimation(fadeIn);
        }
    }

    public void setNavigationBarEnabled(boolean enabled) {
        for (int i = 0; i < TAB_ALL_INDICES.length; i++) {
            if (enabled) {
                mBottomNavigationView.enableItemAtPosition(TAB_ALL_INDICES[i]);
            } else {
                mBottomNavigationView.disableItemAtPosition(TAB_ALL_INDICES[i]);
            }
        }
    }

    public void showMyAddress(String orderID) {
        if (!mPresenter.isTablet()) {
            getCurrentRouter().pushController(RouterTransaction.with(new ViewAddressController(false, null, true, orderID))
                    .pushChangeHandler(new HorizontalChangeHandler())
                    .popChangeHandler(new HorizontalChangeHandler()));
        } else {
            if (mAccountsRouter != null) {
                Controller controller = getCurrentControllerOnRouter(mAccountsRouter);
                if (controller instanceof AccountMvpView) {
                    ((AccountMvpView) controller).showChangeDeliveryAddressController(true, orderID);
                }

                if (controller instanceof BaseController) {
                    ((BaseController) controller).refreshContents();
                }
            }
        }
    }

    public void showSendContactMessage(boolean isCalledFromOrders, int invoiceNumber, String description) {
        mActivity.setDraggableViewPager(false);
        setVisibleContainer(TAB_CONTACT_INDEX);

        Controller controller = getCurrentControllerOnRouter(mContactRouter);
        if (controller instanceof ViewContactsMvpView) {
            if (mViewContactsMvpView == null) {
                mViewContactsMvpView = mActivity.getContactsController();
            }

            mViewContactsMvpView.sendOrderMessage(isCalledFromOrders, invoiceNumber, description);
        }
        if (controller instanceof BaseController) {
            ((BaseController) controller).refreshContents();
        }

        containerWillBeDisplayed(mContactContainer);
    }

    public void showMyReturns(int invoiceNumber, boolean calledFromOrder, String productId) {
        mActivity.setDraggableViewPager(false);
        Controller controller = getCurrentControllerOnRouter(mAccountsRouter);

        if (mAccountMvpView == null) {
            mAccountMvpView = mActivity.getAccountController();
        }

        mAccountMvpView.addNewReturns(invoiceNumber, calledFromOrder, productId);

        if (controller instanceof BaseController) {
            ((BaseController) controller).refreshContents();
        }

        containerWillBeDisplayed(mAccountsContainer);

    }

    public void showViewReturnsDetails(String returnID, String productName, boolean isFromOrders) {
        mActivity.setDraggableViewPager(false);

        Controller controller = getCurrentControllerOnRouter(mAccountsRouter);

        if (mAccountMvpView == null) {
            mAccountMvpView = mActivity.getAccountController();
        }

        mAccountMvpView.showReturnDetails(returnID, productName, isFromOrders);

        if (controller instanceof BaseController) {
            ((BaseController) controller).refreshContents();
        }

        containerWillBeDisplayed(mAccountsContainer);
    }

    public void showCancelOrderDialog(String orderNumber, String reason) {

        if (mPresenter.isTablet()) {
            CustomAlertDialog.showCustomCancelOrderDialog(
                    mActivity,
                    orderNumber,
                    new CustomAlertDialog.CustomDialogButtonListener() {
                        @Override
                        public void onYes(Object object) {
                            mActivity.callRefundOrder(orderNumber, reason, new HashMap<>());
                        }

                        @Override
                        public void onNo(Object object) {

                        }

                        @Override
                        public void onClose(Object object) {

                        }
                    });
        } else {
            BottomDialogCancelOrders bottomSheetFragment = new BottomDialogCancelOrders(
                    new BottomDialogCancelOrders.BottomDialogButtonListener() {
                        @Override
                        public void onYes(Object object) {
                            HashMap<String, Integer> map = new HashMap<>();
                            if (object instanceof String) {
                                map.put(orderNumber, Integer.parseInt((String) object));
                            }
                            mActivity.callRefundOrder(orderNumber, reason, map);
                        }

                        @Override
                        public void onNo(Object object) {

                        }

                        @Override
                        public void onClose(Object object) {

                        }
                    });
            Bundle bundle = new Bundle();

            bundle.putString(ActionConstants.ORDER_INVOICE_NUMBER, orderNumber);
            bundle.putString(ActionConstants.ORDER_REASON, reason);
            bundle.putBoolean(ActionConstants.ORDER_SHOULD_SHOW_CANCEL_ORDER, true);

            bottomSheetFragment.setArguments(bundle);
            bottomSheetFragment.show(mActivity.getSupportFragmentManager(), "DialogBottomCancelOrders");
        }

    }

    public void showCancelItemDialog(String imageUrl, String itemName, String itemId, String invoiceNumber, String reason,
                                     int quantity, int totalItems) {

        if (mPresenter.isTablet()) {
            CustomAlertDialog.showCancelItemDialog(
                    mActivity,
                    imageUrl,
                    itemName,
                    quantity,
                    totalItems,
                    new CustomAlertDialog.CustomDialogButtonListener() {
                        @Override
                        public void onYes(Object object) {
                            HashMap<String, Integer> map = new HashMap<>();
                            if (object instanceof String) {
                                map.put(itemId, Integer.parseInt((String) object));
                            }
                            mActivity.callRefundOrder(invoiceNumber, reason, map);
                        }

                        @Override
                        public void onNo(Object object) {

                        }

                        @Override
                        public void onClose(Object object) {

                        }
                    });
        } else {
            BottomDialogCancelOrders bottomSheetFragment = new BottomDialogCancelOrders(
                    new BottomDialogCancelOrders.BottomDialogButtonListener() {
                        @Override
                        public void onYes(Object object) {
                            HashMap<String, Integer> map = new HashMap<>();
                            if (object instanceof String) {
                                map.put(itemId, Integer.parseInt((String) object));
                            }
                            mActivity.callRefundOrder(invoiceNumber, reason, map);
                        }

                        @Override
                        public void onNo(Object object) {

                        }

                        @Override
                        public void onClose(Object object) {

                        }
                    });
            Bundle bundle = new Bundle();

            bundle.putString(ActionConstants.ORDER_ITEM_IMAGE_URL, imageUrl);
            bundle.putString(ActionConstants.ORDER_ITEM_DESCRIPTION, itemName);
            bundle.putString(ActionConstants.ORDER_INVOICE_NUMBER, invoiceNumber);
            bundle.putString(ActionConstants.ORDER_REASON, reason);
            bundle.putString(ActionConstants.ORDER_QUANTITY, String.valueOf(quantity));
            bundle.putString(ActionConstants.ORDER_SUBTOTAL_ITEM, String.valueOf(totalItems));
            bundle.putBoolean(ActionConstants.ORDER_SHOULD_SHOW_CANCEL_ORDER, false);

            bottomSheetFragment.setArguments(bundle);
            bottomSheetFragment.show(mActivity.getSupportFragmentManager(), "DialogBottomCancelItemOrders");
        }
    }

    public void callCreateOrderRefund(String invoiceNumber, String reason, HashMap<String, Integer> items) {
        CreateRefundRequest createRefundRequest = new CreateRefundRequest();
        createRefundRequest.setInvoiceNo(invoiceNumber);
        createRefundRequest.setReason(reason);
        createRefundRequest.setItems(items);

        mPresenter.callCreateRefund(createRefundRequest);
    }

}
