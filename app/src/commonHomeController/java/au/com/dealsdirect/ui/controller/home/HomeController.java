package au.com.dealsdirect.ui.controller.home;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.auth.AuthHandler;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Value;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.account.AccountController;
import au.com.dealsdirect.ui.controller.account.AccountMvpView;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutController;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutMvpView;
import au.com.dealsdirect.ui.controller.checkout.paymentsuccess.PaymentSuccessController;
import au.com.dealsdirect.ui.controller.contact.viewcontacthistory.ViewContactHistoryController;
import au.com.dealsdirect.ui.controller.contact.viewcontacts.ViewContactsController;
import au.com.dealsdirect.ui.controller.invite.InviteSendController;
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
import butterknife.OnClick;

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

    private static final String KEY_TEXT = "HomeController.KEY_TEXT";

    @Inject
    HomeMvpPresenter<HomeMvpView> mPresenter;

    @BindView(R.id.product_details_add_to_cart)
    ImageView mImageAddToCartAnimation;

    @BindView(R.id.controller_first_frame)
    ViewGroup mFirstControllerContainer;

    @BindView(R.id.controller_second_frame)
    ViewGroup mSecondControllerContainer;

    @BindView(R.id.controller_third_frame)
    ViewGroup mThirdControllerContainer;

    @BindView(R.id.controller_fourth_frame)
    ViewGroup mFourthControllerContainer;

    @BindView(R.id.controller_fifth_frame)
    ViewGroup mFifthControllerContainer;

    public AHBottomNavigation getBottomNavigationView() {
        return mBottomNavigationView;
    }

    @BindView(R.id.controller_home_bottom_nav)
    AHBottomNavigation mBottomNavigationView;

    private HashMap<Integer,Pair<Router,ViewGroup>> mRouterContainerMapping;

    private View mRoot;
    private Router mShopRouter;
    private Router mAccountsRouter;
    private Router mContactsRouter;
    private Router mInvitesRouter;
    private Router mCheckoutRouter;
    private List<Router> mRouterList;
    private List<ViewGroup> mContainersList;
    private CheckoutMvpView mCheckoutMvpView;

    private int currentVisibleIndex = 1;
    private int previousVisibleIndex = 0;

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
        Log.d("deeplinking", "homecontroller setup");

        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        mRoot = view;

        mShopRouter = getChildRouter(mFirstControllerContainer);
        mAccountsRouter = getChildRouter(mSecondControllerContainer);
        mContactsRouter = getChildRouter(mThirdControllerContainer);
        mInvitesRouter = getChildRouter(mFourthControllerContainer);
        mCheckoutRouter = getChildRouter(mFifthControllerContainer);

        mShopRouter = getChildRouter(mFirstControllerContainer);
        mActivity.setSaleItemsShopRouter(mShopRouter);

        Log.d("deeplinking", "homecontroller onviewbound");
        if (!mShopRouter.hasRootController()) {
            ShopsController shopsController = new ShopsController();
            mActivity.setShopController(shopsController);
//            mActivity.shopControllerCallback();

            mShopRouter.setRoot(RouterTransaction.with(shopsController)
                    .tag(ShopsController.TAG));
        }

        if (!mAccountsRouter.hasRootController()) {
            mAccountsRouter.setRoot(RouterTransaction.with(AccountController.newInstance()));
            mActivity.setAccountsRouter(mAccountsRouter);
        }

        if (!mContactsRouter.hasRootController()) {
            mContactsRouter.setRoot(RouterTransaction.with(ViewContactsController.newInstance()));
        }

        if (!mInvitesRouter.hasRootController()) {
            mInvitesRouter.setRoot(RouterTransaction.with(InviteSendController.newInstance()));
        }

        if (!mCheckoutRouter.hasRootController()) {
            GateKeeper.setRoot(mCheckoutRouter, GateKeeper.Destination.CHECKOUT,
                    RouterTransaction.with(CheckoutController.newInstance()).tag(getActivity().getResources().getString(R.string.checkout_controller)));
            mActivity.setCheckoutRouter(mCheckoutRouter);
        }
        mRouterContainerMapping = new HashMap<>();
        mRouterContainerMapping.put(0, new Pair<>(mShopRouter,mFirstControllerContainer));
        mRouterContainerMapping.put(1, new Pair<>(mAccountsRouter,mSecondControllerContainer));
        mRouterContainerMapping.put(2, new Pair<>(mContactsRouter,mThirdControllerContainer));
        mRouterContainerMapping.put(3, new Pair<>(mInvitesRouter,mFourthControllerContainer));
        mRouterContainerMapping.put(4, new Pair<>(mCheckoutRouter,mFifthControllerContainer));
        setVisibleContainer(0);

        mActivity.setHomeRouter(mShopRouter);
        mActivity.setShopRouter(mShopRouter);

        /* May 4, 2018 - Deep Link using ShopRouter */
//        mActivity.shopRouterCallback();

        AHBottomNavigationAdapter navigationAdapter = new AHBottomNavigationAdapter(getActivity(), R.menu.bottom_navigation_menu);
        navigationAdapter.setupWithBottomNavigation(mBottomNavigationView);
        mBottomNavigationView.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        mBottomNavigationView.setCurrentItem(0);
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

        initControllers(true);

        if (mPresenter.isAuthorized()) {
            mPresenter.callGetBasketItemsQuantity();
        }

        mShopRouter.addChangeListener(new ControllerChangeHandler.ControllerChangeListener() {
            @Override
            public void onChangeStarted(@Nullable Controller to, @Nullable Controller from, boolean isPush, @NonNull ViewGroup container, @NonNull ControllerChangeHandler handler) {
                Log.d("homecontroller", "change listner = " + to.toString());

            }

            @Override
            public void onChangeCompleted(@Nullable Controller to, @Nullable Controller from, boolean isPush, @NonNull ViewGroup container, @NonNull ControllerChangeHandler handler) {
                if (mBottomNavigationView == null) return;

                if (to instanceof HomeController || to instanceof ShopsController) {
                    mBottomNavigationView.setCurrentItem(0);
                } else if (to instanceof AccountController) {
                    mBottomNavigationView.setCurrentItem(1);
                } else if (to instanceof ViewContactsController) {
                    mBottomNavigationView.setCurrentItem(2);
                } else if (to instanceof InviteSendController) {
                    mBottomNavigationView.setCurrentItem(3);
                } else if (to instanceof CheckoutController) {
                    mBottomNavigationView.setCurrentItem(4);
                }


                /* May 7, 2018 - Check if deep linked to contact history, if so update current item */
                if (getCurrentRouter() != null && getCurrentRouter() == mContactsRouter) {
                    if (getCurrentControllerOnRouter(getCurrentRouter()) instanceof ViewContactHistoryController) {
                        mBottomNavigationView.setCurrentItem(2);

                    }
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

            if (isAttached())
                mActivity.setIsFromCategories(false);

            if (!wasSelected) {

                Controller checkoutController = getCurrentControllerOnRouter(mCheckoutRouter);
                if (position != 4 && checkoutController instanceof PaymentSuccessController) {
                    mCheckoutRouter.popToRoot();
                }

                switch (position) {
                    case 0:
                        showFirstTabController();
                        break;
                    case 1:
                        mActivity.setDraggableViewPager(false);
                        showSecondTabController();
                        break;
                    case 2:
                        mActivity.setDraggableViewPager(false);
                        showThirdTabController();
                        break;
                    case 3:
                        mActivity.setDraggableViewPager(false);
                        showFourthTabController();
                        break;
                    case 4:
                        mActivity.setDraggableViewPager(false);
                        showFifthTabController();
                        break;
                }
            } else {
                switch (position) {
                    case 0:
                        mShopRouter.popToRoot();
                        break;
                    case 1:
                        mAccountsRouter.popToRoot();
                        break;
                    case 2:
                        mContactsRouter.popToRoot();
                        break;
                    case 3:
                        mInvitesRouter.popToRoot();
                        break;
                    case 4:
                        mCheckoutMvpView.getDisplayRouter().popToRoot();
                        break;
                }
            }
            return true;
        });
    }

    public Router getPopUpHostRouter(){
        return mShopRouter;
    }

    public void initControllers(boolean includeShop) {
        if (includeShop) {
            ShopsController shopsController = new ShopsController();
            mActivity.setShopController(shopsController);
            mShopRouter.setRoot(RouterTransaction.with(shopsController).tag(ShopsController.TAG));
        }

        mAccountsRouter = getChildRouter(mSecondControllerContainer);
        mAccountsRouter.setRoot(RouterTransaction.with(AccountController.newInstance()));
        mActivity.setAccountsRouter(mAccountsRouter);

        resetRouters();
    }

    public void resetInviteRouter() {
        mInvitesRouter = getChildRouter(mFourthControllerContainer);
        mInvitesRouter.setRoot(RouterTransaction.with(InviteSendController.newInstance()));

    }

    public void resetContactsRouter() {
        mContactsRouter = getChildRouter(mThirdControllerContainer);
        mActivity.setContactRouter(mContactsRouter);
        mContactsRouter.setRoot(RouterTransaction.with(ViewContactsController.newInstance()));

    }

    public void resetCheckoutRouter() {
        mCheckoutRouter = getChildRouter(mFifthControllerContainer);
        Controller checkoutController = ControllerFactory.getInstance(GateKeeper.Destination.CHECKOUT);
        mCheckoutMvpView =  (CheckoutMvpView) checkoutController;
        mCheckoutRouter.setRoot(RouterTransaction.with(checkoutController)
                .tag(getActivity().getResources().getString(R.string.checkout_controller)));
    }

    @Override
    public void showFirstTabController() {

        if (mShopRouter != null) {
            Controller controller = getCurrentControllerOnRouter(mShopRouter);
            if (controller instanceof ShopsController) {
                ((MainActivity) getActivity()).getMainController().setViewpagerDraggable(true);
            }
        }

        setVisibleContainer(0);
    }


    public void sendSaleItemToCheckout(Value getCurrentOrder) {
        mCheckoutMvpView.getPresenter().updateCart(getCurrentOrder);
    }

    @Override
    public void showSecondTabController() {
        mActivity.getMainController().setViewpagerDraggable(false);
        setVisibleContainer(1);
        int size = mAccountsRouter.getBackstack().size();
        if (mAccountsRouter.getBackstack().get(size - 1).controller() instanceof AccountMvpView)
            ((AccountMvpView) mAccountsRouter.getBackstack().get(size - 1).controller()).initLoginDrawable();
    }

    @Override
    public void showThirdTabController() {
        mActivity.getMainController().setViewpagerDraggable(false);
        setVisibleContainer(2);
        if (!mActivity.isAuthorized()) {
            mActivity.showLoginController(getCurrentRouter(), new AuthHandler() {
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
    public void showFourthTabController() {
        mActivity.getMainController().setViewpagerDraggable(false);
        setVisibleContainer(3);
        if (!mActivity.isAuthorized()) {
            mActivity.showLoginController(getCurrentRouter(), new AuthHandler() {
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
        mActivity.getMainController().setViewpagerDraggable(false);
        setVisibleContainer(4);
        if (!mActivity.isAuthorized()) {
            mActivity.showLoginController(getCurrentRouter(), new AuthHandler() {
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

    public void deepLinkContactHistory() {

        setVisibleContainer(2);
        if (!mActivity.isAuthorized()) {
            mActivity.showLoginController(mContactsRouter, new AuthHandler() {
                @Override
                public void success() {
                    resetRouters();
                    mContactsRouter.pushController(RouterTransaction.with(ViewContactHistoryController.newInstance(
                            "Comments and Suggestions",
                            "",
                            0,
                            "",
                            4319646))
                            .pushChangeHandler(new HorizontalChangeHandler())
                            .popChangeHandler(new HorizontalChangeHandler()));
                }

                @Override
                public void error() {

                }
            });
        } else {
            resetRouters();
            mContactsRouter.pushController(RouterTransaction.with(ViewContactHistoryController.newInstance(
                    "Comments and Suggestions",
                    "",
                    0,
                    "",
                    4319646))
                    .pushChangeHandler(new HorizontalChangeHandler())
                    .popChangeHandler(new HorizontalChangeHandler()));
        }
    }

    public void deepLinkSaleItems(String bannerTitle, String saleId, String bannerId) {

        Bundle args = new BundleBuilder(new Bundle())
                .putString(SALEITEMS_TITLE, bannerTitle)
                .putString(SALEITEMS_SALE_ID, saleId)
                .putString(SALEITEMS_BANNER_ID, bannerId)
                .build();


        int deepLinkSaleItemsDelay = 1000;
        Handler handler = new Handler();
        handler.postDelayed(() -> {

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
        }, deepLinkSaleItemsDelay);

    }

    public void deepLinkSaleCategory(String categoryName, String categoryIdentifier) {

        Bundle args = new BundleBuilder(new Bundle())
                .putString(SALEITEMS_CATEGORY_ID, categoryIdentifier)
                .putString(SALEITEMS_CATEGORY_NAME, categoryName)
                .build();


        int deepLinkSaleItemsDelay = 1000;
        Handler handler = new Handler();
        handler.postDelayed(() -> {

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
        }, deepLinkSaleItemsDelay);

    }

    public boolean isAccountsActive() {
        return mSecondControllerContainer != null && mSecondControllerContainer.isShown();
    }

    public boolean isShopActive() {
        return mFirstControllerContainer != null && mFirstControllerContainer.isShown();
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
            getBottomNavigationView().setNotification(notification, 4);
        }
    }

    @Override
    public boolean isPopUpControllerVisible() {
        return false;
    }

    public void removeBasketItemCount() {
        getBottomNavigationView().setNotification("", 4);
    }

    private void proceedToController(int id) {
        switch (id){
            case 2:
                showThirdTabController();
                break;
            case 3:
                showFourthTabController();
                break;
            case 4:
                showFifthTabController();
                break;
        }
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

    public void setVisibleContainer(int i) {
        mRouterContainerMapping.get(currentVisibleIndex).second.setVisibility(View.GONE);
        previousVisibleIndex = currentVisibleIndex;
        mRouterContainerMapping.get(i).second.setVisibility(View.VISIBLE);
        mBottomNavigationView.setCurrentItem(i, false);
        currentVisibleIndex = i;
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
        resetInviteRouter();
        resetContactsRouter();
        resetCheckoutRouter();
    }

    public boolean isCheckoutRouterVisible() {
        return mBottomNavigationView.getCurrentItem() == 4;
    }
}
