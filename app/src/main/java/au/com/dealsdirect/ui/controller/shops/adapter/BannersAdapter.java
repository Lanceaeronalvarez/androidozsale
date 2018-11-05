package au.com.dealsdirect.ui.controller.shops.adapter;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.banner.GetBannerResponse;
import au.com.dealsdirect.ui.controller.shops.ShopsMvpPresenter;
import au.com.dealsdirect.utils.AppLogger;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.ScreenUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * dp Created by Admin on 6/7/17.
 */

public class BannersAdapter extends RecyclerView.Adapter<BannersAdapter.ViewHolder> implements StickyRecyclerHeadersAdapter {

    private int mComputedHeight = -1;
    private List<GetBannerResponse.Group> mGroups;
    private List<GetBannerResponse.Banner> mSales;
    private Context mContext;
    private ShopsMvpPresenter mPresenter;
    private int mWidth;
    private int mHeight;
    private int mNumberOfColumns;

    public BannersAdapter(
            Context context,
            ShopsMvpPresenter presenter,
            List<GetBannerResponse.Group> sales) {

        mGroups = sales;
        mSales = new ArrayList<>();
        for (GetBannerResponse.Group group : sales) {
            mSales.addAll(group.getBanners());
        }
        mContext = context;
        mPresenter = presenter;

        mWidth = mContext.getResources().getInteger(mPresenter.isTablet() ? R.integer.banner_tablet_width : R.integer.banner_mobile_width);
        mHeight = mContext.getResources().getInteger(mPresenter.isTablet() ? R.integer.banner_tablet_height : R.integer.banner_mobile_height);

        setupDimensions();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.viewholder_banner_layout)
        ViewGroup layout;

        @BindView(R.id.viewholder_banner_image)
        ImageView image;

        @BindView(R.id.viewholder_banner_overlay)
        View overlay;

        @BindView(R.id.viewholder_banner_name)
        TextView name;

        ViewHolder(View view, int height) {
            super(view);
            ButterKnife.bind(this, view);

            if (height > 0) {
                GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) layout.getLayoutParams();
                params.height = height;
                layout.setLayoutParams(params);
            }
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
        mSales = new ArrayList<>();
        addAll(bannerResponses);
    }

    public void addAll(List<GetBannerResponse.Group> bannerResponses) {
        int previousCount = mSales.size();

        for (GetBannerResponse.Group group : bannerResponses) {
            mSales.addAll(group.getBanners());
        }
        notifyItemRangeInserted(previousCount, mSales.size() - previousCount);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_banner, parent, false);
        return new ViewHolder(view, mComputedHeight);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        GetBannerResponse.Banner item = mSales.get(position);
        holder.name.setText(item.getDescription());
        String imgUrl;

        imgUrl = ImageUtils.appendBannerSizeUrl(item.getImage(), mWidth, mHeight);


        ImageUtils.loadImage(imgUrl, holder.image);
        if (!item.getIsAvailable()) {
            holder.overlay.setEnabled(false);
        }

        RxView.clicks(holder.layout)
            .throttleFirst(1000, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(action -> mPresenter.selectBanner(
                    item.getDestinationId(),
                    item.getDescription(),
                    item.getId(),
                    position,
                    imgUrl,
                    item.getIsAvailable()));
    }

    @Override
    public long getHeaderId(int position) {

        String title = position < getItemCount() ? mSales.get(position).getGroup().getTitle() : "";

        return title == null || title.equals("") ? -1 : title.charAt(0);
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_banner_header, parent, false);
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        HeaderViewHolder holder = (HeaderViewHolder) viewHolder;

        String title = position < getItemCount() ? mSales.get(position).getGroup().getTitle() : "";

        holder.headerText.setText(title == null ? "" : title);
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        ImageUtils.clearImage(holder.image);
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public int getItemCount() {
        return mSales.size();
    }
    
    public int getNumberOfColumns() { return mNumberOfColumns; }

    public GetBannerResponse.Banner getItem(int position) {
        return position > 0 && mSales.size() > position ? mSales.get(position) : null;
    }

    public List<GetBannerResponse.Group> getData() {
        return mGroups;
    }

    public void setupDimensions() {
        int minColumns = mContext.getResources().getInteger(mPresenter.isTablet() ? R.integer.banner_tablet_min_column_count : R.integer.banner_mobile_min_column_count);
        int maxColumns = mContext.getResources().getInteger(mPresenter.isTablet() ? R.integer.banner_tablet_max_column_count : R.integer.banner_mobile_max_column_count);

        if (!mContext.getResources().getBoolean(R.bool.is_ourpay_app)) {
            // Dynamic Height Computation
            ImageUtils.Grid grid = ImageUtils.getRangedGridDefinition(
                    mWidth, mHeight,
                    ScreenUtils.getScreenWidth(mContext),
                    minColumns, maxColumns);
            mNumberOfColumns = grid.getColumn();
            mComputedHeight = (int) grid.getItemHeight();
            String orientation = ScreenUtils.getOrientation(mContext) == Configuration.ORIENTATION_LANDSCAPE ? "Landscape" : "Portrait";
            AppLogger.d(orientation + " Width: " + ScreenUtils.getScreenWidth(mContext) + " Height: " + mComputedHeight);
        }
    }
}
