package au.com.dealsdirect.ui.controller.home;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;

import java.util.HashMap;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.auth.AuthHandler;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Value;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.account.AccountMvpView;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutMvpView;
import au.com.dealsdirect.ui.controller.checkout.paymentsuccess.PaymentSuccessController;
import au.com.dealsdirect.ui.controller.contact.viewcontacts.ViewContactsMvpView;
import au.com.dealsdirect.ui.controller.main.MainController;
import au.com.dealsdirect.ui.controller.saleitemdetails.SaleItemDetailsController;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsController;
import au.com.dealsdirect.ui.controller.shops.ShopsController;
import au.com.dealsdirect.ui.custom.transitions.SharedArcFadePopChangeHandler;
import au.com.dealsdirect.ui.custom.transitions.SharedArcFadePushChangeHandler;
import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.CartUtil;
import au.com.dealsdirect.utils.module.ControllerFactory;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;

import static au.com.dealsdirect.utils.BundleKeys.SALEITEMDETAILS_KEY_IS_DEEP_LINKED_WITH_SALE;
import static au.com.dealsdirect.utils.BundleKeys.SALEITEMDETAILS_KEY_SEO_IDENTIFIER_ID;
import static au.com.dealsdirect.utils.BundleKeys.SALEITEMDETAILS_KEY_SKU_ID;
import static au.com.dealsdirect.utils.BundleKeys.SALEITEMS_BANNER_ID;
import static au.com.dealsdirect.utils.BundleKeys.SALEITEMS_CATEGORY_ID;
import static au.com.dealsdirect.utils.BundleKeys.SALEITEMS_CATEGORY_NAME;
import static au.com.dealsdirect.utils.BundleKeys.SALEITEMS_SALE_ID;
import static au.com.dealsdirect.utils.BundleKeys.SALEITEMS_TITLE;

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
    ViewGroup mContactContainer;

    @BindView(R.id.controller_fourth_frame)
    ViewGroup mAccountsContainer;

    @BindView(R.id.controller_fifth_frame)
    ViewGroup mCheckoutContainer;

    @BindView(R.id.login_host_frame)
    ViewGroup mLoginHostContainer;

    private HashMap<Integer, Pair<Router, ViewGroup>> mRouterContainerMapping;

    private Router mShopRouter;
    private Router mCategoriesRouter;
    private Router mContactRouter;
    private Router mAccountsRouter;
    private Router mCheckoutRouter;
    private Router mPopUpHostRouter;

    private AccountMvpView mAccountMvpView;
    private CheckoutMvpView mCheckoutMvpView;
    public ViewContactsMvpView mViewContactsMvpView;
    private AHBottomNavigation mBottomNavigationView;

    public static int currentVisibleIndex = 0;
    private int previousVisibleIndex = 0;
    private boolean initNewBadge = false;

    public int mSavedIndex;
    private boolean mHasSavedStateInstance;
    public static boolean mIsInitialSavedInstanceLoad;
    public ViewContactsMvpView mViewContactsController;
    private int mDefaultTab;

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

        mDefaultTab = mHasSavedStateInstance ? mSavedIndex : TAB_SHOP_INDEX;

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
        setVisibleContainer(mDefaultTab);

        AHBottomNavigationAdapter navigationAdapter = new AHBottomNavigationAdapter(getActivity(), R.menu.bottom_navigation_menu);
        navigationAdapter.setupWithBottomNavigation(mBottomNavigationView);
        mBottomNavigationView.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        mBottomNavigationView.setCurrentItem(mDefaultTab);
        mBottomNavigationView.setDefaultBackgroundColor(getResources().getColor(R.color.bottom_nav_background));
        mBottomNavigationView.setAccentColor(getResources().getColor(R.color.bottom_nav_accent));
        mBottomNavigationView.setInactiveColor(getResources().getColor(R.color.bottom_nav_inactive));

//        ADD "NEW" Badge to categories
        AHNotification notification = new AHNotification.Builder()
                .setText("NEW")
                .setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.bottom_nav_badge))
                .setTextColor(ContextCompat.getColor(getActivity(), R.color.white))
                .build();
        getBottomNavigationView().setNotification(notification, TAB_CATEGORIES_INDEX);
        initNewBadge = true;
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

            if (!mainController.shouldBottomNavigationViewEnabled() ||
                            mainController.isHomeViewPagerDragging()) {
                return false;
            }
            
            if (!wasSelected || mIsInitialSavedInstanceLoad) {

                if (mIsInitialSavedInstanceLoad) {
                    position = mSavedIndex;
                } else {
                    mainController.goToPage(MainController.SHOP_INDEX);
                }

                Controller checkoutController = getCurrentControllerOnRouter(mCheckoutRouter);
                if (position != TAB_CATEGORIES_INDEX && checkoutController instanceof PaymentSuccessController) {
                    mCheckoutRouter.popToRoot();
                }

                switch (position) {
                    case TAB_SHOP_INDEX:
                        showFirstTabController();
                        break;
                    case TAB_CATEGORIES_INDEX:
                        if(initNewBadge) {
                            getBottomNavigationView().setNotification(new AHNotification(), TAB_CATEGORIES_INDEX);
                            initNewBadge = false;
                        }
                        showSecondTabController();
                        break;
                    case TAB_ACCOUNT_INDEX:
                        showThirdTabController();
                        break;
                    case TAB_CONTACT_INDEX:
                        showFourthTabController();
                        break;
                    case TAB_CHECKOUT_INDEX:
                        showFifthTabController();
                        break;
                    default:
                        break;

                }
            } else {
                switch (position) {
                    case TAB_SHOP_INDEX:
                        mShopRouter.popToRoot();
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
                }
            }
            return true;
        });

    }

    public void initControllers(boolean includeShop) {
        if (includeShop) {
            mShopRouter = getChildRouter(mShopContainer);
            ShopsController shopsController = ShopsController.newInstance();
            mShopRouter.setRoot(RouterTransaction.with(shopsController).tag(ShopsController.TAG));
            mActivity.setShopController(shopsController);
        }

        mCategoriesRouter = getChildRouter(mCategoriesContainer);
        mCategoriesRouter.setRoot(RouterTransaction.with(ControllerFactory.getInstance(GateKeeper.Destination.CATEGORIES)));

        if (mPresenter.isTablet()) {
            mPopUpHostRouter = getChildRouter(mLoginHostContainer);
            mPopUpHostRouter.setPopsLastView(true);
        }

        resetContactsRouter();

        resetAccountRouter();

        resetCheckoutRouter();

        mActivity.setHomeRouter(mShopRouter);
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

        if (mPresenter.isTablet()) {
            mPopUpHostRouter = getChildRouter(mLoginHostContainer);
            mPopUpHostRouter.setPopsLastView(true);
        }

        mViewContactsMvpView = mActivity.getContactsController();
        mCheckoutMvpView = mActivity.getCheckoutController();
        mAccountMvpView = mActivity.getAccountController();
        mActivity.setHomeRouter(getRouter());
    }

    public void setSavedCurrentItem() {
        if (mIsInitialSavedInstanceLoad) mBottomNavigationView.setCurrentItem(currentVisibleIndex);
    }

    public void showSplashSavedInstance(Router router){
        if (!mActivity.hasShownSplash) {
            if (mActivity.getHomeRouter() == null) {
                mActivity.setHomeRouter(router);
            }
            mActivity.showSplashScreen();
        }
    }

    public void resetContactsRouter() {
        mContactRouter = getChildRouter(mContactContainer);
        mActivity.setContactRouter(mContactRouter);
        if (!mHasSavedStateInstance) {
            Controller contactsController = ControllerFactory.getInstance(GateKeeper.Destination.CONTACT_US);
            mViewContactsMvpView = (ViewContactsMvpView) contactsController;
            mContactRouter.setRoot(RouterTransaction.with(contactsController).tag(ViewContactsMvpView.TAG));
        }
    }


    public void resetCheckoutRouter() {
        mCheckoutRouter = getChildRouter(mCheckoutContainer);
        mActivity.setCheckoutRouter(mCheckoutRouter);
        if (!mHasSavedStateInstance || mActivity.getCheckoutController() == null) {
            if (!mPresenter.isTablet()) {
                Controller checkoutController = ControllerFactory.getInstance(GateKeeper.Destination.CHECKOUT);
                mCheckoutRouter.setRoot(RouterTransaction.with(checkoutController)
                        .tag(getActivity().getResources().getString(R.string.checkout_controller)));
                mCheckoutMvpView = (CheckoutMvpView) checkoutController;
            } else {
                Controller checkoutHostController = ControllerFactory.getInstance(GateKeeper.Destination.CHECKOUT_HOST);
                mCheckoutRouter.setRoot(RouterTransaction.with(checkoutHostController)
                        .tag(getActivity().getResources().getString(R.string.checkout_host_controller)));
                mCheckoutMvpView = (CheckoutMvpView) checkoutHostController;
            }
        }
    }

    public void resetAccountRouter() {
        mAccountsRouter = getChildRouter(mAccountsContainer);
        mActivity.setAccountsRouter(mAccountsRouter);
        Controller accountController = ControllerFactory.getInstance(GateKeeper.Destination.ACCOUNT);
        mAccountsRouter.setRoot(RouterTransaction.with(accountController));
        mAccountMvpView = (AccountMvpView) accountController;
    }

    public ViewContactsMvpView getContactsController() {
        return mViewContactsController;
    }

    public void setContactsController (ViewContactsMvpView viewContactsMvpView) {
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

    @Override
    public void showFirstTabController() {

        if (mShopRouter != null) {
            Controller controller = getCurrentControllerOnRouter(mShopRouter);
            mActivity.setDraggableViewPager(controller instanceof ShopsController);

            if (!mIsInitialSavedInstanceLoad) {
                if (controller instanceof SaleItemsController) {
                    mShopRouter.popToRoot();
                }
            }
        }

        mIsInitialSavedInstanceLoad = false;
        setVisibleContainer(TAB_SHOP_INDEX);
    }

    @Override
    public void showSecondTabController() {
        mActivity.setDraggableViewPager(false);
        mIsInitialSavedInstanceLoad = false;
        setVisibleContainer(TAB_CATEGORIES_INDEX);
    }

    @Override
    public void showThirdTabController() {
        mIsInitialSavedInstanceLoad = false;
        setVisibleContainer(TAB_ACCOUNT_INDEX);
        mActivity.setDraggableViewPager(false);

        int size = mAccountsRouter.getBackstack().size();
        if (mAccountsRouter.getBackstack().get(size - 1).controller() instanceof AccountMvpView) {
            ((AccountMvpView) mAccountsRouter.getBackstack().get(size - 1).controller()).initLoginDrawable();
        }
    }

    @Override
    public void showFourthTabController() {
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
        } else {
            if (mActivity.isAuthorized()) {
                Controller controller = getCurrentControllerOnRouter(mContactRouter);
                if (controller instanceof ViewContactsMvpView) {
                    if (mViewContactsMvpView == null) {
                        mViewContactsMvpView = mActivity.getContactsController();
                    }

                    mViewContactsMvpView.getPresenter().loadContacts();
                }
            }
        }

        mIsInitialSavedInstanceLoad = false;
    }

    @Override
    public void showFifthTabController() {
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
        } else { //should load cart everytime checkout is clicked on bottom nav
            if (mActivity.isAuthorized()) {
                Controller controller = getCurrentControllerOnRouter(mCheckoutRouter);
                if (controller instanceof CheckoutMvpView) {
                    if (mCheckoutMvpView == null) {
                        resetCheckoutRouter();
                    } else {
                        mCheckoutMvpView.loadCart();
                    }
                }
            }
        }
        mIsInitialSavedInstanceLoad = false;
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
                    .setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.bottom_nav_badge))
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
                mActivity.setDraggableViewPager(true);
            }
        }
    }

    public void resetVisibleContainer() {
        if (!(getCurrentRouter() ==  mAccountsRouter || getCurrentRouter() == mShopRouter)) {
            mRouterContainerMapping.get(currentVisibleIndex).second.setVisibility(View.GONE);
            if (currentVisibleIndex == previousVisibleIndex) {
                previousVisibleIndex = 0;
            }
            mRouterContainerMapping.get(previousVisibleIndex).second.setVisibility(View.VISIBLE);
            mBottomNavigationView.setCurrentItem(previousVisibleIndex, false);
            currentVisibleIndex = previousVisibleIndex;

        }
        if (getCurrentRouter() == mShopRouter) {
            showBottomNav();
        }
    }

    @Override
    public void onDetach(View view) {
        super.onDetach(view);
    }

    public Router getCurrentRouter() {
        if (mPresenter.isTablet() && isPopUpControllerVisible()) {
            return mPopUpHostRouter;
        } else {
            return mRouterContainerMapping.get(currentVisibleIndex).first;
        }
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

    @Override
    public boolean isPopUpControllerVisible() {
        return mPopUpHostRouter != null && mPopUpHostRouter.getBackstackSize() >= 1;
    }

    public Router getPopUpHostRouter() {
        return mPopUpHostRouter;
    }


    public void deepLinkSaleItemDetails(String seoIdentifierId, String skuId, boolean isWithSale) {

        Bundle bundle = new Bundle();
        bundle.putString(SALEITEMDETAILS_KEY_SEO_IDENTIFIER_ID, seoIdentifierId);
        bundle.putString(SALEITEMDETAILS_KEY_SKU_ID, skuId);
        bundle.putBoolean(SALEITEMDETAILS_KEY_IS_DEEP_LINKED_WITH_SALE, isWithSale);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (mShopRouter != null)
                mShopRouter.pushController(RouterTransaction.with(SaleItemDetailsController.newInstance(bundle))
                        .pushChangeHandler(new FadeChangeHandler(false))
                        .popChangeHandler(new FadeChangeHandler()));

        } else {
            if (mShopRouter != null)
                mShopRouter.pushController(RouterTransaction.with(SaleItemDetailsController.newInstance(bundle))
                        .pushChangeHandler(new SharedArcFadePushChangeHandler())
                        .popChangeHandler(new SharedArcFadePopChangeHandler()));

        }
    }


    public void deepLinkSaleItems(String bannerTitle, String saleId, String bannerId) {

        Bundle args = new BundleBuilder(new Bundle())
                .putString(SALEITEMS_TITLE, bannerTitle)
                .putString(SALEITEMS_SALE_ID, saleId)
                .putString(SALEITEMS_BANNER_ID, bannerId)
                .build();

        if (!mPresenter.isAuthorized()) {

            // Invoke login if no auth or not an open app
            if (mShopRouter != null)
                mShopRouter.pushController(RouterTransaction.with(
                        new SaleItemsController(args))
                        .tag(mActivity.getString(R.string.sale_items_controller_tag))
                        .pushChangeHandler(new HorizontalChangeHandler())
                        .popChangeHandler(new HorizontalChangeHandler()));
        } else {

            // Check if sale is available
            mShopRouter.pushController(RouterTransaction.with(
                    new SaleItemsController(args))
                    .tag(mActivity.getString(R.string.sale_items_controller_tag))
                    .pushChangeHandler(new HorizontalChangeHandler())
                    .popChangeHandler(new HorizontalChangeHandler()));
        }

    }

    public void sendSaleItemToCheckout(Value getCurrentOrder) {
        mCheckoutMvpView.getPresenter().updateCartValues(getCurrentOrder);
    }

    public void deepLinkSaleCategory(String categoryName, String categoryIdentifier) {

        Bundle args = new BundleBuilder(new Bundle())
                .putString(SALEITEMS_CATEGORY_ID, categoryIdentifier)
                .putString(SALEITEMS_CATEGORY_NAME, categoryName)
                .build();

        if (!mPresenter.isAuthorized()) {

            // Invoke login if no auth or not an open app
            if (mShopRouter != null)
                mShopRouter.pushController(RouterTransaction.with(
                        new SaleItemsController(args))
                        .tag(mActivity.getString(R.string.sale_items_controller_tag))
                        .pushChangeHandler(new HorizontalChangeHandler())
                        .popChangeHandler(new HorizontalChangeHandler()));
        } else {

            // Check if sale is available
            mShopRouter.pushController(RouterTransaction.with(
                    new SaleItemsController(args))
                    .tag(mActivity.getString(R.string.sale_items_controller_tag))
                    .pushChangeHandler(new HorizontalChangeHandler())
                    .popChangeHandler(new HorizontalChangeHandler()));
        }
    }
}
