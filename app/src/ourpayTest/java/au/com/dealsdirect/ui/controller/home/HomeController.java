package au.com.dealsdirect.ui.controller.home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
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

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.auth.AuthHandler;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.account.AccountController;
import au.com.dealsdirect.ui.controller.account.AccountMvpView;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutController;
import au.com.dealsdirect.ui.controller.checkout.paymentsuccess.PaymentSuccessController;
import au.com.dealsdirect.ui.controller.contact.viewcontacts.ViewContactsController;
import au.com.dealsdirect.ui.controller.dashboard.DashboardController;
import au.com.dealsdirect.ui.controller.invite.InviteSendController;
import au.com.dealsdirect.ui.controller.shops.ShopsController;
import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.CartUtil;
import butterknife.BindView;

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
    ViewGroup mShopContainer;

    @BindView(R.id.controller_second_frame)
    ViewGroup mPaymentScheduleContainer;

    @BindView(R.id.controller_third_frame)
    ViewGroup mOurpayContainer;

    @BindView(R.id.controller_fourth_frame)
    ViewGroup mAccountsContainer;

    @BindView(R.id.controller_fifth_frame)
    ViewGroup mCheckoutContainer;



    public AHBottomNavigation getBottomNavigationView() {
        return mBottomNavigationView;
    }

    @BindView(R.id.controller_home_bottom_nav)
    AHBottomNavigation mBottomNavigationView;

    private Router mShopRouter;
    private Router mPaymentScheduleRouter;
    private Router mOurPayRouter;
    private Router mAccountsRouter;
    private Router mCheckoutRouter;
    private List<Router> mRouterList;
    private List<ViewGroup> mContainersList;

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

//        mShopRouter = Conductor.attachRouter(getActivity(), mShopContainer, null);
//        mAccountsRouter = Conductor.attachRouter(getActivity(), mAccountsContainer, null);
//        mPaymentScheduleRouter = Conductor.attachRouter(getActivity(), mOurpayContainer, null);
//        mOurPayRouter = Conductor.attachRouter(getActivity(), mPaymentScheduleContainer, null);
//        mCheckoutRouter = Conductor.attachRouter(getActivity(), mCheckoutContainer, null);

        mShopRouter = getChildRouter(mShopContainer);
        if (!mShopRouter.hasRootController()) {
            ShopsController shopsController = new ShopsController();
            mActivity.setShopController(shopsController);
            mShopRouter.setRoot(RouterTransaction.with(shopsController)
                    .tag(ShopsController.TAG));
        }

        mPaymentScheduleRouter = getChildRouter(mPaymentScheduleContainer);

        if (!mPaymentScheduleRouter.hasRootController()) {
            mPaymentScheduleRouter.setRoot(RouterTransaction.with(ViewContactsController.newInstance())
                    .tag(ViewContactsController.TAG));
        }

        mOurPayRouter = getChildRouter(mOurpayContainer);

        if (!mOurPayRouter.hasRootController()) {
            mOurPayRouter.setRoot(RouterTransaction.with(InviteSendController.newInstance())
                    .tag(getActivity().getResources().getString(R.string.invite_friends_tag)));
        }

        mAccountsRouter = getChildRouter(mAccountsContainer);

        if (!mAccountsRouter.hasRootController()) {
            mAccountsRouter.setRoot(RouterTransaction.with(AccountController.newInstance()));
            mActivity.setAccountsRouter(mAccountsRouter);
        }

        mCheckoutRouter = getChildRouter(mCheckoutContainer);

        if (!mCheckoutRouter.hasRootController()) {
            mCheckoutRouter.setRoot(RouterTransaction.with(new CheckoutController())
                    .tag(getActivity().getResources().getString(R.string.checkout_controller)));
            ((MainActivity) getActivity()).setCheckoutRouter(mCheckoutRouter);
        }

        mContainersList = new ArrayList<>();
        mContainersList.add(mShopContainer);
        mContainersList.add(mPaymentScheduleContainer);
        mContainersList.add(mOurpayContainer);
        mContainersList.add(mAccountsContainer);
        mContainersList.add(mCheckoutContainer);
        setVisibleContainer(0);

        mRouterList = new ArrayList<>();
        mRouterList.add(mShopRouter);
        mRouterList.add(mPaymentScheduleRouter);
        mRouterList.add(mOurPayRouter);
        mRouterList.add(mAccountsRouter);
        mRouterList.add(mCheckoutRouter);

        mActivity.setHomeRouter(mShopRouter);
        AHBottomNavigationAdapter navigationAdapter = new AHBottomNavigationAdapter(getActivity(), R.menu.bottom_navigation_menu);
        navigationAdapter.setupWithBottomNavigation(mBottomNavigationView);
        mBottomNavigationView.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        mBottomNavigationView.setCurrentItem(0);
        mBottomNavigationView.setDefaultBackgroundColor(getResources().getColor(R.color.white));
        mBottomNavigationView.setAccentColor(getResources().getColor(R.color.bottom_nav_accent_color));
        mBottomNavigationView.setInactiveColor(getResources().getColor(R.color.gray_title_text));
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
                    mBottomNavigationView.setCurrentItem(0);
                } else if (to instanceof DashboardController) {
                    mBottomNavigationView.setCurrentItem(2);
                } else if (to instanceof AccountController) {
                    mBottomNavigationView.setCurrentItem(3);
                } else if (to instanceof CheckoutController) {
                    mBottomNavigationView.setCurrentItem(4);
                }
            }
        });

        mBottomNavigationView.setOnTabSelectedListener((position, wasSelected) -> {


            if (mCheckoutRouter!=null){
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
                        showSecondTabController();
                        break;
                    case 2:
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
                    default:
                        break;

                }
            } else {
                if (position == 0) {
                    mShopRouter.popToRoot();
                } else if (position == 4) {
                    mAccountsRouter.popToRoot();
                }
            }
            return true;
        });

    }

    public void initControllers(boolean includeShop) {
        if (includeShop) {
            ShopsController shopsController = new ShopsController();
            mActivity.setShopController(shopsController);
            mShopRouter.setRoot(RouterTransaction.with(shopsController).tag(ShopsController.TAG));
        }

        mPaymentScheduleRouter = getChildRouter(mPaymentScheduleContainer);
        mPaymentScheduleRouter.setRoot(RouterTransaction.with(ViewContactsController.newInstance())
                .tag(ViewContactsController.TAG));

        mOurPayRouter = getChildRouter(mOurpayContainer);
        mOurPayRouter.setRoot(RouterTransaction.with(InviteSendController.newInstance())
                .tag(getActivity().getResources().getString(R.string.invite_friends_tag)));

        mAccountsRouter = getChildRouter(mAccountsContainer);
        mAccountsRouter.setRoot(RouterTransaction.with(AccountController.newInstance()));
        mActivity.setAccountsRouter(mAccountsRouter);

        mCheckoutRouter = getChildRouter(mCheckoutContainer);
        mCheckoutRouter.setRoot(RouterTransaction.with(new CheckoutController())
                .tag(getActivity().getResources().getString(R.string.checkout_controller)));
    }

    public void resetInviteRouter() {
        mOurPayRouter = getChildRouter(mPaymentScheduleContainer);
        mOurPayRouter.setRoot(RouterTransaction.with(InviteSendController.newInstance())
                .tag(getActivity().getResources().getString(R.string.invite_friends_tag)));

    }

    public void resetContactsRouter() {
        mPaymentScheduleRouter = getChildRouter(mOurpayContainer);
        mPaymentScheduleRouter.setRoot(RouterTransaction.with(ViewContactsController.newInstance())
                .tag(getActivity().getResources().getString(R.string.view_contacts_controller)));

    }

    public void resetCheckoutRouter() {
        mCheckoutRouter = getChildRouter(mCheckoutContainer);
        mCheckoutRouter.setRoot(RouterTransaction.with(new CheckoutController())
                .tag(getActivity().getResources().getString(R.string.checkout_controller)));
    }

    @Override
    public void showFirstTabController() {

        if (mShopRouter!=null){
            Controller controller = getCurrentControllerOnRouter(mShopRouter);
            if (controller instanceof ShopsController) {
                ((MainActivity)getActivity()).getMainController().setViewpagerDraggable(true);
            }
        }

        setVisibleContainer(0);
    }

    @Override
    public void showSecondTabController() {
        setVisibleContainer(1);

        if (!mActivity.isAuthorized()) {
            mActivity.showLoginController(mPaymentScheduleRouter, new AuthHandler() {
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
    public void showThirdTabController() {
        setVisibleContainer(2);

        if (!mActivity.isAuthorized()) {
            mActivity.showLoginController(mOurPayRouter, new AuthHandler() {
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
        setVisibleContainer(3);
        int size = mAccountsRouter.getBackstack().size();
        if (mAccountsRouter.getBackstack().get(size - 1).controller() instanceof AccountMvpView)
            ((AccountMvpView) mAccountsRouter.getBackstack().get(size - 1).controller()).initLoginDrawable();
    }

    @Override
    public void showFifthTabController() {
        setVisibleContainer(4);

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
            getBottomNavigationView().setNotification(notification, 4);
        }
    }

    public void removeBasketItemCount() {
        getBottomNavigationView().setNotification("", 4);
    }

    public void hideBottomNav() {
        if (mBottomNavigationView != null)
            mBottomNavigationView.setVisibility(View.GONE);
    }

    public void showBottomNav() {
        if (mBottomNavigationView != null && mBottomNavigationView.getVisibility() == View.GONE) {
            mBottomNavigationView.setVisibility(View.VISIBLE);
            mBottomNavigationView.bringToFront();
        }
    }

    public void setVisibleContainer(int i) {
        mContainersList.get(currentVisibleIndex).setVisibility(View.GONE);
        previousVisibleIndex = currentVisibleIndex;
        mContainersList.get(i).setVisibility(View.VISIBLE);
        mBottomNavigationView.setCurrentItem(i, false);
        currentVisibleIndex = i;
    }

    public void setShopRouterViewPagerDraggable(){
        if (mShopRouter!=null){
            Controller controller = getCurrentControllerOnRouter(mShopRouter);
            if (controller instanceof ShopsController) {
                ((MainActivity)getActivity()).getMainController().setViewpagerDraggable(true);
            }
        }
    }

    public void resetVisibleContainer() {

        if (!(getCurrentRouter() == mAccountsRouter || getCurrentRouter() == mShopRouter)) {
            mContainersList.get(currentVisibleIndex).setVisibility(View.GONE);
            mContainersList.get(previousVisibleIndex).setVisibility(View.VISIBLE);
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
        return mRouterList.get(currentVisibleIndex);
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
        resetInviteRouter();
        resetCheckoutRouter();
    }

    public boolean isCheckoutRouterVisible(){
        Log.d("ourpay", "current item = "+mBottomNavigationView.getCurrentItem());
        return mBottomNavigationView.getCurrentItem()==4;
    }
}
