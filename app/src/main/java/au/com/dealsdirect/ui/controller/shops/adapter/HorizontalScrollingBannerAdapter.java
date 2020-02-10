package au.com.dealsdirect.ui.controller.shops.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.banner.GetBannerResponse;
import au.com.dealsdirect.utils.ImageUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class HorizontalScrollingBannerAdapter extends RecyclerView.Adapter<HorizontalScrollingBannerAdapter.ViewHolder> {

    private boolean hasInitializedDimensions = false;

    private int cellWidth;
    private int cellHeight;

    private List<GetBannerResponse.Banner> dataSource;
    private List<GetBannerResponse.Banner> indicatorSource;

    private RecyclerView recyclerView = null;

    private OnBannerTappedListener onBannerTappedListener = null;

    private static final int THROTTLE_FIRST_WINDOW_DURATION = 1000;
    private static final int VIEW_TYPE_PAGE_INDICATOR = 2;
    private static final int VIEW_TYPE_BANNER = 1;

    private Activity mActivity;

    private int mViewType;
    private int mCurrentIndicatorPosition;

    public HorizontalScrollingBannerAdapter(Activity activity, int viewType, int currentIndicatorPosition) {
        mActivity = activity;
        mViewType = viewType;
        mCurrentIndicatorPosition = currentIndicatorPosition;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = null;

        switch (viewType) {
            case VIEW_TYPE_BANNER:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.viewholder_banner_for_horizontal, parent, false);
                break;
            case VIEW_TYPE_PAGE_INDICATOR:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.circle_indicator_image_layout, parent, false);
                break;
        }

        return new ViewHolder(view, cellWidth);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        switch (mViewType) {
            case VIEW_TYPE_BANNER:
                int virtualPosition = position % dataSource.size();

                GetBannerResponse.Banner item = dataSource.get(virtualPosition);

                int width;
                int height;
                if (cellWidth > cellHeight) {
                    width = holder.itemView.getContext().getResources().getInteger(R.integer.sliding_banner_width);
                    height = holder.itemView.getContext().getResources().getInteger(R.integer.sliding_banner_height);
                } else {
                    width = holder.itemView.getContext().getResources().getInteger(R.integer.sponsored_banner_width);
                    height = holder.itemView.getContext().getResources().getInteger(R.integer.sponsored_banner_height);
                }
                String imgUrl = ImageUtils.appendBannerSizeUrl(item.getImage(), width, height);

                if (mActivity != null && !mActivity.isDestroyed()) {
                    ImageUtils.loadImage(imgUrl, holder.image);
                }

                if (holder.subscription != null) {
                    holder.subscription.dispose();
                }

                if (item.getGroup().getIsClickable()) {
                    holder.subscription = RxView.clicks(holder.layout)
                            .throttleFirst(
                                    THROTTLE_FIRST_WINDOW_DURATION,
                                    TimeUnit.MILLISECONDS)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(action -> {
                                if (onBannerTappedListener != null) {
                                    onBannerTappedListener.onBannerTapped(item);
                                }
                            });
                }
                break;
            case VIEW_TYPE_PAGE_INDICATOR:
                if (position != mCurrentIndicatorPosition) {
                    holder.circleIndicatorImage.setImageResource(R.drawable.circle_indicator_inactive);
                } else {
                    holder.circleIndicatorImage.setImageResource(R.drawable.circle_indicator_active);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return (dataSource != null && dataSource.size() != 0) ? dataSource.size() + getEdgeBufferSize() * 2 :
                indicatorSource.size();
    }

    public List<GetBannerResponse.Banner> getDataSource() {
        return dataSource;
    }

    @Override
    public int getItemViewType(int position) {
        return mViewType;
    }

    public void setDataSource(List<GetBannerResponse.Banner> dataSource) {
        this.dataSource = dataSource;
        if (recyclerView != null && !recyclerView.isComputingLayout()) {
            notifyDataSetChanged();
        }
    }

    public List<GetBannerResponse.Banner> getIndicatorSource() {
        return indicatorSource;
    }

    public void setIndicatorSource(List<GetBannerResponse.Banner> indicatorSource) {
        this.indicatorSource = indicatorSource;
    }

    public void setupDimensions(int width, int height) {
        cellWidth = width;
        cellHeight = height;
        if (recyclerView != null) {
            recyclerView.getRecycledViewPool().clear();
            if (!recyclerView.isComputingLayout()) {
                notifyDataSetChanged();
            }
        }
    }

    public int getCellWidth() {
        return cellWidth;
    }

    public int getCellHeight() {
        return cellHeight;
    }

    private int getEdgeBufferSize() {
        if (recyclerView != null && recyclerView.getWidth() > 0 && cellWidth > 0) {
            return (int) (2 * Math.ceil(recyclerView.getWidth() / (float) cellWidth));
        } else {
            return Math.max(3, dataSource.size());
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
        if (!hasInitializedDimensions) {
            hasInitializedDimensions = true;
            setupDimensions(cellWidth, cellHeight);
        }
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.recyclerView = null;
    }

    public OnBannerTappedListener getOnBannerTappedListener() {
        return onBannerTappedListener;
    }

    public void setOnBannerTappedListener(OnBannerTappedListener onBannerTappedListener) {
        this.onBannerTappedListener = onBannerTappedListener;
    }

    public void resetReyclerViewPosition() {
        if (recyclerView != null) {
            recyclerView.scrollToPosition(getEdgeBufferSize());
        }
    }

    public void wrapScrollPosition(int speed) {
        if (recyclerView == null) {
            return;
        }
        int x = recyclerView.computeHorizontalScrollOffset();
        if (speed > 0 && x > getCellWidth() * (getDataSource().size() + getEdgeBufferSize())) {
            recyclerView.scrollBy(-getScrollRange(), 0);
        } else if (speed < 0 && x < getCellWidth() * getEdgeBufferSize()) {
            recyclerView.scrollBy(getScrollRange(), 0);
        }
    }

    private int getScrollRange() {
        return getCellWidth() * getDataSource().size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.viewholder_banner_layout)
        ViewGroup layout;

        @BindView(R.id.viewholder_banner_image)
        public
        ImageView image;

        @BindView(R.id.vh_sale_item_image)
        public
        ImageView circleIndicatorImage;

        ViewHolder(View view, int width) {
            super(view);
            ButterKnife.bind(this, view);

            if (width > 0) {
                ViewGroup.LayoutParams params = layout.getLayoutParams();
                params.width = width;
                layout.setLayoutParams(params);
            }
        }

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        Disposable subscription;
    }

    interface OnBannerTappedListener {
        void onBannerTapped(GetBannerResponse.Banner banner);
    }
}
