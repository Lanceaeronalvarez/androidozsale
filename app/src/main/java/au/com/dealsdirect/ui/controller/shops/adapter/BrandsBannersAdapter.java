package au.com.dealsdirect.ui.controller.shops.adapter;

import android.app.Activity;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.banner.GetTopBrandsResponse;
import au.com.dealsdirect.ui.controller.shops.ShopsMvpPresenter;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.ScreenUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class BrandsBannersAdapter extends RecyclerView.Adapter<BrandsBannersAdapter.BrandBannerViewHolder> implements ResettableDimensions {

    private static boolean SHOW_BRAND_DESCRIPTION = false;

    private List<GetTopBrandsResponse> mTopBrands;

    private int mOrientation;

    private int mComputedHeight = -1;
    private Activity mActivity;
    private ShopsMvpPresenter mPresenter;
    private RecyclerView recyclerView = null;
    private int mWidth;
    private int mHeight;
    private int mNumberOfColumns;

    boolean useOldBannerDimensions;

    private OnBrandBannerClickListener mOnBrandBannerClickListener;

    private static final int THROTTLE_FIRST_WINDOW_DURATION = 1000;

    private static final int VIEWTYPE_OLD = 0;
    private static final int VIEWTYPE_NEW = 1;

    public BrandsBannersAdapter(
            Activity activity,
            ShopsMvpPresenter presenter,
            List<GetTopBrandsResponse> topBrands,
            int orientation,
            boolean useOldBannerDimensions,
            OnBrandBannerClickListener onBrandBannerClickListener) {

        mTopBrands = topBrands;

        mActivity = activity;
        mPresenter = presenter;

        mOrientation = orientation;

        this.useOldBannerDimensions = useOldBannerDimensions;

        mOnBrandBannerClickListener = onBrandBannerClickListener;

        resetDimensions();
    }

    static class BrandBannerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.viewholder_banner_image)
        ImageView image;

        @BindView(R.id.viewholder_brand_banner_info_container)
        ViewGroup info;

        @BindView(R.id.viewholder_banner_info_name)
        TextView name;

        Disposable subscription;

        BrandBannerViewHolder(@NonNull View itemView, int height) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            if (height > 0) {
                ViewGroup.LayoutParams params = itemView.getLayoutParams();
                params.height = height;
                itemView.setLayoutParams(params);
            }
        }
    }

    @Override
    public BrandBannerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_banner_for_top_brands, parent, false);

        return new BrandBannerViewHolder(view, mComputedHeight);
    }

    @Override
    public void onBindViewHolder(BrandBannerViewHolder holder, int position) {

        GetTopBrandsResponse item = mTopBrands.get(position);
        int width = mWidth;
        int height = mHeight;
        if (SHOW_BRAND_DESCRIPTION && useOldBannerDimensions && item.getName() != null && !item.getName().isEmpty()) {
            holder.name.setVisibility(View.VISIBLE);
            holder.name.setText(item.getName());
        } else {
            holder.name.setVisibility(View.GONE);
        }

        if (item.getDescription() != null && !item.getDescription().isEmpty()) {
            holder.info.setVisibility(View.VISIBLE);
            holder.info.setOnClickListener(v -> {
                if (mOnBrandBannerClickListener != null) {
                    mOnBrandBannerClickListener.onInfoClick(item.getName(), item.getDescription());
                }
            });
        } else {
            holder.info.setVisibility(View.GONE);
        }

        String imgUrl = ImageUtils.appendBannerSizeUrl(item.getImage(), width, height);

        imgUrl += "?profile=sb&b&width=" + width;

        ImageUtils.loadImage(imgUrl, holder.image);

        if (holder.subscription != null) {
            holder.subscription.dispose();
        }

        holder.subscription = RxView.clicks(holder.itemView)
                .throttleFirst(
                        THROTTLE_FIRST_WINDOW_DURATION,
                        TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action -> {
                    if (mOnBrandBannerClickListener != null) {
                        mOnBrandBannerClickListener.onBannerClick(item);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return mTopBrands.size();
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
                    resId = mPresenter.isTablet() ? R.integer.brand_banner_tablet_landscape_column_count : R.integer.old_banner_mobile_landscape_column_count;
                    break;
                default:
                    resId = mPresenter.isTablet() ? R.integer.brand_banner_tablet_portrait_column_count : R.integer.old_banner_mobile_portrait_column_count;
                    break;
            }
            minColumns = mActivity.getResources().getInteger(resId);
        }
        final int maxColumns = minColumns;

        mOrientation = orientation;

        // Dynamic Height Computation
        ImageUtils.Grid grid = ImageUtils.getRangedGridDefinition(
                mWidth, mHeight,
                ScreenUtils.getScreenWidth(mActivity),
                minColumns, maxColumns);
        mNumberOfColumns = grid.getColumn();
        mComputedHeight = (int) grid.getItemHeight();

        notifyDataSetChanged();
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

    @Override
    public int getItemViewType(int position) {
        return useOldBannerDimensions ? VIEWTYPE_OLD : VIEWTYPE_NEW;
    }

    @Override
    public int getNumberOfColumns() {
        return mNumberOfColumns;
    }

    @Override
    public boolean isUseOldBannerDimensions() {
        return useOldBannerDimensions;
    }

    @Override
    public void setUseOldBannerDimensions(boolean useOldBannerDimensions) {
        this.useOldBannerDimensions = useOldBannerDimensions;
    }

    public interface OnBrandBannerClickListener {
        void onBannerClick(GetTopBrandsResponse brand);

        void onInfoClick(String title, String description);
    }
}
