package au.com.dealsdirect.ui.controller.searchfilter.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.controller.searchfilter.SearchFilterMvpPresenter;
import au.com.dealsdirect.utils.AppLogger;
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

    public Set<Integer> getSelectedFacets() {
        return mSelectedFacets;
    }

    private Set<Integer> mSelectedFacets = new HashSet<Integer>();
    private String mFilterType = "";
    private RecyclerView mRecyclerView;
    private List<SearchChipModel> mSearchItemsList;
    private List<SearchChipModel> mPreSelectedFilters;

    public FacetItemsAdapter(List<String> data, SearchFilterMvpPresenter presenter, Set<Integer> selectedFacets, RecyclerView recyclerView,
                             List<SearchChipModel> preSelectedFilters) {
        mData = data;
        mPresenter = presenter;
        mSelectedFacets = selectedFacets;
        mRecyclerView = recyclerView;
        mPreSelectedFilters = preSelectedFilters;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_facet_items, parent, false);
        FacetItemsViewHolder vh = new FacetItemsViewHolder(view);

        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int currentPosition = vh.getAdapterPosition();

                if (currentPosition < 0 || currentPosition >= mData.size()) {
                    return;
                }

                switch (mFilterType) {
                    //single selection, allows unselection logic for sort type of facet.
                    case BundleKeys.SORT_FACETFILTER_NAME:
                        int index = -1;
                        if (!mSearchItemsList.isEmpty()) {
                            for (SearchChipModel chip : mSearchItemsList) {
                                if (chip.getFilterType()
                                        .equalsIgnoreCase(BundleKeys.SORT_FACETFILTER_NAME)) {
                                    index = getData().indexOf(chip.getChipTitle());
                                    break;
                                }
                            }
                            if (index >= 0) {
                                FacetItemsViewHolder oldVH = (FacetItemsViewHolder) mRecyclerView.findViewHolderForLayoutPosition(index);
                                if (oldVH != null) {
                                    oldVH.toggle();
                                    oldVH.itemView.setSelected(false);
                                }
                                removeChip(index);
                            }
                        }

                        if (index != currentPosition) {
                            vh.toggle();
                            vh.itemView.setSelected(true);
                            addChip(currentPosition);
                        }
                        break;

                    //regular logic for other facets(multi-selection/unselection)
                    default:
                        if (!vh.isSelected) {
                            vh.toggle();
                            vh.itemView.setSelected(true);
                            mSelectedFacets.add(currentPosition);
                            addChip(currentPosition);
                        } else {
                            vh.toggle();
                            vh.itemView.setSelected(false);
                            removeChip(currentPosition);
                        }
                        break;
                }
            }
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        FacetItemsViewHolder vh = (FacetItemsViewHolder) holder;
        vh.mFacetItemTitle.setText(mData.get(position));

        vh.isSelected = isFacetItemActive(position);
        vh.itemView.setSelected(vh.isSelected);
        vh.mFacetCheck.setVisibility(vh.isSelected ? View.VISIBLE : View.GONE);

    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        mData = null;
        mPresenter = null;
        mSelectedFacets = null;
        mRecyclerView = null;
        super.onDetachedFromRecyclerView(recyclerView);
    }

    public void setSearchItemsList(List<SearchChipModel> list) {
        mSearchItemsList = list;
    }

    private boolean isFacetItemActive(int position) {
        for (SearchChipModel chip : mSearchItemsList) {
            if (chip.getChipTitle().equals(mData.get(position))) {
                return true;
            }
        }

        if (mPreSelectedFilters != null && mPreSelectedFilters.size() != 0) {
            for (SearchChipModel chip : mPreSelectedFilters) {
                if (chip.getChipTitle().equals(mData.get(position))) {
                    return true;
                }
            }
        }
        return false;
    }

    private void addChip(int position) {
        SearchChipModel newChip = new SearchChipModel(mFilterType, mData.get(position), position);
        mSearchItemsList.add(newChip);
        mPresenter.onFacetItemClicked(mSearchItemsList);
    }

    private void removeChip(int position) {
        SearchChipModel chipToRemove = null;
        String chipTitle = getData().get(position);

        if (mPreSelectedFilters != null && mPreSelectedFilters.size() != 0) {
            for(Iterator<SearchChipModel> it = mPreSelectedFilters.iterator(); it.hasNext();) {
                SearchChipModel chip = it.next();
                if(chip.getChipTitle().equals(mData.get(position))) {
                    it.remove();
                }
            }
        }

        for (SearchChipModel chip : mSearchItemsList) {
            if (getFilterType() == BundleKeys.SORT_FACETFILTER_NAME && chip.getFilterType().equals(BundleKeys.SORT_FACETFILTER_NAME)
                    || chip.getChipTitle().equals(chipTitle)) {
                chipToRemove = chip;
                break;
            }
        }

        mSearchItemsList.remove(chipToRemove);
        mPresenter.onFacetItemClicked(mSearchItemsList);
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

        @BindView(R.id.viewholder_facet_item_title)
        TextView mFacetItemTitle;

        @BindView(R.id.viewholder_subcategory_check)
        ImageView mFacetCheck;

        boolean isSelected = false;

        public FacetItemsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void toggle() {
            isSelected = !isSelected;
            mFacetCheck.setVisibility(isSelected ? View.VISIBLE : View.GONE);
        }
    }
}
