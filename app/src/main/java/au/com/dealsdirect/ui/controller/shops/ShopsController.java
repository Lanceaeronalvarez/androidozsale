package au.com.dealsdirect.ui.controller.shops;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
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
import au.com.dealsdirect.ui.base.BasePullToRefreshController;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsController;
import au.com.dealsdirect.ui.controller.shops.adapter.BannersAdapter;
import au.com.dealsdirect.ui.controller.shops.listener.BannerClickListener;
import au.com.dealsdirect.ui.custom.transitions.SimpleChangeHandler;
import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.ui.main.MainMvpView;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.DialogUtils;
import au.com.dealsdirect.utils.PaginateUtils;
import butterknife.BindView;
import butterknife.OnClick;


/**
 * dp Created by Admin on 6/6/17.
 */

public class ShopsController extends BasePullToRefreshController implements ShopsMvpView, BannerClickListener {

    public static final String TAG = "ShopsController";
    private static final String KEY_TEXT = "ShopController.KEY_TEXT";
    private static final String KEY_CATEGORY_ID = "ShopController.KEY_CATEGORY_ID";
    private static final String KEY_CATEGORY_NAME = "ShopController.KEY_CATEGORY_NAME";
    private static final String KEY_CATEGORY_MAP = "ShopController.KEY_CATEGORY_KEY";

    @Inject
    ShopsMvpPresenter<ShopsMvpView> mPresenter;

    @BindView(R.id.controller_shop_banner_recycler)
    RecyclerView shopsControllerBannerRecyclerView;

    @BindView(R.id.partial_toolbar_search_icon)
    ImageButton shopsControllerSearchView;

    @BindView(R.id.partial_toolbar_hamburger)
    ImageButton mShopsControllerHamburgerView;

    @BindView(R.id.partial_toolbar_logo)
    ImageView mShopsControllerToolbarLogo;

    @BindView(R.id.partial_toolbar_logo_title_view)
    TextView mShopsControllerToolbarTextView;

    private BannersAdapter mBannersAdapter;
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

    private View child;
    private RelativeLayout item;
    private ImageView rightOption;

    private int mBannerClickCounter = 0;
    private boolean isRefreshShop = false;
    private int mPaginateManagerCounter = 0;

    BannerClickListener mBannerClickListener;

    @Override
    protected void onAttach(@NonNull View view) {
        mPresenter.onAttach(this);
        assert (getActivity()) != null;

        mBannerClickCounter = 0;
        ((MainActivity) getActivity()).setShopController(this);
        super.onAttach(view);
    }

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
        View view = super.inflateView(inflater, container);

        fillToolbar(inflater.inflate(R.layout.partial_toolbar_logo, container, false));
        fillContent(inflater.inflate(R.layout.controller_shop, container, false));

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
        hideLoading();
        super.onDetach(view);
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @Override
    protected void setUp(View view) {

        assert (getActivity()) != null;
        ((MainActivity) getActivity()).getMainController().showBottomNav();
        ((MainActivity) getActivity()).setDraggableViewPager(true);
        hideKeyboard();

        mPaginateCallbacks = new Paginate.Callbacks() {
            @Override
            public void onLoadMore() {
                // Load next page of data (e.g. network or database)
                page++;
                bannerOffset += newBannerCount; //load 10 banners every page
                bannerLimit = newBannerCount;
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

        mBannersAdapter = new BannersAdapter(getActivity(), mPresenter, sales, this);
        shopsControllerBannerRecyclerView.setLayoutManager(mLayoutManager);
        shopsControllerBannerRecyclerView.setAdapter(mBannersAdapter);

        if (sales.isEmpty()) {
            shopsControllerBannerRecyclerView.setVisibility(View.GONE);
            mPresenter.loadShopsBanner(mCategoryName, mCategoryID, bannerOffset, bannerLimit);
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
    public void unBindPaginate() {
        if(mPaginateManager != null) {
            mPaginateManager.unbind();
        }
    }

    @Override
    public void onBannerClicked(
            String saleId,
            String bannerTitle,
            String bannerId,
            int position,
            String imageUrl,
            boolean isAvailable) {

        if (mBannerClickCounter != 1) {
            mBannerClickCounter = +1;


            List<String> names = new ArrayList<>();
            names.add(bannerId + position);
            if (!mPresenter.isAccessAnonymousEnabled() && !mPresenter.isAuthorized()) {

                // Invoke login if no auth or not an open app
                assert (getActivity()) != null;
                ((MainMvpView) getActivity()).showLoginController(getRouter(), new AuthHandler() {
                    @Override
                    public void success() {
                        ((MainActivity) getActivity()).callGCMRegisterSubscriber();
                        ((MainActivity) getActivity()).getHomeRouter()
                                .pushController(RouterTransaction.with(
                                        SaleItemsController.newInstance(
                                                saleId,
                                                bannerTitle,
                                                bannerId,
                                                position,
                                                imageUrl, null))
                                        .tag(getActivity().getString(R.string.sale_items_controller_tag))
                                        .pushChangeHandler(new HorizontalChangeHandler())
                                        .popChangeHandler(new HorizontalChangeHandler()));
                    }

                    @Override
                    public void error() {

                    }
                });
            } else {

// Check if sale is available
                //TODO: Need computation for date and time when sale response is cached
                if (isAvailable) {
                    assert (getActivity()) != null;
                    ((MainActivity) getActivity())
                            .getHomeRouter()
                            .pushController(RouterTransaction.with(
                                    SaleItemsController.newInstance(
                                            saleId,
                                            bannerTitle,
                                            bannerId,
                                            position,
                                            imageUrl,
                                            null))
                                    .tag(getActivity().getString(R.string.sale_items_controller_tag))
                                    .pushChangeHandler(new HorizontalChangeHandler())
                                    .popChangeHandler(new HorizontalChangeHandler()));
                } else {
                    DialogUtils.showYesDialog(getActivity(), "", "Sale is currently closed", "OK", (dialogInterface, i) -> dialogInterface.dismiss());
                }
            }
        }
    }

    @OnClick(R.id.partial_toolbar_hamburger)
    void onClickHamburger() {

        assert (getActivity()) != null;
        ((MainActivity) getActivity()).setRootViewpagerItem(0);
    }

    @OnClick(R.id.partial_toolbar_logo)
    void onClickLogo() {
        shopsControllerBannerRecyclerView.smoothScrollToPosition(0);
        shopsControllerBannerRecyclerView.postDelayed(() -> shopsControllerBannerRecyclerView.scrollToPosition(0), 500);
    }

    @SuppressWarnings({"ConstantConditions", "deprecation"})
    @OnClick(R.id.partial_toolbar_search_icon)
    void onSearchClick() {

        Bundle saleItemBundle = new BundleBuilder(new Bundle())
                .putString("SaleItemsController.KEY_TITLE", "")
                .putString("SaleItemsController.SEARCH_KEY", "")
                .putBoolean("SaleItemsController.FROM_SHOP_SEARCH", true)
                .build();
        Router router = getRouter();

        router.pushController(RouterTransaction.with(
                SaleItemsController.newInstance(saleItemBundle))
                .tag(getActivity().getString(R.string.sale_items_controller_tag))
                .pushChangeHandler(new SimpleChangeHandler())
                .popChangeHandler(new FadeChangeHandler()));
    }

    @Override
    public void showShopBanners(List<GetBannerResponse> getBannerResponses) {
        shopsControllerBannerRecyclerView.setVisibility(View.VISIBLE);

        loadingInProgress = false;

        if (page == 0 || isRefreshShop) {
            mBannersAdapter.replace(getBannerResponses);
            mPaginateManager = null;
            mPaginateManager = PaginateUtils.init(shopsControllerBannerRecyclerView, mPaginateCallbacks);
            isRefreshShop = false;
        } else {

            mBannersAdapter.addAll(getBannerResponses);

            if (getBannerResponses.isEmpty()) {
                hasLoadedAllItems = true;
            }
        }

        sales = mBannersAdapter.getData();
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


    @Override
    public void onError(String message) {
        super.onError(message);

        bannerOffset -= newBannerCount;
        bannerLimit = newBannerCount;
        page--;

        loadingInProgress = false;
        if (mBannersAdapter != null) {
            mBannersAdapter.notifyDataSetChanged();
        }
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

    public void goToItemsFromCategories(Bundle bundle) {
        //noinspection ConstantConditions
        getRouter().pushController(RouterTransaction.with(
                SaleItemsController.newInstance(bundle))
                .tag(getResources().getString(R.string.sale_items_controller_tag))
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));
    }

    public void goToSalesFromCategories(GetCategoryTreeResponse getCategoryTreeResponse) {
        mPresenter.onAttach(this);
        if (shopsControllerBannerRecyclerView != null) {
            shopsControllerBannerRecyclerView.setVisibility(View.GONE);
        }

        if (getCategoryTreeResponse.getKey() != null) {

            mPresenter.loadShopsBanner(getCategoryTreeResponse.getKey(), getCategoryTreeResponse.getId(), 0, 0);
            if (mShopsControllerToolbarLogo != null)
                mShopsControllerToolbarLogo.setVisibility(View.GONE);
            mShopsControllerToolbarTextView.setVisibility(View.VISIBLE);
            mShopsControllerToolbarTextView.setText(getCategoryParentKey(getCategoryTreeResponse.getKey()));
            mShopsControllerHamburgerView.setImageDrawable(getActivity().getDrawable(R.drawable.ic_pink_chevron));
            shopsControllerSearchView.setVisibility(View.INVISIBLE);
            ((MainActivity) getActivity()).setIsFromCategories(true);
        } else {
            assert (getActivity()) != null;
            ((MainActivity) getActivity()).setIsFromCategories(false);
            loadShopBanners();
        }
    }
//
//    @SuppressWarnings({"deprecation", "ConstantConditions"})
//    public void showSearchToolbar() {
//        if (shopsControllerSearchView != null)
//            shopsControllerSearchView.setImageDrawable(getResources().getDrawable(R.drawable.ic_close));
//
//        mShopsControllerHamburgerView.animate().rotation(-90).setDuration(200).start();
//
//        //noinspection ConstantConditions
//        item = (RelativeLayout) getToolbar();
//
//        //noinspection ConstantConditions
//        child = getActivity().getLayoutInflater().inflate(R.layout.partial_toolbar_search, null);
//        item.addView(child);
//
//        child.setBackgroundColor(getResources().getColor(R.color.toolbar_active_skin));
//        rightOption = (ImageView) child.findViewById(R.id.partial_toolbar_search_right_option);
//        rightOption.setBackgroundColor(getResources().getColor(R.color.toolbar_active_skin));
//
//        EditText searchField = (EditText) child.findViewById(R.id.partial_toolbar_search_field);
//        searchField.setActivated(true);
//        searchField.setFocusable(true);
//
////        if (searchField.requestFocus()) {
////            KeyboardUtils.showSoftInput(searchField, getActivity());
////        }
//
//        //noinspection deprecation
//        rightOption.setImageDrawable(getResources().getDrawable(R.drawable.ic_close));
//        rightOption.animate().rotation(360).setDuration(200).start();
//
//        searchField.setOnEditorActionListener((v, actionId, event) -> {
//            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                performSearch(searchField.getText().toString());
//                return true;
//            }
//            return false;
//        });

//        mShopsControllerToolbarLogo.setVisibility(View.GONE);
//    }

//    public void hideSearchToolbar() {
//        child.startAnimation(outToRightAnimation());
//        item.removeView(child);
//
//        //noinspection deprecation,ConstantConditions
//        shopsControllerSearchView.setImageDrawable(
//                getResources().getDrawable(R.drawable.ic_search));
//        rightOption.animate().rotation(-360).setDuration(200).start();
//        mShopsControllerHamburgerView.animate().rotation(0).setDuration(200).start();
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            //noinspection ConstantConditions
//            getActivity().dismissKeyboardShortcutsHelper();
//        }
//
//        Handler handler = new Handler();
//        handler.postDelayed(() -> {
//                    mShopsControllerToolbarLogo.setVisibility(View.VISIBLE);
//                }, 300);
//    }

    public String getCategoryParentKey(String saleCategoryKey) {
        return saleCategoryKey + " • All";
    }

    public void loadShopBanners() {

        mShopsControllerToolbarLogo.setVisibility(View.VISIBLE);
        mShopsControllerToolbarTextView.setVisibility(View.GONE);
        mShopsControllerHamburgerView.setImageDrawable(getActivity().getDrawable(R.drawable.ic_action_menu));
        shopsControllerSearchView.setVisibility(View.VISIBLE);

        GetCategoryTreeResponse shopCategory = new GetCategoryTreeResponse();
        mPresenter.loadShopsBanner(shopCategory.getKey(), shopCategory.getId(), 0, 0);
    }

    public void goToSaleItemsFromCategorySearch() {

        Bundle saleItemBundle = new BundleBuilder(new Bundle())
                .putString("SaleItemsController.KEY_TITLE", "")
                .putString("SaleItemsController.SEARCH_KEY", "")
                .putBoolean("SaleItemsController.FROM_CATEGORY_SEARCH", true)
                .build();

        getRouter().pushController(RouterTransaction.with(
                SaleItemsController.newInstance(saleItemBundle))
                .tag(getResources().getString(R.string.sale_items_controller_tag))
                .pushChangeHandler(new SimpleChangeHandler())
                .popChangeHandler(new FadeChangeHandler()));

    }

    @Override
    public void onRefreshStart() {
        super.onRefreshStart();
        shopsControllerBannerRecyclerView.setVisibility(View.GONE);
        isRefreshShop = true;
        bannerOffset = 0;
        mPresenter.loadShopsBanner(mCategoryName, mCategoryID, bannerOffset, newBannerCount, true);
    }
}
