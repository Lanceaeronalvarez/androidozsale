package au.com.dealsdirect.ui.controller.saleitems;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.paginate.Paginate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsResponse;
import au.com.dealsdirect.data.network.model.sorting.SortingResponse;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.saleitems.adapter.SaleItemsAdapter;
import au.com.dealsdirect.ui.controller.searchfilter.SearchFilterController;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.SearchChipModel;
import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.ui.main.SharedActivity;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.JsonUtils;
import au.com.dealsdirect.utils.PaginateUtils;
import butterknife.BindView;
import butterknife.OnClick;

import static au.com.dealsdirect.ui.controller.searchfilter.SearchFilterController.KEY_ORIG_SELECTED;
import static au.com.dealsdirect.ui.controller.searchfilter.SearchFilterController.KEY_SELECTED_FACETS;

/**
 * dp Created by Admin on 6/8/17.
 */

public class SaleItemsController extends BaseController implements SaleItemsMvpView {

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

    private String mSaleId;
    private String mTitle;
    private String mCategoryKey = "";
    private String mSearchQuery;
    private boolean hasShowedItems = false;

    private List<GetSaleItemsResponse.Products> mSaleItems = new LinkedList<>();
    private List<GetSaleItemsResponse.Facets> mFacets = new ArrayList<>();
    private String mSortingListJsonString = "";

    @BindView(R.id.controller_sale_items_grid_view)
    RecyclerView mSaleItemsRecyclerView;

    @BindView(R.id.partial_toolbar_arrow_title)
    TextView mTitleTextView;

    @BindView(R.id.controller_sale_items_placeholder)
    LinearLayout mPlaceholder;

    private SaleItemsAdapter mSaleItemsAdapter;

    private Paginate mPaginateManager;

    private Paginate.Callbacks mPaginateCallbacks;

    private int page = 0;
    private boolean loadingInProgress = false;
    private boolean hasLoadedAllItems = false;
    private boolean isFiltered = false;

    private List<SearchChipModel> mChipFilters = new ArrayList<>();

    //store state of selection from filters
    private String mPreviousSelectedFacetIndicesJsonString = "";

    boolean initialLoad = false;

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

        if (args.containsKey(KEY_TITLE))
            mTitle = getArgs().getString(KEY_TITLE, "");
        if (args.containsKey(KEY_SALE_ID))
            mSaleId = getArgs().getString(KEY_SALE_ID, "");
        if (args.containsKey(KEY_CATEGORY_MAP))
            mCategoryKey = getArgs().getString(KEY_CATEGORY_MAP, "");
        if (args.containsKey(KEY_SEARCH_QUERY))
            mSearchQuery = getArgs().getString(KEY_SEARCH_QUERY, "");
        if (args.containsKey(KEY_CHIPS_FILTER))
            mChipFilters = JsonUtils.convertStringToObject(getArgs().getString(KEY_CHIPS_FILTER, ""), new TypeToken<ArrayList<SearchChipModel>>() {
            }.getType());

    }

    public void onPassFiltersData(Bundle args) {
        if (args.containsKey(KEY_TITLE))
            mTitle = args.getString(KEY_TITLE, "");
        if (args.containsKey(KEY_SALE_ID))
            mSaleId = args.getString(KEY_SALE_ID, "");
        if (args.containsKey(KEY_CATEGORY_MAP))
            mCategoryKey = args.getString(KEY_CATEGORY_MAP, "");
        if (args.containsKey(KEY_SEARCH_QUERY))
            mSearchQuery = args.getString(KEY_SEARCH_QUERY, "");
        if (args.containsKey(KEY_CHIPS_FILTER)) {
            mChipFilters = JsonUtils.convertStringToObject(args.getString(KEY_CHIPS_FILTER, ""), new TypeToken<ArrayList<SearchChipModel>>() {
            }.getType());
        }
        if (args.containsKey(KEY_SELECTED_FACETS))
            mPreviousSelectedFacetIndicesJsonString = args.getString(KEY_SELECTED_FACETS,"");

        isFiltered = true;
        page = 0;
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_sale_items, container, false);
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);

        assert (getActivity()) != null;
        ((MainActivity) getActivity()).setDraggableViewPager(false);

        if (mCategoryKey != null && !mCategoryKey.isEmpty()) {
            Log.d("saletitle", "with category " + mCategoryKey);

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
            mTitleTextView.setText(newString);

        } else {
            Log.d("saletitle", "without category ");
            mTitleTextView.setText(mTitle);
        }
        setUp(view);
    }

    @Override
    public void onDetach(View view) {
        mPresenter.onDetach();
        super.onDetach(view);
    }

    @Override
    protected void setUp(View view) {
        hideKeyboard();

        mPresenter.loadSortingFacets();

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

        mSaleItemsAdapter = new SaleItemsAdapter(mSaleItems, mPresenter, mSaleId, mTitle);
        mSaleItemsRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mSaleItemsRecyclerView.setAdapter(mSaleItemsAdapter);


        showLoading();
        mPresenter.loadSaleItems(mCategoryKey, mSaleId, mSearchQuery, page, mChipFilters);

        if(!mSaleItems.isEmpty()){
            mPaginateManager = PaginateUtils.init(mSaleItemsRecyclerView, mPaginateCallbacks);
        }

    }

    @Override
    public void onLoadSortingFacetsFinished(List<SortingResponse> responseList) {
        mSortingListJsonString = new Gson().toJson(responseList);
    }

    @Override
    public void showSaleItems(GetSaleItemsResponse getSaleItemsResponse) {
        List<GetSaleItemsResponse.Products> items = getSaleItemsResponse.products;
        mFacets = getSaleItemsResponse.facets;

        loadingInProgress = false;

        if (!initialLoad) {
            mSaleItemsAdapter.replaceData(items);
            mPaginateManager = PaginateUtils.init(mSaleItemsRecyclerView, mPaginateCallbacks);
            initialLoad = true;
        } else if(isFiltered){
            mSaleItemsAdapter.replaceData(items);
            isFiltered = false;
        } else {
            mSaleItemsAdapter.addData(items);
            if (items.size() == 0) {
                hasLoadedAllItems = true;
            }
        }



        mSaleItems = mSaleItemsAdapter.getData();

        if (mSaleItems == null || mSaleItems.isEmpty()) {
            mPlaceholder.setVisibility(View.VISIBLE);
            mSaleItemsRecyclerView.setVisibility(View.GONE);
            mPaginateManager.unbind();
        } else {
            mPlaceholder.setVisibility(View.GONE);
            mSaleItemsRecyclerView.setVisibility(View.VISIBLE);
        }

//        if (page == 0) {
//            mSaleItemsAdapter = new SaleItemsAdapter(mSaleItems, mPresenter, mSaleId, mTitle);
//            mSaleItemsRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
//            mSaleItemsRecyclerView.setAdapter(mSaleItemsAdapter);
//
//            mPaginateManager = PaginateUtils.init(mSaleItemsRecyclerView, mPaginateCallbacks);
//        } else {
//            mSaleItemsAdapter.addData(mSaleItems);
//        }
    }


    @Override
    protected void onSaveViewState(@NonNull View view, @NonNull Bundle outState) {
        super.onSaveViewState(view, outState);
        Log.d("flowTesting", "onSaveViewState");
    }

    @Override
    protected void onRestoreViewState(@NonNull View view, @NonNull Bundle savedViewState) {
        super.onRestoreViewState(view, savedViewState);
        Log.d("flowTesting", "onRestoreViewState");
    }

    @SuppressWarnings("ConstantConditions")
    @OnClick(R.id.partial_toolbar_arrow_view)
    void onBackClick() {
        getActivity().onBackPressed();
    }

    @OnClick(R.id.partial_toolbar_filter_view)
    void showFilters() {
        getRouter().pushController(RouterTransaction.with(SearchFilterController.newInstance(new Gson().toJson(mFacets)
                , mSortingListJsonString
                , mSaleId
                , mCategoryKey
                ,mPreviousSelectedFacetIndicesJsonString
                ,new Gson().toJson(mChipFilters)))
                .pushChangeHandler(new VerticalChangeHandler())
                .popChangeHandler(new VerticalChangeHandler()));
    }

    @Override
    public void refresh() {
        loadingInProgress = true;
        mPresenter.loadSaleItems(mCategoryKey, mSaleId, mSearchQuery, page, mChipFilters);
    }

    @Override
    public void showProductDetails(RecyclerView.ViewHolder viewHolder, int position, String seoIdentifierId, String imageUrl, String itemId, String saleId) {

        List<String> names = new ArrayList<>();
        names.add(itemId);
        mSaleItemsRecyclerView.smoothScrollToPosition(position);

        final Handler handler = new Handler();
        handler.postDelayed(() -> {

            Intent intent = new Intent();
            intent.setClass(getActivity(), SharedActivity.class);

            intent.putExtra("KEY_IMAGE_ID", imageUrl);
            intent.putExtra("KEY_SEO_IDENTIFIER", seoIdentifierId);
            intent.putExtra("KEY_ITEM_ID", itemId);
            intent.putExtra("KEY_SALE_ID", saleId);
            intent.putExtra("KEY_SALE_NAME", ((SaleItemsAdapter.ViewHolder) viewHolder).mSaleItemName.getText());
            intent.putExtra("KEY_SALE_PRICE", ((SaleItemsAdapter.ViewHolder) viewHolder).mSalePrice.getText());
            intent.putExtra("KEY_SALE_OLD_PRICE", ((SaleItemsAdapter.ViewHolder) viewHolder).mOldPrice.getText());
            Log.d("LogBundle", ((SaleItemsAdapter.ViewHolder) viewHolder).mSaleItemName.getText() + " , " +
                    ((SaleItemsAdapter.ViewHolder) viewHolder).mSalePrice.getText() + " , " +
                    ((SaleItemsAdapter.ViewHolder) viewHolder).mOldPrice.getText());


            ActivityOptions options =
                    ActivityOptions.makeSceneTransitionAnimation(getActivity(),
                            Pair.create(((SaleItemsAdapter.ViewHolder) viewHolder).mSaleItemImage, "transition"),
                            Pair.create(((SaleItemsAdapter.ViewHolder) viewHolder).mSaleItemImage, "cardbackground"));

            //noinspection ConstantConditions
            getActivity().startActivityForResult(intent, getActivity().getTaskId(), options.toBundle());

        }, 200);
    }
}
