package au.com.dealsdirect.ui.controller.saleitemdetails;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.events.YouMayAlsoLikeEventRequest;
import au.com.dealsdirect.data.network.model.productdetails.GetYouMayAlsoLikeResponse;
import au.com.dealsdirect.data.network.model.saleitemdetails.RecommendedItemsResponse;
import au.com.dealsdirect.service.datacollection.core.DataCollector;
import au.com.dealsdirect.service.datacollection.enums.EventTypeId;
import au.com.dealsdirect.service.datacollection.enums.Events;
import au.com.dealsdirect.ui.controller.saleitemdetails.listener.SaleDetailsImageListener;
import au.com.dealsdirect.ui.controller.shops.adapter.HorizontalScrollingBannerAdapter;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.ScreenUtils;
import au.com.dealsdirect.utils.ScrollingImageHorizontal.HorizontalRecyclerViewHolder;


/**
 * Created by MTC on 2019-12-17.
 */
public class SaleItemDetailsScrollingImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<GetYouMayAlsoLikeResponse> mData;
    private List<RecommendedItemsResponse> mRecommendedData = new ArrayList<>();
    private HorizontalScrollingBannerAdapter mSlidingBannersAdapter = null;
    private static final float SLIDING_BANNER_WIDTH_PERCENT = 0.7f;
    public static final int VIEW_HOLDER_TYPE_LANDSCAPE = 1;
    public static final int VIEW_HOLDER_TYPE_SLIDING_BANNER = 1 << 2;
    private RecyclerView recyclerView = null;
    private int mOffset;
    private SaleDetailsImageListener mListener;
    SaleItemDetailsMvpPresenter mPresenter;

    private static final int[] BANNER_ORDER = {
            VIEW_HOLDER_TYPE_SLIDING_BANNER
    };


    public SaleItemDetailsScrollingImageAdapter(Context context, SaleItemDetailsMvpPresenter presenter,
                                                SaleDetailsImageListener listener,
                                                List<GetYouMayAlsoLikeResponse> data,
                                                List<RecommendedItemsResponse> recommendedData) {

        mContext = context;
        mPresenter = presenter;
        mListener = listener;
        mData = data;
        mRecommendedData = recommendedData;

        setupSlidingBannersDimensions();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_sale_details_scrolling_image, parent, false);

        return new HorizontalRecyclerViewHolder(view,
                (int) computeSlidingBannersGrid().getItemHeight(),
                mSlidingBannersAdapter,
                true,
                true);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (holder instanceof HorizontalRecyclerViewHolder) {
            HorizontalRecyclerViewHolder horizontalRecyclerViewHolder = (HorizontalRecyclerViewHolder) holder;
            horizontalRecyclerViewHolder.onViewRecycled();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        HorizontalRecyclerViewHolder horizontalRecyclerViewHolder = (HorizontalRecyclerViewHolder) holder;
        horizontalRecyclerViewHolder.onViewBound();
        setupSlidingBannersDimensions();
        horizontalRecyclerViewHolder.setAdapter(mSlidingBannersAdapter);

        if (mSlidingBannersAdapter != null) {
            mSlidingBannersAdapter.resetReyclerViewPosition();
            horizontalRecyclerViewHolder.snapToCenter(false);
        }
    }

    private ImageUtils.Grid computeSlidingBannersGrid() {
        //set similar image height and width of sponsored banners
        int numberOfColumns = mPresenter.isTablet() ?
                mContext.getResources().getInteger(R.integer.sponsored_banner_column_count_for_tablet) :
                mContext.getResources().getInteger(R.integer.sponsored_banner_column_count);
        int width = mContext.getResources().getInteger(R.integer.sponsored_banner_width);
        int height = mContext.getResources().getInteger(R.integer.sponsored_banner_height);
        return ImageUtils.getRangedGridDefinition(
                width, height,
                ScreenUtils.getScreenWidth(mContext),
                numberOfColumns, numberOfColumns);
    }

    private void setupSlidingBannersDimensions() {
        if (mSlidingBannersAdapter != null) {
            ImageUtils.Grid slidingBannersGrid = computeSlidingBannersGrid();
            mSlidingBannersAdapter.setupDimensions(
                    (int) slidingBannersGrid.getItemWidth(),
                    (int) slidingBannersGrid.getItemHeight());
        }
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public void addAll(List<GetYouMayAlsoLikeResponse> bannerResponses) {
        int previousCount = mData.size();

        mData.addAll(bannerResponses);

        mOffset = bannerResponses.size();
        if (recyclerView != null && !recyclerView.isComputingLayout()) {
            notifyItemRangeInserted(previousCount, mData.size() - previousCount);
        }
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        if (holder instanceof HorizontalRecyclerViewHolder) {
            HorizontalRecyclerViewHolder viewHolder = (HorizontalRecyclerViewHolder) holder;
            viewHolder.onViewRecycled();
            viewHolder.recyclerView.setAdapter(null);
        }
        super.onViewRecycled(holder);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.recyclerView = null;
    }

    public void setSlidingBannersAdapter(HorizontalScrollingBannerAdapter slidingBannersAdapter) {
        boolean willInsert = mSlidingBannersAdapter == null && slidingBannersAdapter != null;
        boolean willDelete = mSlidingBannersAdapter != null && slidingBannersAdapter == null;
        if (willDelete) {
            int index = getPositionOfSlidingBanners();
            mSlidingBannersAdapter.setOnItemTappedListener(null);
            mSlidingBannersAdapter = null;
            notifyDataSetChanged();
        } else {
            mSlidingBannersAdapter = slidingBannersAdapter;
            if (mSlidingBannersAdapter != null) {

                if (mSlidingBannersAdapter.getBannerViewType() == HorizontalScrollingBannerAdapter.BannerViewType.YouMayAlsoLike) {
                    mSlidingBannersAdapter
                            .setOnItemTappedListener(SaleItemDetailsScrollingImageAdapter.this::onBannerTapped);
                } else {
                    mSlidingBannersAdapter
                            .setOnItemRecommendedListener(SaleItemDetailsScrollingImageAdapter.this::onBannerTapped);
                }

            }
            notifyDataSetChanged();
        }
    }

    private int getPositionOfSlidingBanners() {
        return getPositionOfViewType(VIEW_HOLDER_TYPE_SLIDING_BANNER);
    }

    private int getPositionOfViewType(int viewType) {
        int pos = -1;
        if (isViewTypeVisible(viewType)) {
            for (int i = BANNER_ORDER.length - 1; i >= 0; i--) {
                if (pos < 0) {
                    if (BANNER_ORDER[i] == (viewType & (~VIEW_HOLDER_TYPE_LANDSCAPE))) {
                        pos = i;
                    }
                } else if (!isViewTypeVisible(BANNER_ORDER[i])) {
                    pos--;
                }
            }
        }
        return pos;
    }

    private boolean isViewTypeVisible(int viewType) {
        switch (viewType & (~VIEW_HOLDER_TYPE_LANDSCAPE)) {
            case VIEW_HOLDER_TYPE_SLIDING_BANNER:
                return isSlidingBannersVisible();
            default:
                for (int value : BANNER_ORDER) {
                    if (value == (viewType & (~VIEW_HOLDER_TYPE_LANDSCAPE))) {
                        return true;
                    }
                }
                return false;
        }
    }

    private boolean isSlidingBannersVisible() {
        return mSlidingBannersAdapter != null;
    }


    private void onBannerTapped(GetYouMayAlsoLikeResponse responseLike) {

        YouMayAlsoLikeEventRequest YouMayAlsoLikeEventRequest = new YouMayAlsoLikeEventRequest();
        YouMayAlsoLikeEventRequest.setEventType(EventTypeId.EVENT_YOU_MAY_ALSO_LIKE);

        YouMayAlsoLikeEventRequest.RecommendationsViewInfo recommendationsViewInfo = new YouMayAlsoLikeEventRequest.RecommendationsViewInfo();
        recommendationsViewInfo.setType(EventTypeId.EVENT_SIMS);
        recommendationsViewInfo.setProductId(responseLike.getMasterProductId());
        recommendationsViewInfo.setProductsQty(mData.size());

        int position = 0;
        for (int i = 0; i < mData.size(); i++) {
            if (responseLike.getMasterProductId().equals(mData.get(i).getMasterProductId())) {
                position = i;
            }
        }

        recommendationsViewInfo.setPosition(String.valueOf(position));
        YouMayAlsoLikeEventRequest.setRecommendationsViewInfo(recommendationsViewInfo);

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(DataCollector.EventParameters.SALE_NAME, responseLike.getName());
        parameters.put(DataCollector.EventParameters.SCREEN_NAME, "Product Details");
        parameters.put(DataCollector.EventParameters.YOU_MAY_ALSO_LIKE_REQUEST, YouMayAlsoLikeEventRequest);
        DataCollector.logEvent(Events.YouMayAlsoLikeEvent, parameters);

        mListener.reloadSaleItemDetails(responseLike);
    }

    private void onBannerTapped(RecommendedItemsResponse response) {
        mListener.reloadSaleItemDetails(response);
    }

    public void clear() {
        mOffset = 0;
        mData.clear();
        mRecommendedData.clear();
        if (recyclerView != null && !recyclerView.isComputingLayout()) {
            notifyDataSetChanged();
        }
    }
}
