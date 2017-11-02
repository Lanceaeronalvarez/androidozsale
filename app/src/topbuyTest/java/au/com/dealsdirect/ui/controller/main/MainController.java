package au.com.dealsdirect.ui.controller.main;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.support.RouterPagerAdapter;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.home.HomeController;
import au.com.dealsdirect.ui.controller.shops.ShopsController;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.module.ControllerFactory;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;

/**
 * dp Created by Admin on 6/6/17.
 */

public class MainController extends BaseController implements MainMvpView {

    public static final String TAG = "MainController";

    private String mChosenSubCategoryItemKey = "";

    @Inject
    MainMvpPresenter<MainMvpView> mPresenter;

    @BindView(R.id.home_viewpager)
    MainCustomViewPager mHomeViewPager;

    Controller mSaleItemsController;
    Controller mCheckoutController;
    Controller mAccountsController;

    public static MainController newInstance() {

        return new MainController(
                new BundleBuilder(new Bundle())
                        .build());
    }

    public MainController(Bundle args) {
        super(args);
    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);
        mPresenter.onAttach(this);
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {

        View view = inflater.inflate(R.layout.controller_main, container, false);
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);

        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @Override
    protected void setUp(View view) {

        mSaleItemsController = ControllerFactory.getInstance(GateKeeper.Destination.SALEITEMS);
        mCheckoutController = ControllerFactory.getInstance(GateKeeper.Destination.CHECKOUT);
        mAccountsController = ControllerFactory.getInstance(GateKeeper.Destination.ACCOUNT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getActivity().getResources().getColor(R.color.colorAccent));
        }
        setupViewPager();
    }

    private void setupViewPager() {

        RouterPagerAdapter mViewPagerAdapter = new RouterPagerAdapter(this) {
            @Override
            public void configureRouter(@NonNull Router router, int position) {
                if (!router.hasRootController()) {
                    switch (position) {
                        case 0:
                            router.setRoot(RouterTransaction.with(mAccountsController));
                            break;
                        case 1:
                            router.setRoot(RouterTransaction.with(mSaleItemsController));
                            break;
                        case 2:
                            router.setRoot(RouterTransaction.with(mCheckoutController));
                            break;
                        default:
                            router.setRoot(RouterTransaction.with(mSaleItemsController));
                            break;
                    }
                }
            }

            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return "Page " + position;
            }
        };


        mHomeViewPager.setAdapter(mViewPagerAdapter);
        mHomeViewPager.setCurrentItem(1);
        mHomeViewPager.setMyScroller();

        mActivity.isViewPagerSet(true);

    }

    public void goToCheckout() {
        if (mHomeViewPager != null) {
            mHomeViewPager.setCurrentItem(2);
        }
    }

    public void goToSaleItems() {
        if (mHomeViewPager != null) {
            mHomeViewPager.setCurrentItem(1);
        }
    }

    public void goToAccounts(){
        if (mHomeViewPager != null) {
            mHomeViewPager.setCurrentItem(0);
        }
    }

    public void setViewpagerDraggable(boolean isDraggable) {

        if (mHomeViewPager != null) {
            mHomeViewPager.setSwipeable(isDraggable);
        }
    }

    public MainCustomViewPager getHomeViewPager() {
        return mHomeViewPager;
    }


    @Override
    public void hideBottomNav() {

    }

    @Override
    public void showBottomNav() {

    }

    @Override
    public void setChosenCategoryItemKey(String key) {

    }

    @Override
    public void setSelectedSubCategoryItem(View view) {

    }

    @Override
    public View getSelectedSubCategoryItem() {
        return null;
    }

    @Override
    public String getChosenCategoryItemKey() {
        return null;
    }

    @Override
    public HomeController getHomeController() {
        return null;
    }
}
