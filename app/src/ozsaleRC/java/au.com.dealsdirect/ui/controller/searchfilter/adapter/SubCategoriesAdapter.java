package au.com.dealsdirect.ui.controller.searchfilter.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * dp Created by Admin on 6/25/17.
 */

public class SubCategoriesAdapter extends RecyclerView.Adapter<SubCategoriesAdapter.SubCategoriesViewHolder> {

    private List<GetCategoryTreeResponse> mData = new ArrayList<>();
    private SubCategoriesAdapter mSubCategoryItemsAdapter;
    private int mMarginRight;
    static GetCategoryTreeResponse mPreviousCategory;
    private String mChosenCategoryKey;
    private Context mContext;
    private RecyclerView.RecycledViewPool mViewPool;

    private Set<String> mSelectedCategoryKeys;

    private OnClickCategoryListener mOnClickListener;

    public SubCategoriesAdapter(
            Context context,
            String chosenCategorykey,
            List<GetCategoryTreeResponse> data,
            int marginRight) {


        mContext = context;
        mChosenCategoryKey = chosenCategorykey;
        mData = data;
        mMarginRight = marginRight;
        mViewPool = new RecyclerView.RecycledViewPool();
    }

    @Override
    public SubCategoriesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_filter_subcategory, parent, false);
        SubCategoriesViewHolder viewHolder = new SubCategoriesViewHolder(view);
        viewHolder.subCategoryItemsRecyclerView.setRecycledViewPool(mViewPool);
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) viewHolder.subCategoryTitle.getLayoutParams();
        lp.setMargins(mMarginRight, 0, 0, 0);
        viewHolder.subCategoryTitle.setLayoutParams(lp);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SubCategoriesViewHolder holder, int position) {

        GetCategoryTreeResponse categoryItem = mData.get(position);

        if (!mData.get(position).getName().isEmpty()) {
            holder.subCategoryTitle.setText(mData.get(position).getName());

            boolean isCategorySelected = categoryItem != null && (categoryItem.isSelected() || categoryItem.getKey() == mChosenCategoryKey);
            if (mSelectedCategoryKeys != null) {
                isCategorySelected = mSelectedCategoryKeys.contains(categoryItem.getKey());
            }
            holder.subCategoryCheck.setVisibility(isCategorySelected ? View.VISIBLE : View.GONE);
            holder.itemView.setSelected(isCategorySelected);

            List<GetCategoryTreeResponse> subCategoryItems = categoryItem.getChildren();

            if (subCategoryItems != null && !subCategoryItems.isEmpty()) {
                int marginRight = mMarginRight + (int) mContext.getResources().getDimension(R.dimen.margin_large);
                mSubCategoryItemsAdapter = new SubCategoriesAdapter(mContext, mChosenCategoryKey, subCategoryItems, marginRight);
                mSubCategoryItemsAdapter.setOnClickListener(mOnClickListener);
                mSubCategoryItemsAdapter.setSelectedCategories(mSelectedCategoryKeys);
                holder.subCategoryItemsRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.VERTICAL, false));
                holder.subCategoryItemsRecyclerView.swapAdapter(mSubCategoryItemsAdapter, true);

                holder.itemView.setActivated(false);
                holder.subCategoryItemsRecyclerView.setVisibility(View.VISIBLE);
            } else {
                holder.subCategoryItemsRecyclerView.setVisibility(View.GONE);
                holder.subCategoryItemsRecyclerView.setLayoutManager(null);
                holder.subCategoryItemsRecyclerView.swapAdapter(null, true);
            }

            holder.itemView.setOnClickListener(view -> {
                if (mOnClickListener != null) {
                    mOnClickListener.onClick(categoryItem.getKey());
                }
            });

        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    public void replaceData(List<GetCategoryTreeResponse> getCategoryTreeResponses) {
        mData = new ArrayList<>(getCategoryTreeResponses);
        notifyDataSetChanged();
    }

    static class SubCategoriesViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.viewholder_subcategory_title)
        TextView subCategoryTitle;

        @BindView(R.id.viewholder_subcategory_items_recyclerview)
        RecyclerView subCategoryItemsRecyclerView;

        @BindView(R.id.viewholder_subcategory_check)
        ImageView subCategoryCheck;

        SubCategoriesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void setOnClickListener(OnClickCategoryListener listener) {
        mOnClickListener = listener;
    }

    public void setSelectedCategories(Set<String> selectedCategoryKeys) {
        mSelectedCategoryKeys = selectedCategoryKeys;
        notifyDataSetChanged();
    }

    public interface OnClickCategoryListener {
        void onClick(String categoryItemKey);
    }
}