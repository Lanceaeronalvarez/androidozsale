package au.com.dealsdirect.ui.controller.shops;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.paginate.Paginate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.auth.AuthHandler;
import au.com.dealsdirect.data.network.model.banner.GetBannerResponse;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.categories.CategoriesController;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsController;
import au.com.dealsdirect.ui.controller.shops.adapter.BannersAdapter;
import au.com.dealsdirect.ui.controller.shops.listener.BannerClickListener;
import au.com.dealsdirect.ui.custom.transitions.HorizontalNavTransitionChangeHandler;
import au.com.dealsdirect.ui.main.MainMvpView;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.KeyboardUtils;
import au.com.dealsdirect.utils.PaginateUtils;
import butterknife.BindView;
import butterknife.OnClick;


/**
 * dp Created by Admin on 6/6/17.
 */

public class ShopsController extends BaseController implements ShopsMvpView, BannerClickListener {

    private static final String KEY_TEXT = "ShopController.KEY_TEXT";
    private static final String KEY_CATEGORY_ID = "ShopController.KEY_CATEGORY_ID";
    private static final String KEY_CATEGORY_NAME = "ShopController.KEY_CATEGORY_NAME";
    private static final String KEY_CATEGORY_MAP = "ShopController.KEY_CATEGORY_KEY";

    @Inject
    ShopsMvpPresenter<ShopsMvpView> mPresenter;

    @BindView(R.id.controller_shop_banner_recycler)
    RecyclerView shopsControllerBannerRecyclerView;

    @BindView(R.id.partial_toolbar_search_icon)
    ImageView shopsControllerSearchView;

    @BindView(R.id.partial_toolbar_hamburger)
    ImageView mShopsControllerHamburgerView;

    @BindView(R.id.controller_shop_toolbar)
    View mShopsControllerToolbar;

    private BannersAdapter mBannersAdapter;
    private BannerClickListener mBannerClickListener;
    private Paginate.Callbacks mPaginateCallbacks;
    private Paginate mPaginateManager;

    private int page = 0;
    private boolean loadingInProgress = false;
    private boolean hasLoadedAllItems = false;

    private int newBannerCount = 10;
    private int bannerOffset = 0;
    private int bannerLimit = bannerOffset + newBannerCount;

    private String mCategoryID;
    private String mCategoryName;
    private String mCategoryKey;

    private GridLayoutManager mLayoutManager;

    private List<GetCategoryTreeResponse> mPreLoadedCategories = new LinkedList<>();
    private List<GetBannerResponse> sales = new LinkedList<>();
    private Map<String, List<GetCategoryTreeResponse>> mCategoryMap = new HashMap<>();


    public static ShopsController newInstance(GetCategoryTreeResponse getCategoryTreeResponse) {

        return new ShopsController(
                new BundleBuilder(new Bundle())
                        .putString(KEY_CATEGORY_ID, getCategoryTreeResponse.getId())
                        .putString(KEY_CATEGORY_NAME, getCategoryTreeResponse.getName())
                        .putString(KEY_CATEGORY_MAP, getCategoryTreeResponse.getKey())
                        .build());
    }

    public ShopsController() {

    }

    public ShopsController(Bundle args) {
        super(args);
        mCategoryID = getArgs().getString(KEY_CATEGORY_ID);
        mCategoryName = getArgs().getString(KEY_CATEGORY_NAME);
        mCategoryKey = getArgs().getString(KEY_CATEGORY_MAP);
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {

        View view = inflater.inflate(R.layout.controller_shop, container, false);
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
    public void onDetach(View view) {
        mPresenter.onDetach();
        hideLoading();
        super.onDetach(view);
    }

    @Override
    protected void setUp(View view) {

        mPaginateCallbacks = new Paginate.Callbacks() {
            @Override
            public void onLoadMore() {
                // Load next page of data (e.g. network or database)
                page++;
                bannerOffset += newBannerCount; //load 10 banners every page
                bannerLimit += newBannerCount;
                refresh();

            }

            @Override
            public boolean isLoading() {
                // Indicate whether new page loading is in progress or not
                return loadingInProgress;
            }

            @Override
            public boolean hasLoadedAllItems() {
                // Indicate whether all data (pages) are loaded or not
                return hasLoadedAllItems;
            }
        };

        mBannerClickListener = this;

        mBannersAdapter = new BannersAdapter(getActivity(), mPresenter, new ArrayList(), mBannerClickListener);

        if (getResources().getBoolean(R.bool.is_tablet)) {
            mLayoutManager = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);
        } else {
            mLayoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
        }

        shopsControllerBannerRecyclerView.setLayoutManager(mLayoutManager);
        shopsControllerBannerRecyclerView.setAdapter(mBannersAdapter);

        if (sales.size() == 0) {
            mPresenter.loadShopsBanner(mCategoryName, mCategoryID, 0, 0);

        } else {
            shopsControllerBannerRecyclerView.setAdapter(mBannersAdapter);

            mBannersAdapter.replace(sales);
            mPaginateManager = PaginateUtils.init(shopsControllerBannerRecyclerView, mPaginateCallbacks);

        }


        if (mPreLoadedCategories.size() == 0) {
            mPresenter.loadCategoryTree();
        }
    }

    @Override
    public void refresh() {
        loadingInProgress = true;
        mPresenter.loadShopsBanner(mCategoryName, mCategoryID, bannerOffset, bannerLimit);
    }

    @Override
    public void onBannerClicked(
            String saleId,
            String bannerTitle,
            String bannerId,
            int position,
            String imageUrl) {

        if (!mPresenter.isAccessAnonymousEnabled() && !mPresenter.isAuthorized()) {

            assert (getActivity()) != null;
            ((MainMvpView) getActivity()).showLoginController(getRouter(), new AuthHandler() {
                @Override
                public void success() {
                    getRouter().pushController(RouterTransaction.with(
                            SaleItemsController.newInstance(
                                    saleId,
                                    bannerTitle,
                                    bannerId,
                                    position,
                                    imageUrl, null))
                            .pushChangeHandler(new HorizontalChangeHandler())
                            .popChangeHandler(new HorizontalChangeHandler()));
                }

                @Override
                public void error() {

                }
            });
        } else {
            getRouter().pushController(RouterTransaction.with(
                    SaleItemsController.newInstance(saleId, bannerTitle, bannerId, position, imageUrl, null))
                    .pushChangeHandler(new HorizontalChangeHandler())
                    .popChangeHandler(new HorizontalChangeHandler()));
        }
    }

    @OnClick(R.id.partial_toolbar_hamburger)
    void onClickHamburger() {

        getRouter().pushController(RouterTransaction.with(CategoriesController.newInstance(mCategoryMap, mPreLoadedCategories))
                .pushChangeHandler(new HorizontalNavTransitionChangeHandler(100))
                .popChangeHandler(new HorizontalNavTransitionChangeHandler(100)));

//        ((MainActivity) getActivity()).hideBottomNav();
    }


    @SuppressWarnings({"ConstantConditions", "deprecation"})
    @OnClick(R.id.partial_toolbar_search_icon)
    void onSearchClick() {

        shopsControllerSearchView.setImageDrawable(getResources().getDrawable(R.drawable.ic_close));

        mShopsControllerHamburgerView.animate().rotation(-90).setDuration(200).start();

        RelativeLayout item = (RelativeLayout) getView().findViewById(R.id.controller_shop_toolbar_container);

        @SuppressLint("InflateParams")
        View child = getActivity().getLayoutInflater().inflate(R.layout.partial_toolbar_search, null);
        item.addView(child);

        child.setBackgroundColor(getResources().getColor(R.color.toolbar_active_skin));
        ImageView rightOption = (ImageView) child.findViewById(R.id.partial_toolbar_search_right_option);
        rightOption.setBackgroundColor(getResources().getColor(R.color.toolbar_active_skin));

        EditText searchField = (EditText) child.findViewById(R.id.partial_toolbar_search_field);
        searchField.setActivated(true);
        searchField.setFocusable(true);

        if (searchField.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }


        rightOption.setImageDrawable(getResources().getDrawable(R.drawable.ic_close));
        rightOption.animate().rotation(360).setDuration(200).start();

        searchField.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(searchField.getText().toString());
                return true;
            }
            return false;
        });


        rightOption.setOnClickListener(view -> {
            child.startAnimation(outToRightAnimation());
            item.removeView(child);

            //noinspection deprecation
            shopsControllerSearchView.setImageDrawable(
                    getResources().getDrawable(R.drawable.ic_search));
            rightOption.animate().rotation(-360).setDuration(200).start();
            mShopsControllerHamburgerView.animate().rotation(0).setDuration(200).start();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                getActivity().dismissKeyboardShortcutsHelper();
            }

        });

        child.startAnimation(inFromRightAnimation());
    }

    @Override
    public void showShopBanners(List<GetBannerResponse> getBannerResponses) {
        sales = getBannerResponses;
        loadingInProgress = false;

        if (page == 0) {
            Log.d("items", "replaced");
            mBannersAdapter.replace(getBannerResponses);
            mPaginateManager = PaginateUtils.init(shopsControllerBannerRecyclerView, mPaginateCallbacks);
        } else {
            Log.d("items", "added");
            mBannersAdapter.addAll(getBannerResponses);
        }
    }

    @Override
    public void storeCategories(List<GetCategoryTreeResponse> categories) {
        mPreLoadedCategories = categories;
        createCategoryMap(mPreLoadedCategories);
    }


    private Animation inFromRightAnimation() {

        Animation inFromRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromRight.setDuration(200);
        inFromRight.setInterpolator(new AccelerateInterpolator());
        return inFromRight;
    }


    private Animation outToRightAnimation() {
        Animation outtoRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoRight.setDuration(200);
        outtoRight.setInterpolator(new AccelerateInterpolator());
        return outtoRight;
    }

    private void performSearch(String searchQuery) {

        Bundle saleItemBundle = new BundleBuilder(new Bundle())
                .putString("SaleItemsController.KEY_TITLE", searchQuery)
                .putString("SaleItemsController.SEARCH_KEY", searchQuery)
                .build();

        if (!searchQuery.isEmpty())

            KeyboardUtils.hideSoftInput(getActivity());
        getRouter().pushController(RouterTransaction.with(
                SaleItemsController.newInstance(saleItemBundle))
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));
    }

    @Override
    public void onError(String message) {
        super.onError(message);

        bannerOffset -= newBannerCount;
        bannerLimit -= newBannerCount;
        page--;

        loadingInProgress = false;
        mBannersAdapter.notifyDataSetChanged();
    }

    private void createCategoryMap(List<GetCategoryTreeResponse> categories) {

        for (GetCategoryTreeResponse i : categories) {

            if (i.getChildren() != null) {
                mCategoryMap.put("shop", categories);

                int childrenSize = i.getChildren().size();
                if (childrenSize != 0) {

                    addToMap(i.getChildren());
                }
                mCategoryMap.put(i.getKey(), i.getChildren());

            }
        }

        mPreLoadedCategories = fillCategoryContent();
    }

    private void addToMap(List<GetCategoryTreeResponse> list) {

        for (GetCategoryTreeResponse i : list) {

            int childrenSize = i.getChildren().size();
            if (childrenSize != 0) {
                addToMap(i.getChildren());
            }

            mCategoryMap.put(i.getKey(), i.getChildren());

        }
    }

    private List<GetCategoryTreeResponse> fillCategoryContent() {
        return mCategoryMap.get("shop");


    }


}
