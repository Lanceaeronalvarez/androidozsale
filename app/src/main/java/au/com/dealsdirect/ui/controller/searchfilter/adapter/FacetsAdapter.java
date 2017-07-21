package au.com.dealsdirect.ui.controller.searchfilter.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.controller.searchfilter.SearchFilterMvpPresenter;
import butterknife.BindView;
import butterknife.ButterKnife;


public class FacetsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<String> mData;
    private SearchFilterMvpPresenter mPresenter;
    private int mLastPosition = -1;
    private RecyclerView.ViewHolder mLastSelectedViewHolder = null;
    private Context mContext;

    public FacetsAdapter(Context context, List<String> data, SearchFilterMvpPresenter presenter) {
        mContext = context;
        mData = data;
        mPresenter = presenter;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_facets, parent, false);
        FacetsViewHolder vh = new FacetsViewHolder(view, mPresenter);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        FacetsViewHolder vh = (FacetsViewHolder) holder;

        setAnimation(holder.itemView, position);

        if (!mData.isEmpty()) {

            vh.mFacetName.setText(mData.get(position));
            vh.mFacetBackground.setImageDrawable(mContext.getDrawable(mapDrawable(position)));

            vh.itemView.setOnClickListener(view -> {
                if (mLastSelectedViewHolder == null) {
                    mLastSelectedViewHolder = vh;
                    vh.itemView.setActivated(true);
                    mPresenter.onFacetClicked(position);

                } else {
                    if(mLastSelectedViewHolder != vh) {
                        mLastSelectedViewHolder.itemView.setActivated(false);
                        mLastSelectedViewHolder = vh;
                        mLastSelectedViewHolder.itemView.setActivated(true);
                        mPresenter.onFacetClicked(position);
                    }
                }
            });

        }

    }

    private int mapDrawable(int position){
        switch (position){
            case 0:
                return R.drawable.bg_filter_sort_icon;
            case 1:
                return R.drawable.bg_filter_categories_icon;
            case 2:
                return R.drawable.bg_filter_brand_icon;
            case 3:
                return R.drawable.bg_filter_size_icon;
            case 4:
                return R.drawable.bg_filter_color_icon;
            case 5:
                return R.drawable.bg_filter_price_icon;
            default:
                return -1;
        }

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }


    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }

    public static class FacetsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.row_facets_image)
        public ImageView mFacetBackground;

        @BindView(R.id.row_facets_name)
        public TextView mFacetName;

        public SearchFilterMvpPresenter mPresenter;

        public FacetsViewHolder(View itemView, SearchFilterMvpPresenter presenter) {
            super(itemView);
            mPresenter = presenter;
            ButterKnife.bind(this, itemView);
        }


    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > mLastPosition) {
            Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            mLastPosition = position;
        }
    }

    public String getItemAt(int position) {
        return mData.get(position);
    }

}

