package au.com.dealsdirect.ui.controller.main;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.support.RouterPagerAdapter;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.base.BaseController;
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

    @BindView(R.id.viewpage_main)
    MainCustomViewPager mMainViewPager;

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

//        mHomeController = HomeController.newInstance();
//        mCategoriesController = CategoriesController.newInstance();

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
//                            router.setRoot(RouterTransaction.with(mCategoriesController)
//                                    .pushChangeHandler(new FadeChangeHandler(100))
//                                    .popChangeHandler(new FadeChangeHandler(100)));
                            break;
                        case 1:
//                            router.setRoot(RouterTransaction.with(mHomeController));
                            break;
                        default:
//                            router.setRoot(RouterTransaction.with(mHomeController));
                            break;
                    }
                }
            }

            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return "Page " + position;
            }
        };


        mMainViewPager.setAdapter(mViewPagerAdapter);
        mMainViewPager.setCurrentItem(1);
        mMainViewPager.setMyScroller();

        mActivity.isViewPagerSet(true);

    }

    public void goToCheckout() {
        if (mMainViewPager != null) {
            mMainViewPager.setCurrentItem(1);
        }
    }

    public void goToShops() {
        if (mMainViewPager != null) {
            mMainViewPager.setCurrentItem(0);
        }
    }

    public void setViewpagerDraggable(boolean isDraggable) {

        if (mMainViewPager != null) {
            mMainViewPager.setSwipeable(isDraggable);
        }
    }

    public MainCustomViewPager getMainViewPager() {
        return mMainViewPager;
    }

}
