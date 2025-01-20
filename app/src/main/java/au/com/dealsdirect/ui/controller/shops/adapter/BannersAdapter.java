package au.com.dealsdirect.ui.controller.shops.adapter;

import static au.com.dealsdirect.ui.controller.shops.adapter.HorizontalScrollingBannerAdapter.BannerStyle;

import android.content.Context;
import android.content.res.Configuration;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Priority;
import com.jakewharton.rxbinding2.view.RxView;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.banner.GetBannerResponse;
import au.com.dealsdirect.service.datacollection.enums.Events;
import au.com.dealsdirect.ui.controller.bestsellers.BestSellersWidgetHelper;
import au.com.dealsdirect.ui.controller.saleitemdetails.HorizontalScrollingItemsAdapter;
import au.com.dealsdirect.ui.controller.trendingbrands.TrendingBrandsWidgetHelper;
import au.com.dealsdirect.utils.CommonUtils;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.ScreenUtils;
import au.com.dealsdirect.utils.ScrollingImageHorizontal.HorizontalRecyclerBannerViewHolder;
import au.com.dealsdirect.utils.ScrollingImageHorizontal.HorizontalRecyclerItemsViewHolder;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

public class BannersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ResettableDimensions {

    private int mOrientation;

    private int mComputedWidth = -1;
    private int mComputedHeight = -1;
    private final List<GetBannerResponse.Group> mGroups = new ArrayList<>();
    private final List<GetBannerResponse.Banner> mSales = new ArrayList<>();

    private GetBannerResponse.Banner promoBanner;
    private GetBannerResponse.Banner leaderboardBanner = null;
    private Consumer<Object> leaderboardBannerOnClick = null;
    private final Context context;
    private int mWidth;
    private int mHeight;
    private final int mWidthForPromoBanner;
    private final int mHeightForPromoBanner;
    private int mNumberOfColumns;
    private int mOffset;
    private String mLastGroupType = "";
    private final boolean isTablet;
    private static final int SPANNABLE_STRING_START_INDEX = 6;
    private static final float DISCOUNT_VALUE_SCALE_FACTOR = 1.8f;

    // note: please don't combine different banner types
    // with type int of 32 bits, we have a maximum of 32 banner/widget types
    public static final int VIEW_HOLDER_TYPE_OLD = 1;
    public static final int VIEW_HOLDER_TYPE_LANDSCAPE = 1 << 1;
    public static final int VIEW_HOLDER_TYPE_NORMAL_BANNER = 1 << 2;
    public static final int VIEW_HOLDER_TYPE_LEADER_BANNER = 1 << 3;
    public static final int VIEW_HOLDER_TYPE_PROMO_BANNER = 1 << 4;
    public static final int VIEW_HOLDER_TYPE_SLIDING_BANNER = 1 << 5;
    public static final int VIEW_HOLDER_TYPE_TRENDING_BRANDS_WIDGET = 1 << 6;
    public static final int VIEW_HOLDER_TYPE_BEST_SELLERS_WIDGET = 1 << 7;
    public static final int VIEW_HOLDER_TYPE_SPONSORED_BANNER = 1 << 8;
    public static final int VIEW_HOLDER_TYPE_FOOTER = 1 << 9;
    public static final int VIEW_HOLDER_TYPE_SPACER = 1 << 10;


    private static final int[] BANNER_ORDER = {
            VIEW_HOLDER_TYPE_LEADER_BANNER,
            VIEW_HOLDER_TYPE_PROMO_BANNER,
            VIEW_HOLDER_TYPE_SLIDING_BANNER,
            VIEW_HOLDER_TYPE_TRENDING_BRANDS_WIDGET,
            VIEW_HOLDER_TYPE_SPONSORED_BANNER,
            VIEW_HOLDER_TYPE_SPACER,
            VIEW_HOLDER_TYPE_NORMAL_BANNER // NORMAL_BANNER should always be at the bottom
    };

    private final Map<Integer, Integer> normalBannerInsertPositions;

    private static int getBannerOrderPosition(int viewHolderType) {
        for (int i = 0; i < BANNER_ORDER.length; i++) {
            if (i == viewHolderType) {
                return i;
            }
        }
        return -1;
    }

    private boolean useOldBannerDimensions;

    private static final int THROTTLE_FIRST_WINDOW_DURATION = 1000;

    public static int removeViewHolderOrientationModifier(int viewHolderType) {
        return viewHolderType & (~VIEW_HOLDER_TYPE_LANDSCAPE);
    }

    public static boolean isViewHolderTypeLandscape(int viewHolderType) {
        return (viewHolderType & VIEW_HOLDER_TYPE_LANDSCAPE) != 0;
    }

    public static int removeViewHolderOldModifier(int viewHolderType) {
        return viewHolderType & (~VIEW_HOLDER_TYPE_OLD);
    }

    public static boolean isViewHolderOldType(int viewHolderType) {
        return (viewHolderType & VIEW_HOLDER_TYPE_OLD) != 0;
    }

    public static int removeViewHolderTypeModifiers(int viewHolderType) {
        return removeViewHolderOldModifier(removeViewHolderOrientationModifier(viewHolderType));
    }

    private HorizontalScrollingBannerAdapter mSlidingBannersAdapter = null;
    private HorizontalScrollingBannerAdapter mTrendingBrandsAdapter = null;
    private HorizontalScrollingBannerAdapter mSponsoredBannersAdapter = null;
    private HorizontalScrollingItemsAdapter mBestSellersAdapter = null;
    private final BannersAdapterHelper bannersAdapterHelper;
    private TrendingBrandsWidgetHelper trendingBrandsWidgetHelper = null;
    private BestSellersWidgetHelper bestSellersWidgetHelper = null;


    private final HashSet<HorizontalRecyclerBannerViewHolder> horizontalRecyclerViewHolders = new HashSet<>();

    public BannersAdapter(Context context, List<GetBannerResponse.Group> sales, int orientation, boolean useOldBannerDimensions, boolean isTablet, BannersAdapterHelper helper) {

        replace(sales);

        this.context = context;
        this.isTablet = isTablet;

        mWidthForPromoBanner = context.getResources().getInteger(R.integer.old_banner_mobile_width);
        mHeightForPromoBanner = context.getResources().getInteger(R.integer.old_banner_mobile_height);

        mOrientation = orientation;

        this.useOldBannerDimensions = useOldBannerDimensions;

        bannersAdapterHelper = helper;

        normalBannerInsertPositions = new HashMap<Integer, Integer>() {{
            put(VIEW_HOLDER_TYPE_BEST_SELLERS_WIDGET, isTablet ? 4 : 5);
        }};

        resetDimensions();
    }

    class SpacerViewHolder extends RecyclerView.ViewHolder {

        public SpacerViewHolder(@NonNull View itemView) {
            super(itemView);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, isTablet ? (int) itemView.getResources().getDimension(R.dimen.horizontal_banner_spacer_size_for_tablet) : (int) itemView.getResources().getDimension(R.dimen.horizontal_banner_spacer_size));
            itemView.setLayoutParams(params);
            itemView.setVisibility(View.VISIBLE);
            itemView.setBackgroundColor(itemView.getResources().getColor(R.color.white));
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.viewholder_banner_header_text)
        TextView headerText;

        HeaderViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public void replace(List<GetBannerResponse.Group> bannerResponses) {
        synchronized (mSales) {
            clear();
            for (GetBannerResponse.Group group : bannerResponses) {
                if (group.getType().equals("promo")) {
                    List<GetBannerResponse.Banner> banners = group.getBanners();
                    if (banners != null && !banners.isEmpty()) {
                        promoBanner = banners.get(0);
                        break;
                    }
                }
            }
        }
        addAll(bannerResponses);
    }

    public void addAll(List<GetBannerResponse.Group> bannerResponses) {
        synchronized (mSales) {
            mGroups.addAll(bannerResponses);

            int previousCount = mSales.size();

            for (GetBannerResponse.Group group : bannerResponses) {
                for (GetBannerResponse.Banner banner : group.getBanners()) {
                    preloadImageForBanner(banner);
                    mSales.add(banner);
                }
            }

            if (mOffset == 0 && promoBanner != null) {
                mSales.remove(promoBanner);
            }

            if (!bannerResponses.isEmpty()) {
                GetBannerResponse.Group bannerGroup = bannerResponses.get(bannerResponses.size() - 1);
                if (!mLastGroupType.equals(bannerGroup.getType())) {
                    mOffset = 0;
                }
                mOffset += bannerGroup.getBanners().size();
                mLastGroupType = bannerGroup.getType();
                notifyItemRangeInserted(getPositionOfNormalBanners() + previousCount, mSales.size() - previousCount);
            }
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;

        switch (removeViewHolderOldModifier(removeViewHolderOrientationModifier(viewType))) {
            case VIEW_HOLDER_TYPE_SPACER:
                return new SpacerViewHolder(new View(parent.getContext(), null));
            case VIEW_HOLDER_TYPE_LEADER_BANNER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_leaderboard_banner, parent, false);
                return new LeaderboardBannerViewHolder(view, getLeaderboardBannerHeight());
            case VIEW_HOLDER_TYPE_SLIDING_BANNER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_horizontal_scrolling_banner, parent, false);
                return new HorizontalRecyclerBannerViewHolder(
                        view,
                        (int) computeSlidingBannersGrid().getItemHeight(),
                        mSlidingBannersAdapter,
                        true,
                        true,
                        HorizontalPageIndicatorAdapter.Style.CIRCLE);
            case VIEW_HOLDER_TYPE_TRENDING_BRANDS_WIDGET: {
                if (trendingBrandsWidgetHelper == null) {
                    trendingBrandsWidgetHelper = new TrendingBrandsWidgetHelper(mTrendingBrandsAdapter, context, isTablet);
                }
                return trendingBrandsWidgetHelper.createViewHolder(parent);
            }
            case VIEW_HOLDER_TYPE_BEST_SELLERS_WIDGET:
                if (bestSellersWidgetHelper == null) {
                    bestSellersWidgetHelper = new BestSellersWidgetHelper(mBestSellersAdapter, context, isTablet);
                }
                return bestSellersWidgetHelper.createViewHolder(parent, mOrientation);
            case VIEW_HOLDER_TYPE_SPONSORED_BANNER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_horizontal_scrolling_banner, parent, false);
                return new HorizontalRecyclerBannerViewHolder(
                        view,
                        (int) computeSponsoredBannersGrid().getItemHeight(),
                        mSponsoredBannersAdapter,
                        false,
                        false,
                        HorizontalPageIndicatorAdapter.Style.CIRCLE);
            case VIEW_HOLDER_TYPE_FOOTER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_container, parent, false);
                return new FooterViewHolder(view);
            default:
                if (isViewHolderOldType(viewType)) {
                    if (parent.getContext().getResources().getBoolean(R.bool.is_using_older_banner)) {
                        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_older_banner, parent, false);
                    } else {
                        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_old_banner, parent, false);
                    }
                } else {
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_banner, parent, false);
                }

                return new BannerViewHolder(view, mComputedHeight, viewType);
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (holder instanceof HorizontalRecyclerBannerViewHolder) {
            HorizontalRecyclerBannerViewHolder horizontalRecyclerViewHolder = (HorizontalRecyclerBannerViewHolder) holder;
            horizontalRecyclerViewHolder.onViewRecycled();
            horizontalRecyclerViewHolder.onViewRemoved();
            horizontalRecyclerViewHolders.remove(horizontalRecyclerViewHolder);
        }
        if (holder instanceof HorizontalRecyclerItemsViewHolder) {
            HorizontalRecyclerItemsViewHolder viewHolder = (HorizontalRecyclerItemsViewHolder) holder;
            viewHolder.onViewRecycled();
            viewHolder.onViewRemoved();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        BannerViewHolder bannerViewHolder = null;
        HorizontalRecyclerBannerViewHolder horizontalRecyclerBannersViewHolder = null;
        HorizontalRecyclerItemsViewHolder horizontalRecyclerItemsViewHolder = null;
        FooterViewHolder footerViewHolder = null;
        LeaderboardBannerViewHolder leaderboardBannerViewHolder = null;
        if (holder instanceof BannerViewHolder) {
            bannerViewHolder = (BannerViewHolder) holder;
        } else if (holder instanceof HorizontalRecyclerBannerViewHolder) {
            horizontalRecyclerBannersViewHolder = (HorizontalRecyclerBannerViewHolder) holder;
            horizontalRecyclerBannersViewHolder.onViewBound();
            horizontalRecyclerViewHolders.add(horizontalRecyclerBannersViewHolder);
        } else if (holder instanceof HorizontalRecyclerItemsViewHolder) {
            horizontalRecyclerItemsViewHolder = (HorizontalRecyclerItemsViewHolder) holder;
            horizontalRecyclerItemsViewHolder.onViewBound();
        } else if (holder instanceof LeaderboardBannerViewHolder) {
            leaderboardBannerViewHolder = (LeaderboardBannerViewHolder) holder;
        } else if (holder instanceof FooterViewHolder) {
            footerViewHolder = (FooterViewHolder) holder;
        }
        switch (removeViewHolderOldModifier(removeViewHolderOrientationModifier(holder.getItemViewType()))) {
            case VIEW_HOLDER_TYPE_SPACER:
                break;
            case VIEW_HOLDER_TYPE_LEADER_BANNER:
                if (leaderboardBannerViewHolder == null) {
                    break;
                }
                ImageUtils.loadImageGif(leaderboardBanner.getImage(), leaderboardBannerViewHolder.image);

                if (leaderboardBannerViewHolder.subscription != null) {
                    leaderboardBannerViewHolder.subscription.dispose();
                }

                if (leaderboardBannerOnClick != null) {
                    leaderboardBannerViewHolder.subscription =
                            RxView.clicks(leaderboardBannerViewHolder.layout)
                                    .throttleFirst(THROTTLE_FIRST_WINDOW_DURATION, TimeUnit.MILLISECONDS)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(leaderboardBannerOnClick);
                }
                break;
            case VIEW_HOLDER_TYPE_SLIDING_BANNER:
                if (horizontalRecyclerBannersViewHolder == null) {
                    break;
                }
                setupSlidingBannersDimensions();

                horizontalRecyclerBannersViewHolder.setAdapter(mSlidingBannersAdapter);

                horizontalRecyclerBannersViewHolder.setPageIndicatorVisibility(View.VISIBLE);

                if (mSlidingBannersAdapter != null) {
                    mSlidingBannersAdapter.resetReyclerViewPosition();
                    horizontalRecyclerBannersViewHolder.setPageIndicatorItemCount(mSlidingBannersAdapter.getDataSource().size());
                } else {
                    horizontalRecyclerBannersViewHolder.setPageIndicatorItemCount(0);
                }

                if (horizontalRecyclerBannersViewHolder.getPageIndicatorAdapter() != null) {
                    horizontalRecyclerBannersViewHolder.getPageIndicatorAdapter().setSelectedPosition(0);
                }

                break;
            case VIEW_HOLDER_TYPE_TRENDING_BRANDS_WIDGET:
                if (trendingBrandsWidgetHelper == null || horizontalRecyclerBannersViewHolder == null) {
                    break;
                }
                trendingBrandsWidgetHelper.onBindViewHolder(horizontalRecyclerBannersViewHolder);
                break;
            case VIEW_HOLDER_TYPE_BEST_SELLERS_WIDGET:
                if (bestSellersWidgetHelper == null || horizontalRecyclerItemsViewHolder == null) {
                    break;
                }
                bestSellersWidgetHelper.onBindViewHolder(horizontalRecyclerItemsViewHolder, mOrientation);
                break;
            case VIEW_HOLDER_TYPE_SPONSORED_BANNER:
                if (horizontalRecyclerBannersViewHolder == null) {
                    break;
                }
                setupSponsoredBannersDimensions();
                horizontalRecyclerBannersViewHolder.setAdapter(mSponsoredBannersAdapter);
                horizontalRecyclerBannersViewHolder.setPageIndicatorVisibility(View.GONE);
                if (mSponsoredBannersAdapter != null) {
                    mSponsoredBannersAdapter.resetReyclerViewPosition();
                }
                break;
            case VIEW_HOLDER_TYPE_FOOTER:
                if (footerViewHolder == null) {
                    break;
                }
                if (bannersAdapterHelper.isGoogleAdsEnabled()) {
                    CommonUtils.showAdmob(context, footerViewHolder.adView, context.getResources().getString(R.string.admob_banners_id));
                }
                break;
            default:
                if (bannerViewHolder == null) {
                    break;
                }
                synchronized (mSales) {
                    int viewType = removeViewHolderOldModifier(removeViewHolderOrientationModifier(holder.getItemViewType()));
                    GetBannerResponse.Banner item;
                    int width;
                    int height;
                    if (viewType == VIEW_HOLDER_TYPE_PROMO_BANNER) {
                        item = promoBanner;
                        width = mWidthForPromoBanner;
                        height = mHeightForPromoBanner;
                    } else {
                        item = mSales.get(getPositionOfNormalBannersFromAdapterPosition(position));
                        width = mWidth;
                        height = mHeight;
                    }
                    if (item.getDescription() != null && !item.getDescription().isEmpty()) {
                        bannerViewHolder.name.setVisibility(View.VISIBLE);
                        bannerViewHolder.name.setText(item.getDescription());
                    } else {
                        bannerViewHolder.name.setVisibility(View.GONE);
                    }

                    if (item.getBannerText() != null && !item.getBannerText().isEmpty()) {
                        bannerViewHolder.discount.setVisibility(View.VISIBLE);
                        bannerViewHolder.discount.setText(item.getBannerText());
                    } else {
                        bannerViewHolder.discount.setVisibility(View.GONE);
                    }

                    if (item.getPercentOffText() != null && item.getPercentOffText().length() > 0 && bannerViewHolder.itemView.getContext().getResources().getBoolean(R.bool.is_dynamic_discount_banners_enabled)) {
                        bannerViewHolder.percentOff.setVisibility(View.VISIBLE);
                        bannerViewHolder.percentOff.setText(item.getPercentOffText());
                    } else {
                        bannerViewHolder.percentOff.setVisibility(View.GONE);
                    }

                    bannerViewHolder.freeShipping.setVisibility(item.getFreeDelivery() ? View.VISIBLE : View.GONE);

                    if (item.getDeliveryType() != null) {
                        bannerViewHolder.freeShipping.setOnClickListener(v -> {
                            bannersAdapterHelper.onClickFreeDelivery(String.valueOf(item.getDeliveryThreshold()), item.getDeliveryType());
                        });
                    }

                    bannerViewHolder.rearrangeStickers(mComputedWidth);

                    String imgUrl;

                    final Integer resolutionOverride = useOldBannerDimensions ? null : context.getResources().getInteger(R.integer.banner_resolution_override);
                    imgUrl = ImageUtils.appendBannerSizeUrl(item.getImage(), width, height, resolutionOverride);

                    ImageUtils.loadImageWithPriority(imgUrl, bannerViewHolder.image, Priority.HIGH);

                    if (bannerViewHolder.subscription != null) {
                        bannerViewHolder.subscription.dispose();
                    }

                    if (item.getGroup().getIsClickable()) {
                        bannerViewHolder.subscription =
                                RxView.clicks(bannerViewHolder.layout)
                                        .throttleFirst(THROTTLE_FIRST_WINDOW_DURATION, TimeUnit.MILLISECONDS)
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(action ->
                                                bannersAdapterHelper.onBannerTapped(
                                                        item,
                                                        holder.getAdapterPosition(),
                                                        imgUrl,
                                                        Events.RegularBannerClickEvent,
                                                        item.getDescription()));
                    }
                }
                break;
        }
    }

    @Nullable
    private String titleForHeader(int position) {
        synchronized (mSales) {
            if (position == getPositionOfSponsoredBanners()) {
                return mSponsoredBannersAdapter != null ? mSponsoredBannersAdapter.getTitle() : null;
            } else if (position == getPositionOfTrendingBrands() && !mTrendingBrandsAdapter.isShowHeader()) {
                return mTrendingBrandsAdapter != null ? mTrendingBrandsAdapter.getTitle() : null;
            } else if (position >= getPositionOfNormalBanners()) {
                return position < getItemCount() - 1 ? mSales.get(position - getPositionOfNormalBanners()).getGroup().getTitle() : "";
            }
            return null;
        }
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        if (holder instanceof BannerViewHolder) {
            BannerViewHolder bannerViewHolder = (BannerViewHolder) holder;
            if (bannerViewHolder.subscription != null) {
                bannerViewHolder.subscription.dispose();
            }
        }
        if (holder instanceof HorizontalRecyclerBannerViewHolder) {
            HorizontalRecyclerBannerViewHolder viewHolder = (HorizontalRecyclerBannerViewHolder) holder;
            viewHolder.onViewRecycled();
            horizontalRecyclerViewHolders.remove(viewHolder);
            viewHolder.onViewRemoved();
        }
        if (holder instanceof HorizontalRecyclerItemsViewHolder) {
            HorizontalRecyclerItemsViewHolder viewHolder = (HorizontalRecyclerItemsViewHolder) holder;
            viewHolder.onViewRecycled();
            viewHolder.onViewRemoved();
        }
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        synchronized (mSales) {
            return mSales.size() + getPositionOfNormalBanners() + 1;
        }
    }

    private boolean isViewTypeVisible(int viewType) {
        switch (removeViewHolderOrientationModifier(viewType)) {
            case VIEW_HOLDER_TYPE_PROMO_BANNER:
                return promoBanner != null;
            case VIEW_HOLDER_TYPE_LEADER_BANNER:
                return isLeaderBannersVisible();
            case VIEW_HOLDER_TYPE_SLIDING_BANNER:
                return isSlidingBannersVisible();
            case VIEW_HOLDER_TYPE_TRENDING_BRANDS_WIDGET:
                return isTrendingBrandsVisible();
            case VIEW_HOLDER_TYPE_BEST_SELLERS_WIDGET:
                return isBestSellersVisible();
            case VIEW_HOLDER_TYPE_SPONSORED_BANNER:
                return isSponsoredBannersVisible();
            case VIEW_HOLDER_TYPE_SPACER:
                return isSlidingBannersVisible() || isTrendingBrandsVisible() || isSponsoredBannersVisible();
            default:
                for (int value : BANNER_ORDER) {
                    if (value == (removeViewHolderOrientationModifier(viewType))) {
                        return true;
                    }
                }
                return false;
        }
    }

    private int getPositionOfViewType(int viewType) {
        int pos = -1;
        if (isViewTypeVisible(viewType)) {
            for (int i = BANNER_ORDER.length - 1; i >= 0; i--) {
                if (pos < 0) {
                    if (BANNER_ORDER[i] == removeViewHolderOrientationModifier(viewType)) {
                        pos = i;
                    }
                } else if (!isViewTypeVisible(BANNER_ORDER[i])) {
                    pos--;
                }
            }
            if (pos < 0) {
                Integer insertPosition = normalBannerInsertPositions.get(removeViewHolderOrientationModifier(viewType));
                if (insertPosition != null) {
                    pos = insertPosition * mNumberOfColumns + getPositionOfNormalBanners();
                }
            }
        }
        return pos;
    }

    private int getPositionOfNormalBanners() {
        return getPositionOfViewType(VIEW_HOLDER_TYPE_NORMAL_BANNER);
    }

    private int getPositionOfNormalBannersFromAdapterPosition(int position) {
        final int pos = position - getPositionOfNormalBanners();
        int adjustment = 0;
        for (Integer insertPos : normalBannerInsertPositions.values()) {
            if (insertPos < pos) {
                adjustment++;
            }
        }
        return pos - adjustment;
    }

    private int getPositionOfLeaderboardBanner() {
        return getPositionOfViewType(VIEW_HOLDER_TYPE_LEADER_BANNER);
    }

    private int getPositionOfPromoBanner() {
        return getPositionOfViewType(VIEW_HOLDER_TYPE_PROMO_BANNER);
    }

    private int getPositionOfSpacer() {
        return getPositionOfViewType(VIEW_HOLDER_TYPE_SPACER);
    }

    private int getPositionOfSlidingBanners() {
        return getPositionOfViewType(VIEW_HOLDER_TYPE_SLIDING_BANNER);
    }

    private int getPositionOfTrendingBrands() {
        return getPositionOfViewType(VIEW_HOLDER_TYPE_TRENDING_BRANDS_WIDGET);
    }

    private int getPositionOfBestSellers() {
        return getPositionOfViewType(VIEW_HOLDER_TYPE_BEST_SELLERS_WIDGET);
    }

    private int getPositionOfSponsoredBanners() {
        return getPositionOfViewType(VIEW_HOLDER_TYPE_SPONSORED_BANNER);
    }

    public int getNumberOfColumns() {
        return mNumberOfColumns;
    }

    public GetBannerResponse.Banner getItem(int position) {
        synchronized (mSales) {
            int adjustedPos = position - getPositionOfNormalBanners();
            return adjustedPos > 0 && adjustedPos < mSales.size() ? mSales.get(adjustedPos) : null;
        }
    }

    public List<GetBannerResponse.Group> getData() {
        return mGroups;
    }

    @Override
    public void resetDimensions() {
        if (useOldBannerDimensions) {
            mWidth = context.getResources().getInteger(isTablet ? R.integer.old_banner_tablet_width : R.integer.old_banner_mobile_width);
            mHeight = context.getResources().getInteger(isTablet ? R.integer.old_banner_tablet_height : R.integer.old_banner_mobile_height);
        } else {
            mWidth = context.getResources().getInteger(isTablet ? R.integer.sale_banner_tablet_width : R.integer.sale_banner_mobile_width);
            mHeight = context.getResources().getInteger(isTablet ? R.integer.sale_banner_tablet_height : R.integer.sale_banner_mobile_height);
        }
        setupDimensions(mOrientation);
    }

    @Override
    public void setupDimensions(int orientation) {

        int minColumns = bannersAdapterHelper.getBannerColumnCount();
        if (useOldBannerDimensions || minColumns < 1) {
            int resId;
            switch (ScreenUtils.getOrientation(context)) {
                case Configuration.ORIENTATION_LANDSCAPE:
                    resId = isTablet ? R.integer.old_banner_tablet_landscape_column_count : R.integer.old_banner_mobile_landscape_column_count;
                    break;
                default:
                    resId = isTablet ? R.integer.old_banner_tablet_portrait_column_count : R.integer.old_banner_mobile_portrait_column_count;
                    break;
            }
            minColumns = context.getResources().getInteger(resId);
        }
        final int maxColumns = minColumns;

        mOrientation = orientation;

        if (!context.getResources().getBoolean(R.bool.is_ourpay_app)) {
            // Dynamic Height Computation
            ImageUtils.Grid grid = ImageUtils.getRangedGridDefinition(mWidth, mHeight, ScreenUtils.getScreenWidth(context), minColumns, maxColumns);
            mNumberOfColumns = grid.getColumn();
            mComputedWidth = (int) grid.getItemWidth();
            if (useOldBannerDimensions) {
                mComputedHeight = (int) grid.getItemHeight();
            } else {
                mComputedHeight = (int) (grid.getItemHeight() + context.getResources().getDimension(R.dimen.banner_sale_info_height));
            }
            if (isTablet) {
                mComputedHeight += context.getResources().getDimension(R.dimen.margin_tiny) * 2;
            }

            setupSlidingBannersDimensions();
            if (trendingBrandsWidgetHelper != null) {
                trendingBrandsWidgetHelper.setupTrendingBrandsDimensions();
            }
            setupSponsoredBannersDimensions();
        }
    }

    private int getHorizontalPaddingForHorizontalBanners() {
        float dimen = context.getResources().getDimension(R.dimen.horizontal_banner_spacing);
        return (int) Math.ceil(dimen) * 2;
    }

    private int getVerticalPaddingForHorizontalBanners(HorizontalScrollingBannerAdapter adapter) {
        final boolean isCircular = adapter != null && adapter.getBannerStyle() == BannerStyle.CIRCULAR;
        float dimen = 0;
        if (isCircular) {
            dimen += context.getResources().getDimension(R.dimen.horizontal_circular_banner_top_padding);
            dimen += context.getResources().getDimension(R.dimen.horizontal_circular_banner_bottom_padding);
        } else {
            dimen += context.getResources().getDimension(R.dimen.horizontal_banner_bottom_padding);
        }
        return (int) Math.ceil(dimen);
    }

    private int getLeaderboardBannerHeight() {
        final int numberOfColumns = 1;
        final int width = context.getResources().getInteger(isTablet ? R.integer.leaderboard_banner_tablet_width : R.integer.leaderboard_banner_mobile_width);
        final int height = context.getResources().getInteger(isTablet ? R.integer.leaderboard_banner_tablet_height : R.integer.leaderboard_banner_mobile_height);
        ImageUtils.Grid grid = ImageUtils.getRangedGridDefinition(width, height, ScreenUtils.getScreenWidth(context), numberOfColumns, numberOfColumns);
        return (int) grid.getItemHeight();
    }

    private Pair<Integer, Integer> slidingBannersImageSize() {
        int width;
        int height;
        if (isTablet) {
            width = context.getResources().getInteger(R.integer.sliding_banner_tablet_width);
            height = context.getResources().getInteger(R.integer.sliding_banner_tablet_height);
        } else if (useOldBannerDimensions) {
            width = context.getResources().getInteger(R.integer.old_sliding_banner_width);
            height = context.getResources().getInteger(R.integer.old_sliding_banner_height);
        } else {
            width = context.getResources().getInteger(R.integer.sliding_banner_mobile_width);
            height = context.getResources().getInteger(R.integer.sliding_banner_mobile_height);
        }
        return new Pair<>(width, height);
    }

    private ImageUtils.Grid computeSlidingBannersGrid() {
        final int numberOfColumns = context.getResources().getInteger(isTablet ? R.integer.sliding_banner_tablet_column_count : R.integer.sliding_banner_mobile_column_count);
        final int width = slidingBannersImageSize().first;
        final int height = slidingBannersImageSize().second;
        ImageUtils.Grid grid = ImageUtils.getRangedGridDefinition(width, height, ScreenUtils.getScreenWidth(context), numberOfColumns, numberOfColumns);
        return new ImageUtils.Grid(1, grid.getItemWidth(), grid.getItemHeight() + context.getResources().getDimension(R.dimen.banner_sale_info_height) + context.getResources().getDimension(R.dimen.horizontal_banner_circle_indicator_height));
    }

    private void setupSlidingBannersDimensions() {
        if (mSlidingBannersAdapter == null) {
            return;
        }
        ImageUtils.Grid slidingBannersGrid = computeSlidingBannersGrid();
        mSlidingBannersAdapter.setImageWidth(slidingBannersImageSize().first);
        mSlidingBannersAdapter.setImageHeight(slidingBannersImageSize().second);
        int width = (int) slidingBannersGrid.getItemWidth();
        if (isTablet) {
            width += context.getResources().getDimension(R.dimen.promo_banner_tablet_right_padding);
        }
        mSlidingBannersAdapter.setupDimensions(width, (int) slidingBannersGrid.getItemHeight());
    }

    private Pair<Integer, Integer> sponsoredBannersImageSize() {
        int width;
        int height;
        if (isTablet) {
            width = context.getResources().getInteger(R.integer.sponsored_banner_width_for_tablet);
            height = context.getResources().getInteger(R.integer.sponsored_banner_height_for_tablet);
        } else {
            width = context.getResources().getInteger(R.integer.sponsored_banner_width);
            height = context.getResources().getInteger(R.integer.sponsored_banner_height);
        }
        return new Pair<>(width, height);
    }

    private ImageUtils.Grid computeSponsoredBannersGrid() {
        final int numberOfColumns = isTablet ? context.getResources().getInteger(R.integer.sponsored_banner_column_count_for_tablet) : context.getResources().getInteger(R.integer.sponsored_banner_column_count);
        final int width = sponsoredBannersImageSize().first;
        final int height = sponsoredBannersImageSize().second;
        ImageUtils.Grid grid = ImageUtils.getRangedGridDefinition(width, height, ScreenUtils.getScreenWidth(context), numberOfColumns, numberOfColumns);
        return new ImageUtils.Grid(1, grid.getItemWidth() + getHorizontalPaddingForHorizontalBanners(), grid.getItemHeight() + getVerticalPaddingForHorizontalBanners(mSponsoredBannersAdapter));
    }

    private void setupSponsoredBannersDimensions() {
        if (mSponsoredBannersAdapter == null) {
            return;
        }
        ImageUtils.Grid sponsoredBannersGrid = computeSponsoredBannersGrid();
        mSponsoredBannersAdapter.setImageWidth(sponsoredBannersImageSize().first);
        mSponsoredBannersAdapter.setImageHeight(sponsoredBannersImageSize().second);
        mSponsoredBannersAdapter.setupDimensions((int) sponsoredBannersGrid.getItemWidth(), (int) sponsoredBannersGrid.getItemHeight());
    }

    public int getOffset() {
        return mOffset;
    }

    public String getLastGroupType() {
        return mLastGroupType;
    }

    public void clear() {
        synchronized (mSales) {
            mLastGroupType = "";
            mOffset = 0;
            mSales.clear();
            mGroups.clear();
            promoBanner = null;
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = (useOldBannerDimensions ? 1 : 0) | (mOrientation == Configuration.ORIENTATION_LANDSCAPE ? VIEW_HOLDER_TYPE_LANDSCAPE : 0);
        if (position == getPositionOfLeaderboardBanner()) {
            viewType = viewType | VIEW_HOLDER_TYPE_LEADER_BANNER;
        } else if (position == getPositionOfPromoBanner()) {
            viewType = viewType | VIEW_HOLDER_TYPE_PROMO_BANNER;
        } else if (position == getPositionOfSpacer()) {
            viewType = viewType | VIEW_HOLDER_TYPE_SPACER;
        } else if (position == getPositionOfSlidingBanners()) {
            viewType = viewType | VIEW_HOLDER_TYPE_SLIDING_BANNER;
        } else if (position == getPositionOfTrendingBrands()) {
            viewType = viewType | VIEW_HOLDER_TYPE_TRENDING_BRANDS_WIDGET;
        } else if (position == getPositionOfBestSellers()) {
            viewType = viewType | VIEW_HOLDER_TYPE_BEST_SELLERS_WIDGET;
        } else if (position == getPositionOfSponsoredBanners()) {
            viewType = viewType | VIEW_HOLDER_TYPE_SPONSORED_BANNER;
        } else if (position == (getItemCount() - 1) && getItemCount() != 0) {
            viewType = viewType | VIEW_HOLDER_TYPE_FOOTER;
        } else {
            viewType = viewType | VIEW_HOLDER_TYPE_NORMAL_BANNER;
        }
        return viewType;
    }

    private boolean isLeaderBannersVisible() {
        return leaderboardBanner != null;
    }

    private boolean isSlidingBannersVisible() {
        return mSlidingBannersAdapter != null;
    }

    private boolean isTrendingBrandsVisible() {
        return mTrendingBrandsAdapter != null;
    }

    private boolean isBestSellersVisible() {
        return mBestSellersAdapter != null;
    }

    private boolean isSponsoredBannersVisible() {
        return mSponsoredBannersAdapter != null;
    }

    public void setLeaderboardBanner(GetBannerResponse.Banner banner, Consumer<Object> onClick) {
        boolean willInsert = leaderboardBanner == null && banner != null;
        boolean willDelete = leaderboardBanner != null && banner == null;
        if (willDelete) {
            int index = getPositionOfLeaderboardBanner();
            leaderboardBanner = null;
            leaderboardBannerOnClick = null;
            notifyItemRemoved(index);
        } else if (willInsert) {
            leaderboardBanner = banner;
            leaderboardBannerOnClick = onClick;
            notifyItemInserted(getBannerOrderPosition(VIEW_HOLDER_TYPE_LEADER_BANNER));
        }
    }

    public HorizontalScrollingBannerAdapter getSlidingBannersAdapter() {
        return mSlidingBannersAdapter;
    }

    public void setSlidingBannersAdapter(HorizontalScrollingBannerAdapter slidingBannersAdapter) {
        boolean willInsert = mSlidingBannersAdapter == null && slidingBannersAdapter != null;
        boolean willDelete = mSlidingBannersAdapter != null && slidingBannersAdapter == null;
        if (willDelete) {
            int index = getPositionOfSlidingBanners();
            mSlidingBannersAdapter.setOnBannerTappedListener(null);
            mSlidingBannersAdapter = null;
            notifyItemRemoved(index);
        } else if (willInsert) {
            mSlidingBannersAdapter = slidingBannersAdapter;
            mSlidingBannersAdapter.setOnBannerTappedListener((banner, position) -> bannersAdapterHelper.onBannerTapped(banner, position, "", Events.BannerClickEvent, banner.getBannerType()));
            notifyItemInserted(getBannerOrderPosition(VIEW_HOLDER_TYPE_SLIDING_BANNER));
        }
    }

    public HorizontalScrollingBannerAdapter getTrendingBrandsAdapter() {
        return mTrendingBrandsAdapter;
    }

    public void setTrendingBrandsAdapter(HorizontalScrollingBannerAdapter trendingBrandsAdapter) {
        boolean willInsert = mTrendingBrandsAdapter == null && trendingBrandsAdapter != null;
        boolean willDelete = mTrendingBrandsAdapter != null && trendingBrandsAdapter == null;
        if (willDelete) {
            int index = getPositionOfTrendingBrands();
            mTrendingBrandsAdapter.setOnBannerTappedListener(null);
            mTrendingBrandsAdapter = null;
            notifyItemRemoved(index);
        } else if (willInsert) {
            mTrendingBrandsAdapter = trendingBrandsAdapter;
            mTrendingBrandsAdapter.preloadBannerImages(context);
            if (mTrendingBrandsAdapter != null) {
                mTrendingBrandsAdapter.setOnBannerTappedListener((banner, position) -> bannersAdapterHelper.onBannerTapped(banner, position, "", Events.BannerClickEvent, banner.getBannerType()));
            }
            notifyItemInserted(getBannerOrderPosition(VIEW_HOLDER_TYPE_TRENDING_BRANDS_WIDGET));
        }
    }

    public HorizontalScrollingItemsAdapter getBestSellersAdapter() {
        return mBestSellersAdapter;
    }

    public void setBestSellersAdapter(HorizontalScrollingItemsAdapter bestSellersAdapter) {
        boolean willInsert = mBestSellersAdapter == null && bestSellersAdapter != null;
        boolean willDelete = mBestSellersAdapter != null && bestSellersAdapter == null;
        if (willDelete) {
            int index = getPositionOfBestSellers();
            mBestSellersAdapter = null;
            notifyItemRemoved(index);
        } else if (willInsert) {
            mBestSellersAdapter = bestSellersAdapter;
            notifyItemInserted(getBannerOrderPosition(VIEW_HOLDER_TYPE_BEST_SELLERS_WIDGET));
        }
    }

    public HorizontalScrollingBannerAdapter getSponsoredBannersAdapter() {
        return mSponsoredBannersAdapter;
    }

    public void setSponsoredBannersAdapter(HorizontalScrollingBannerAdapter sponsoredBannersAdapter) {
        boolean willInsert = mSponsoredBannersAdapter == null && sponsoredBannersAdapter != null;
        boolean willDelete = mSponsoredBannersAdapter != null && sponsoredBannersAdapter == null;
        if (willDelete) {
            int index = getPositionOfSponsoredBanners();
            mSponsoredBannersAdapter.setOnBannerTappedListener(null);
            mSponsoredBannersAdapter = null;
            notifyItemRemoved(index);
        } else if (willInsert) {
            mSponsoredBannersAdapter = sponsoredBannersAdapter;
            mSponsoredBannersAdapter.preloadBannerImages(context);
            if (mSponsoredBannersAdapter != null) {
                mSponsoredBannersAdapter.setOnBannerTappedListener((banner, position) -> bannersAdapterHelper.onBannerTapped(banner, position, "", Events.SponsoredBannerClickEvent, banner.getBannerText()));
            }
            notifyItemInserted(getBannerOrderPosition(VIEW_HOLDER_TYPE_SPONSORED_BANNER));
        }
    }

    public void restartHorizontalViewHolders() {
        for (HorizontalRecyclerBannerViewHolder viewHolder : horizontalRecyclerViewHolders) {
            viewHolder.onViewBound();
        }
    }

    public void stopHorizontalViewHolders() {
        for (HorizontalRecyclerBannerViewHolder viewHolder : horizontalRecyclerViewHolders) {
            viewHolder.onViewRecycled();
        }
    }

    @Override
    public boolean isUseOldBannerDimensions() {
        return useOldBannerDimensions;
    }

    @Override
    public void setUseOldBannerDimensions(boolean useOldBannerDimensions) {
        this.useOldBannerDimensions = useOldBannerDimensions;
    }

    private void preloadImageForBanner(GetBannerResponse.Banner banner) {
        final int width = banner == promoBanner ? mWidthForPromoBanner : mWidth;
        final int height = banner == promoBanner ? mHeightForPromoBanner : mHeight;
        final Integer resolutionOverride = useOldBannerDimensions ? null : context.getResources().getInteger(R.integer.banner_resolution_override);
        final String imgUrl = ImageUtils.appendBannerSizeUrl(banner.getImage(), width, height, resolutionOverride);

        ImageUtils.preLoadImage(imgUrl, context);
    }

    public StickyRecyclerHeadersAdapter getStickyRecyclerHeadersAdapter() {
        return new StickyRecyclerHeadersAdapter() {
            @Override
            public long getHeaderId(int position) {
                String title = titleForHeader(position);
                if (title != null) {
                    return title.hashCode();
                }
                return -1;
            }

            @Override
            public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_banner_header, parent, false);
                return new HeaderViewHolder(view);
            }

            @Override
            public void onBindHeaderViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
                HeaderViewHolder holder = (HeaderViewHolder) viewHolder;
                String title = titleForHeader(position);
                holder.headerText.setText(title == null ? "" : title);
            }

            @Override
            public int getItemCount() {
                return BannersAdapter.this.getItemCount();
            }
        };
    }
}