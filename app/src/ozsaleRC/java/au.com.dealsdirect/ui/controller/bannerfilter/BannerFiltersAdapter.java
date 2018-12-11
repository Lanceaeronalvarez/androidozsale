package au.com.dealsdirect.ui.controller.bannerfilter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

    private Context mContext;
    private BannerFilterClickListener mClickListener;
    private List<GetCategoryTreeResponse> mBannerFilterList;

    public BannerFiltersAdapter(Context context, BannerFilterClickListener clickListener,
                                ArrayList<GetCategoryTreeResponse> bannerFilterList) {
        this.mContext = context;
        this.mClickListener = clickListener;
        this.mBannerFilterList = bannerFilterList;
    }

    @Override
    public BannerFiltersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_text_item, parent, false);
        return new BannerFiltersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BannerFiltersViewHolder holder, int position) {
        holder.bannerFilterText.setText(mBannerFilterList.get(position).getName());
        holder.itemView.setOnClickListener(view -> mClickListener.onBannerClicked(position, mBannerFilterList.get(position)));
    }

    @Override
    public int getItemCount() {
        return mBannerFilterList.size();
    }

    public void replaceData(List<GetCategoryTreeResponse> bannerFilterList) {
        this.mBannerFilterList = bannerFilterList;
        notifyDataSetChanged();
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
