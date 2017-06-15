package au.com.dealsdirect.ui.controller.saleitems;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.bluelinelabs.conductor.changehandler.TransitionChangeHandlerCompat;
import com.paginate.Paginate;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.saleitems.GetPublicSaleItemsRequest;
import au.com.dealsdirect.data.network.model.saleitems.GetPublicSaleItemsResponse;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.productdetails.ProductDetailsController;
import au.com.dealsdirect.ui.controller.saleitems.adapter.SaleItemsAdapter;
import au.com.dealsdirect.ui.custom.transitions.SharedElementTransitionChangehandler;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.PaginateUtils;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * dp Created by Admin on 6/8/17.
 */

public class SaleItemsController extends BaseController implements SaleItemsMvpView {

    private static final String KEY_SALE_ID = "SaleItemsController.KEY_SALE_ID";
    private static final String KEY_TITLE = "SaleItemsController.KEY_TITLE";
    private static final String KEY_HEADER_IMAGE = "SaleItemsController.header_image_url";
    private static final String KEY_FROM_POSITION = "SaleItemsController.position";

    private String mSaleId;
    private String mTitle;
    private int fromPosition;
    private String imageHeaderUrl;

    private List<GetPublicSaleItemsResponse.Item> saleItems = new LinkedList<>();


    @BindView(R.id.controller_sale_items_grid_view)
    RecyclerView mSaleItemsRecyclerView;

    @BindView(R.id.partial_toolbar_arrow_title)
    TextView mTitleTextView;

    private SaleItemsAdapter mSaleItemsAdapter;

    private Paginate mPaginateManager;

    private Paginate.Callbacks mPaginateCallbacks;

    int page = 0;
    private boolean loadingInProgress = false;
    private boolean hasLoadedAllItems = false;

    @Inject
    SaleItemsMvpPresenter<SaleItemsMvpView> mPresenter;

    public SaleItemsController(String bannerTitle, String saleId, int fromPosition,
                               String imageUrl) {

        this(new BundleBuilder(new Bundle()).putString(KEY_SALE_ID, saleId)
                .putString(KEY_TITLE, bannerTitle)
                .putString(KEY_SALE_ID, saleId)
                .putString(KEY_HEADER_IMAGE, imageUrl)
                .putInt(KEY_FROM_POSITION, fromPosition)
                .build());
    }


    public SaleItemsController(Bundle args) {
        super(args);
        mTitle = getArgs().getString(KEY_TITLE);
        mSaleId = getArgs().getString(KEY_SALE_ID);
        fromPosition = getArgs().getInt(KEY_FROM_POSITION);
        imageHeaderUrl = getArgs().getString(KEY_HEADER_IMAGE);

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

        setUp(view);

        GetPublicSaleItemsRequest getPublicSaleItemsRequest =
                new GetPublicSaleItemsRequest(mSaleId, 100, "en", "DA", "");


        if (saleItems.size() == 0) {
            mPresenter.loadSaleItems(getPublicSaleItemsRequest);
        } else {
            mSaleItemsAdapter = new SaleItemsAdapter(saleItems, mPresenter, mSaleId);
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
            mSaleItemsRecyclerView.setLayoutManager(layoutManager);
            mSaleItemsRecyclerView.setAdapter(mSaleItemsAdapter);
            mPaginateManager = PaginateUtils.init(mSaleItemsRecyclerView, mPaginateCallbacks);

        }
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @Override
    protected void setUp(View view) {

        mTitleTextView.setText(mTitle);

        mPaginateCallbacks = new Paginate.Callbacks() {
            @Override
            public void onLoadMore() {
                // Load next page of data (e.g. network or database)
                refresh();
                page++;
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

    }

    @Override
    public void showSaleItems(GetPublicSaleItemsResponse getPublicSaleItemsResponse) {

        List<GetPublicSaleItemsResponse.List> tempList =
                getPublicSaleItemsResponse.getGetPublicSaleItemsObject()
                        .getList();

        List<GetPublicSaleItemsResponse.Item> allItems = new LinkedList<>();

        for (int x = 0; x < tempList.size(); x++) {

            List<GetPublicSaleItemsResponse.SubCategory> tempSubCategories = new LinkedList<>();
            tempSubCategories = tempList.get(x)
                    .getSubCategories();

            for (int y = 0; y < tempSubCategories.size(); y++) {

                List<GetPublicSaleItemsResponse.Item> tempItems;
                tempItems = tempSubCategories.get(y)
                        .getItems();

                for (int z = 0; z < tempItems.size(); z++) {
                    allItems.add(tempItems.get(z));

                }
            }
        }

        saleItems = allItems;
        mSaleItemsAdapter = new SaleItemsAdapter(allItems, mPresenter, mSaleId);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        mSaleItemsRecyclerView.setLayoutManager(layoutManager);
        mSaleItemsRecyclerView.setAdapter(mSaleItemsAdapter);
        mPaginateManager = PaginateUtils.init(mSaleItemsRecyclerView, mPaginateCallbacks);

        loadingInProgress = false;
        hasLoadedAllItems = true;
        mPaginateManager.setHasMoreDataToLoad(!hasLoadedAllItems);
    }

    @Override
    public void refresh() {
        // showFilterBar();
        loadingInProgress = true;
        //        mPresenter.searchProducts(page, mToolbarEditText.getText().toString(), FilterSingleton.getSelectedFilters(mFilterMode));
    }

    @Override
    public void showProductDetails(String imageUrl, String itemId, String saleId) {

        String imageTransitionName = itemId;

        List<String> names = new ArrayList<>();
        names.add(imageTransitionName);

        getRouter().pushController(RouterTransaction.with(new ProductDetailsController(imageUrl, itemId, saleId))
                .pushChangeHandler(
                        new TransitionChangeHandlerCompat(
                                new SharedElementTransitionChangehandler(names),
                                new FadeChangeHandler()))
                .popChangeHandler(
                        new TransitionChangeHandlerCompat(
                                new SharedElementTransitionChangehandler(names),
                                new FadeChangeHandler())));

    }

    @OnClick(R.id.partial_toolbar_arrow_view)
    public void onBackClick() {
        getActivity().onBackPressed();
    }
}
