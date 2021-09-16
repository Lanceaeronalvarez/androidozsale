package au.com.dealsdirect.ui.controller.searchfilter.adapter;

import android.content.Context;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.controller.searchfilter.SearchFilterMvpPresenter;
import au.com.dealsdirect.utils.BundleKeys;
import butterknife.BindView;
import butterknife.ButterKnife;


public class FacetsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Pair<String, String>> mData;
    private SearchFilterMvpPresenter mPresenter;
    private int mLastPosition = -1;
    private RecyclerView.ViewHolder mLastSelectedViewHolder = null;
    private Context mContext;
    private List<SearchChipModel> mSearchChips;

    public FacetsAdapter(Context context, ArrayList<Pair<String, String>> data, SearchFilterMvpPresenter presenter, List<SearchChipModel> searchChips) {
        mContext = context;
        mData = data;
        mPresenter = presenter;
        mSearchChips = searchChips;
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

            vh.mFacetName.setText(mData.get(position).second);

            if (isFacetActive(mData.get(position).first)) {
                vh.mFacetIndicator.setVisibility(View.VISIBLE);
            } else {
                vh.mFacetIndicator.setVisibility(View.INVISIBLE);
            }

            if (mLastSelectedViewHolder == null) {
                mLastSelectedViewHolder = vh;
                vh.itemView.setActivated(true);
//                mPresenter.onFacetItemClicked(position);
            }

            vh.itemView.setOnClickListener(view -> {
                if (mLastSelectedViewHolder != vh) {
                    mLastSelectedViewHolder.itemView.setActivated(false);
                    mLastSelectedViewHolder = vh;
                    mLastSelectedViewHolder.itemView.setActivated(true);
//                    mPresenter.onFacetItemClicked(position);
                }
            });

        }

    }

    private String getFacetFilterType(int position) {
        return mData.get(position).first;
    }

    private int mapDrawable(int position) {
        String facetFilterType = getFacetFilterType(position);
        switch (facetFilterType) {
            case BundleKeys.SORT_FACETFILTER_NAME:
                return R.drawable.bg_filter_sort_icon;
            case BundleKeys.CATEGORY_TREE_FACET:
                return R.drawable.bg_filter_categories_icon;
            case BundleKeys.BRANDS_FACETFILTER_NAME:
                return R.drawable.bg_filter_brand_icon;
            case BundleKeys.SIZES_FACETFILTER_NAME:
                return R.drawable.bg_filter_size_icon;
            case BundleKeys.COLORS_FACETFILTER_NAME:
                return R.drawable.bg_filter_color_icon;
            case BundleKeys.PRICE_FACETFILTER_NAME:
                return R.drawable.bg_filter_price_icon;
            default:
                return -1;
        }
    }

    public void add(Pair<String, String> newFacet) {
        mData.add(newFacet);
        notifyItemInserted(mData.size() - 1);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public ArrayList<Pair<String, String>> getData() {
        return mData;
    }

    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }

    public static class FacetsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.row_facets_name)
        public TextView mFacetName;

        @BindView(R.id.row_indicator_active_text)
        public TextView mFacetIndicator;

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

    private boolean isFacetActive(String key) {
        for (SearchChipModel chip : mSearchChips) {
            if (chip.getFilterType().equals(key)) {
                return true;
            }
        }
        return false;
    }

    public void updateSelectedSearchChips(List<SearchChipModel> selectedSearchChips) {
        mSearchChips = selectedSearchChips;
    }

}

