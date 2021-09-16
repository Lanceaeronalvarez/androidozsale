package au.com.dealsdirect.ui.controller.bannerfilter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by pauldesilva on 4/13/18.
 */

public class BannerFiltersAdapter extends RecyclerView.Adapter<BannerFiltersAdapter.BannerFiltersViewHolder> {

    private static final int CATEGORY_NORMAL = 0;
    private static final int CATEGORY_BRAND = 1;

    private BannerFilterClickListener mClickListener;
    private List<GetCategoryTreeResponse> mBannerFilterList;

    private boolean isBrandsAvailable = true;

    public BannerFiltersAdapter(BannerFilterClickListener clickListener,
                                ArrayList<GetCategoryTreeResponse> bannerFilterList) {
        this.mClickListener = clickListener;
        this.mBannerFilterList = bannerFilterList;
    }

    @NonNull
    @Override
    public BannerFiltersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_text_item, parent, false);
        return new BannerFiltersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BannerFiltersViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case CATEGORY_NORMAL:
                holder.bannerFilterText.setText(mBannerFilterList.get(position).getName());
                holder.itemView.setOnClickListener(view -> mClickListener.onCategoryClicked(position, mBannerFilterList.get(position)));
                break;
            case CATEGORY_BRAND:
                holder.bannerFilterText.setText("Brands");
                holder.itemView.setOnClickListener(view -> mClickListener.onBrandsClicked());
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position < mBannerFilterList.size() ? CATEGORY_NORMAL : CATEGORY_BRAND;
    }

    @Override
    public int getItemCount() {
        return mBannerFilterList.size() + (!mBannerFilterList.isEmpty() && isBrandsAvailable ? 1 : 0);
    }

    public void replaceData(List<GetCategoryTreeResponse> bannerFilterList) {
        this.mBannerFilterList = bannerFilterList;
        notifyDataSetChanged();
    }

    public boolean isBrandsAvailable() {
        return isBrandsAvailable;
    }

    public void setBrandsAvailable(boolean brandsAvailable) {
        isBrandsAvailable = brandsAvailable;
    }

    public static class BannerFiltersViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.row_viewholder_text)
        public TextView bannerFilterText;

        public BannerFiltersViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
