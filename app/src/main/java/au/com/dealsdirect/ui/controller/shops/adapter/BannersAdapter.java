package au.com.dealsdirect.ui.controller.shops.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

public class BannersAdapter extends RecyclerView.Adapter<BannersAdapter.ViewHolder> {

    private int mComputedHeight = -1;
    private List<GetBannerResponse> mSales;
    private Context mContext;
    private ShopsMvpPresenter mPresenter;
    private BannerClickListener mBannerClickListener;

    public BannersAdapter(
            Context context,
            ShopsMvpPresenter presenter,
            List<GetBannerResponse> sales,
            BannerClickListener bannerClickListener) {

        this.mSales = sales;
        this.mContext = context;
        this.mPresenter = presenter;
        this.mBannerClickListener = bannerClickListener;

        if(!mContext.getResources().getBoolean(R.bool.is_ourpay_app)) {
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

            if(height > 0) {
                GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) layout.getLayoutParams();
                params.height = height;
                layout.setLayoutParams(params);
            }
        }
    }

    public void replace(List<GetBannerResponse> bannerResponses) {
        mSales = new ArrayList<>(bannerResponses);
        notifyDataSetChanged();
    }

    public void addAll(List<GetBannerResponse> bannerResponses) {
        mSales.addAll(bannerResponses);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_banner, parent, false);
        return new ViewHolder(view, mComputedHeight);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        GetBannerResponse item = mSales.get(position);
        holder.name.setText(item.getDescription());
        String imgUrl;

        if (mPresenter.isTablet()) {
            imgUrl = ImageUtils.getBannerTabletSize(item.getImage());
            //AppLogger.d("IMG " + ImageUtils.getBannerTabletSize(item.getImage()));
        } else {
            imgUrl = ImageUtils.getBannerMobileSize(item.getImage());
        }

        ImageUtils.loadImage(mContext, imgUrl, holder.image);
        if (!item.getIsAvailable()) {
            holder.overlay.setEnabled(false);
        }

        holder.layout.setOnClickListener(view ->
                mBannerClickListener.onBannerClicked(
                        item.getDestinationID(),
                        item.getDescription(),
                        item.getId(),
                        position,
                        ImageUtils.getBannerMobileSize(item.getImage()),
                        item.getIsAvailable()));
    }

    @Override
    public int getItemCount() {
        return mSales.size();
    }


    public List<GetBannerResponse> getData() {
        return mSales;
    }
}
