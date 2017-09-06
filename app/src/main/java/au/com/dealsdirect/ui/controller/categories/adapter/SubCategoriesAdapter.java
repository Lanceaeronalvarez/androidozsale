package au.com.dealsdirect.ui.controller.categories.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.ui.controller.categories.CategoriesMvpPresenter;
import au.com.dealsdirect.ui.controller.categories.listener.SubCategoryClickListener;
import au.com.dealsdirect.ui.controller.categories.listener.SubCategoryItemClickListener;
import au.com.dealsdirect.ui.main.MainActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * dp Created by Admin on 6/25/17.
 */

public class SubCategoriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<GetCategoryTreeResponse> mData = new ArrayList<>();
    private CategoriesMvpPresenter mPresenter;
    private SubCategoryClickListener mSubCategoryAdapterClickListener;
    private SubCategoryItemClickListener mSubCategoryItemClickListener;
    private SubCategoryItemsAdapter mSubCategoryItemsAdapter;
    private Map<String, List<GetCategoryTreeResponse>> mCategoryMap = new HashMap<>();
    private boolean mAnimateInsert = true;
    private boolean mIsResetSubCategories = false;

    public SubCategoriesAdapter(
            List<GetCategoryTreeResponse> data,
            CategoriesMvpPresenter presenter,
            SubCategoryItemClickListener subCategoryItemClickListener,
            Map<String, List<GetCategoryTreeResponse>> categoryMap) {

        mData = data;
        mPresenter = presenter;
        mSubCategoryItemClickListener = subCategoryItemClickListener;
        mCategoryMap = categoryMap;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_subcategory, parent, false);
        return new SubCategoriesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (!mData.isEmpty()) {
            if (!mData.get(position).getName().equals("empty")) {
                if (!mData.get(position).getName().isEmpty()) {

                    ((SubCategoriesViewHolder) holder)
                            .subCategoryTitle.setText(mData.get(position).getName());
                }

                Context context = holder.itemView.getContext();
                String chosenKey = ((MainActivity) context).getMainController().getChosenCategoryItemKey();

                List<GetCategoryTreeResponse> subCategoryItems = getSubCategoryItems(mData.get(position).getKey());

                if (subCategoryItems.size() == 1 && subCategoryItems.get(0).getName().equalsIgnoreCase("All")) {

                    if (mData.get(position).getKey().equals(chosenKey)) {

                        ((SubCategoriesViewHolder) holder)
                                .subCategoryTitle.setTextColor(context.getResources().getColor(R.color.category_text_active));
                    }else{
                        ((SubCategoriesViewHolder) holder)
                                .subCategoryTitle.setTextColor(context.getResources().getColor(R.color.category_text_inactive));
                    }

                    ((SubCategoriesViewHolder) holder).subCategoryItemsRecyclerView.setVisibility(View.GONE);
                    ((SubCategoriesViewHolder) holder).subCategoryTitle.setOnClickListener(v -> {

                        GetCategoryTreeResponse item = subCategoryItems.get(0);
                        mSubCategoryItemClickListener.onSubCategoryItemClicked(item.getKey(), item.getName(), item.getKey());
                        ((MainActivity) context).getMainController().getSelectedSubCategoryItem().setActivated(false);
                        ((MainActivity) context).getMainController().setChosenCategoryItemKey(mData.get(position).getKey());
                    });
                    return;
                } else {
                    mSubCategoryItemsAdapter = new SubCategoryItemsAdapter(subCategoryItems, mPresenter, mSubCategoryItemClickListener, mAnimateInsert, mIsResetSubCategories);
                    ((SubCategoriesViewHolder) holder).subCategoryItemsRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.VERTICAL, false));
                    ((SubCategoriesViewHolder) holder).subCategoryItemsRecyclerView.setAdapter(mSubCategoryItemsAdapter);
                }

                if (!subCategoryItems.isEmpty()) {
                    holder.itemView.setActivated(false);
                    ((SubCategoriesViewHolder) holder).subCategoryItemsRecyclerView.setVisibility(View.GONE);

                    ((SubCategoriesViewHolder) holder).mViewholder_subcategory_container
                            .setOnClickListener(view -> {

                                if (holder.itemView.isActivated()) {
                                    holder.itemView.setActivated(false);
                                    ((SubCategoriesViewHolder) holder)
                                            .subCategoryItemsRecyclerView.setVisibility(View.GONE);
                                } else {
                                    holder.itemView.setActivated(true);
                                    ((SubCategoriesViewHolder) holder)
                                            .subCategoryItemsRecyclerView.setVisibility(View.VISIBLE);

                                    mSubCategoryItemsAdapter = new SubCategoryItemsAdapter(subCategoryItems, mPresenter, mSubCategoryItemClickListener, mAnimateInsert, mIsResetSubCategories);
                                    ((SubCategoriesViewHolder) holder).subCategoryItemsRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.VERTICAL, false));
                                    ((SubCategoriesViewHolder) holder).subCategoryItemsRecyclerView.setAdapter(mSubCategoryItemsAdapter);
                                }

                            });
                }
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if (mData != null)
            return mData.size();
        return 0;
    }

    public void replaceData(List<GetCategoryTreeResponse> getCategoryTreeResponses) {
        mData = new ArrayList<>(getCategoryTreeResponses);
        notifyDataSetChanged();
    }

    static class SubCategoriesViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.viewholder_subcategory_container)
        RelativeLayout mViewholder_subcategory_container;

        @BindView(R.id.viewholder_subcategory_title)
        TextView subCategoryTitle;

        @BindView(R.id.viewholder_subcategory_items_recyclerview)
        RecyclerView subCategoryItemsRecyclerView;

        SubCategoriesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener((v) -> {
                //do public sales banner api call
            });
        }
    }

    private List<GetCategoryTreeResponse> getSubCategoryItems(String categoryKey) {
        return mCategoryMap.get(categoryKey);
    }

    public void animateInsertItems(boolean animateInsert) {
        mAnimateInsert = animateInsert;
    }

    public void isResetSubCategories(boolean isResetSubCategories) {
        mIsResetSubCategories = isResetSubCategories;
    }

}
