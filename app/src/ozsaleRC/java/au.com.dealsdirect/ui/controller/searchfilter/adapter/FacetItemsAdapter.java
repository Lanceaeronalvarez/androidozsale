package au.com.dealsdirect.ui.controller.searchfilter.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

    public Set<Integer> getSelectedFacets() {
        return mSelectedFacets;
    }

    private Set<Integer> mSelectedFacets = new HashSet<Integer>();
    private String mFilterType = "";
    private int selectedPos = -1;
    private RecyclerView mRecyclerView;
    private List<SearchChipModel> mSearchItemsList;

    public FacetItemsAdapter(List<String> data, SearchFilterMvpPresenter presenter, Set<Integer> selectedFacets, RecyclerView recyclerView) {
        mData = data;
        mPresenter = presenter;
        mSelectedFacets = selectedFacets;
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
                    if (!vh.isSelected) {
                        if (selectedPos == -1) {
                            vh.toggle();
                            vh.itemView.setSelected(true);
                            mSelectedFacets.add(position);
                            addChip(position);
                            selectedPos = position;
                        } else {
                            if (selectedPos != position) {
                                FacetItemsViewHolder oldVH = (FacetItemsViewHolder) mRecyclerView.findViewHolderForLayoutPosition(selectedPos);
                                oldVH.toggle();
                                oldVH.itemView.setSelected(false);
                                mSelectedFacets.remove(selectedPos);
                                removeChip();

                                vh.toggle();
                                vh.itemView.setSelected(true);
                                mSelectedFacets.add(position);
                                addChip(position);
                                selectedPos = position;
                            }
                        }

                    } else {
                        if (selectedPos == position) {
                            vh.toggle();
                            vh.itemView.setSelected(false);
                            mSelectedFacets.remove(position);
                            removeChip();
                            selectedPos = -1;
                        }
                    }

                } else { //regular logic for other facets(multi-selection/unselection)

                    if (!vh.isSelected) {
                        vh.toggle();
                        vh.itemView.setSelected(true);
                        mSelectedFacets.add(position);
                    } else {
                        vh.toggle();
                        vh.itemView.setSelected(false);
                        mSelectedFacets.remove(position);
                    }

                    mPresenter.onFacetItemClicked(new HashSet<Integer>(mSelectedFacets));

                }
            }
        });

        applySelection(vh, position);
    }

    public void updateSelectedFacets(Set<Integer> selectedFacets) {
        mSelectedFacets = new HashSet<Integer>(selectedFacets);
    }

    public void applySelection(FacetItemsViewHolder vh, int position) {
        if (mSelectedFacets.contains(position)) {
            vh.isSelected = true;
            vh.itemView.setSelected(true);
            vh.mFacetCheck.setVisibility(View.VISIBLE);
        } else {
            vh.isSelected = false;
            vh.itemView.setSelected(false);
            vh.mFacetCheck.setVisibility(View.GONE);
        }
    }

    public void setSearchItemsList(List<SearchChipModel> list) {
        mSearchItemsList = list;
    }

    private void addChip(int position) {
        SearchChipModel newChip = new SearchChipModel(mFilterType, mData.get(position), position);
        mSearchItemsList.add(newChip);
    }

    private void removeChip() {
        SearchChipModel chipToRemove = null;
        for (SearchChipModel chip : mSearchItemsList) {
            if (chip.getFilterType().equals(BundleKeys.SORT_FACETFILTER_NAME)) {
                chipToRemove = chip;
            }
        }
        if (chipToRemove != null) {
            mSearchItemsList.remove(chipToRemove);
        }

    }

    public void clearSelectedFacets() {
        mSelectedFacets.clear();
    }


    public void replaceData(List<String> data) {
        mData = new ArrayList<>(data);
        notifyDataSetChanged();
    }

    public void setFilterType(String type) {
        mFilterType = type;
        if (mFilterType == BundleKeys.SORT_FACETFILTER_NAME) { //update selectedPos when coming back from saleitemslist
            List<Integer> tempList = new ArrayList<>(mSelectedFacets);
            if (!tempList.isEmpty()) {
                selectedPos = tempList.get(0);
            }
        }
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
