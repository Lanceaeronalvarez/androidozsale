package au.com.dealsdirect.ui.controller.shops.adapter;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
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
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.banner.GetBannerResponse;
import au.com.dealsdirect.data.network.model.events.BannerClickEventRequest;
import au.com.dealsdirect.service.datacollection.core.DataCollector;
import au.com.dealsdirect.service.datacollection.enums.EventParameters;
import au.com.dealsdirect.service.datacollection.enums.EventTypeId;
import au.com.dealsdirect.service.datacollection.enums.Events;
import au.com.dealsdirect.ui.controller.saleitems.OnClickFreeDeliveryListener;
import au.com.dealsdirect.ui.controller.shops.ShopsMvpPresenter;
import au.com.dealsdirect.utils.CommonUtils;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.ScreenUtils;
import au.com.dealsdirect.utils.ScrollingImageHorizontal.HorizontalRecyclerViewHolder;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;

import static android.graphics.Typeface.BOLD;

/**
 * dp Created by Admin on 6/7/17.
 */

public class BannersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ResettableDimensions {

    private int mOrientation;

    private int mComputedWidth = -1;
    private int mComputedHeight = -1;
    private final List<GetBannerResponse.Group> mGroups = new ArrayList<>();
    private final List<GetBannerResponse.Banner> mSales = new ArrayList<>();
    private GetBannerResponse.Banner promoBanner;
    private Activity mActivity;
    private ShopsMvpPresenter mPresenter;
    private int mWidth;
    private int mHeight;
    private int mWidthForPromoBanner;
    private int mHeightForPromoBanner;
    private int mNumberOfColumns;
    private int mOffset;
    private String mLastGroupType = "";
    private static final int SPANNABLE_STRING_START_INDEX = 6;
    private static final float DISCOUNT_VALUE_SCALE_FACTOR = 1.8f;

    // note: please don't combine different banner types
    public static final int VIEW_HOLDER_TYPE_OLD = 1;
    public static final int VIEW_HOLDER_TYPE_LANDSCAPE = 1 << 1;
    public static final int VIEW_HOLDER_TYPE_NORMAL_BANNER = 1 << 2;
    public static final int VIEW_HOLDER_TYPE_PROMO_BANNER = 1 << 3;
    public static final int VIEW_HOLDER_TYPE_SLIDING_BANNER = 1 << 4;
    public static final int VIEW_HOLDER_TYPE_CATEGORY_BANNER = 1 << 5;
    public static final int VIEW_HOLDER_TYPE_SPONSORED_BANNER = 1 << 6;
    public static final int VIEW_HOLDER_TYPE_FOOTER = 1 << 7;
    public static final int VIEW_HOLDER_TYPE_SPACER = 1 << 8;

    private static final int[] BANNER_ORDER = {
            VIEW_HOLDER_TYPE_PROMO_BANNER,
            VIEW_HOLDER_TYPE_SLIDING_BANNER,
            VIEW_HOLDER_TYPE_CATEGORY_BANNER,
            VIEW_HOLDER_TYPE_SPONSORED_BANNER,
            VIEW_HOLDER_TYPE_SPACER,
            VIEW_HOLDER_TYPE_NORMAL_BANNER
    };

    private boolean useOldBannerDimensions = false;

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
    private HorizontalScrollingBannerAdapter mCategoryBannersAdapter = null;
    private HorizontalScrollingBannerAdapter mSponsoredBannersAdapter = null;
    private OnClickFreeDeliveryListener mListener;

    private HashSet<HorizontalRecyclerViewHolder> horizontalRecyclerViewHolders = new HashSet<>();

    public BannersAdapter(
            Activity activity,
            ShopsMvpPresenter presenter,
            List<GetBannerResponse.Group> sales,
            int orientation,
            boolean useOldBannerDimensions,
            OnClickFreeDeliveryListener listener) {

        replace(sales);

        mActivity = activity;
        mPresenter = presenter;

        mWidthForPromoBanner = mActivity.getResources().getInteger(R.integer.old_banner_mobile_width);
        mHeightForPromoBanner = mActivity.getResources().getInteger(R.integer.old_banner_mobile_height);

        mOrientation = orientation;

        this.useOldBannerDimensions = useOldBannerDimensions;

        mListener = listener;

        resetDimensions();
    }

    class SpacerViewHolder extends RecyclerView.ViewHolder {

        public SpacerViewHolder(@NonNull View itemView) {
            super(itemView);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    mPresenter.isTablet() ?
                            (int) itemView.getResources().getDimension(R.dimen.horizontal_banner_spacer_size_for_tablet) :
                            (int) itemView.getResources().getDimension(R.dimen.horizontal_banner_spacer_size)
            );
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
                mOffset = bannerGroup.getBanners().size();
                mLastGroupType = bannerGroup.getType();
                notifyItemRangeInserted(
                        getPositionOfNormalBanners() + previousCount,
                        mSales.size() - previousCount);
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;

        switch (removeViewHolderOldModifier(removeViewHolderOrientationModifier(viewType))) {
            case VIEW_HOLDER_TYPE_SPACER:
                return new SpacerViewHolder(new View(parent.getContext(), null));
            case VIEW_HOLDER_TYPE_SLIDING_BANNER:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.viewholder_horizontal_scrolling_banner, parent, false);
                return new HorizontalRecyclerViewHolder(view,
                        (int) computeSlidingBannersGrid().getItemHeight(),
                        mSlidingBannersAdapter,
                        true,
                        true);
            case VIEW_HOLDER_TYPE_CATEGORY_BANNER:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.viewholder_horizontal_scrolling_banner, parent, false);
                return new HorizontalRecyclerViewHolder(view,
                        (int) computeCategoryBannersGrid().getItemHeight(),
                        mCategoryBannersAdapter,
                        false,
                        false);
            case VIEW_HOLDER_TYPE_SPONSORED_BANNER:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.viewholder_horizontal_scrolling_banner, parent, false);
                return new HorizontalRecyclerViewHolder(view,
                        (int) computeSponsoredBannersGrid().getItemHeight(),
                        mSponsoredBannersAdapter,
                        false,
                        false);
            case VIEW_HOLDER_TYPE_FOOTER:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.footer_ads, parent, false);
                return new BannerViewHolder(view);
            default:
                if (isViewHolderOldType(viewType)) {
                    if (parent.getContext().getResources().getBoolean(R.bool.is_using_older_banner)) {
                        view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.viewholder_older_banner, parent, false);
                    } else {
                        view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.viewholder_old_banner, parent, false);
                    }
                } else {
                    view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.viewholder_banner, parent, false);
                }

                return new BannerViewHolder(view, mComputedHeight, viewType);
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (holder instanceof HorizontalRecyclerViewHolder) {
            HorizontalRecyclerViewHolder horizontalRecyclerViewHolder = (HorizontalRecyclerViewHolder) holder;
            horizontalRecyclerViewHolder.onViewRecycled();
            horizontalRecyclerViewHolders.remove(horizontalRecyclerViewHolder);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        BannerViewHolder bannerViewHolder = null;
        HorizontalRecyclerViewHolder horizontalRecyclerViewHolder = null;
        if (holder instanceof BannerViewHolder) {
            bannerViewHolder = (BannerViewHolder) holder;
        } else if (holder instanceof HorizontalRecyclerViewHolder) {
            horizontalRecyclerViewHolder = (HorizontalRecyclerViewHolder) holder;
            horizontalRecyclerViewHolder.onViewBound();
            horizontalRecyclerViewHolders.add(horizontalRecyclerViewHolder);
        }
        switch (removeViewHolderOldModifier(removeViewHolderOrientationModifier(holder.getItemViewType()))) {
            case VIEW_HOLDER_TYPE_SPACER:
                break;
            case VIEW_HOLDER_TYPE_SLIDING_BANNER:
                setupSlidingBannersDimensions();

                if (horizontalRecyclerViewHolder != null) {
                    horizontalRecyclerViewHolder.setAdapter(mSlidingBannersAdapter);

                    horizontalRecyclerViewHolder.circleIndicatorRecyclerView.setVisibility(View.VISIBLE);

                    if (mSlidingBannersAdapter != null) {
                        mSlidingBannersAdapter.resetReyclerViewPosition();
                        horizontalRecyclerViewHolder.setCircleIndicatorItemCount(mSlidingBannersAdapter.getDataSource().size());
                    } else {
                        horizontalRecyclerViewHolder.setCircleIndicatorItemCount(0);
                    }

                    if (horizontalRecyclerViewHolder.getCircleIndicatorAdapter() != null) {
                        horizontalRecyclerViewHolder.getCircleIndicatorAdapter().setSelectedPosition(0);
                    }
                }

                break;
            case VIEW_HOLDER_TYPE_CATEGORY_BANNER:
                setupCategoryBannersDimensions();
                horizontalRecyclerViewHolder.setAdapter(mCategoryBannersAdapter);
                horizontalRecyclerViewHolder.circleIndicatorRecyclerView.setVisibility(View.GONE);
                if (mCategoryBannersAdapter != null) {
                    mCategoryBannersAdapter.resetReyclerViewPosition();
                }
                // TODO
                break;
            case VIEW_HOLDER_TYPE_SPONSORED_BANNER:
                setupSponsoredBannersDimensions();
                horizontalRecyclerViewHolder.setAdapter(mSponsoredBannersAdapter);
                horizontalRecyclerViewHolder.circleIndicatorRecyclerView.setVisibility(View.GONE);
                if (mSponsoredBannersAdapter != null) {
                    mSponsoredBannersAdapter.resetReyclerViewPosition();
                }
                break;
            case VIEW_HOLDER_TYPE_FOOTER:
                if (mPresenter.isGoogleAdsEnabled()) {
                    CommonUtils.showAdmob(mActivity, bannerViewHolder.adView,
                            mActivity.getResources().getString(R.string.admob_banners_id));
                }
                break;
            default:
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
                        item = mSales.get(position - getPositionOfNormalBanners());
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

                    if (item.getPercentOffText() != null &&
                            item.getPercentOffText().length() > 0 &&
                            bannerViewHolder.itemView.getContext().getResources().getBoolean(R.bool.is_dynamic_discount_banners_enabled)) {
                        bannerViewHolder.percentOff.setVisibility(View.VISIBLE);
                        bannerViewHolder.percentOff.setText(item.getPercentOffText());
                    } else {
                        bannerViewHolder.percentOff.setVisibility(View.GONE);
                    }

                    bannerViewHolder.freeShipping.setVisibility(item.getFreeDelivery() ? View.VISIBLE : View.GONE);

                    if (item.getDeliveryType() != null) {
                        bannerViewHolder.freeShipping.setOnClickListener(v -> {
                            mListener.onClickFreeDelivery(String.valueOf(item.getDeliveryThreshold()), item.getDeliveryType());
                        });
                    }

                    bannerViewHolder.rearrangeStickers(mComputedWidth);

                    String imgUrl;

                    imgUrl = ImageUtils.appendBannerSizeUrl(item.getImage(), width, height, !useOldBannerDimensions);

                    ImageUtils.loadImageWithPriority(imgUrl, bannerViewHolder.image, Priority.HIGH);

                    if (bannerViewHolder.subscription != null) {
                        bannerViewHolder.subscription.dispose();
                    }

                    if (item.getGroup().getIsClickable()) {
                        bannerViewHolder.subscription = RxView.clicks(bannerViewHolder.layout)
                                .throttleFirst(
                                        THROTTLE_FIRST_WINDOW_DURATION,
                                        TimeUnit.MILLISECONDS)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(action -> onBannerTapped(item, holder.getAdapterPosition(), imgUrl, Events.RegularBannerClickEvent, item.getDescription()));
                    }
                }
                break;
        }
    }

    private BannerClickEventRequest createBannerClickEventRequest(EventParameters.SpecialBannerType type, GetBannerResponse.Banner banner, int position) {
        BannerClickEventRequest bannerClickEventRequest = new BannerClickEventRequest();
        bannerClickEventRequest.setEventType(EventTypeId.EVENT_SLIDER_BANNER);

        BannerClickEventRequest.BannerInfo bannerInfo = new BannerClickEventRequest.BannerInfo();
        bannerInfo.setBannerType(type.getValue());
        bannerInfo.setSaleId(banner.getId());
        bannerInfo.setLink(banner.getLink());
        bannerInfo.setPos(position);
        bannerInfo.setCategory("");

        bannerClickEventRequest.setBannerInfo(bannerInfo);

        return bannerClickEventRequest;
    }

    private void specialBannerEvent(BannerClickEventRequest bannerClickEventRequest, String saleName) {
        HashMap<String, Object> eventParameters = new HashMap<>();
        eventParameters.put(DataCollector.EventParameters.BANNER_TYPE, "SliderBanners");
        eventParameters.put(DataCollector.EventParameters.SALE_NAME, saleName);
        eventParameters.put(DataCollector.EventParameters.APP_CONTEXT, mActivity);
        eventParameters.put(DataCollector.EventParameters.SCREEN_NAME, "SaleBanners");
        eventParameters.put(DataCollector.EventParameters.BANNER_CLICK_REQUEST, bannerClickEventRequest);
        DataCollector.logEvent(Events.BannerClickEvent, eventParameters);
    }

    private void onBannerTapped(GetBannerResponse.Banner banner, int position, String imgUrl, Events bannerType,
                                String saleName) {
        if (banner.getBannerType() != null && banner.getBannerType().equals("categoryShop")) {
            Uri uri = Uri.parse(banner.getLink());
            String id = uri.getLastPathSegment();

            specialBannerEvent(createBannerClickEventRequest(
                    EventParameters.SpecialBannerType.SHOP_BY_CATEGORY, banner, position), saleName);

            mPresenter.selectCategoryBanner(id);
        } else if (banner.getLink() != null && !banner.getLink().isEmpty()) {
            String link = banner.getLink();
            Pattern pattern = Pattern.compile("(?<=/s/)([^?\\n\\r])+");
            Matcher matcher = pattern.matcher(link);
            String title = " ";
            if (banner.getDescription() != null && !banner.getDescription().isEmpty()) {
                title = banner.getDescription();
            }

            specialBannerEvent(createBannerClickEventRequest(
                    EventParameters.SpecialBannerType.SALE_CAMPAIGN, banner, position), saleName);

            if (matcher.find()) {
                String id = matcher.group();
                mPresenter.selectBanner(
                        id,
                        title,
                        banner.getId(),
                        position,
                        imgUrl,
                        banner.getEndDate(),
                        banner.getIsAvailable());

            } else {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(banner.getLink()));
                mActivity.startActivity(browserIntent);
            }
        } else {

            if (bannerType.equals(Events.RegularBannerClickEvent)) {
                HashMap<String, Object> eventParameters = new HashMap<>();
                eventParameters.put(DataCollector.EventParameters.BANNER_TYPE, "RegularBanners");
                eventParameters.put(DataCollector.EventParameters.SALE_NAME, saleName);
                eventParameters.put(DataCollector.EventParameters.APP_CONTEXT, mActivity);
                eventParameters.put(DataCollector.EventParameters.SCREEN_NAME, "SaleBanners");
                DataCollector.logEvent(Events.BannerClickEvent, eventParameters);
            } else if (bannerType.equals(Events.SponsoredBannerClickEvent)) {
                specialBannerEvent(createBannerClickEventRequest(
                        EventParameters.SpecialBannerType.SPONSORED, banner, position), saleName);
            }

            mPresenter.selectBanner(
                    banner.getDestinationId(),
                    banner.getDescription(),
                    banner.getId(),
                    position,
                    imgUrl,
                    banner.getEndDate(),
                    banner.getIsAvailable());

        }
    }

    @Nullable
    private String titleForHeader(int position) {
        synchronized (mSales) {
            if (position == getPositionOfSponsoredBanners()) {
                return mSponsoredBannersAdapter != null ? mSponsoredBannersAdapter.getTitle() : null;
            } else if (position == getPositionOfCategoryBanners()) {
                return mCategoryBannersAdapter != null ? mCategoryBannersAdapter.getTitle() : null;
            } else if (position >= getPositionOfNormalBanners()) {
                return position < getItemCount() - 1 ? mSales.get(position - getPositionOfNormalBanners()).getGroup().getTitle() : "";
            }
            return null;
        }
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        if (holder instanceof BannerViewHolder) {
            BannerViewHolder bannerViewHolder = (BannerViewHolder) holder;
            if (bannerViewHolder.subscription != null) {
                bannerViewHolder.subscription.dispose();
            }
            if (!mActivity.isDestroyed()) {
                ImageUtils.clearImage(bannerViewHolder.image);
            }
        }
        if (holder instanceof HorizontalRecyclerViewHolder) {
            HorizontalRecyclerViewHolder viewHolder = (HorizontalRecyclerViewHolder) holder;
            viewHolder.onViewRecycled();
            viewHolder.recyclerView.setAdapter(null);
            viewHolder.circleIndicatorRecyclerView.setAdapter(null);
            horizontalRecyclerViewHolders.remove(viewHolder);
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
            case VIEW_HOLDER_TYPE_SLIDING_BANNER:
                return isSlidingBannersVisible();
            case VIEW_HOLDER_TYPE_CATEGORY_BANNER:
                return isCategoryBannersVisible();
            case VIEW_HOLDER_TYPE_SPONSORED_BANNER:
                return isSponsoredBannersVisible();
            case VIEW_HOLDER_TYPE_SPACER:
                return isSlidingBannersVisible() || isCategoryBannersVisible() || isSponsoredBannersVisible();
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
        }
        return pos;
    }

    private int getPositionOfNormalBanners() {
        return getPositionOfViewType(VIEW_HOLDER_TYPE_NORMAL_BANNER);
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

    private int getPositionOfCategoryBanners() {
        return getPositionOfViewType(VIEW_HOLDER_TYPE_CATEGORY_BANNER);
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
            mWidth = mActivity.getResources().getInteger(mPresenter.isTablet() ? R.integer.old_banner_tablet_width : R.integer.old_banner_mobile_width);
            mHeight = mActivity.getResources().getInteger(mPresenter.isTablet() ? R.integer.old_banner_tablet_height : R.integer.old_banner_mobile_height);
        } else {
            mWidth = mActivity.getResources().getInteger(mPresenter.isTablet() ? R.integer.sale_banner_tablet_width : R.integer.sale_banner_mobile_width);
            mHeight = mActivity.getResources().getInteger(mPresenter.isTablet() ? R.integer.sale_banner_tablet_height : R.integer.sale_banner_mobile_height);
        }
        setupDimensions(mOrientation);
    }

    @Override
    public void setupDimensions(int orientation) {

        int minColumns = mPresenter.getBannerColumnCount();
        if (useOldBannerDimensions || minColumns < 1) {
            int resId;
            switch (ScreenUtils.getOrientation(mActivity)) {
                case Configuration.ORIENTATION_LANDSCAPE:
                    resId = mPresenter.isTablet() ? R.integer.old_banner_tablet_landscape_column_count : R.integer.old_banner_mobile_landscape_column_count;
                    break;
                default:
                    resId = mPresenter.isTablet() ? R.integer.old_banner_tablet_portrait_column_count : R.integer.old_banner_mobile_portrait_column_count;
                    break;
            }
            minColumns = mActivity.getResources().getInteger(resId);
        }
        final int maxColumns = minColumns;

        mOrientation = orientation;

        if (!mActivity.getResources().getBoolean(R.bool.is_ourpay_app)) {
            // Dynamic Height Computation
            ImageUtils.Grid grid = ImageUtils.getRangedGridDefinition(
                    mWidth, mHeight,
                    ScreenUtils.getScreenWidth(mActivity),
                    minColumns, maxColumns);
            mNumberOfColumns = grid.getColumn();
            mComputedWidth = (int) grid.getItemWidth();
            if (useOldBannerDimensions) {
                mComputedHeight = (int) grid.getItemHeight();
            } else {
                mComputedHeight = (int) (grid.getItemHeight() + mActivity.getResources().getDimension(R.dimen.banner_sale_info_height));
            }
            if (mPresenter.isTablet()) {
                mComputedHeight += mActivity.getResources().getDimension(R.dimen.margin_tiny) * 2;
            }

            setupSlidingBannersDimensions();
            setupCategoryBannersDimensions();
            setupSponsoredBannersDimensions();
        }
    }

    private int getHorizontalPaddingForHorizontalBanners() {
        float dimen = mActivity.getResources().getDimension(R.dimen.horizontal_banner_spacing);
        return (int) Math.ceil(dimen) * 2;
    }

    private int getBottomPaddingForHorizontalBanners() {
        float dimen = mActivity.getResources().getDimension(R.dimen.horizontal_banner_bottom_padding);
        return (int) Math.ceil(dimen);
    }

    private Pair<Integer, Integer> slidingBannersImageSize() {
        int width;
        int height;
        if (mPresenter.isTablet()) {
            width = mActivity.getResources().getInteger(R.integer.sliding_banner_tablet_width);
            height = mActivity.getResources().getInteger(R.integer.sliding_banner_tablet_height);
        } else if (useOldBannerDimensions) {
            width = mActivity.getResources().getInteger(R.integer.old_sliding_banner_width);
            height = mActivity.getResources().getInteger(R.integer.old_sliding_banner_height);
        } else {
            width = mActivity.getResources().getInteger(R.integer.sliding_banner_mobile_width);
            height = mActivity.getResources().getInteger(R.integer.sliding_banner_mobile_height);
        }
        return new Pair<>(width, height);
    }

    private ImageUtils.Grid computeSlidingBannersGrid() {
        final int numberOfColumns = mActivity.getResources().getInteger(mPresenter.isTablet() ? R.integer.sliding_banner_tablet_column_count : R.integer.sliding_banner_mobile_column_count);
        final int width = slidingBannersImageSize().first;
        final int height = slidingBannersImageSize().second;
        ImageUtils.Grid grid = ImageUtils.getRangedGridDefinition(
                width, height,
                ScreenUtils.getScreenWidth(mActivity),
                numberOfColumns, numberOfColumns);
        return new ImageUtils.Grid(
                1,
                grid.getItemWidth(),
                grid.getItemHeight() + mActivity.getResources().getDimension(R.dimen.banner_sale_info_height) + mActivity.getResources().getDimension(R.dimen.horizontal_banner_circle_indicator_height));
    }

    private void setupSlidingBannersDimensions() {
        if (mSlidingBannersAdapter != null) {
            ImageUtils.Grid slidingBannersGrid = computeSlidingBannersGrid();
            mSlidingBannersAdapter.setImageWidth(slidingBannersImageSize().first);
            mSlidingBannersAdapter.setImageHeight(slidingBannersImageSize().second);
            int width = (int) slidingBannersGrid.getItemWidth();
            if (mPresenter.isTablet()) {
                width += mActivity.getResources().getDimension(R.dimen.promo_banner_tablet_right_padding);
            }
            mSlidingBannersAdapter.setupDimensions(
                    width,
                    (int) slidingBannersGrid.getItemHeight());
            mSlidingBannersAdapter.setUseHigherResolution(true);
        }
    }

    private Pair<Integer, Integer> categoryBannersImageSize() {
        int width;
        int height;
        if (mPresenter.isTablet()) {
            width = mActivity.getResources().getInteger(R.integer.category_banner_width_for_tablet);
            height = mActivity.getResources().getInteger(R.integer.category_banner_height_for_tablet);
        } else {
            width = mActivity.getResources().getInteger(R.integer.category_banner_width);
            height = mActivity.getResources().getInteger(R.integer.category_banner_height);
        }
        return new Pair<>(width, height);
    }

    private ImageUtils.Grid computeCategoryBannersGrid() {
        final int numberOfColumns = mPresenter.isTablet() ?
                mActivity.getResources().getInteger(R.integer.category_banner_column_count_for_tablet) :
                mActivity.getResources().getInteger(R.integer.category_banner_column_count);
        final int width = categoryBannersImageSize().first;
        final int height = categoryBannersImageSize().second;
        ImageUtils.Grid grid = ImageUtils.getRangedGridDefinition(
                width, height,
                ScreenUtils.getScreenWidth(mActivity),
                numberOfColumns, numberOfColumns);
        return new ImageUtils.Grid(
                1,
                grid.getItemWidth() + getHorizontalPaddingForHorizontalBanners(),
                grid.getItemHeight() + mActivity.getResources().getDimension(R.dimen.horizontal_banner_title_height) + getBottomPaddingForHorizontalBanners()
        );
    }

    private void setupCategoryBannersDimensions() {
        if (mCategoryBannersAdapter != null) {
            ImageUtils.Grid categoryBannersGrid = computeCategoryBannersGrid();
            mCategoryBannersAdapter.setImageWidth(categoryBannersImageSize().first);
            mCategoryBannersAdapter.setImageHeight(categoryBannersImageSize().second);
            mCategoryBannersAdapter.setupDimensions(
                    (int) categoryBannersGrid.getItemWidth(),
                    (int) categoryBannersGrid.getItemHeight()
            );
        }
    }

    private Pair<Integer, Integer> sponsoredBannersImageSize() {
        int width;
        int height;
        if (mPresenter.isTablet()) {
            width = mActivity.getResources().getInteger(R.integer.sponsored_banner_width_for_tablet);
            height = mActivity.getResources().getInteger(R.integer.sponsored_banner_height_for_tablet);
        } else {
            width = mActivity.getResources().getInteger(R.integer.sponsored_banner_width);
            height = mActivity.getResources().getInteger(R.integer.sponsored_banner_height);
        }
        return new Pair<>(width, height);
    }

    private ImageUtils.Grid computeSponsoredBannersGrid() {
        final int numberOfColumns = mPresenter.isTablet() ?
                mActivity.getResources().getInteger(R.integer.sponsored_banner_column_count_for_tablet) :
                mActivity.getResources().getInteger(R.integer.sponsored_banner_column_count);
        final int width = sponsoredBannersImageSize().first;
        final int height = sponsoredBannersImageSize().second;
        ImageUtils.Grid grid = ImageUtils.getRangedGridDefinition(
                width, height,
                ScreenUtils.getScreenWidth(mActivity),
                numberOfColumns, numberOfColumns);
        return new ImageUtils.Grid(
                1,
                grid.getItemWidth() + getHorizontalPaddingForHorizontalBanners(),
                grid.getItemHeight() + getBottomPaddingForHorizontalBanners()
        );
    }

    private void setupSponsoredBannersDimensions() {
        if (mSponsoredBannersAdapter != null) {
            ImageUtils.Grid sponsoredBannersGrid = computeSponsoredBannersGrid();
            mSponsoredBannersAdapter.setImageWidth(sponsoredBannersImageSize().first);
            mSponsoredBannersAdapter.setImageHeight(sponsoredBannersImageSize().second);
            mSponsoredBannersAdapter.setupDimensions(
                    (int) sponsoredBannersGrid.getItemWidth(),
                    (int) sponsoredBannersGrid.getItemHeight());
        }
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
        int viewType = (useOldBannerDimensions ? 1 : 0) |
                (mOrientation == Configuration.ORIENTATION_LANDSCAPE ? VIEW_HOLDER_TYPE_LANDSCAPE : 0);
        if (position == getPositionOfPromoBanner()) {
            viewType = viewType | VIEW_HOLDER_TYPE_PROMO_BANNER;
        } else if (position == getPositionOfSpacer()) {
            viewType = viewType | VIEW_HOLDER_TYPE_SPACER;
        } else if (position == getPositionOfSlidingBanners()) {
            viewType = viewType | VIEW_HOLDER_TYPE_SLIDING_BANNER;
        } else if (position == getPositionOfCategoryBanners()) {
            viewType = viewType | VIEW_HOLDER_TYPE_CATEGORY_BANNER;
        } else if (position == getPositionOfSponsoredBanners()) {
            viewType = viewType | VIEW_HOLDER_TYPE_SPONSORED_BANNER;
        } else if (position == (getItemCount() - 1) && getItemCount() != 0) {
            viewType = viewType | VIEW_HOLDER_TYPE_FOOTER;
        } else {
            viewType = viewType | VIEW_HOLDER_TYPE_NORMAL_BANNER;
        }
        return viewType;
    }

    private boolean isSlidingBannersVisible() {
        return mSlidingBannersAdapter != null;
    }

    private boolean isCategoryBannersVisible() {
        return mCategoryBannersAdapter != null;
    }

    private boolean isSponsoredBannersVisible() {
        return mSponsoredBannersAdapter != null;
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
            notifyDataSetChanged();
        } else if (willInsert) {
            mSlidingBannersAdapter = slidingBannersAdapter;
            if (mSlidingBannersAdapter != null) {
                mSlidingBannersAdapter
                        .setOnBannerTappedListener((banner, position) -> BannersAdapter.this
                                .onBannerTapped(banner, position, "",
                                        Events.BannerClickEvent, banner.getBannerType()));
            }
            notifyDataSetChanged();
        }
    }

    public HorizontalScrollingBannerAdapter getCategoryBannersAdapter() {
        return mCategoryBannersAdapter;
    }

    public void setCategoryBannersAdapter(HorizontalScrollingBannerAdapter categoryBannersAdapter) {
        boolean willInsert = mCategoryBannersAdapter == null && categoryBannersAdapter != null;
        boolean willDelete = mCategoryBannersAdapter != null && categoryBannersAdapter == null;
        if (willDelete) {
            int index = getPositionOfCategoryBanners();
            mCategoryBannersAdapter.setOnBannerTappedListener(null);
            mCategoryBannersAdapter = null;
            notifyDataSetChanged();
        } else if (willInsert) {
            mCategoryBannersAdapter = categoryBannersAdapter;
            mCategoryBannersAdapter.preloadBannerImages(mActivity);
            if (mCategoryBannersAdapter != null) {
                mCategoryBannersAdapter
                        .setOnBannerTappedListener((banner, position) -> BannersAdapter.this
                                .onBannerTapped(banner, position, "",
                                        Events.BannerClickEvent, banner.getBannerType()));
            }
            notifyDataSetChanged();
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
            notifyDataSetChanged();
        } else if (willInsert) {
            mSponsoredBannersAdapter = sponsoredBannersAdapter;
            mSponsoredBannersAdapter.preloadBannerImages(mActivity);
            if (mSponsoredBannersAdapter != null) {
                mSponsoredBannersAdapter.setOnBannerTappedListener((banner, position) -> BannersAdapter.this
                        .onBannerTapped(banner, position, "",
                                Events.SponsoredBannerClickEvent, banner.getBannerText()));
            }
            notifyDataSetChanged();
        }
    }

    public void restartHorizontalViewHolders() {
        for (HorizontalRecyclerViewHolder viewHolder : horizontalRecyclerViewHolders) {
            viewHolder.onViewBound();
        }
    }

    public void stopHorizontalViewHolders() {
        for (HorizontalRecyclerViewHolder viewHolder : horizontalRecyclerViewHolders) {
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
        final String imgUrl = ImageUtils.appendBannerSizeUrl(banner.getImage(), width, height, !useOldBannerDimensions);

        ImageUtils.preLoadImage(imgUrl, mActivity);
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