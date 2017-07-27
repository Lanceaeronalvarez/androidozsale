package au.com.dealsdirect.ui.controller.search;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.bumptech.glide.load.Key;
import com.paginate.Paginate;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsResponse;
import au.com.dealsdirect.data.network.model.sorting.SortingResponse;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsMvpPresenter;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsMvpView;
import au.com.dealsdirect.ui.controller.saleitems.adapter.SaleItemsAdapter;
import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.ui.main.SharedActivity;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.KeyboardUtils;
import au.com.dealsdirect.utils.PaginateUtils;
import butterknife.BindView;
import butterknife.OnClick;

/*
 * Created by DP on 06/19/2017.
 */

public class SearchController extends BaseController implements SaleItemsMvpView {

    public static final String TAG = "SearchController";
    private static final String KEY_TEXT = "SearchController.KEY_TEXT";

    @BindView(R.id.partial_toolbar_search_right_option)
    ImageButton mSearchToolbarRightOption;

    @BindView(R.id.partial_toolbar_search_left_option)
    ImageButton mSearchToolbarLeftOption;

    @BindView(R.id.partial_toolbar_search_field)
    EditText mSearchToolbarSearchField;

    @BindView(R.id.controller_search_sale_items_placeholder)
    LinearLayout mSearchPlaceholder;

    @BindView(R.id.controller_search_sale_items_grid_view)
    RecyclerView mSearchSaleItemsRecyclerView;

    @Inject
    SaleItemsMvpPresenter<SaleItemsMvpView> mPresenter;

    private SaleItemsAdapter mSaleItemsAdapter;

    private Paginate mPaginateManager;
    private Paginate.Callbacks mPaginateCallbacks;

    private int page = 0;
    private boolean loadingInProgress = false;
    private boolean hasLoadedAllItems = false;

    private List<GetSaleItemsResponse.Products> saleItems = new LinkedList<>();

    private String searchQuery = "";

    public static SearchController newInstance() {

        return new SearchController(
                new BundleBuilder(new Bundle())
                        .build());
    }

    public SearchController(Bundle args) {
        super(args);
    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);

        assert (getActivity()) != null;
        ((MainActivity)getActivity()).hideKeyboard();
    }

    @NonNull
    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_search, container, false);

        getControllerComponent().inject(this);

        mPresenter.onAttach(this);

        return view;
    }

    @Override
    public void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    @Override
    protected void setUp(View view) {
        // Setup views here
        mPresenter.loadSaleItems("","","" ,0, new ArrayList());

        assert (getActivity()) != null;
        ((MainActivity)getActivity()).setDraggableViewPager(false);

        mSearchToolbarSearchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mPresenter.loadSaleItems("","",charSequence.toString(),0, new ArrayList());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

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


        //noinspection ConstantConditions
        mSearchToolbarRightOption.setImageDrawable(
                getActivity().getResources().getDrawable(R.drawable.ic_close));

        mSearchToolbarSearchField.setActivated(true);
        mSearchToolbarSearchField.setFocusable(true);

//        if (mSearchToolbarSearchField.requestFocus()) {
//            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//        }
    }

    @Override
    public void onDestroyView(View view) {
        mPresenter.onDetach();
//        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        super.onDestroyView(view);
    }


    @Override
    public void onLoadSortingFacetsFinished(List<SortingResponse> responseList) {

    }

    @Override
    public void showSaleItems(GetSaleItemsResponse getSaleItemsResponse) {
        saleItems = getSaleItemsResponse.products;

        loadingInProgress = false;

        if(saleItems == null || saleItems.isEmpty()){
            if (!hasLoadedAllItems){
                hasLoadedAllItems = true;
                mSearchPlaceholder.setVisibility(View.VISIBLE);
                mSearchSaleItemsRecyclerView.setVisibility(View.GONE);
                return;
            }
        }else{

            hasLoadedAllItems = true;
            mSearchPlaceholder.setVisibility(View.GONE);
            mSearchSaleItemsRecyclerView.setVisibility(View.VISIBLE);
        }

        if (page == 0) {
            mSaleItemsAdapter = new SaleItemsAdapter(saleItems, mPresenter, null, searchQuery);
            mSearchSaleItemsRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            mSearchSaleItemsRecyclerView.setAdapter(mSaleItemsAdapter);

            mPaginateManager = PaginateUtils.init(mSearchSaleItemsRecyclerView, mPaginateCallbacks);
        } else {
            mSaleItemsAdapter.addData(saleItems);
        }

        if (mSearchToolbarSearchField.requestFocus()) {
            KeyboardUtils.showSoftInput(mSearchToolbarSearchField, getActivity());
        }
    }

    @Override
    public void refresh() {

    }

    @Override
    public void showProductDetails(RecyclerView.ViewHolder viewHolder, int position, String seoIdentifierId, String imageUrl, String itemId, String saleId) {

        List<String> names = new ArrayList<>();
        names.add(itemId);
        mSearchSaleItemsRecyclerView.smoothScrollToPosition(position);

        final Handler handler = new Handler();
        handler.postDelayed(() -> {

            Intent intent = new Intent();
            intent.setClass(getActivity(), SharedActivity.class);

            intent.putExtra("KEY_IMAGE_ID", imageUrl);
            intent.putExtra("KEY_SEO_IDENTIFIER", seoIdentifierId);
            intent.putExtra("KEY_ITEM_ID", itemId);
            intent.putExtra("KEY_SALE_ID", saleId);
            intent.putExtra("KEY_SALE_NAME", ((SaleItemsAdapter.ViewHolder) viewHolder).mSaleItemName.getText());
            intent.putExtra("KEY_SALE_PRICE",((SaleItemsAdapter.ViewHolder) viewHolder).mSalePrice.getText());
            intent.putExtra("KEY_SALE_OLD_PRICE",((SaleItemsAdapter.ViewHolder) viewHolder).mOldPrice.getText());


            ActivityOptions options =
                    ActivityOptions.makeSceneTransitionAnimation(getActivity(),
                            Pair.create(((SaleItemsAdapter.ViewHolder) viewHolder).mSaleItemImage, "transition"),
                            Pair.create(((SaleItemsAdapter.ViewHolder) viewHolder).mSaleItemImage, "cardbackground"));

            //noinspection ConstantConditions
            getActivity().startActivityForResult(intent, getActivity().getTaskId(), options.toBundle());

        }, 200);
    }

    @OnClick(R.id.partial_toolbar_search_right_option)
    void onBackClick(){
        assert (getActivity()) != null;
        ((MainActivity)getActivity()).hideKeyboard();
        getActivity().onBackPressed();
    }
}
