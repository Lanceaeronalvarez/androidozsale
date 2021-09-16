package au.com.dealsdirect.ui.controller.categories.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.ui.controller.categories.listener.SaleCategoryClickListener;
import au.com.dealsdirect.ui.controller.categories.listener.SubCategoryItemClickListener;
import au.com.dealsdirect.ui.controller.main.MainController;
import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.utils.AppLogger;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * dp Created by Admin on 6/25/17.
 */

public class SubSaleCategoryAdapter extends RecyclerView.Adapter<SubSaleCategoryAdapter.SubCategoriesViewHolder> {

    private List<GetCategoryTreeResponse> mData;
    private SubCategoryItemClickListener mSubCategoryItemClickListener;
    private SaleCategoryClickListener mCategoryAdapterClickListener;
    private Map<String, List<GetCategoryTreeResponse>> mCategoryMap;
    private boolean mAnimateInsert = true;
    private Context mContext;

    private int parentPosition = -1;

    public SubSaleCategoryAdapter(Context context,
                                  List<GetCategoryTreeResponse> data,
                                  SubCategoryItemClickListener subCategoryItemClickListener,
                                  SaleCategoryClickListener categoryClickListener,
                                  Map<String, List<GetCategoryTreeResponse>> categoryMap) {

        mContext = context;
        mData = data;
        mSubCategoryItemClickListener = subCategoryItemClickListener;
        mCategoryMap = categoryMap;
        mCategoryAdapterClickListener = categoryClickListener;
    }

    public int getParentPosition() {
        return parentPosition;
    }

    public void setParentPosition(int parentPosition) {
        this.parentPosition = parentPosition;
    }

    @Override
    public SubCategoriesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (mContext.getResources().getBoolean(R.bool.should_use_old_category_layout)) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_subcategory, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_salesubcategory, parent, false);
        }

        return new SubCategoriesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SubCategoriesViewHolder holder, int position) {
        holder.setParentPosition(parentPosition);
        if (mData != null && !mData.isEmpty() && !mData.get(position).getName().equals("empty")) {
            holder.subCategoryTitle.setText(mData.get(position).getName());
            holder.subCategoryBorder.setVisibility(mContext.getResources().getBoolean(R.bool.should_use_old_category_layout) ?
                    View.VISIBLE : View.GONE);

            MainController mainController = ((MainActivity) mContext).getMainController();
            List<GetCategoryTreeResponse> subCategoryItems = getSubCategoryItems(mData.get(position).getKey());

            if (subCategoryItems.size() == 1 && subCategoryItems.get(0).getName().equalsIgnoreCase("All")) {
                holder.subCategoryItemsRecyclerView.setVisibility(View.GONE);
                holder.subCategoryCheckImageView.setVisibility(View.GONE);

                holder.itemView.setOnClickListener(v -> {

                    mSubCategoryItemClickListener.onSubCategoryItemClicked(subCategoryItems.get(0).getKey(), subCategoryItems.get(0).getName(),
                            subCategoryItems.get(0).getKey(), new ArrayList<>());

                    if (mainController.getSelectedSubCategoryItem() != null) {
                        mainController.getSelectedSubCategoryItem().setActivated(false);
                    }

                    mainController.setChosenCategoryItemKey(mData.get(position).getKey());

                    holder.subCategoryCheckImageView.setVisibility(View.VISIBLE);

                    View previousItemView = mainController.getSelectedSubCategoryItem();
                    if (previousItemView != null) {
                        SubCategoryItemsAdapter.SubCategoryItemViewHolder subCategoryItemViewHolder = new SubCategoryItemsAdapter.SubCategoryItemViewHolder(previousItemView);
                        subCategoryItemViewHolder.subCategoryCheck.setVisibility(View.GONE);
                    }

                    View lastItemView = mainController.getPreviousSubcategoryItem();
                    if (lastItemView != null && !lastItemView.equals(holder.itemView)) {
                        SubCategoriesViewHolder subCategoryItemViewHolder = new SubCategoriesViewHolder(lastItemView);
                        subCategoryItemViewHolder.subCategoryCheckImageView.setVisibility(View.INVISIBLE);
                    }

                    mainController.setPreviousSubcategoryItem(holder.itemView);


                });

            } else if (!subCategoryItems.isEmpty()) {
                holder.subCategoryItemsRecyclerView.setVisibility(View.GONE);
                holder.subCategoryCheckImageView.setVisibility(View.GONE);

                holder.itemView.setOnClickListener(view -> {
                    boolean isItemViewActivated = holder.itemView.isActivated();

                    holder.itemView.setActivated(!isItemViewActivated);
                    holder.subCategoryItemsBorder.setVisibility(isItemViewActivated ? View.GONE : View.VISIBLE);
                    holder.subCategoryItemsRecyclerView.setVisibility(isItemViewActivated ? View.GONE : View.VISIBLE);
                    holder.subCategoryCheckImageView.setVisibility(!(mData.get(position).getChildren().size() > 0) && isItemViewActivated ? View.VISIBLE : View.GONE);
                    boolean bool = !(mData.get(position).getChildren().size() > 0) && isItemViewActivated;
                    AppLogger.d("boolean: " + bool + " haschildren " + !(mData.get(position).getChildren().size() > 0) + " isItemActivated: " + isItemViewActivated);

                    if (!isItemViewActivated) {
                        if (mContext.getResources().getBoolean(R.bool.should_use_old_category_layout)) {
                            SubCategoryItemsAdapter subCategoryItemsAdapter = new SubCategoryItemsAdapter(mContext, subCategoryItems, mSubCategoryItemClickListener, mAnimateInsert,
                                    mData.get(position).getLinkOptions());
                            holder.subCategoryItemsRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.VERTICAL, false));
                            holder.subCategoryItemsRecyclerView.setMotionEventSplittingEnabled(false);
                            holder.subCategoryItemsRecyclerView.setAdapter(subCategoryItemsAdapter);
                        } else {
                            mCategoryAdapterClickListener.onSubCategoryClicked(position, holder, mData.get(position));
                        }
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

    public static class SubCategoriesViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.viewholder_subcategory_container)
        FrameLayout subcategoryContainer;

        @BindView(R.id.viewholder_subcategory_title)
        TextView subCategoryTitle;

        @BindView(R.id.viewholder_subcategory_items_recyclerview)
        public RecyclerView subCategoryItemsRecyclerView;

        @BindView(R.id.viewholder_subcategory_border)
        View subCategoryBorder;

        @BindView(R.id.viewholder_subcategoryitems_border)
        View subCategoryItemsBorder;

        @BindView(R.id.viewholder_subcategory_check)
        ImageView subCategoryCheckImageView;

        private int parentPosition = -1;

        SubCategoriesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public int getParentPosition() {
            return parentPosition;
        }

        public void setParentPosition(int parentPosition) {
            this.parentPosition = parentPosition;
        }
    }

    private List<GetCategoryTreeResponse> getSubCategoryItems(String categoryKey) {
        return mCategoryMap.get(categoryKey);
    }

    public void animateInsertItems(boolean animateInsert) {
        mAnimateInsert = animateInsert;
    }

}
