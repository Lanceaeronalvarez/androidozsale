package au.com.dealsdirect.ui.controller.categories.adapter;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.ui.controller.categories.CategoriesMvpPresenter;
import au.com.dealsdirect.ui.controller.categories.listener.SubCategoryClickListener;
import au.com.dealsdirect.ui.controller.categories.listener.SubCategoryItemClickListener;
import au.com.dealsdirect.ui.controller.main.MainController;
import au.com.dealsdirect.ui.main.MainActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * dp Created by Admin on 6/25/17.
 */

public class SubCategoriesAdapter extends RecyclerView.Adapter<SubCategoriesAdapter.SubCategoriesViewHolder> {

    private List<GetCategoryTreeResponse> mData = new ArrayList<>();
    private CategoriesMvpPresenter mPresenter;
    private SubCategoryItemClickListener mSubCategoryItemClickListener;
    private SubCategoryItemsAdapter mSubCategoryItemsAdapter;
    private Map<String, List<GetCategoryTreeResponse>> mCategoryMap = new HashMap<>();
    private boolean mAnimateInsert = true;
    private Context mContext;

    public SubCategoriesAdapter(Context context,
                                List<GetCategoryTreeResponse> data,
                                CategoriesMvpPresenter presenter,
                                SubCategoryItemClickListener subCategoryItemClickListener,
                                Map<String, List<GetCategoryTreeResponse>> categoryMap) {

        mContext = context;
        mData = data;
        mPresenter = presenter;
        mSubCategoryItemClickListener = subCategoryItemClickListener;
        mCategoryMap = categoryMap;
    }

    @Override
    public SubCategoriesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_subcategory, parent, false);
        return new SubCategoriesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SubCategoriesViewHolder holder, int position) {
        if (!mData.isEmpty() && !mData.get(position).getName().equals("empty")) {
            holder.subCategoryTitle.setText(mData.get(position).getName());
            holder.subCategoryBorder.setVisibility(View.VISIBLE);

            MainController mainController = ((MainActivity) mContext).getMainController();
            List<GetCategoryTreeResponse> subCategoryItems = getSubCategoryItems(mData.get(position).getKey());

            if (subCategoryItems.size() == 1 && subCategoryItems.get(0).getName().equalsIgnoreCase("All")) {
                holder.subCategoryItemsRecyclerView.setVisibility(View.GONE);
                holder.subCategoryCheckImageView.setVisibility(View.GONE);

                holder.subCategoryTitle.setOnClickListener(v -> {
                    mSubCategoryItemClickListener.onSubCategoryItemClicked(subCategoryItems.get(0).getKey(), subCategoryItems.get(0).getName(), subCategoryItems.get(0).getKey());

                    if (mainController.getSelectedSubCategoryItem() != null) {
                        mainController.getSelectedSubCategoryItem().setActivated(false);
                    }

                    mainController.setChosenCategoryItemKey(mData.get(position).getKey());
                });

            } else if (!subCategoryItems.isEmpty()) {
                holder.subCategoryItemsRecyclerView.setVisibility(View.GONE);
                holder.subCategoryCheckImageView.setVisibility(View.GONE);

                holder.subcategoryContainer.setOnClickListener(view -> {
                    boolean isItemViewActivated = holder.itemView.isActivated();

                    holder.itemView.setActivated(!isItemViewActivated);
                    holder.subCategoryItemsBorder.setVisibility(isItemViewActivated ? View.GONE : View.VISIBLE);
                    holder.subCategoryItemsRecyclerView.setVisibility(isItemViewActivated ? View.GONE : View.VISIBLE);
                    holder.subCategoryCheckImageView.setVisibility(isItemViewActivated ? View.GONE : View.VISIBLE);

                    if (!isItemViewActivated) {
                        mSubCategoryItemsAdapter = new SubCategoryItemsAdapter(subCategoryItems, mSubCategoryItemClickListener, mAnimateInsert);
                        holder.subCategoryItemsRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.VERTICAL, false));
                        holder.subCategoryItemsRecyclerView.setMotionEventSplittingEnabled(false);
                        holder.subCategoryItemsRecyclerView.setAdapter(mSubCategoryItemsAdapter);
                    }
                });
            }
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

        @BindView(R.id.viewholder_subcategory_container)
        FrameLayout subcategoryContainer;

        @BindView(R.id.viewholder_subcategory_title)
        TextView subCategoryTitle;

        @BindView(R.id.viewholder_subcategory_items_recyclerview)
        RecyclerView subCategoryItemsRecyclerView;

        @BindView(R.id.viewholder_subcategory_border)
        View subCategoryBorder;

        @BindView(R.id.viewholder_subcategoryitems_border)
        View subCategoryItemsBorder;

        @BindView(R.id.viewholder_subcategory_check)
        ImageView subCategoryCheckImageView;

        SubCategoriesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private List<GetCategoryTreeResponse> getSubCategoryItems(String categoryKey) {
        return mCategoryMap.get(categoryKey);
    }

    public void animateInsertItems(boolean animateInsert) {
        mAnimateInsert = animateInsert;
    }

}
