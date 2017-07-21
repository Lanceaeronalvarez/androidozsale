package au.com.dealsdirect.ui.controller.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import au.com.dealsdirect.ui.controller.shops.ShopsController;
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

    @Inject
    MainMvpPresenter<MainMvpView> mPresenter;

    @BindView(R.id.home_viewpager)
    MainCustomViewPager mHomeViewPager;

    @BindView(R.id.controller_home_splash_container)
    LinearLayout mHomeSplashContainer;

    private Router mChildRouter;
    private List<GetCategoryTreeResponse> mPreLoadedCategories = new LinkedList<>();
    private List<GetBannerResponse> sales = new LinkedList<>();
    private Map<String, List<GetCategoryTreeResponse>> mCategoryMap = new HashMap<>();
    private RouterPagerAdapter mViewPagerAdapter = null;

    private HomeController mHomeController;
    private CategoriesController mCategoriesController;

    public static MainController newInstance() {

        return new MainController(
                new BundleBuilder(new Bundle())
                        .build());
    }

    public MainController(Bundle args) {
        super(args);
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
        if (mPreLoadedCategories.size() == 0) {
            mPresenter.loadCategoryTree();
        }
        setUp(view);
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @Override
    protected void setUp(View view) {
//        getRouter().setRoot(RouterTransaction.with(new ShopsController()));
        setupViewPager();
    }


    @Override
    public void storeCategories(List<GetCategoryTreeResponse> categories) {
        mPreLoadedCategories = categories;
        createCategoryMap(mPreLoadedCategories);
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

        mPreLoadedCategories = fillCategoryContent();
        setupViewPager();
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

    private void setupViewPager(){

        mViewPagerAdapter = new RouterPagerAdapter(this) {
            @Override
            public void configureRouter(@NonNull Router router, int position) {
                if (!router.hasRootController()) {

                    mHomeController = HomeController.newInstance();
                    if (mPreLoadedCategories.size()!=0) {
                        mCategoriesController = CategoriesController.newInstance(
                                mCategoryMap,
                                mPreLoadedCategories);

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
                    }else{
                        router.setRoot(RouterTransaction.with(new ShopsController()));

                    }
                }
            }

            @Override
            public int getCount() {
                if (mPreLoadedCategories.size()!=0){
                    return 2;
                }else{
                    return 1;
                }
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return "Page " + position;
            }
        };


        mHomeViewPager.setAdapter(mViewPagerAdapter);
        mHomeViewPager.setCurrentItem(1);
        mHomeViewPager.setMyScroller();
        //noinspection deprecation
        mHomeViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                assert (getActivity()) != null;
                ((MainActivity)getActivity()).setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    public void goToCategories(){
        mHomeViewPager.setCurrentItem(0);
    }

    public void goToShops(){
        mHomeViewPager.setCurrentItem(1);
    }

    public void setViewpagerDraggable(boolean isDraggable){
        mHomeViewPager.setSwipeable(isDraggable);
    }

    public int getActiveItem(){
        return mHomeViewPager.getCurrentItem();
    }

    public void showSaleItems(){
        mHomeViewPager.setCurrentItem(1);

    }

    private List<GetCategoryTreeResponse> updateCategoryChildren(GetCategoryTreeResponse categoryTree){
        if (!categoryTree.getName().equals("All")) {
            GetCategoryTreeResponse getCategoryTreeResponse = new GetCategoryTreeResponse();
            getCategoryTreeResponse.setName("All");
            getCategoryTreeResponse.setKey(categoryTree.getKey());
            getCategoryTreeResponse.setChildren(new ArrayList<>());
            getCategoryTreeResponse.setNodeType("usual");

            List<GetCategoryTreeResponse> newList = new ArrayList<>();

            if (categoryTree.getChildren()!=null){
                for (int i=0;i <categoryTree.getChildren().size()+1;i++){

                    if (i==0) {
                        newList.add(getCategoryTreeResponse);

                    } else{

                        newList.add(categoryTree.getChildren().get(i-1));
                    }
                }
            }
            return newList;
        }
        return categoryTree.getChildren();
    }

    public void hideBottomNav(){
        mHomeController.hideBottomNav();
    }

    public void showBottomNav(){
        mHomeController.showBottomNav();
    }

    public void setChosenCategoryItemKey(String key){
        mChosenSubCategoryItemKey = key;
    }

    public String getChosenCategoryItemKey(){
        return mChosenSubCategoryItemKey;
    }

    public String getCategoryParentKey(){
        char c = '>';
        int charCount = 0;
        String newString = "";
        for (int i = 0; i < mChosenSubCategoryItemKey.length(); i++){
            String getChar = String.valueOf(mChosenSubCategoryItemKey.charAt(i));
            if (!getChar.equals(String.valueOf(c))){
                newString = newString + mChosenSubCategoryItemKey.charAt(i);

            } else{
                charCount++;
                if (charCount>3){
                    break;
                }
                newString = newString + mChosenSubCategoryItemKey.charAt(i);

            }
        }
        return newString;
    }
}
