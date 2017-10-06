package au.com.dealsdirect.ui.controller.saleitems;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.paginate.Paginate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsRequest;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsResponse;
import au.com.dealsdirect.data.network.model.sorting.SortingResponse;
import au.com.dealsdirect.ui.base.BasePullToRefreshController;
import au.com.dealsdirect.ui.controller.saleitemdetails.SaleItemDetailsController;
import au.com.dealsdirect.ui.controller.saleitems.adapter.SaleItemsAdapter;
import au.com.dealsdirect.ui.controller.searchfilter.SearchFilterController;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.SearchChipModel;
import au.com.dealsdirect.ui.custom.SearchEditText;
import au.com.dealsdirect.ui.custom.transitions.SharedArcFadePopChangeHandler;
import au.com.dealsdirect.ui.custom.transitions.SharedArcFadePushChangeHandler;
import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.JsonUtils;
import au.com.dealsdirect.utils.PaginateUtils;
import butterknife.BindView;
import butterknife.OnClick;

import static au.com.dealsdirect.ui.controller.searchfilter.SearchFilterController.BRANDS_FACETFILTER_NAME;
import static au.com.dealsdirect.ui.controller.searchfilter.SearchFilterController.COLORS_FACETFILTER_NAME;
import static au.com.dealsdirect.ui.controller.searchfilter.SearchFilterController.KEY_SELECTED_FACETS;
import static au.com.dealsdirect.ui.controller.searchfilter.SearchFilterController.PRICE_FACETFILTER_NAME;
import static au.com.dealsdirect.ui.controller.searchfilter.SearchFilterController.SEARCH_QUERY_NAME;
import static au.com.dealsdirect.ui.controller.searchfilter.SearchFilterController.SIZES_FACETFILTER_NAME;
import static au.com.dealsdirect.ui.controller.searchfilter.SearchFilterController.SORT_FACETFILTER_NAME;

/**
 * dp Created by Admin on 6/8/17.
 */

public class SaleItemsController extends BasePullToRefreshController implements SaleItemsMvpView {

    public static final String TAG = SaleItemsController.class.getSimpleName();

    public static final String KEY_SALE_ID = "SaleItemsController.KEY_SALE_ID";
    public static final String KEY_BANNER_ID = "SaleItemsController.KEY_BANNER_ID";
    public static final String KEY_TITLE = "SaleItemsController.KEY_TITLE";
    public static final String KEY_HEADER_IMAGE = "SaleItemsController.header_image_url";
    public static final String KEY_FROM_POSITION = "SaleItemsController.position";
    public static final String KEY_CATEGORY_MAP = "SaleItemsController.CATEGORY_KEY";
    public static final String KEY_SEARCH_QUERY = "SaleItemsController.SEARCH_KEY";
    public static final String KEY_CHIPS_FILTER = "SaleItemsController.CHIPS_FILTER";
    private static final String KEY_REQUEST_FROM = "SaleITemsController.REQUEST_FROM";
    private static final String KEY_FROM_CATEGORIES = "SaleItemsController.IS_FROM_CATEGORY";
    private static final String KEY_FROM_SHOP_SEARCH = "SaleItemsController.FROM_SHOP_SEARCH";
    private static final String KEY_FROM_CATEGORY_SEARCH = "SaleItemsController.FROM_CATEGORY_SEARCH";

    private String mSaleId;
    private String mTitle;
    private String mCategoryKey = "";
    private String mCategoryForTitle = "";
    private String mSearchQuery = "";

    private List<GetSaleItemsResponse.Products> mSaleItems = new LinkedList<>();
    private List<GetSaleItemsResponse.Facets> mFacets = new ArrayList<>();
    private List<GetCategoryTreeResponse> mCategoryTreeResponse = new LinkedList<>();
    private List<SortingResponse> mSortingResponse = new ArrayList<>();
    private String mSortingListJsonString = "";
    private boolean mIsFilterClicked = false;

    @BindView(R.id.controller_sale_items_grid_view)
    RecyclerView mSaleItemsRecyclerView;

    @BindView(R.id.partial_toolbar_field_title_edittext)
    SearchEditText mSaleItemsToolbarField;

    @BindView(R.id.partial_toolbar_field_title_textview)
    TextView mSaleItemsToolbarTitle;

    @BindView(R.id.controller_sale_items_placeholder)
    LinearLayout mPlaceholder;

    @BindView(R.id.partial_toolbar_field_title_left_option)
    ImageButton mSaleItemsBackIcon;

    @BindView(R.id.partial_toolbar_field_title_right_option)
    ImageButton mSaleItemsFilterIcon;

    @BindView(R.id.controller_sale_items_opaque_view)
    RelativeLayout mSaleItemsOpaqueCover;

    @BindView(R.id.controller_search_popular_subheader)
    TextView mPopularProductsHeader;

    private SaleItemsAdapter mSaleItemsAdapter;

    private Paginate mPaginateManager;

    private Paginate.Callbacks mPaginateCallbacks;

    private int page = 0;
    private boolean loadingInProgress = false;
    private boolean hasLoadedAllItems = false;
    private boolean mIsFromCategory = false;
    private int mSaleItemClickCounter = 0;
    private boolean isFiltered = false;
    private boolean mIsSearch = false;
    private boolean mFromShopSearch = false;
    private boolean mFromCategorySearch = false;

    private List<SearchChipModel> mChipFilters = new ArrayList<>();

    //store state of selection from filters
    private String mPreviousSelectedFacetIndicesJsonString = "";

    //store removed query chips
    private List<String> mRemovedChipTitles;

    boolean initialLoad = false;

    private TextWatcher mTextWatcher = new TextWatcher() {
        private Timer timer = new Timer();
        private final long DELAY = 1000; // milliseconds

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Log.d("saleitemscontroller", "on text changed");

            mSearchQuery = s.toString();
            mIsSearch = true;
            mSaleItemsToolbarTitle.setText(mSearchQuery);

            timer.cancel();
            timer = new Timer();
            timer.schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            if (before != 0 || count != 0) {
                                getActivity().runOnUiThread(() -> showLoading());
                                mChipFilters = removeSearchQueryChips(mChipFilters);
                                buildSearchQueryChips(mChipFilters);
                                mPresenter.loadSaleItems(createSaleItemsRequest(mCategoryKey, mSaleId, 0, mChipFilters));
                            }
                        }
                    }, DELAY);

        }

        @Override
        public void afterTextChanged(Editable editable) {
            Log.d("saleitemscontroller", "after text changed");
        }
    };

    @Inject
    SaleItemsMvpPresenter<SaleItemsMvpView> mPresenter;

    public static SaleItemsController newInstance(Bundle args) {

        return new SaleItemsController(args);
    }

    public static SaleItemsController newInstance(
            String saleId,
            String bannerTitle,
            String bannerId,
            int fromPosition,
            String imageUrl,
            String categoryKey) {


        return new SaleItemsController(
                new BundleBuilder(new Bundle())
                        .putString(KEY_TITLE, bannerTitle)
                        .putString(KEY_SALE_ID, saleId)
                        .putString(KEY_BANNER_ID, bannerId)
                        .putString(KEY_HEADER_IMAGE, imageUrl)
                        .putInt(KEY_FROM_POSITION, fromPosition)
                        .putString(KEY_CATEGORY_MAP, categoryKey)
                        .build());
    }


    public SaleItemsController(Bundle args) {
        super(args);

        if (args.containsKey(KEY_TITLE)) {
            mTitle = getArgs().getString(KEY_TITLE, "");
            mTitle = mTitle.replaceAll(">>>", " • ");
        }
        if (args.containsKey(KEY_SALE_ID))
            mSaleId = getArgs().getString(KEY_SALE_ID, "");
        if (args.containsKey(KEY_CATEGORY_MAP))
            mCategoryKey = getArgs().getString(KEY_CATEGORY_MAP, "");
        if (args.containsKey(KEY_CHIPS_FILTER))
            mChipFilters = JsonUtils.convertStringToObject(getArgs().getString(KEY_CHIPS_FILTER, ""), new TypeToken<ArrayList<SearchChipModel>>() {
            }.getType());
        if (args.containsKey(KEY_FROM_SHOP_SEARCH))
            mFromShopSearch = getArgs().getBoolean(KEY_FROM_SHOP_SEARCH, true);
        if (args.containsKey(KEY_FROM_CATEGORY_SEARCH))
            mFromCategorySearch = getArgs().getBoolean(KEY_FROM_CATEGORY_SEARCH, true);
        if (args.containsKey(KEY_FROM_CATEGORIES)) {
            mIsFromCategory = getArgs().getBoolean(KEY_FROM_CATEGORIES, true);
        }

    }

    public void onPassFiltersData(Bundle args) {

        if (args.containsKey(KEY_SALE_ID))
            mSaleId = args.getString(KEY_SALE_ID, "");
        if (args.containsKey(KEY_CATEGORY_MAP))
            mCategoryKey = args.getString(KEY_CATEGORY_MAP, "");
            if(!mCategoryKey.isEmpty()){
                mCategoryForTitle = mCategoryKey.replaceAll(">>>", " • ");
            } else {
                mCategoryForTitle = "";
            }
        if (args.containsKey(KEY_CHIPS_FILTER)) {
            mChipFilters = JsonUtils.convertStringToObject(args.getString(KEY_CHIPS_FILTER, ""), new TypeToken<ArrayList<SearchChipModel>>() {
            }.getType());
        }

        if (args.containsKey(KEY_SELECTED_FACETS))
            mPreviousSelectedFacetIndicesJsonString = args.getString(KEY_SELECTED_FACETS, "");

        isFiltered = true;
        page = 0;
    }


    @Override
    protected void onAttach(@NonNull View view) {
        mPresenter.onAttach(this);
        mSaleItemClickCounter = 0;

        determineToolbarTitle();
        if(!mSaleItems.isEmpty()) {
            mSaleItemsFilterIcon.setOnClickListener(view12 -> showFilters());
        }

        super.onAttach(view);
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = super.inflateView(inflater, container);

        fillToolbar(inflater.inflate(R.layout.partial_toolbar_field_title, container, false));
        fillContent(inflater.inflate(R.layout.controller_sale_items, container, false));

        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        return view;
    }

    @Override
    public void onRefreshStart() {
        super.onRefreshStart();
        page = 0;
        mPresenter.loadSaleItems(createSaleItemsRequest(mCategoryKey, mSaleId, page, mChipFilters));
        mPresenter.loadSaleItems(createSaleItemsRequest(mCategoryKey, mSaleId, page, null));
    }

    @Override
    protected void onActivityResumed(@NonNull Activity activity) {
        super.onActivityResumed(activity);
        ((MainActivity) getActivity()).setDraggableViewPager(false);
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);

//        if (mIsFromCategory){
//            mSaleItemsBackIcon.setOnClickListener(view1 -> {
//                mSaleItemsBackIcon.setOnClickListener(view2 -> {
//                    getActivity().onBackPressed();
//
//                });
//                ((MainActivity) getActivity()).goToCategories();
//            });
//        }else

        if (mFromShopSearch) {
            if (mSaleItemsToolbarTitle.getText().toString().isEmpty() && mSearchQuery.isEmpty() && !isFiltered) {
                activateSearch();
            }
        } else {
            mSaleItemsBackIcon.setOnClickListener(view12 -> {
                getActivity().onBackPressed();
            });
        }

        setUp(view);
    }

    private void determineToolbarTitle() {

        //always set edittext string to empty initially
        mSaleItemsToolbarField.setText("");

        String lookingForText = getResources().getString(R.string.i_am_looking_for);
        String categoryToolbarString = buildCategoryToolbarTitle();


        //determining hint logic
        //category precedes above all
        if (!mCategoryKey.isEmpty()) {
            mSaleItemsToolbarField.setHint(categoryToolbarString);
        } else if (!mTitle.isEmpty()) {
            mSaleItemsToolbarField.setHint(mTitle);
        } else {
            mSaleItemsToolbarField.setHint(lookingForText);
        }


        String editTextString = mSaleItemsToolbarField.getText().toString();
        //determining toolbartitle logic
        //category precedes above all
        if (!mSearchQuery.isEmpty()) {
            mSaleItemsToolbarTitle.setText(mSearchQuery);
            mSaleItemsToolbarField.setText(mSearchQuery);
        } else if (!editTextString.isEmpty()) {
            mSaleItemsToolbarTitle.setText(editTextString);
        }else if(!mCategoryForTitle.isEmpty()){
            mSaleItemsToolbarTitle.setText(mCategoryForTitle);
        } else if (!mTitle.isEmpty()) {
            mSaleItemsToolbarTitle.setText(mTitle);
        } else {
            mSaleItemsToolbarTitle.setText(getResources().getString(R.string.i_am_looking_for));
        }

        //show popular products if saleitemstoolbar title is "looking for"

        if (mSaleItemsToolbarTitle.getText().equals(lookingForText) && mChipFilters.isEmpty()){
            mPopularProductsHeader.setVisibility(View.VISIBLE);
        }

    }

    private String buildCategoryToolbarTitle() {
        char c = '>';
        int charCount = 0;
        String newString = "";
        for (int i = 0; i < mCategoryKey.length(); i++) {
            String getChar = String.valueOf(mCategoryKey.charAt(i));
            if (!getChar.equals(String.valueOf(c))) {
                newString = newString + mCategoryKey.charAt(i);

            } else {
                if (charCount == 2) {
                    newString = newString + " • ";
                    charCount = 0;
                }
                charCount++;
            }
        }
        return newString;
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @Override
    protected void setUp(View view) {
        hideKeyboard();

        mPresenter.loadSortingFacets();
        mSaleItemsToolbarTitle.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

        ((MainActivity) getActivity()).getMainController().setViewpagerDraggable(false);

        mPaginateCallbacks = new Paginate.Callbacks() {
            @Override
            public void onLoadMore() {
                // Load next page of data (e.g. network or database)
                page++;
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

        mSaleItemsAdapter = new SaleItemsAdapter(getActivity(), mSaleItems, mPresenter, mSaleId);
        if (mPresenter.isTablet()) {
            mSaleItemsRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        } else {
            mSaleItemsRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        }

        mSaleItemsRecyclerView.setAdapter(mSaleItemsAdapter);

        if (isFiltered || mSaleItems.isEmpty()) {
            showLoading();

            mPresenter.loadSaleItems(createSaleItemsRequest(mCategoryKey, mSaleId, page, mChipFilters));

            /* show popular products after filter with empty chips */
            if (mCategoryKey.isEmpty()&& (mSaleId==null||mSaleId.isEmpty()) && mChipFilters.isEmpty()){
                mPopularProductsHeader.setVisibility(View.VISIBLE);
            }

            /* set if still in search */
            if (mCategoryKey.isEmpty() && (mSaleId==null||mSaleId.isEmpty())){
                mIsSearch  = true;
                mFromShopSearch = true;
            }

            if (!mChipFilters.isEmpty()){
                isFiltered = true;
            } else {
                isFiltered = false;
            }


            mChipFilters = removeSearchQueryChips(mChipFilters);
            mSearchQuery = buildSearchQueryText(mRemovedChipTitles);

            //this api call serves to get the correct facets for SearchFiltersController to display
            // we need to remove any chip filters to return the base facets
            // giving any filters(ff=) will change the facet return;

            mPresenter.loadSaleItems(createSaleItemsRequest(mCategoryKey, mSaleId, page, null));
        } else if (!mSaleItems.isEmpty()) {
            mPaginateManager = PaginateUtils.init(mSaleItemsRecyclerView, mPaginateCallbacks);
        }

        mSaleItemsToolbarField.setOnKeyboardListener((keyboardEditText, showing) -> {
            if (!showing) {
                deactivateSearch();
            }
        });
    }

    @Override
    public void onLoadSortingFacetsFinished(List<SortingResponse> responseList) {
        mSortingResponse = responseList;
        mSortingListJsonString = new Gson().toJson(responseList);
    }

    @Override
    public void showSaleItems(GetSaleItemsResponse getSaleItemsResponse, boolean forFacetCorrection) {

        //allow showing filters only when sale items have loaded.
        mSaleItemsFilterIcon.setOnClickListener(view12 -> showFilters());

        List<GetSaleItemsResponse.Products> items = getSaleItemsResponse.products;

        if (forFacetCorrection) {
            mFacets = getSaleItemsResponse.facets;
            mCategoryTreeResponse = getSaleItemsResponse.getCategories();
        } else {

            if ((!isFiltered && mFromShopSearch && mChipFilters.isEmpty()) || mFromCategorySearch) {
                if (mSaleItemsToolbarField.getText().toString().isEmpty()) {
                    mPopularProductsHeader.setVisibility(View.VISIBLE);
                } else {
                    mPopularProductsHeader.setVisibility(View.GONE);
                }
            } else {
                if (mChipFilters.isEmpty() &&
                        mSaleItemsToolbarTitle.getText().toString().isEmpty() &&
                        mSaleItemsToolbarField.getHint().toString().equals(getResources().getString(R.string.i_am_looking_For))) {
                    mPopularProductsHeader.setVisibility(View.VISIBLE);
                } else {
                    mPopularProductsHeader.setVisibility(View.GONE);

                }
            }

            enablePullToRefresh(true); //default true

            if (mIsFromCategory) {
                enablePullToRefresh(false); //disable ptr when coming from categories
            }

            if (items.size() == 0) {
                mFacets = getSaleItemsResponse.facets;
            }

            mCategoryTreeResponse = getSaleItemsResponse.getCategories();

            loadingInProgress = false;

            if (items.size() == 0 && page != 0) {
                hasLoadedAllItems = true;
                mPaginateManager.setHasMoreDataToLoad(false);
                enablePullToRefresh(false);
                page = 0;
            } else {

                if (!mChipFilters.isEmpty() || mIsSearch) {
                    enablePullToRefresh(false);
                    isFiltered = false;
                }


                if (page == 0) {
                    if (mPaginateManager != null) {
                        mPaginateManager.unbind();
                    }
                    mSaleItemsAdapter.replaceData(items);

                    mPaginateManager = PaginateUtils.init(mSaleItemsRecyclerView, mPaginateCallbacks);
                    mSaleItemsRecyclerView.scrollToPosition(0);
                    hasLoadedAllItems = false;
                    page = 0;
                } else {
                    mSaleItemsAdapter.addData(items);
                }
            }

            mSaleItems = mSaleItemsAdapter.getData();

            if (mSaleItems == null || mSaleItems.isEmpty()) {
                mPlaceholder.setVisibility(View.VISIBLE);
                mSaleItemsRecyclerView.setVisibility(View.GONE);
            } else {
                mPlaceholder.setVisibility(View.GONE);
                mSaleItemsRecyclerView.setVisibility(View.VISIBLE);
            }
        }
    }


    @Override
    protected void onSaveViewState(@NonNull View view, @NonNull Bundle outState) {
        super.onSaveViewState(view, outState);
    }

    @Override
    protected void onRestoreViewState(@NonNull View view, @NonNull Bundle savedViewState) {
        super.onRestoreViewState(view, savedViewState);
    }

    //
//    @SuppressWarnings("ConstantConditions")
//    @OnClick(R.id.partial_toolbar_field_title_left_option)
    void onBackClick() {
        if (!mIsFilterClicked) {
            mIsFilterClicked = true;
            getActivity().onBackPressed();
            new Handler().postDelayed(new TimerTask() {
                @Override
                public void run() {
                    mIsFilterClicked = false;
                }
            }, 2000);
        }
    }

    //    @OnClick(R.id.partial_toolbar_field_title_right_option)
    void showFilters() {
        if (!mIsFilterClicked) {

            mIsFilterClicked = true;
            mChipFilters = removeSearchQueryChips(mChipFilters);
            buildSearchQueryChips(mChipFilters);

            getRouter().pushController(RouterTransaction.with(SearchFilterController.newInstance(
                    new Gson().toJson(mCategoryTreeResponse),
                    new Gson().toJson(mFacets)
                    , mSortingListJsonString
                    , mSaleId
                    , mCategoryKey
                    , mPreviousSelectedFacetIndicesJsonString
                    , new Gson().toJson(mChipFilters)
                    , mSearchQuery))
                    .pushChangeHandler(new VerticalChangeHandler())
                    .popChangeHandler(new VerticalChangeHandler()));
            mFromShopSearch = false;
            mFromCategorySearch = false;


            new Handler().postDelayed(new TimerTask() {
                @Override
                public void run() {
                    mIsFilterClicked = false;
                }
            }, 2000);
        }
    }


    @Override
    public void refresh() {
        Log.d("chipsBuilt", "refreshcalled");
        loadingInProgress = true;
        mChipFilters = removeSearchQueryChips(mChipFilters);
        buildSearchQueryChips(mChipFilters);
        mPresenter.loadSaleItems(createSaleItemsRequest(mCategoryKey, mSaleId, page, mChipFilters));
    }

    @Override
    public void showProductDetails(RecyclerView.ViewHolder viewHolder, int position, String seoIdentifierId, String imageUrl, String skuId, String saleId) {

        if (mSaleItemClickCounter != 1) {
            mSaleItemClickCounter = +1;

            Handler clickHandler = new Handler();
            clickHandler.postDelayed(() -> {
                mSaleItemClickCounter = 0;
            }, 2000);

            List<String> names = new ArrayList<>();
            names.add(getResources().getString(R.string.transition_sale_image_indexed, position));
            mSaleItemsRecyclerView.smoothScrollToPosition(position);

            Bundle bundle = new Bundle();
            bundle.putInt("KEY_POSITION", position);
            bundle.putString("KEY_IMAGE_ID", imageUrl);
            bundle.putString("KEY_SEO_IDENTIFIER", seoIdentifierId);
            bundle.putString("KEY_SKU_ID", skuId);
            bundle.putString("KEY_SALE_ID", saleId);
            bundle.putString("KEY_SALE_NAME", ((SaleItemsAdapter.ViewHolder) viewHolder).name.getText().toString());
            bundle.putString("KEY_SALE_PRICE", ((SaleItemsAdapter.ViewHolder) viewHolder).price.getText().toString());
            bundle.putString("KEY_SALE_OLD_PRICE", ((SaleItemsAdapter.ViewHolder) viewHolder).oldPrice.getText().toString());

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                getRouter().pushController(RouterTransaction.with(SaleItemDetailsController.newInstance(bundle))
                        .pushChangeHandler(new FadeChangeHandler())
                        .popChangeHandler(new FadeChangeHandler()));
            } else {
                getRouter().pushController(RouterTransaction.with(SaleItemDetailsController.newInstance(bundle))
                        .pushChangeHandler(new SharedArcFadePushChangeHandler())
                        .popChangeHandler(new SharedArcFadePopChangeHandler()));
            }


            mFromShopSearch = false;
            mFromCategorySearch = false;
        }

    }

    @Override
    public void unbindPaginate() {
        if (mPaginateManager != null) {
            mPaginateManager.unbind();
        }
    }

    @OnClick(R.id.partial_toolbar_field_title_textview)
    void onViewSearch() {
        activateSearch();
    }

    @OnClick(R.id.controller_sale_items_opaque_view)
    void onClickCover() {
        deactivateSearch();
    }

    @OnClick(R.id.partial_toolbar_field_title_edittext)
    void onToolbarFieldClick() {
        mSaleItemsOpaqueCover.setVisibility(View.VISIBLE);
    }

    public void deactivateSearch() {
        mSaleItemsToolbarField.removeTextChangedListener(mTextWatcher);
        mSearchQuery = mSaleItemsToolbarField.getText().toString();

        mSaleItemsBackIcon.setImageDrawable(getActivity().getDrawable(R.drawable.ic_pink_chevron));
        mSaleItemsFilterIcon.setImageDrawable(getActivity().getDrawable(R.drawable.ic_toolbar_filter));

        mSaleItemsOpaqueCover.setVisibility(View.GONE);
        mSaleItemsToolbarField.setActivated(false);
        mSaleItemsToolbarField.setVisibility(View.GONE);
        mSaleItemsToolbarTitle.setVisibility(View.VISIBLE);

        mSaleItemsBackIcon.setOnClickListener(view -> onBackClick());
        mSaleItemsFilterIcon.setOnClickListener(view12 -> showFilters());

        determineToolbarTitle();
        hideKeyboard();
    }

    public void activateSearch() {
        android.os.Handler handler = new android.os.Handler();
        handler.postDelayed(() -> {
            if (mSaleItemsToolbarField != null && mSaleItemsToolbarField.requestFocus()) {
                InputMethodManager inputMethodManager =
                        (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                inputMethodManager.toggleSoftInputFromWindow(
                        mSaleItemsToolbarField.getApplicationWindowToken(),
                        InputMethodManager.SHOW_FORCED, 0);
            }
        }, 200);

//        if (mCategoryKey.isEmpty() && mTitle.isEmpty() && mSearchQuery.isEmpty()) {
//            mSaleItemsToolbarTitle.setText(getActivity().getResources().getString(R.string.i_am_looking_for));
//        }

        mSaleItemsBackIcon.setImageDrawable(getActivity().getDrawable(R.drawable.ic_search));
        mSaleItemsFilterIcon.setImageDrawable(getActivity().getDrawable(R.drawable.ic_close));

        mSaleItemsBackIcon.setOnClickListener(null);
        mSaleItemsFilterIcon.setOnClickListener(view -> {
            deactivateSearch();
        });

        mSaleItemsOpaqueCover.setVisibility(View.VISIBLE);
        mSaleItemsToolbarField.setVisibility(View.VISIBLE);

        mSaleItemsToolbarField.setActivated(true);
        mSaleItemsToolbarField.setSelection(mSaleItemsToolbarField.getText().length());
        mSaleItemsToolbarTitle.setVisibility(View.GONE);

        mSaleItemsToolbarField.addTextChangedListener(mTextWatcher);
        mSaleItemsToolbarField.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
//                    hideKeyboard();
                mSearchQuery = textView.getText().toString();
                mIsSearch = true;
                mChipFilters = removeSearchQueryChips(mChipFilters);
                buildSearchQueryChips(mChipFilters);
                mSaleItemsToolbarTitle.setText(mSearchQuery);
                showLoading();

                mPresenter.loadSaleItems(createSaleItemsRequest(mCategoryKey, mSaleId, 0, mChipFilters));
                deactivateSearch();
            }

            return false;
        });

    }

    private List<SearchChipModel> removeSearchQueryChips(List<SearchChipModel> chipFilters) {

        List<SearchChipModel> chipList = new ArrayList<>(chipFilters);
        mRemovedChipTitles = new ArrayList<>();
        for (SearchChipModel chip : chipFilters) {
            if (chip.getFilterType().equals(SEARCH_QUERY_NAME)) {
                chipList.remove(chip);
                mRemovedChipTitles.add(chip.getChipTitle());
            }
        }
        return chipList;
    }

    private String buildSearchQueryText(List<String> chipTitles) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String string : chipTitles) {
            stringBuilder.append(string);
            stringBuilder.append(" ");
        }

        return stringBuilder.toString().trim();
    }

    private void buildSearchQueryChips(List<SearchChipModel> chipFilters) {
        if (!mSearchQuery.isEmpty()) {
            String[] splitted = mSearchQuery.split("\\s+");
            for (String str : splitted) {
                chipFilters.add(new SearchChipModel(SearchFilterController.SEARCH_QUERY_NAME, str, -1));
            }
        }

    }

    private GetSaleItemsRequest createSaleItemsRequest(String categoryKey, String saleId, int pageNumber, List<SearchChipModel> chipsList) {
        List<String> saleIds = new LinkedList<>();
        HashMap<String, List<String>> facetFilters = new HashMap<>();

        GetSaleItemsRequest getSaleItemsRequest = new GetSaleItemsRequest();

        if (!categoryKey.isEmpty())
            getSaleItemsRequest.setCategoryKey("[\"" + categoryKey + "\"]");
        else
            getSaleItemsRequest.setCategoryKey("[]");


        getSaleItemsRequest.setSorting("");
        getSaleItemsRequest.setPageNumber(String.valueOf(pageNumber));
        getSaleItemsRequest.setQuery("");

        getSaleItemsRequest.setPageSize("50");

        if (saleId != null) {

            if (!saleId.isEmpty())
                saleIds.add(saleId);
        }


        if (!saleIds.isEmpty()) {
            facetFilters.put("saleId", saleIds);
        }

        if (chipsList == null) {
            getSaleItemsRequest.setHasFilters(false);
        } else {
            if (chipsList.size() != 0) {
                ArrayList<String> searchQueryFilters = new ArrayList<>();
                ArrayList<String> brandNameFacetFilters = new ArrayList<>();
                ArrayList<String> colorFacetFilters = new ArrayList<>();
                ArrayList<String> sizesFacetFilters = new ArrayList<>();
                ArrayList<String> priceFacetFilters = new ArrayList<>();

                for (SearchChipModel chip : chipsList) {
                    String facetName = chip.getFilterType();
                    if (facetName.equals(BRANDS_FACETFILTER_NAME)) {
                        brandNameFacetFilters.add(chip.getChipTitle());
                    } else if (facetName.equals(COLORS_FACETFILTER_NAME)) {
                        colorFacetFilters.add(chip.getChipTitle());
                    } else if (facetName.equals(SIZES_FACETFILTER_NAME)) {
                        sizesFacetFilters.add(chip.getChipTitle());
                    } else if (facetName.equals(PRICE_FACETFILTER_NAME)) {
                        priceFacetFilters.add(chip.getChipTitle());
                    } else if (facetName.equals(SEARCH_QUERY_NAME)) {
                        searchQueryFilters.add(chip.getChipTitle());
                    } else if (facetName.equals(SORT_FACETFILTER_NAME)) {
                        getSaleItemsRequest.setSorting(mapSortingTitleToKey(chip.getChipTitle()));
                    }
                }

                facetFilters.put(BRANDS_FACETFILTER_NAME, brandNameFacetFilters);
                facetFilters.put(COLORS_FACETFILTER_NAME, colorFacetFilters);
                facetFilters.put(SIZES_FACETFILTER_NAME, sizesFacetFilters);
                facetFilters.put(PRICE_FACETFILTER_NAME, priceFacetFilters);


                if (searchQueryFilters.size() != 0) {
                    StringBuilder result = new StringBuilder();
                    for (int i = 0; i < searchQueryFilters.size(); i++) {
                        if (i > 0) {
                            result.append(" ");
                        }
                        result.append(searchQueryFilters.get(i));
                    }

                    getSaleItemsRequest.setQuery(result.toString());
                }
            }

            getSaleItemsRequest.setHasFilters(true);
        }

        String facetFiltersString = new Gson().toJson(facetFilters);

        getSaleItemsRequest.setFacetFilter(facetFiltersString);

        if (pageNumber == 0)
            page = pageNumber;

        return getSaleItemsRequest;
    }

    private String mapSortingTitleToKey(String title) {
        for (SortingResponse response : mSortingResponse) {
            if (response.getTitle().equalsIgnoreCase(title)) {
                return response.getKey();
            }
        }

        return "";
    }

}
