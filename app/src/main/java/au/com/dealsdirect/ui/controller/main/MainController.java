package au.com.dealsdirect.ui.controller.main;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.bluelinelabs.conductor.support.RouterPagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.banner.GetBannerResponse;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.categories.CategoriesController;
import au.com.dealsdirect.ui.controller.home.HomeController;
import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;

/**
 * dp Created by Admin on 6/6/17.
 */

public class MainController extends BaseController implements MainMvpView {

    public static final String TAG = "MainController";
    private static final String KEY_TEXT = "MainController.KEY_TEXT";

    private String mChosenSubCategoryItemKey = "";
    private String mChosenSubCategoryKey = "";

    @Inject
    MainMvpPresenter<MainMvpView> mPresenter;

    public MainCustomViewPager getHomeViewPager() {
        return mHomeViewPager;
    }

    @BindView(R.id.home_viewpager)
    MainCustomViewPager mHomeViewPager;

    @BindView(R.id.controller_home_splash_container)
    LinearLayout mHomeSplashContainer;

    private RouterPagerAdapter mViewPagerAdapter = null;

    private HomeController mHomeController;
    private CategoriesController mCategoriesController;
    private MainActivity mActivity;

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
        mActivity = (MainActivity) getActivity();
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
        mCategoriesController = CategoriesController.newInstance();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getActivity().getResources().getColor(R.color.colorAccent));
        }
        setupViewPager();

    }

    private void createCategoryMap(List<GetCategoryTreeResponse> categories) {

        List<GetCategoryTreeResponse> newList;
        mCategoryMap.put("shop", categories);

        for (GetCategoryTreeResponse i : categories) {

            newList = updateCategoryChildren(i);

            if (newList != null) {

                int childrenSize = newList.size();
                if (childrenSize != 1) {

                    addToMap(newList);
                }

                mCategoryMap.put(i.getKey(), newList);
            }
        }
    }

    private void addToMap(List<GetCategoryTreeResponse> list) {
        List<GetCategoryTreeResponse> newList2;

        for (GetCategoryTreeResponse i : list) {
            newList2 = updateCategoryChildren(i);

            int childrenSize = newList2.size();
            if (childrenSize != 1) {
                addToMap(newList2);
            }

            mCategoryMap.put(i.getKey(), newList2);

        }
    }

    private List<GetCategoryTreeResponse> fillCategoryContent() {

        return mCategoryMap.get("shop");
    }

    private void setupViewPager() {

        mViewPagerAdapter = new RouterPagerAdapter(this) {
            @Override
            public void configureRouter(@NonNull Router router, int position) {
                if (!router.hasRootController()) {
                    switch (position) {
                        case 0:
                            router.setRoot(RouterTransaction.with(mCategoriesController)
                                    .pushChangeHandler(new FadeChangeHandler(100))
                                    .popChangeHandler(new FadeChangeHandler(100)));
                            break;
                        case 1:
                            router.setRoot(RouterTransaction.with(mHomeController));
                            break;
                        default:
                            router.setRoot(RouterTransaction.with(mHomeController));
                            break;
                    }
                }
            }

            @Override
            public int getCount() {
//                if (mPreLoadedCategories.size() != 0) {
//                    return 2;
//                } else {
//                    return 1;
//                }
                return 2;
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
        //noinspection deprecation

    }

    public void goToCategories() {
        mHomeViewPager.setCurrentItem(0);
    }

    public void goToShops() {
        mHomeViewPager.setCurrentItem(1);
    }

    public void setViewpagerDraggable(boolean isDraggable) {

      if (mHomeViewPager!=null)
          mHomeViewPager.setSwipeable(isDraggable);
    }

    public int getActiveItem() {
        return mHomeViewPager.getCurrentItem();
    }

    public void showSaleItems() {
        mHomeViewPager.setCurrentItem(1);

    }

    private List<GetCategoryTreeResponse> updateCategoryChildren(GetCategoryTreeResponse categoryTree) {
        if (!categoryTree.getName().equals("All")) {
            GetCategoryTreeResponse getCategoryTreeResponse = new GetCategoryTreeResponse();
            getCategoryTreeResponse.setName("All");
            getCategoryTreeResponse.setKey(categoryTree.getKey());
            getCategoryTreeResponse.setChildren(new ArrayList<>());
            getCategoryTreeResponse.setNodeType("usual");

            List<GetCategoryTreeResponse> newList = new ArrayList<>();

            if (categoryTree.getChildren() != null) {
                for (int i = 0; i < categoryTree.getChildren().size() + 1; i++) {

                    if (i == 0) {
                        newList.add(getCategoryTreeResponse);

                    } else {

                        newList.add(categoryTree.getChildren().get(i - 1));
                    }
                }
            }
            return newList;
        }
        return categoryTree.getChildren();
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

    public void setChosenCategoryKey(String key){
        mChosenSubCategoryKey = key;
    }

    public void setSelectedSubCategoryItem(View view){
        mLastSelectedSubCategoryItem = view;
    }

    public View getSelectedSubCategoryItem(){
        return mLastSelectedSubCategoryItem;
    }

    public String getChosenCategoryItemKey() {
        return mChosenSubCategoryItemKey;
    }

    public String getChosenCategoryKey(){ return mChosenSubCategoryKey;}



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
}
