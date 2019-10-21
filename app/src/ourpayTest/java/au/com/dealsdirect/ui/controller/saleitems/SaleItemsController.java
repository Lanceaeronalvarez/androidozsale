package au.com.dealsdirect.ui.controller.saleitems;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.paginate.Paginate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
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
import au.com.dealsdirect.ui.controller.searchfilter.SearchFilterController;
import au.com.dealsdirect.ui.controller.searchfilter.SearchFilterMvpView;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.SearchChipModel;
import au.com.dealsdirect.ui.custom.SearchEditText;
import au.com.dealsdirect.ui.custom.transitions.SharedArcFadePopChangeHandler;
import au.com.dealsdirect.ui.custom.transitions.SharedArcFadePushChangeHandler;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.JsonUtils;
import au.com.dealsdirect.utils.PaginateUtils;
import au.com.dealsdirect.utils.StringUtils;
import au.com.dealsdirect.utils.module.ControllerFactory;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;
import butterknife.OnClick;

import static au.com.dealsdirect.utils.BundleKeys.SALEITEMS_CATEGORY_MAP;
import static au.com.dealsdirect.utils.BundleKeys.SALEITEMS_CHIPS_FILTER;
import static au.com.dealsdirect.utils.BundleKeys.SALEITEMS_FROM_CATEGORIES;
import static au.com.dealsdirect.utils.BundleKeys.SALEITEMS_FROM_CATEGORY_SEARCH;
import static au.com.dealsdirect.utils.BundleKeys.SALEITEMS_FROM_SHOP_SEARCH;
import static au.com.dealsdirect.utils.BundleKeys.SALEITEMS_SALE_ID;
import static au.com.dealsdirect.utils.BundleKeys.SALEITEMS_TITLE;

/**
 * dp Created by Admin on 6/8/17.
 */

public class SaleItemsController extends BasePullToRefreshController implements SaleItemsMvpView {

    public static final String TAG = SaleItemsController.class.getSimpleName();
    private static final String CATEGORY_KEY_SEPARATOR = ">>>";
    private static final String CATEGORY_KEY_SEPARATOR_REPLACEMENT = " • ";

    private String mSaleId = "";
    private String mTitle = "";
    private String mCategoryKey = "";
    private String mCategoryForTitle = "";
    private String mSearchQuery = "";

    private List<GetSaleItemsResponse.Products> mSaleItems = new LinkedList<>();
    private List<GetSaleItemsResponse.Facets> mFacets = new ArrayList<>();
    private List<GetCategoryTreeResponse> mCategoryTreeResponse = new LinkedList<>();
    private List<GetCategoryTreeResponse> mPrevCategoryTreeResponse = new LinkedList<>();
    private List<SortingResponse> mSortingResponse = new ArrayList<>();
    private String mSortingListJsonString = "";
    private boolean mIsFilterClicked = false;
    private boolean mIsSearchClicked = false;
    private boolean mIsCategoryChanged = false;

    @BindView(R.id.controller_sale_items_grid_view)
    RecyclerView mSaleItemsRecyclerView;

    @BindView(R.id.partial_toolbar_search_edittext_layout)
    LinearLayout mSaleItemsToolbarEditTextLayout;

    @BindView(R.id.partial_toolbar_field_title_edittext)
    SearchEditText mSaleItemsToolbarField;

    @BindView(R.id.partial_toolbar_field_title_textview)
    TextView mSaleItemsToolbarTitle;

    @BindView(R.id.controller_sale_items_placeholder)
    LinearLayout mPlaceholder;

    @BindView(R.id.partial_toolbar_field_title_left_option)
    View mSaleItemsBackIcon;

    @BindView(R.id.partial_toolbar_clear_button)
    ImageButton mClearSearchButton;

    @BindView(R.id.partial_toolbar_search_cancel)
    TextView mCancelText;

    @BindView(R.id.partial_toolbar_search_btn)
    ImageButton mSearchBtn;

    @BindView(R.id.controller_search_filter_frame)
    ViewGroup mSearchFilterContainer;

//    @BindView(R.id.controller_sale_items_opaque_view)
//    RelativeLayout mSaleItemsOpaqueCover;

    @BindView(R.id.controller_search_popular_subheader)
    TextView mPopularProductsHeader;

    private SaleItemsAdapter mSaleItemsAdapter;
    private Paginate mPaginateManager;
    private Paginate.Callbacks mPaginateCallbacks;

    private Router mSearchFilterRouter;
    private Controller mSearchFilterController;
    private SearchFilterMvpView mSearchFilterMvpView;

    private int page = 0;
    private boolean loadingInProgress = false;
    private boolean hasLoadedAllItems = false;
    private boolean mIsFromCategory = false;
    private int mSaleItemClickCounter = 0;
    private boolean mIsSearch = false;
    private boolean mFromShopSearch = false;
    private boolean mFromCategorySearch = false;

    private String mChosenCategory;
    private String mChosenCategoryKey;
    private boolean initialLoad;

    private List<SearchChipModel> mChipFilters = new ArrayList<>();

    //store state of selection from filters
    private String mPreviousSelectedFacetIndicesJsonString = "";

    //store removed query chips
    private List<String> mRemovedChipTitles;


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
                                mActivity.runOnUiThread(() -> showLoading());
                                mChipFilters = removeSearchQueryChips(mChipFilters);
                                buildSearchQueryChips(mChipFilters);
                                mPresenter.loadSaleItems(createSaleItemsRequest(mSearchFilterMvpView.getCategoryKeys(), mSaleId, 0, mChipFilters, ""));
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

    public static SaleItemsController newInstance() {


        return new SaleItemsController(new BundleBuilder(new Bundle()).build());
    }


    public SaleItemsController(Bundle args) {
        super(args);

        if (args.containsKey(SALEITEMS_TITLE)) {
            mTitle = getArgs().getString(SALEITEMS_TITLE, "");
            mTitle = mTitle.replaceAll(CATEGORY_KEY_SEPARATOR, CATEGORY_KEY_SEPARATOR_REPLACEMENT);
        }
        if (args.containsKey(SALEITEMS_SALE_ID))
            mSaleId = getArgs().getString(SALEITEMS_SALE_ID, "");
        if (args.containsKey(SALEITEMS_CATEGORY_MAP))
            mCategoryKey = getArgs().getString(SALEITEMS_CATEGORY_MAP, "");
        if (args.containsKey(SALEITEMS_CHIPS_FILTER))
            mChipFilters = JsonUtils.convertStringToObject(getArgs().getString(SALEITEMS_CHIPS_FILTER, ""), new TypeToken<ArrayList<SearchChipModel>>() {
            }.getType());
        if (args.containsKey(SALEITEMS_FROM_SHOP_SEARCH))
            mFromShopSearch = getArgs().getBoolean(SALEITEMS_FROM_SHOP_SEARCH, true);
        if (args.containsKey(SALEITEMS_FROM_CATEGORY_SEARCH))
            mFromCategorySearch = getArgs().getBoolean(SALEITEMS_FROM_CATEGORY_SEARCH, true);
        if (args.containsKey(SALEITEMS_FROM_CATEGORIES)) {
            mIsFromCategory = getArgs().getBoolean(SALEITEMS_FROM_CATEGORIES, true);
        }

    }

    public void onPassFiltersData(Bundle args) {

        if (args.containsKey(SALEITEMS_SALE_ID))
            mSaleId = args.getString(SALEITEMS_SALE_ID, "");
        if (args.containsKey(SALEITEMS_CATEGORY_MAP))
            mCategoryKey = args.getString(SALEITEMS_CATEGORY_MAP, "");
        if (!mCategoryKey.isEmpty()) {
            mCategoryForTitle = mCategoryKey.replaceAll(CATEGORY_KEY_SEPARATOR, CATEGORY_KEY_SEPARATOR_REPLACEMENT);
        } else {
            mCategoryForTitle = "";
        }
        if (args.containsKey(SALEITEMS_CHIPS_FILTER)) {
            mChipFilters = JsonUtils.convertStringToObject(args.getString(SALEITEMS_CHIPS_FILTER, ""), new TypeToken<ArrayList<SearchChipModel>>() {
            }.getType());
        }

        if (args.containsKey(BundleKeys.KEY_SELECTED_FACETS))
            mPreviousSelectedFacetIndicesJsonString = args.getString(BundleKeys.KEY_SELECTED_FACETS, "");

        page = 0;
    }

    @Override
    protected void onAttach(@NonNull View view) {
        mPresenter.onAttach(this);
        mSaleItemClickCounter = 0;

        determineToolbarTitle();
        if (!mSaleItems.isEmpty()) {
            mSearchBtn.setOnClickListener(v -> toggleSearch());
            mCancelText.setOnClickListener(v -> toggleSearch());
        }

        super.onAttach(view);
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_sale_items, container, false);
        bindPtrViews(view);
        fillContent(inflater.inflate(R.layout.partial_controller_sale_items, container, false));

        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        return view;
    }

    @Override
    public void onRefreshStart() {
        super.onRefreshStart();
        page = 0;
        mPresenter.loadSaleItems(createSaleItemsRequest(mSearchFilterMvpView.getCategoryKeys(), mSaleId, page, mChipFilters, ""));
    }

    @Override
    protected void onActivityResumed(@NonNull Activity activity) {
        super.onActivityResumed(activity);
        mActivity.setDraggableViewPager(false);
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);


        mSaleItemsBackIcon.setOnClickListener(view12 -> {
            mActivity.onBackPressed();
        });


        setUp(view);
        setRetainViewMode(RetainViewMode.RETAIN_DETACH);
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
        } else if (!mCategoryForTitle.isEmpty()) {
            mSaleItemsToolbarTitle.setText(mCategoryForTitle);
        } else if (!mTitle.isEmpty()) {
            mSaleItemsToolbarTitle.setText(mTitle);
        } else {
            mSaleItemsToolbarTitle.setText(getResources().getString(R.string.i_am_looking_for));
        }

        //show popular products if saleitemstoolbar title is "looking for"

        if (mSaleItemsToolbarTitle.getText().equals(lookingForText) && mChipFilters.isEmpty()) {
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

        mClearSearchButton.setOnClickListener(v -> {
            mSaleItemsToolbarField.setText("");
        });

        mActivity.getMainController().setViewpagerDraggable(false);

        mPaginateCallbacks = new Paginate.Callbacks() {
            @Override
            public void onLoadMore() {
                // Load next page of data (e.g. network or database)
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


        mSearchFilterRouter = getChildRouter(mSearchFilterContainer);

        mSaleItemsAdapter = new SaleItemsAdapter(mActivity, mSaleItems, mPresenter, mSaleId);
        if (mPresenter.isTablet()) {
            mSaleItemsRecyclerView.setLayoutManager(new GridLayoutManager(mActivity, 4));
        } else {
            mSaleItemsRecyclerView.setLayoutManager(new GridLayoutManager(mActivity, 2));
        }

        mSaleItemsRecyclerView.setAdapter(mSaleItemsAdapter);

        mPaginateManager = PaginateUtils.init(mSaleItemsRecyclerView, mPaginateCallbacks);

        initialLoad = true;
        mPresenter.loadSaleItems(createSaleItemsRequest("", mSaleId, page, mChipFilters, ""));

        mSaleItemsToolbarField.setOnKeyboardListener((keyboardEditText, showing) -> {
            if (!showing) {
//                deactivateSearch();
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

        mCategoryTreeResponse = getSaleItemsResponse.getCategories();

        if (forFacetCorrection) {
            mFacets = getSaleItemsResponse.getFacets();
        } else {

            mFacets = getSaleItemsResponse.getFacets();

            //allow showing filters only when sale items have loaded.
            mSearchBtn.setOnClickListener(view -> toggleSearch());
            mCancelText.setOnClickListener(v -> toggleSearch());

            List<GetSaleItemsResponse.Products> items = getSaleItemsResponse.products;


//        LOGIC TO SAVE PREV CATEGORY TO PREVENT LOCKOUT
            if (mCategoryTreeResponse == null || mCategoryTreeResponse.isEmpty()) {
                mCategoryTreeResponse = mPrevCategoryTreeResponse;
            } else {
                mPrevCategoryTreeResponse = mCategoryTreeResponse;
            }


            if ((mSearchQuery.isEmpty() && mFromShopSearch && mChipFilters.isEmpty()) || mFromCategorySearch) {
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

//        if (mIsFromCategory) {
//            enablePullToRefresh(false); //disable ptr when coming from categories
//        }

            loadingInProgress = false;

            if (items.size() == 0 && page != 0) {
                hasLoadedAllItems = true;
                mPaginateManager.setHasMoreDataToLoad(false);
                enablePullToRefresh(false);
                page = 0;
            } else {

                if (!mChipFilters.isEmpty() || mFromCategorySearch || mFromShopSearch || !mSearchQuery.isEmpty()) {
                    enablePullToRefresh(false);
                }


                if (page == 0 || mIsSearch) {
//                if (mPaginateManager != null) {
//                    mPaginateManager.unbind();
//                }


                    if (items.size() <= getResources().getInteger(R.integer.sale_items_threshold)) {
                        hasLoadedAllItems = true;
                        mPaginateManager.setHasMoreDataToLoad(false);
                        page = 0;
                    }
                    mSaleItemsAdapter.replaceData(items);
                    mSaleItemsRecyclerView.scrollToPosition(0);

                    mIsSearch = false;
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


            if (initialLoad) {
                setupSearchFilters();
                initialLoad = false;
            }
        }


        mSearchFilterMvpView.replaceCategoryTree(mCategoryTreeResponse);

        if (mIsCategoryChanged) {
            mSearchFilterMvpView.replaceFacets(mFacets);
            mIsCategoryChanged = false;
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
            mActivity.onBackPressed();
            new Handler().postDelayed(new TimerTask() {
                @Override
                public void run() {
                    mIsFilterClicked = false;
                }
            }, 2000);
        }
    }

    //    @OnClick(R.id.partial_toolbar_field_title_right_option)
    void toggleSearch() {
        if (!mIsSearchClicked) {
            mCancelText.setVisibility(View.VISIBLE);
            mSearchBtn.setVisibility(View.GONE);
            mSaleItemsToolbarEditTextLayout.setVisibility(View.VISIBLE);
            mSaleItemsBackIcon.setVisibility(View.GONE);
            mSaleItemsToolbarTitle.setVisibility(View.GONE);
            mIsSearchClicked = true;
        } else {
            mCancelText.setVisibility(View.GONE);
            mSearchBtn.setVisibility(View.VISIBLE);
            mSaleItemsToolbarEditTextLayout.setVisibility(View.GONE);
            mSaleItemsBackIcon.setVisibility(View.VISIBLE);
            mSaleItemsToolbarTitle.setVisibility(View.VISIBLE);
            mIsSearchClicked = false;
        }
    }

    @Override
    public void refresh() {
        if (mSaleItems.size() < getResources().getInteger(R.integer.sale_items_threshold)) {
            hasLoadedAllItems = true;
            page = 0;
        } else {
            Log.d("chipsBuilt", "refreshcalled");
            loadingInProgress = true;
            mChipFilters = removeSearchQueryChips(mChipFilters);
            buildSearchQueryChips(mChipFilters);
            page++;
            mPresenter.loadSaleItems(createSaleItemsRequest(mSearchFilterMvpView.getCategoryKeys(), mSaleId, page, mChipFilters, ""));
        }
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


    @OnClick(R.id.partial_toolbar_field_title_textview)
    void onViewSearch() {
//        activateSearch();
    }

    //    @OnClick(R.id.controller_sale_items_opaque_view)
    void onClickCover() {
//        deactivateSearch();
    }

//    @OnClick(R.id.partial_toolbar_field_title_edittext)
//    void onToolbarFieldClick() {
//        mSaleItemsOpaqueCover.setVisibility(View.VISIBLE);
//    }

    private void setupSearchFilters() {
        mSearchFilterContainer.setVisibility(View.VISIBLE);

        if (!mSearchFilterRouter.hasRootController()) {
            Bundle bundle = new BundleBuilder(new Bundle())
                    .putString(BundleKeys.KEY_CATEGORY_STRING, new Gson().toJson(mCategoryTreeResponse))
                    .putString(BundleKeys.KEY_FACET_STRING, new Gson().toJson(mFacets))
                    .putString(BundleKeys.KEY_SORTING_STRING, mSortingListJsonString)
                    .putString(BundleKeys.SALEITEMS_SALE_ID, mSaleId)
                    .putString(BundleKeys.SALEITEMS_CATEGORY_MAP, mCategoryKey)
                    .putString(BundleKeys.KEY_SELECTED_FACETS, mPreviousSelectedFacetIndicesJsonString)
                    .putString(BundleKeys.SALEITEMS_CHIPS_FILTER, new Gson().toJson(mChipFilters))
                    .putString(BundleKeys.KEY_SALE_ITEMS_TITLE, mSearchQuery)
                    .build();

            mSearchFilterController = ControllerFactory.getInstance(GateKeeper.Destination.SEARCH_FILTER, bundle);
            mSearchFilterMvpView = (SearchFilterMvpView) mSearchFilterController;
            GateKeeper.setRoot(mSearchFilterRouter, GateKeeper.Destination.SEARCH_FILTER, RouterTransaction.with(mSearchFilterController));
        }
    }

    public void deactivateSearch() {
        mSaleItemsToolbarField.removeTextChangedListener(mTextWatcher);
        mSearchQuery = mSaleItemsToolbarField.getText().toString();

//        mSaleItemsOpaqueCover.setVisibility(View.GONE);
        mSaleItemsToolbarField.setActivated(false);
        mSaleItemsToolbarField.setVisibility(View.GONE);
        mSaleItemsToolbarTitle.setVisibility(View.VISIBLE);

        mSaleItemsBackIcon.setOnClickListener(view -> onBackClick());
        mSearchBtn.setOnClickListener(view12 -> toggleSearch());
        mCancelText.setOnClickListener(v -> toggleSearch());

        determineToolbarTitle();
        hideKeyboard();
    }

    public void activateSearch() {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (mSaleItemsToolbarField != null && mSaleItemsToolbarField.requestFocus()) {
                InputMethodManager inputMethodManager =
                        (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);

                inputMethodManager.toggleSoftInputFromWindow(
                        mSaleItemsToolbarField.getApplicationWindowToken(),
                        InputMethodManager.SHOW_FORCED, 0);
            }
        }, 200);

//        if (mCategoryKey.isEmpty() && mTitle.isEmpty() && mSearchQuery.isEmpty()) {
//            mSaleItemsToolbarTitle.setText(mActivity.getResources().getString(R.string.i_am_looking_for));
//        }

        mSearchBtn.setOnClickListener(view -> {
//            deactivateSearch();
        });

//        mSaleItemsOpaqueCover.setVisibility(View.VISIBLE);
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
//                showLoading();

                mPresenter.loadSaleItems(createSaleItemsRequest(mSearchFilterMvpView.getCategoryKeys(), mSaleId, 0, mChipFilters, ""));
//                deactivateSearch();
            }

            return false;
        });

    }

    private List<SearchChipModel> removeSearchQueryChips(List<SearchChipModel> chipFilters) {

        List<SearchChipModel> chipList = new ArrayList<>(chipFilters);
        mRemovedChipTitles = new ArrayList<>();
        for (SearchChipModel chip : chipFilters) {
            if (chip.getFilterType().equals(BundleKeys.SEARCH_QUERY_NAME)) {
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

    @Override
    public GetSaleItemsRequest createSaleItemsRequest(Set<String> categoryKeys, String saleId, int pageNumber, List<SearchChipModel> chipsList, String query) {
        return createSaleItemsRequest(StringUtils.generateConcatenatedCategories(categoryKeys), saleId, pageNumber, chipsList, query);
    }

    @Override
    public GetSaleItemsRequest createSaleItemsRequest(String categoryKey, String saleId, int pageNumber, List<SearchChipModel> chipsList, String query) {
        List<String> saleIds = new LinkedList<>();
        HashMap<String, List<String>> facetFilters = new HashMap<>();

        GetSaleItemsRequest getSaleItemsRequest = new GetSaleItemsRequest();

        getSaleItemsRequest = StringUtils.updateSaleItemRequest(categoryKey, getSaleItemsRequest);

        getSaleItemsRequest.setSorting("");
        getSaleItemsRequest.setPageNumber(String.valueOf(pageNumber));
        getSaleItemsRequest.setQuery(query);

        getSaleItemsRequest.setPageSize("50");

        if (saleId != null && !saleId.isEmpty()) {
            saleIds.add(saleId);
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
                    if (facetName.equals(BundleKeys.BRANDS_FACETFILTER_NAME)) {
                        brandNameFacetFilters.add(chip.getChipTitle());
                    } else if (facetName.equals(BundleKeys.COLORS_FACETFILTER_NAME)) {
                        colorFacetFilters.add(chip.getChipTitle());
                    } else if (facetName.equals(BundleKeys.SIZES_FACETFILTER_NAME)) {
                        sizesFacetFilters.add(chip.getChipTitle());
                    } else if (facetName.equals(BundleKeys.PRICE_FACETFILTER_NAME)) {
                        priceFacetFilters.add(chip.getChipTitle());
                    } else if (facetName.equals(BundleKeys.SEARCH_QUERY_NAME)) {
                        searchQueryFilters.add(chip.getChipTitle());
                    } else if (facetName.equals(BundleKeys.SORT_FACETFILTER_NAME)) {
                        getSaleItemsRequest.setSorting(mapSortingTitleToKey(chip.getChipTitle()));
                    }
                }

                facetFilters.put(BundleKeys.BRANDS_FACETFILTER_NAME, brandNameFacetFilters);
                facetFilters.put(BundleKeys.COLORS_FACETFILTER_NAME, colorFacetFilters);
                facetFilters.put(BundleKeys.SIZES_FACETFILTER_NAME, sizesFacetFilters);
                facetFilters.put(BundleKeys.PRICE_FACETFILTER_NAME, priceFacetFilters);


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

        page = pageNumber;

        return getSaleItemsRequest;
    }

    @Override
    public void setIsCategoryChanged(boolean isCategoryChanged) {
        mIsCategoryChanged = isCategoryChanged;
    }

    private void buildSearchQueryChips(List<SearchChipModel> chipFilters) {
        if (!mSearchQuery.isEmpty()) {
            String[] splitted = mSearchQuery.split("\\s+");
            for (String str : splitted) {
                chipFilters.add(new SearchChipModel(BundleKeys.SEARCH_QUERY_NAME, str, -1));
            }
        }

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
