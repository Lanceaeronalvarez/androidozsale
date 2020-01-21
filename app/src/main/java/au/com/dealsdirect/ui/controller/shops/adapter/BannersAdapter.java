package au.com.dealsdirect.ui.controller.shops.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jakewharton.rxbinding2.view.RxView;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.banner.GetBannerResponse;
import au.com.dealsdirect.listeners.OnHorizontalSwipeTouchListener;
import au.com.dealsdirect.ui.controller.shops.ShopsMvpPresenter;
import au.com.dealsdirect.utils.CommonUtils;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.ScreenUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

import static android.graphics.Typeface.BOLD;

/**
 * dp Created by Admin on 6/7/17.
 */

public class BannersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements StickyRecyclerHeadersAdapter {

    private int mOrientation;

    private int mComputedHeight = -1;
    private List<GetBannerResponse.Group> mGroups;
    private List<GetBannerResponse.Banner> mSales;
    private Activity mActivity;
    private ShopsMvpPresenter mPresenter;
    private RecyclerView recyclerView = null;
    private int mWidth;
    private int mHeight;
    private int mNumberOfColumns;
    private int mOffset;
    private String mLastGroupType = "";
    private static final int SPANNABLE_STRING_START_INDEX = 6;
    private static final float DISCOUNT_VALUE_SCALE_FACTOR = 1.8f;

    // note: please don't combine different banner types
    public static final int VIEW_HOLDER_TYPE_LANDSCAPE = 1;
    public static final int VIEW_HOLDER_TYPE_NORMAL_BANNER = 1 << 1;
    public static final int VIEW_HOLDER_TYPE_SLIDING_BANNER = 1 << 2;
    public static final int VIEW_HOLDER_TYPE_CATEGORY_BANNER = 1 << 3;
    public static final int VIEW_HOLDER_TYPE_SPONSORED_BANNER = 1 << 4;
    public static final int VIEW_HOLDER_TYPE_FOOTER = 1 << 5;
    public static final int VIEW_HOLDER_TYPE_SPACER = 1 << 6;

    private static final int[] BANNER_ORDER = {
            VIEW_HOLDER_TYPE_SLIDING_BANNER,
            VIEW_HOLDER_TYPE_SPONSORED_BANNER,
            VIEW_HOLDER_TYPE_SPACER,
            VIEW_HOLDER_TYPE_NORMAL_BANNER
    };

    private static final int THROTTLE_FIRST_WINDOW_DURATION = 1000;

    private static final float SLIDING_BANNER_WIDTH_PERCENT = 0.8f;

    private int removeViewHolderOrientationModifier(int viewHolderType) {
        return viewHolderType & (~VIEW_HOLDER_TYPE_LANDSCAPE);
    }

    private int isViewHolderTypeLandscape(int viewHolderType) {
        return viewHolderType & VIEW_HOLDER_TYPE_LANDSCAPE;
    }

    private static final int SCROLL_INTERVAL = 3;
    private static final TimeUnit SCROLL_INTERVAL_TIME_UNIT = TimeUnit.SECONDS;

    private HorizontalScrollingBannerAdapter mSlidingBannersAdapter = null;
    private HorizontalScrollingBannerAdapter mCategoryBannersAdapter = null;
    private HorizontalScrollingBannerAdapter mSponsoredBannersAdapter = null;

    public BannersAdapter(
            Activity activity,
            ShopsMvpPresenter presenter,
            List<GetBannerResponse.Group> sales,
            int orientation) {

        mGroups = sales;
        mSales = new ArrayList<>();
        for (GetBannerResponse.Group group : sales) {
            mSales.addAll(group.getBanners());
        }
        mActivity = activity;
        mPresenter = presenter;

        mWidth = mActivity.getResources().getInteger(mPresenter.isTablet() ? R.integer.banner_tablet_width : R.integer.banner_mobile_width);
        mHeight = mActivity.getResources().getInteger(mPresenter.isTablet() ? R.integer.banner_tablet_height : R.integer.banner_mobile_height);

        mOrientation = orientation;

        setupDimensions(orientation);
    }

    class SpacerViewHolder extends RecyclerView.ViewHolder {

        public SpacerViewHolder(@NonNull View itemView) {
            super(itemView);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    (int) itemView.getResources().getDimension(R.dimen.horizontal_banner_spacer_size)
            );
            itemView.setLayoutParams(params);
            itemView.setVisibility(View.VISIBLE);
            itemView.setBackgroundColor(itemView.getResources().getColor(R.color.white));
        }
    }

    class HorizontalRecyclerViewHolder extends RecyclerView.ViewHolder {

        private boolean isAutoScroll;

        private HorizontalScrollingBannerAdapter adapter;

        private Disposable autoScrollDisposable = null;

        private boolean isSwipeEnabled;

        @BindView(R.id.viewholder_horizontal_scrolling_banner_layout)
        ViewGroup layout;

        @BindView(R.id.viewholder_horizontal_scrolling_banner_recycler_view)
        RecyclerView recyclerView;

        HorizontalRecyclerViewHolder(View view,
                                     int height,
                                     HorizontalScrollingBannerAdapter adapter,
                                     boolean isAutoScroll,
                                     boolean isSwipeEnabled) {
            super(view);
            ButterKnife.bind(this, view);

            if (height > 0) {
                float spacing = view.getContext().getResources().getDimension(R.dimen.horizontal_banner_spacing);

                ViewGroup.LayoutParams params = layout.getLayoutParams();
                params.height = height + (int) (spacing * 2);
                layout.setLayoutParams(params);
            }

            LinearLayoutManager layoutManager = new LinearLayoutManager(
                    recyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false);

            recyclerView.setLayoutManager(layoutManager);

            recyclerView.setNestedScrollingEnabled(false);

            setAdapter(adapter);

            this.isAutoScroll = isAutoScroll;
            this.isSwipeEnabled = isSwipeEnabled;
        }

        public HorizontalScrollingBannerAdapter getAdapter() {
            return adapter;
        }

        public void setAdapter(HorizontalScrollingBannerAdapter adapter) {
            this.adapter = adapter;
            if (recyclerView != null) {
                recyclerView.setAdapter(adapter);
                if (adapter != null) {
                    adapter.resetReyclerViewPosition();
                }
            }
        }

        public void onViewBound() {
            previousX = recyclerView.computeHorizontalScrollOffset();
            setupScrollListener();

            if (isAutoScroll) {
                startAutoscroll();
            }
        }

        private final ViewTreeObserver.OnScrollChangedListener onScrollChangedListener = () ->
                HorizontalRecyclerViewHolder.this.onScrollChanged(recyclerView.computeHorizontalScrollOffset());


        public void onViewRecycled() {
            clearScrollListener();
            stopAutoScroll();
        }

        @SuppressLint("ClickableViewAccessibility")
        private void setupScrollListener() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                recyclerView.setOnScrollChangeListener(
                        (v, scrollX, scrollY, oldScrollX, oldScrollY) -> onScrollChanged(recyclerView.computeHorizontalScrollOffset()));
            } else {
                recyclerView.getViewTreeObserver().addOnScrollChangedListener(onScrollChangedListener);
            }

            if (isSwipeEnabled) {
                recyclerView.setOnTouchListener(new OnHorizontalSwipeTouchListener() {
                    @Override
                    public void onFinishDragging() {
                        snapToCenter(true);
                    }

                    @Override
                    public void onSwipeLeft() {
                        scrollToNext(true);
                    }

                    @Override
                    public void onSwipeRight() {
                        scrollToPrevious(true);
                    }

                    @Override
                    public void onTouchDown() {
                        if (isAutoScroll) {
                            stopAutoScroll();
                        }
                    }

                    @Override
                    public void onTouchUp() {
                        if (isAutoScroll) {
                            startAutoscroll();
                        }
                    }
                });
            }
        }

        @SuppressLint("ClickableViewAccessibility")
        private void clearScrollListener() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                recyclerView.setOnScrollChangeListener(null);
            } else {
                recyclerView.getViewTreeObserver().removeOnScrollChangedListener(onScrollChangedListener);
            }

            recyclerView.setOnTouchListener(null);
        }

        private void startAutoscroll() {
            stopAutoScroll();
            autoScrollDisposable = Observable
                    .interval(
                            SCROLL_INTERVAL,
                            SCROLL_INTERVAL_TIME_UNIT,
                            AndroidSchedulers.mainThread())
                    .doOnNext(o -> scrollToNext(true))
                    .subscribe();
        }

        private void stopAutoScroll() {
            if (autoScrollDisposable != null) {
                autoScrollDisposable.dispose();
                autoScrollDisposable = null;
            }
        }

        private void scrollToNext(boolean withAnimation) {
            if (adapter == null) {
                return;
            }

            Handler mainHandler = new Handler(recyclerView.getContext().getMainLooper());

            Runnable myRunnable = () -> {
                recyclerView.stopScroll();
                wrapAround(1);
                if (withAnimation) {
                    recyclerView.smoothScrollBy(getXBeforeNextPosition(), 0);
                } else {
                    recyclerView.scrollBy(getXBeforeNextPosition(), 0);
                }
            };
            mainHandler.post(myRunnable);
        }

        private void scrollToPrevious(boolean withAnimation) {
            if (adapter == null) {
                return;
            }

            Handler mainHandler = new Handler(recyclerView.getContext().getMainLooper());

            Runnable myRunnable = () -> {
                recyclerView.stopScroll();
                wrapAround(-1);
                int displacement = -getXAfterPreviousPosition();
                if (displacement >= 0) {
                    displacement = -adapter.getCellWidth();
                }
                if (withAnimation) {
                    recyclerView.smoothScrollBy(displacement, 0);
                } else {
                    recyclerView.scrollBy(displacement, 0);
                }
            };
            mainHandler.post(myRunnable);
        }

        public void snapToCenter(boolean withAnimation) {
            int diff = (getXBeforeNextPosition() - getXAfterPreviousPosition()) % adapter.getCellWidth();
            if (diff < 0) {
                scrollToNext(withAnimation);
            } else if (diff > 0) {
                scrollToPrevious(withAnimation);
            }
        }

        private int getXAfterPreviousPosition() {
            if (adapter == null) {
                return 0;
            }
            return (recyclerView.computeHorizontalScrollOffset() + (recyclerView.getWidth() - adapter.getCellWidth()) / 2) % adapter.getCellWidth();
        }

        private int getXBeforeNextPosition() {
            if (adapter == null) {
                return 0;
            }
            return adapter.getCellWidth() - getXAfterPreviousPosition();
        }

        private int previousX = 0;

        private void onScrollChanged(int scrollX) {
            int speed = scrollX - previousX;
            previousX = scrollX;
            wrapAround(speed);
        }

        private void wrapAround(int speed) {
            if (adapter != null) {
                adapter.wrapScrollPosition(speed);
            }

        }
    }


    class BannerViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.viewholder_banner_layout)
        ViewGroup layout;

        @BindView(R.id.viewholder_banner_image)
        ImageView image;

        @BindView(R.id.viewholder_banner_name)
        TextView name;

        @BindView(R.id.viewholder_banner_discount)
        TextView discount;

        @BindView(R.id.viewholder_banner_free_delivery)
        ImageView deliveryImage;

        @BindView(R.id.viewholder_banner_percent_off)
        TextView percentOff;

        @Nullable
        @BindView(R.id.adView_banner)
        View adView;

        BannerViewHolder(View view, int height) {
            super(view);
            ButterKnife.bind(this, view);

            if (height > 0) {
                ViewGroup.LayoutParams params = layout.getLayoutParams();
                params.height = height;
                layout.setLayoutParams(params);
            }

            if (view.getContext().getResources().getBoolean(R.bool.is_using_old_banner)) {
                name.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.bg_banner_name_old));
            } else {
                name.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.bg_banner_name_new));
            }
        }

        BannerViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        Disposable subscription;
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
        mSales.clear();
        addAll(bannerResponses);
    }

    public void addAll(List<GetBannerResponse.Group> bannerResponses) {
        int previousCount = mSales.size();

        for (GetBannerResponse.Group group : bannerResponses) {
            mSales.addAll(group.getBanners());
        }

        if (!bannerResponses.isEmpty()) {
            GetBannerResponse.Group bannerGroup = bannerResponses.get(bannerResponses.size() - 1);
            mOffset = bannerGroup.getBanners().size();
            mLastGroupType = bannerGroup.getType();
            if (recyclerView != null && !recyclerView.isComputingLayout()) {
                notifyItemRangeInserted(previousCount, mSales.size() - previousCount);
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;

        switch (removeViewHolderOrientationModifier(viewType)) {
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
                if (parent.getContext().getResources().getBoolean(R.bool.is_using_old_banner)) {
                    view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.viewholder_old_banner, parent, false);
                } else {
                    view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.viewholder_banner, parent, false);
                }

                return new BannerViewHolder(view, mComputedHeight);
        }
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        BannerViewHolder bannerViewHolder = null;
        HorizontalRecyclerViewHolder horizontalRecyclerViewHolder = null;
        if (holder instanceof BannerViewHolder) {
            bannerViewHolder = (BannerViewHolder) holder;
        } else if (holder instanceof HorizontalRecyclerViewHolder) {
            horizontalRecyclerViewHolder = (HorizontalRecyclerViewHolder) holder;
            horizontalRecyclerViewHolder.onViewBound();
        }
        switch (removeViewHolderOrientationModifier(holder.getItemViewType())) {
            case VIEW_HOLDER_TYPE_SPACER:
                break;
            case VIEW_HOLDER_TYPE_SLIDING_BANNER:
                setupSlidingBannersDimensions();
                horizontalRecyclerViewHolder.setAdapter(mSlidingBannersAdapter);
                if (mSlidingBannersAdapter != null) {
                    mSlidingBannersAdapter.resetReyclerViewPosition();
                    horizontalRecyclerViewHolder.snapToCenter(false);
                }
                break;
            case VIEW_HOLDER_TYPE_CATEGORY_BANNER:
                setupCategoryBannersDimensions();
                horizontalRecyclerViewHolder.setAdapter(mCategoryBannersAdapter);
                if (mCategoryBannersAdapter != null) {
                    mCategoryBannersAdapter.resetReyclerViewPosition();
                }
                // TODO
                break;
            case VIEW_HOLDER_TYPE_SPONSORED_BANNER:
                setupSponsoredBannersDimensions();
                horizontalRecyclerViewHolder.setAdapter(mSponsoredBannersAdapter);
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
                GetBannerResponse.Banner item = mSales.get(position - getPositionOfNormalBanners());
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
                    String percentOffValue = item.getPercentOffText().trim();
                    String[] discountWordArray = percentOffValue.split(" ");
                    percentOffValue = percentOffValue.replace(' ', '\n');
                    int percentSymbolLength = 1;
                    int spannableStringEndParameter = SPANNABLE_STRING_START_INDEX + discountWordArray[1].length() + percentSymbolLength;

                    SpannableString string = new SpannableString(percentOffValue);
                    string.setSpan(new StyleSpan(BOLD), SPANNABLE_STRING_START_INDEX, spannableStringEndParameter, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    string.setSpan(new RelativeSizeSpan(DISCOUNT_VALUE_SCALE_FACTOR), SPANNABLE_STRING_START_INDEX, spannableStringEndParameter, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    bannerViewHolder.percentOff.setVisibility(View.VISIBLE);
                    bannerViewHolder.percentOff.setText(string);
                } else {
                    bannerViewHolder.percentOff.setVisibility(View.GONE);
                }


                bannerViewHolder.deliveryImage.setVisibility(item.getFreeDelivery() ? View.VISIBLE : View.GONE);
                String imgUrl;

                imgUrl = ImageUtils.appendBannerSizeUrl(item.getImage(), mWidth, mHeight);


                ImageUtils.loadImage(imgUrl, bannerViewHolder.image);

                if (bannerViewHolder.subscription != null) {
                    bannerViewHolder.subscription.dispose();
                }

                if (item.getGroup().getIsClickable()) {
                    bannerViewHolder.subscription = RxView.clicks(bannerViewHolder.layout)
                            .throttleFirst(
                                    THROTTLE_FIRST_WINDOW_DURATION,
                                    TimeUnit.MILLISECONDS)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(action -> onBannerTapped(item, holder.getAdapterPosition(), imgUrl));
                }
                break;
        }
    }

    private void onBannerTapped(GetBannerResponse.Banner banner, int position, String imgUrl) {
        if (banner.getLink() != null && !banner.getLink().isEmpty()) {
            String link = banner.getLink();
            Pattern pattern = Pattern.compile("(?<=/s/)([^?\\n\\r])+");
            Matcher matcher = pattern.matcher(link);
            String title = " ";
            if (banner.getDescription() != null && !banner.getDescription().isEmpty()) {
                title = banner.getDescription();
            }
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

    private String titleForHeader(int position) {
        if (position == getPositionOfSponsoredBanners()) {
            return mActivity.getResources().getString(R.string.sponsored);
        } else if (position >= getPositionOfNormalBanners()) {
            return position < getItemCount() ? mSales.get(position - getPositionOfNormalBanners()).getGroup().getTitle() : "";
        }
        return null;
    }

    @Override
    public long getHeaderId(int position) {
        String title = titleForHeader(position);
        return title == null ? -1 : title.hashCode() * 10 + mOrientation;
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
        }
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return mSales.size() + getPositionOfNormalBanners();
    }

    private boolean isViewTypeVisible(int viewType) {
        switch (viewType & (~VIEW_HOLDER_TYPE_LANDSCAPE)) {
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
                    if (value == (viewType & (~VIEW_HOLDER_TYPE_LANDSCAPE))) {
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

    private int getPositionOfNormalBanners() {
        return getPositionOfViewType(VIEW_HOLDER_TYPE_NORMAL_BANNER);
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
        int adjustedPos = position - getPositionOfNormalBanners();
        return adjustedPos > 0 && adjustedPos < mSales.size() ? mSales.get(adjustedPos) : null;
    }

    public List<GetBannerResponse.Group> getData() {
        return mGroups;
    }

    public void setupDimensions(int orientation) {
        int resId;
        switch (ScreenUtils.getOrientation(mActivity)) {
            case Configuration.ORIENTATION_LANDSCAPE:
                resId = mPresenter.isTablet() ? R.integer.banner_tablet_landscape_column_count : R.integer.banner_mobile_landscape_column_count;
                break;
            default:
                resId = mPresenter.isTablet() ? R.integer.banner_tablet_portrait_column_count : R.integer.banner_mobile_portrait_column_count;
                break;
        }
        int minColumns = mActivity.getResources().getInteger(resId);
        int maxColumns = minColumns;

        mOrientation = orientation;

        if (!mActivity.getResources().getBoolean(R.bool.is_ourpay_app)) {
            // Dynamic Height Computation
            ImageUtils.Grid grid = ImageUtils.getRangedGridDefinition(
                    mWidth, mHeight,
                    ScreenUtils.getScreenWidth(mActivity),
                    minColumns, maxColumns);
            mNumberOfColumns = grid.getColumn();
            mComputedHeight = (int) grid.getItemHeight();

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

    private ImageUtils.Grid computeSlidingBannersGrid() {
        int width = mActivity.getResources().getInteger(R.integer.sliding_banner_width);
        int height = mActivity.getResources().getInteger(R.integer.sliding_banner_height);
        ImageUtils.Grid grid = ImageUtils.getRangedGridDefinition(
                width, height,
                ScreenUtils.getScreenWidth(mActivity) * SLIDING_BANNER_WIDTH_PERCENT,
                1, 1);
        return new ImageUtils.Grid(
                1,
                grid.getItemWidth() + getHorizontalPaddingForHorizontalBanners(),
                grid.getItemHeight() + getBottomPaddingForHorizontalBanners()
        );
    }

    private void setupSlidingBannersDimensions() {
        if (mSlidingBannersAdapter != null) {
            ImageUtils.Grid slidingBannersGrid = computeSlidingBannersGrid();
            mSlidingBannersAdapter.setupDimensions(
                    (int) slidingBannersGrid.getItemWidth(),
                    (int) slidingBannersGrid.getItemHeight());
        }
    }

    private ImageUtils.Grid computeCategoryBannersGrid() {
        int width = mActivity.getResources().getInteger(R.integer.sponsored_banner_width);
        int height = mActivity.getResources().getInteger(R.integer.sponsored_banner_height);
        ImageUtils.Grid grid = ImageUtils.getRangedGridDefinition(
                width, height,
                ScreenUtils.getScreenWidth(mActivity),
                1, 1);
        return new ImageUtils.Grid(
                1,
                grid.getItemWidth() + getHorizontalPaddingForHorizontalBanners(),
                grid.getItemHeight() + getBottomPaddingForHorizontalBanners()
        );
    }

    private void setupCategoryBannersDimensions() {
        if (mCategoryBannersAdapter != null) {
            ImageUtils.Grid categoryBannersGrid = computeCategoryBannersGrid();
            mCategoryBannersAdapter.setupDimensions(
                    (int) categoryBannersGrid.getItemWidth(),
                    (int) categoryBannersGrid.getItemHeight()
            );
        }
    }

    private ImageUtils.Grid computeSponsoredBannersGrid() {
        int numberOfColumns = mPresenter.isTablet() ?
                mActivity.getResources().getInteger(R.integer.sponsored_banner_column_count_for_tablet) :
                mActivity.getResources().getInteger(R.integer.sponsored_banner_column_count);
        int width = mActivity.getResources().getInteger(R.integer.sponsored_banner_width);
        int height = mActivity.getResources().getInteger(R.integer.sponsored_banner_height);
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
        mLastGroupType = "";
        mOffset = 0;
        mSales.clear();
        mGroups.clear();
        if (recyclerView != null && !recyclerView.isComputingLayout()) {
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = mOrientation == Configuration.ORIENTATION_LANDSCAPE ? VIEW_HOLDER_TYPE_LANDSCAPE : 0;
        if (position == getPositionOfSpacer()) {
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
        } else {
            mSlidingBannersAdapter = slidingBannersAdapter;
            if (mSlidingBannersAdapter != null) {
                mSlidingBannersAdapter
                        .setOnBannerTappedListener(banner -> BannersAdapter.this
                                .onBannerTapped(banner, -1, ""));
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
            mCategoryBannersAdapter = null;
            notifyDataSetChanged();
        } else {
            mCategoryBannersAdapter = categoryBannersAdapter;
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
        } else {
            mSponsoredBannersAdapter = sponsoredBannersAdapter;
            if (mSponsoredBannersAdapter != null) {
                mSponsoredBannersAdapter.setOnBannerTappedListener(banner -> BannersAdapter.this
                        .onBannerTapped(banner, -1, ""));
            }
            notifyDataSetChanged();
        }
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
}
