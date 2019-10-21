package au.com.dealsdirect.ui.controller.main;

import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.bluelinelabs.conductor.support.RouterPagerAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.categories.CategoriesController;
import au.com.dealsdirect.ui.controller.home.HomeController;
import au.com.dealsdirect.utils.BundleBuilder;
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

    private HomeController mHomeController;

    private View mLastSelectedSubCategoryItem;
    private Map<String, List<GetCategoryTreeResponse>> mCategoryMap = new HashMap<>();

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
                if (!router.hasRootController()) {
                    router.setRoot(RouterTransaction.with(mHomeController)
                            .pushChangeHandler(new FadeChangeHandler(100))
                            .popChangeHandler(new FadeChangeHandler(100)));
                }
            }

            @Override
            public int getCount() {
                return 1;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return "Page " + position;
            }
        };


        mHomeViewPager.setAdapter(mViewPagerAdapter);
        mHomeViewPager.setCurrentItem(0);
        mHomeViewPager.setMyScroller();

        mActivity.isViewPagerSet(true);

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


    public String getCategoryParentKey() {
        char c = '>';
        int charCount = 0;
        String newString = "";
        for (int i = 0; i < mChosenSubCategoryItemKey.length(); i++) {
            String getChar = String.valueOf(mChosenSubCategoryItemKey.charAt(i));
            if (!getChar.equals(String.valueOf(c))) {
                newString = newString + mChosenSubCategoryItemKey.charAt(i);

            } else {
                charCount++;
                if (charCount > 3) {
                    break;
                }
                newString = newString + mChosenSubCategoryItemKey.charAt(i);

            }
        }
        return newString;
    }

    public HomeController getHomeController() {
        return mHomeController;
    }

    public MainCustomViewPager getHomeViewPager() {
        return mHomeViewPager;
    }

}
