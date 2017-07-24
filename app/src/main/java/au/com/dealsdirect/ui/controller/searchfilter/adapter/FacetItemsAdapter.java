package au.com.dealsdirect.ui.controller.searchfilter.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
    private OnSelectListener mOnSelectListener;
    private String mFilterType = "";

    public static interface OnSelectListener {
        void onSelected(Set<Integer> selectPosSet);
    }

    public FacetItemsAdapter(List<String> data, SearchFilterMvpPresenter presenter, Set<Integer> selectedFacets) {
        mData = data;
        mPresenter = presenter;
        mSelectedFacets = selectedFacets;
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
                if (!vh.isSelected) {
                    vh.toggle();
                    vh.itemView.setSelected(true);
                    mSelectedFacets.add(position);
                } else {
                    vh.toggle();
                    vh.itemView.setSelected(false);
                    mSelectedFacets.remove(position);
                }

                if (mOnSelectListener != null) {
                    mOnSelectListener.onSelected(new HashSet<Integer>(mSelectedFacets));
                }
            }
        });

        applySelection(vh,position);
    }

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        mOnSelectListener = onSelectListener;
    }

    public void updateSelectedFacets(Set<Integer> selectedFacets){
        mSelectedFacets = new HashSet<Integer>(selectedFacets);
    }

    public void applySelection(FacetItemsViewHolder vh, int position){
        if(mSelectedFacets.contains(position)){
            vh.isSelected = true;
            vh.itemView.setSelected(true);
        } else {
            vh.isSelected = false;
            vh.itemView.setSelected(false);
        }
    }

    public void clearSelectedFacets(){
        mSelectedFacets.clear();
    }


    public void replaceData(List<String> data) {
        mData = new ArrayList<>(data);
        notifyDataSetChanged();
    }

    public void setFilterType(String type){
        mFilterType = type;
    }

    public String getFilterType(){
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
