package au.com.dealsdirect.ui.controller.shops.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.ArrayList;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.banner.GetBannerResponse;
import au.com.dealsdirect.ui.controller.shops.ShopsMvpPresenter;
import au.com.dealsdirect.ui.controller.shops.listener.BannerClickListener;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.AppConstants;
import au.com.dealsdirect.utils.ScreenUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * dp Created by Admin on 6/7/17.
 */

public class BannersAdapter extends RecyclerView.Adapter<BannersAdapter.ViewHolder> implements StickyRecyclerHeadersAdapter {

    private int mComputedHeight = -1;
    private List<GetBannerResponse.Group> mGroups;
    private List<GetBannerResponse.Banner> mSales;
    private Context mContext;
    private ShopsMvpPresenter mPresenter;
    private BannerClickListener mBannerClickListener;

    public BannersAdapter(
            Context context,
            ShopsMvpPresenter presenter,
            List<GetBannerResponse.Group> sales,
            BannerClickListener bannerClickListener) {

        this.mGroups = sales;
        this.mSales = new ArrayList<>();
        for (GetBannerResponse.Group group : sales) {
            mSales.addAll(group.getBanners());
        }
        this.mContext = context;
        this.mPresenter = presenter;
        this.mBannerClickListener = bannerClickListener;

        if (!mContext.getResources().getBoolean(R.bool.is_ourpay_app)) {
            // Dynamic Height Computation
            if (mPresenter.isTablet()) {

                int screenWidth = ScreenUtils.getScreenWidth(mContext) / 2;

                mComputedHeight = ImageUtils.getComputedBannerHeight(AppConstants.BANNER_TABLET_WIDTH,
                        AppConstants.BANNER_TABLET_HEIGHT, screenWidth);
            } else {

                int screenWidth = ScreenUtils.getScreenWidth(mContext);

                mComputedHeight = ImageUtils.getComputedBannerHeight(AppConstants.BANNER_MOBILE_WIDTH,
                        AppConstants.BANNER_MOBILE_HEIGHT, screenWidth);
            }
        }
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

        if (mPresenter.isTablet()) {
            imgUrl = ImageUtils.getBannerTabletSize(item.getImage());
        } else {
            imgUrl = ImageUtils.getBannerMobileSize(item.getImage());
        }

        ImageUtils.loadImage(mContext, imgUrl, holder.image);
        if (!item.getIsAvailable()) {
            holder.overlay.setEnabled(false);
        }

        holder.layout.setOnClickListener(view ->
                mBannerClickListener.onBannerClicked(
                        item.getDestinationId(),
                        item.getDescription(),
                        item.getId(),
                        position,
                        ImageUtils.getBannerMobileSize(item.getImage()),
                        item.getIsAvailable()));
    }

    @Override
    public long getHeaderId(int position) {
        return mSales.get(position).getGroup().getType().charAt(0);
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_banner_header, parent, false);
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        HeaderViewHolder holder = (HeaderViewHolder) viewHolder;

        holder.headerText.setText(mSales.get(position).getGroup().getType());
    }

    @Override
    public int getItemCount() {
        return mSales.size();
    }


    public GetBannerResponse.Banner getItem(int position) {
        return position > 0 && mSales.size() > position ? mSales.get(position) : null;
    }

    public List<GetBannerResponse.Group> getData() {
        return mGroups;
    }
}
