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
import java.util.Map;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.ui.controller.searchfilter.SearchFilterMvpPresenter;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * dp Created by Admin on 6/25/17.
 */

public class SubCategoriesAdapter extends RecyclerView.Adapter<SubCategoriesAdapter.SubCategoriesViewHolder> {

    private List<GetCategoryTreeResponse> mData = new ArrayList<>();
    private SearchFilterMvpPresenter mSearchFilterPresenter;
    private SubCategoriesAdapter mSubCategoryItemsAdapter;
    private Map<String, GetCategoryTreeResponse> mCategoryMap;
    private int mMarginRight;
    static GetCategoryTreeResponse mPreviousCategory;
    private String mChosenCategoryKey;
    private Context mContext;
    private RecyclerView.RecycledViewPool mViewPool;

    public SubCategoriesAdapter(
            Context context,
            String chosenCategorykey,
            List<GetCategoryTreeResponse> data,
            SearchFilterMvpPresenter searchFilterMvpPresenter,
            Map<String, GetCategoryTreeResponse> categoryMap,
            int marginRight) {


        mContext = context;
        mChosenCategoryKey = chosenCategorykey;
        mData = data;
        mCategoryMap = categoryMap;
        mSearchFilterPresenter = searchFilterMvpPresenter;
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

        GetCategoryTreeResponse categoryItem = mCategoryMap.get(mData.get(position).getKey());

        if (!mData.get(position).getName().isEmpty()) {
            holder.subCategoryTitle.setText(mData.get(position).getName());

            boolean isCategorySelected = categoryItem != null && categoryItem.isSelected();
            holder.subCategoryCheck.setVisibility(isCategorySelected ? View.VISIBLE : View.GONE);
            holder.itemView.setSelected(isCategorySelected);

            List<GetCategoryTreeResponse> subCategoryItems = mData.get(position).getChildren();

            if (subCategoryItems != null && !subCategoryItems.isEmpty()) {
                int marginRight = mMarginRight + (int) mContext.getResources().getDimension(R.dimen.margin_large);
                mSubCategoryItemsAdapter = new SubCategoriesAdapter(mContext, mChosenCategoryKey, subCategoryItems, mSearchFilterPresenter, mCategoryMap, marginRight);
                holder.subCategoryItemsRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.VERTICAL, false));
                holder.subCategoryItemsRecyclerView.swapAdapter(mSubCategoryItemsAdapter, true);

                holder.itemView.setActivated(false);
                holder.subCategoryItemsRecyclerView.setVisibility(View.VISIBLE);
            }

            holder.itemView.setOnClickListener(view -> {
                categoryItem.setSelected(!categoryItem.isSelected());
                mSearchFilterPresenter.selectCategory(categoryItem);
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
}