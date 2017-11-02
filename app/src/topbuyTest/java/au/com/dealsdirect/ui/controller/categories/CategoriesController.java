package au.com.dealsdirect.ui.controller.categories;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mysale.genie.animation.AnimationEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.categories.adapter.CategoriesAdapter;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;

/**
 * dp Created by Admin on 6/6/17.
 */

public class CategoriesController extends BaseController
        implements CategoriesMvpView {

    private static final String ARG_CATEGORY_CURRENT_NAME = "argCategoryCurrentName";
    private static final String ARG_CATEGORY_PREV_NAME = "argCategoryPrevName";
    private static final String ARG_CATEGORY_PREV_KEY = "argCategoryPrevKey";
    private static final String ARG_CATEGORY_CURRENT_KEY = "argCategoryCurrentKey";
    private static final String ARG_CATEGORY_POSITION = "argCategoryCurrentPosition";
    private static String chosenCategoryName="shop";
    private static String prevChosenCategoryName="";
    private static String chosenCategoryKey="";
    private static String prevChosenCategoryKey="";
    private static String lastOptionCategoryName="shop";

    private View mRootView;
    private CategoriesAdapter mAdapter;
    private List<GetCategoryTreeResponse> mCategories;
    private Map<String,List<GetCategoryTreeResponse>> mCategoryMap = new HashMap<>();
    private int itemPosition = 0;
    private int categoriesChangeCount = 0;
    private Bundle fragmentBundle;

    public static String getChosenCategoryName(){
        return chosenCategoryName;
    }

    public static String getLastOptionCategoryName(){
        return lastOptionCategoryName;
    }

    public static void setLastOptionCategoryName(String val){
        lastOptionCategoryName = val;
    }

    public static String getPrevChosenCategoryKey(){
        return prevChosenCategoryKey;
    }

    public void resetHeaderTextViewTransitionName(String text){
        if(headerCategoryTextView!=null){
            ViewCompat.setTransitionName(headerCategoryTextView,text);
        }
    }

    @Inject
    CategoriesMvpPresenter<CategoriesMvpView> mPresenter;

    @BindView(R.id.root_categories_layout)
    LinearLayout mRootLayout;
    @BindView(R.id.current_category_text)
    TextView headerCategoryTextView;
    @BindView(R.id.prev_category_text)
    TextView prevHeaderCategoryTextView;
    @BindView(R.id.header_underline)
    View headerUnderlineView;
    @BindView(R.id.categories_recyclerview)
    RecyclerView mRecyclerView;

    public static CategoriesController newInstance() {
        return new CategoriesController(
                new BundleBuilder(new Bundle())
                        .build());
    }


    public CategoriesController(Bundle args) {
        super(args);
        chosenCategoryKey = args.getString(ARG_CATEGORY_CURRENT_KEY,"");
        chosenCategoryName = args.getString(ARG_CATEGORY_CURRENT_NAME,"shop");
        prevChosenCategoryKey = args.getString(ARG_CATEGORY_PREV_KEY,"");
        prevChosenCategoryName = args.getString(ARG_CATEGORY_PREV_NAME,"");
        itemPosition = args.getInt(ARG_CATEGORY_POSITION,0);
    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);
        mPresenter.onAttach(this);
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {

        View view = inflater.inflate(R.layout.controller_categories, container, false);
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);

        mActivity.setDraggableViewPager(true);
        mActivity.setCategoriesRouter(getRouter());
        hideKeyboard();
        mPresenter.callGetCategoryTree();

//        com.mysale.genie.animation.AnimationEngine.Builder.animate(headerUnderlineView)
//                .scaleX(65)
//                .setDuration(300)
//                .build()
//                .start();

        setUp(view);
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);

    }

    @Override
    protected void onActivityResumed(@NonNull Activity activity) {
        super.onActivityResumed(activity);
    }

    @Override
    protected void setUp(View view) {
        AnimationEngine.Builder.animate(headerUnderlineView)
                .scaleX(65)
                .setDuration(300)
                .build()
                .start();

        initUIValues();
    }

    @Override
    public void showCategories(List<GetCategoryTreeResponse> categories) {
        mCategories = categories;
        createCategoryMap(mCategories);
        setupCategories();
    }

    @Override
    public void showNoNetworkLayout() {

    }

    @Override
    public void hideNoNetworklayout() {

    }

    @Override
    public void onCategoryClicked(au.com.dealsdirect.ui.controller.categories.adapter.CategoriesAdapter.CategoriesViewHolder holder, int position, String categoryName, String categoryKey) {
        prevChosenCategoryName = chosenCategoryName;
        prevChosenCategoryKey = chosenCategoryKey;
        chosenCategoryName = categoryName;
        chosenCategoryKey = categoryKey;

//        //post chosen category key to shop fragment to call api
//        GDebug.log("categoryFilterSelected",categoryKey);
//        mShopPresenter.executeCategoryChangeApiCall(categoryKey);

        //check if is last option
        boolean isOptionLastContent = isOptionLastContent(categoryKey);

        if(isOptionLastContent) {

//            GDebug.log(FilterCategoriesFragment.class.getSimpleName(),"isOptionLastContent: "+isOptionLastContent);

            //collapse fragment
//            mBottomSheetBehavior.setState(CoordinatorLayoutAsBottomSheetBehavior.STATE_COLLAPSED);

            lastOptionCategoryName = categoryName;
//            Log.d("lastOptionCategoryName",lastOptionCategoryName+"onClicked");

        }else {

            //reset lastOptionCategoryName
            lastOptionCategoryName = "";
//            Log.d("lastOptionCategoryName",lastOptionCategoryName+"onCreate");
//            FilterCategoriesFragment fragment = FilterCategoriesFragment
//                    .newInstance(baseActivity,
//                            mCategoryList,
//                            position,
//                            prevChosenCategoryName,
//                            chosenCategoryName,
//                            prevChosenCategoryKey,
//                            chosenCategoryKey,
//                            mShopPresenter);

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                Log.d("holderTextTransition", holder.categoryText.getTransitionName());
//
//                fragment.setSharedElementEnterTransition(new FilterCategoriesTransition());
//                fragment.setSharedElementReturnTransition(new FilterCategoriesTransition());
//                fragment.setEnterTransition(new FilterCategoriesTransition());
//                setExitTransition(new FilterCategoriesTransition());
//            }


            Bundle bundle = new Bundle();
            bundle.putString(ARG_CATEGORY_PREV_NAME, prevChosenCategoryName);
            bundle.putString(ARG_CATEGORY_CURRENT_NAME, chosenCategoryName);

            bundle.putString(ARG_CATEGORY_PREV_KEY, prevChosenCategoryKey);
            bundle.putString(ARG_CATEGORY_CURRENT_KEY, chosenCategoryKey);
            bundle.putInt(ARG_CATEGORY_POSITION, position);

            GateKeeper.push(getRouter(), GateKeeper.Destination.CATEGORIES, bundle);
        }

//            mShopPresenter.changeFilterCategory(fragment,holder.categoryText);
    }

    public void initUIValues() {

        if (chosenCategoryName.equals("shop")) {
            prevHeaderCategoryTextView.setVisibility(View.GONE);
        } else {
            prevHeaderCategoryTextView.setText(prevChosenCategoryName);

        }

        CategoriesColorHelper categoriesColorHelper = new CategoriesColorHelper();
        String[] colorSet = categoriesColorHelper.getBackgroundColor(categoriesChangeCount);

        if (itemPosition >= colorSet.length) {
            while (itemPosition >= colorSet.length) {
                itemPosition = itemPosition - colorSet.length;
            }
        }

        int color = Color.parseColor(colorSet[itemPosition]);

        //set fragment background color
        mRootLayout.setBackgroundColor(color);

        //TODO
        //update welcome header background color in shop
//        if(chosenCategoryName.equals("shop")){
//            color = getResources().getColor(R.color.shop_banner_divider_default);
//        }
//        mShopPresenter.categoryChangeUpdateShopUI(chosenCategoryName,color);
//        updateWelcomeHeaderBackgroundColor(color);
        headerCategoryTextView.setText(chosenCategoryName);
        categoriesChangeCount++;
    }

    private void createCategoryMap(List<GetCategoryTreeResponse> categories) {
        mCategoryMap.put("shop", categories);

        for (GetCategoryTreeResponse i : categories) {

            if (i.getChildren() != null && !i.getChildren().isEmpty()){

                addToMap(i.getChildren());
            }

            mCategoryMap.put(i.getKey(),i.getChildren());
        }

    }

    private void addToMap(List<GetCategoryTreeResponse> list) {
        for (GetCategoryTreeResponse i : list) {

            if (i.getChildren() != null && !i.getChildren().isEmpty()){

                addToMap(i.getChildren());
            }

            mCategoryMap.put(i.getKey(),i.getChildren());

        }
    }

    private List<GetCategoryTreeResponse> fillCategoryContent() {

        if (chosenCategoryName.equals("shop")){
            return mCategoryMap.get(chosenCategoryName);
        }else if (mCategoryMap.get(chosenCategoryKey)!= null){
            return mCategoryMap.get(chosenCategoryKey);
        }else {
            return new ArrayList<>();
        }
    }

    private void setupCategories(){
        mAdapter = new CategoriesAdapter(fillCategoryContent(), "shop", this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    }

    private boolean isOptionLastContent(String option){

        return mCategoryMap.get(option) == null || mCategoryMap.get(option).size() == 0;
    }

}
