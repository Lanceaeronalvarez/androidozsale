package au.com.dealsdirect.ui.controller.shops.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.banner.GetBannerResponse;
import au.com.dealsdirect.ui.controller.shops.ShopsMvpPresenter;
import au.com.dealsdirect.ui.controller.shops.listener.BannerClickListener;
import au.com.dealsdirect.utils.ImageUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * dp Created by Admin on 6/7/17.
 */

public class BannersAdapter extends RecyclerView.Adapter<BannersAdapter.ViewHolder> {

    public static DisplayMetrics DISPLAY_METRICS;

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
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.viewholder_banner_layout)
        LinearLayout layout;

        @BindView(R.id.viewholder_banner_image)
        ImageView image;

        @BindView(R.id.viewholder_banner_name)
        TextView name;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public void replace(List<GetBannerResponse> bannerResponses){
        mSales = new ArrayList<>(bannerResponses);
        notifyDataSetChanged();
    }

    public void addAll(List<GetBannerResponse> bannerResponses){
        mSales.addAll(bannerResponses);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_banner, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        GetBannerResponse item = mSales.get(position);
        holder.name.setText(item.getDescription());

        Log.d("IMG", item.getImage());

//        if (mPresenter.isTablet()) {
//            ImageUtils.loadImage(mContext, ImageUtils.getBannerTabletSize(item.getImage()), holder.image);
//        } else {
            ImageUtils.loadImage(mContext, ImageUtils.getBannerMobileSize(item.getImage()), holder.image);
//        }

        holder.layout.setOnClickListener(view -> mBannerClickListener.onBannerClicked(
                mSales.get(position).getDestinationID(),
                mSales.get(position).getDescription(),
                mSales.get(position).getId(),
                position,
                item.getImage()));
    }

    @Override
    public int getItemCount() {
        return mSales.size();
    }

}