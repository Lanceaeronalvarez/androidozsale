package au.com.dealsdirect.ui.controller.main;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.bluelinelabs.conductor.support.RouterPagerAdapter;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.bannerfilter.BannerFiltersController;
import au.com.dealsdirect.ui.controller.checkout.checkouthost.CheckoutHostController;
import au.com.dealsdirect.ui.controller.home.HomeController;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import butterknife.BindView;

/**
 * dp Created by Admin on 6/6/17.
 */

public class MainController extends BaseController implements MainMvpView {

    public static final String TAG = "Home";

    public static final int BANNER_FILTER_INDEX = 0;

    public static final int SHOP_INDEX = 1;

    private static final int VIEWPAGER_SIZE = 2;

    @Inject
    MainMvpPresenter<MainMvpView> mPresenter;

    @BindView(R.id.home_viewpager)
    MainCustomViewPager mHomeViewPager;
    private boolean mIsHomeViewPagerDragging = false;

    private HomeController mHomeController;

    private BannerFiltersController mBannerFiltersController;

    private CheckoutHostController mCheckoutHostController;

    private View mLastSelectedSubCategoryItem;

    private View mPreviousSubcategoryItem;

    private String mChosenSubCategoryItemKey = "";

    private boolean mHasSavedInstance = false;

    @BindView(R.id.controller_home_bottom_nav)
    AHBottomNavigation mBottomNavigationView;

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

        if (!mHasSavedInstance || mHomeController == null) {
            mHomeController = HomeController.newInstance();
        }

        if (!mHasSavedInstance || mBannerFiltersController == null) {
            mBannerFiltersController = BannerFiltersController.newInstance();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        setupViewPager();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupViewPager() {

        RouterPagerAdapter mViewPagerAdapter = new RouterPagerAdapter(this) {
            @Override
            public void configureRouter(@NonNull Router router, int position) {
                Controller controller = position == BANNER_FILTER_INDEX ? mBannerFiltersController : mHomeController;

                if (!router.hasRootController()) {
                    router.setRoot(RouterTransaction.with(controller)
                            .pushChangeHandler(new FadeChangeHandler(100))
                            .popChangeHandler(new FadeChangeHandler(100)));
                }

                CommonControllerChangeListener.addToRouter(router);
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
        mHomeViewPager.setCurrentItem(SHOP_INDEX);
        mHomeViewPager.setMyScroller();

        mHomeViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == BANNER_FILTER_INDEX &&
                        mBannerFiltersController != null &&
                        mBannerFiltersController.isViewAttached()) {
                    mBannerFiltersController.refreshContents();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mHomeViewPager.setOnTouchListener((v, event) -> {
            if (mHomeViewPager.isSwipeable()) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        mIsHomeViewPagerDragging = false;
                        break;
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        mIsHomeViewPagerDragging = true;
                        break;
                    default:
                        break;
                }
            } else {
                mIsHomeViewPagerDragging = false;
            }
            return false;
        });
    }

    public boolean isHomeViewPagerDragging() {
        return mIsHomeViewPagerDragging;
    }

    public boolean shouldBottomNavigationViewEnabled() {
        return mShouldBottomNavigationViewEnabled;
    }

    public void setShouldBottomNavigationViewEnabled(boolean enabled) {
        mShouldBottomNavigationViewEnabled = enabled;
    }

    public void setViewpagerDraggable(boolean isDraggable) {
        if (mHomeViewPager != null) {
            mHomeViewPager.setIsSwipeable(isDraggable);
        }
        if (!isDraggable) {
            mIsHomeViewPagerDragging = false;
        }
    }

    public boolean getViewpagerDraggable() {
        if (mHomeViewPager != null) {
            return mHomeViewPager.isSwipeable();
        } else {
            return false;
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(BundleKeys.KEY_HAS_SAVED_INSTANCE, true);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mHasSavedInstance = savedInstanceState.getBoolean(BundleKeys.KEY_HAS_SAVED_INSTANCE);
    }

    public void hideBottomNav() {
        if (mHomeController != null) mHomeController.hideBottomNav();
    }

    public void showBottomNav() {
        if (mHomeController != null) mHomeController.showBottomNav();
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

    public String getChosenCategoryItemKey() {
        return mChosenSubCategoryItemKey;
    }

    public HomeController getHomeController() {
        return mHomeController;
    }

    public MainCustomViewPager getHomeViewPager() {
        return mHomeViewPager;
    }

    public void goToPage(int position) {
        mHomeViewPager.setCurrentItem(position);
        if (position == BANNER_FILTER_INDEX) {
            mBannerFiltersController.refreshContents();
        }
    }

    public AHBottomNavigation getBottomNav() {
        return mBottomNavigationView;
    }

    public void setHomeController(HomeController homeController) {
        mHomeController = homeController;
    }

    public void setBannerFiltersController(BannerFiltersController bannerFiltersController) {
        mBannerFiltersController = bannerFiltersController;
    }

    public BannerFiltersController getBannerFiltersController() {
        return mBannerFiltersController;
    }

    public void setCheckoutHostController(CheckoutHostController checkoutHostController) {
        mCheckoutHostController = checkoutHostController;
    }

    public CheckoutHostController getCheckoutHostController() {
        return mCheckoutHostController;
    }

    public Controller getCurrentViewPagerController() {
        if (getHomeViewPager() == null) {
            return null;
        }
        if (SHOP_INDEX == getHomeViewPager().getCurrentItem()) {
            return mHomeController;
        } else {
            return mBannerFiltersController;
        }
    }
}
