package au.com.dealsdirect.ui.controller.searchfilter.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.controller.searchfilter.SearchFilterMvpPresenter;
import au.com.dealsdirect.utils.BundleKeys;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by smartwave on 20/07/2017.
 */

public class FacetItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public List<String> getData() {
        return mData;
    }

    private List<String> mData = new ArrayList<>();
    private SearchFilterMvpPresenter mPresenter;

    private String mFilterType = "";
    private RecyclerView mRecyclerView;
    private SearchTagsAdapter mSearchTagsAdapter;

    public FacetItemsAdapter(List<String> data, SearchFilterMvpPresenter presenter, RecyclerView recyclerView) {
        mData = data;
        mPresenter = presenter;
        mRecyclerView = recyclerView;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_facet_items, parent, false);
        FacetItemsViewHolder vh = new FacetItemsViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        FacetItemsViewHolder vh = (FacetItemsViewHolder) holder;
        vh.mFacetItemTitle.setText(mData.get(position));

        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //single selection, allows unselection logic for sort type of facet.
                if (mFilterType == BundleKeys.SORT_FACETFILTER_NAME) {
                    SearchChipModel sortChip = mSearchTagsAdapter.findSortChip();
                    if (!vh.isSelected) {
                        if (sortChip == null) {
                            vh.toggle();
                            vh.itemView.setSelected(true);
                            addChip(position);
                        } else {
                            FacetItemsViewHolder oldVH = (FacetItemsViewHolder) mRecyclerView.findViewHolderForLayoutPosition(sortChip.getIndex());
                            oldVH.toggle();
                            oldVH.itemView.setSelected(false);
                            removeChip(sortChip.getIndex());


                            vh.toggle();
                            vh.itemView.setSelected(true);
                            addChip(position);

                        }
                    } else {
                        vh.toggle();
                        vh.itemView.setSelected(false);
                        removeChip(position);
                    }
                } else { //regular logic for other facets(multi-selection/unselection)

                    if (!vh.isSelected) {
                        vh.toggle();
                        vh.itemView.setSelected(true);
                        addChip(position);
                    } else {
                        vh.toggle();
                        vh.itemView.setSelected(false);
                        removeChip(position);
                    }
                }
            }
        });

        if (isFacetItemActive(position)) {
            vh.isSelected = true;
            vh.itemView.setSelected(true);
        } else {
            vh.isSelected = false;
            vh.itemView.setSelected(false);
        }
    }

    private boolean isFacetItemActive(int position) {
        for (SearchChipModel chip : mSearchTagsAdapter.getData()) {
            if (chip.getChipTitle().equals(mData.get(position))) {
                return true;
            }
        }
        return false;
    }

    public void setSearchTagsAdapter(SearchTagsAdapter adapter) {
        mSearchTagsAdapter = adapter;
    }

    private void addChip(int position) {
        SearchChipModel newChip = new SearchChipModel(mFilterType, mData.get(position), position);
        mSearchTagsAdapter.add(newChip);
    }

    private void removeSortChip() {
        SearchChipModel chipToRemove = null;
        for (SearchChipModel chip : mSearchTagsAdapter.getData()) {
            if (chip.getFilterType().equals(BundleKeys.SORT_FACETFILTER_NAME)) {
                chipToRemove = chip;
            }
        }
        if (chipToRemove != null) {
            mSearchTagsAdapter.remove(chipToRemove);
        }

    }

    private void removeChip(int position) {
        SearchChipModel chipToRemove = null;
        String chipTitle = getData().get(position);
        for (SearchChipModel chip : mSearchTagsAdapter.getData()) {

//            special logic for sort chips
            if (getFilterType() == BundleKeys.SORT_FACETFILTER_NAME && chip.getFilterType().equals(BundleKeys.SORT_FACETFILTER_NAME)) {
                chipToRemove = chip;
                break;
            }

//            regular logic for others
            if (chip.getChipTitle().equals(chipTitle)) {
                chipToRemove = chip;
                break;
            }
        }

        mSearchTagsAdapter.remove(chipToRemove);
    }

    public void replaceData(List<String> data) {
        mData = new ArrayList<>(data);
        notifyDataSetChanged();
    }

    public void setFilterType(String type) {
        mFilterType = type;
    }

    public String getFilterType() {
        return mFilterType;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class FacetItemsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.viewholder_facet_item_container)
        RelativeLayout mFacetItemContainer;

        @BindView(R.id.viewholder_facet_item_title)
        TextView mFacetItemTitle;

        boolean isSelected = false;

        public FacetItemsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void toggle() {
            isSelected = !isSelected;
        }
    }
}
