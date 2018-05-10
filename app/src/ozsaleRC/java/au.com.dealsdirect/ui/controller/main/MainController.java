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
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.bluelinelabs.conductor.support.RouterPagerAdapter;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.bannerfilter.BannerFiltersController;
import au.com.dealsdirect.ui.controller.home.HomeController;
import au.com.dealsdirect.utils.BundleBuilder;
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

    private HomeController mHomeController;

    private BannerFiltersController mBannerFiltersController;

    private View mLastSelectedSubCategoryItem;

    private String mChosenSubCategoryItemKey = "";


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

        mHomeController = HomeController.newInstance();

        mBannerFiltersController = BannerFiltersController.newInstance();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        setupViewPager();
    }

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

    }

    public void setViewpagerDraggable(boolean isDraggable) {
        if (mHomeViewPager != null) {
            mHomeViewPager.setSwipeable(isDraggable);
        }
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
    }

}
