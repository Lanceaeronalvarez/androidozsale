package au.com.dealsdirect.ui.controller.searchfilter.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
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

    private List<TransformedNode> mData;
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
        mMarginRight = marginRight;
        mViewPool = new RecyclerView.RecycledViewPool();
        replaceData(data);
    }

    @Override
    public SubCategoriesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_filter_subcategory, parent, false);
        SubCategoriesViewHolder viewHolder = new SubCategoriesViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SubCategoriesViewHolder holder, int position) {

        TransformedNode node = mData.get(position);

        GetCategoryTreeResponse categoryItem = node.getObject();

        if (!categoryItem.getName().isEmpty()) {
            holder.subCategoryTitle.setText(categoryItem.getName());

            boolean isCategorySelected = (categoryItem.isSelected() || categoryItem.getKey().equals(mChosenCategoryKey));
            if (mSelectedCategoryKeys != null) {
                isCategorySelected = mSelectedCategoryKeys.contains(categoryItem.getKey());
            }
            holder.subCategoryCheck.setVisibility(isCategorySelected ? View.VISIBLE : View.GONE);
            holder.itemView.setSelected(isCategorySelected);

            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) holder.subCategoryTitle.getLayoutParams();
            int marginRight = mMarginRight * node.getLevel();
            lp.setMargins(marginRight, 0, 0, 0);
            holder.subCategoryTitle.setLayoutParams(lp);

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
        mData = transformData(getCategoryTreeResponses);
        notifyDataSetChanged();
    }

    static class SubCategoriesViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.viewholder_subcategory_title)
        TextView subCategoryTitle;

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

    private class TransformedNode {
        private GetCategoryTreeResponse mObject;
        private int mLevel;

        TransformedNode(GetCategoryTreeResponse object, int level) {
            mObject = object;
            mLevel = level;
        }

        int getLevel() {
            return mLevel;
        }

        GetCategoryTreeResponse getObject() {
            return mObject;
        }
    }

    private List<TransformedNode> transformData(List<GetCategoryTreeResponse> data) {
        ArrayList<TransformedNode> list = new ArrayList<>();
        for (GetCategoryTreeResponse node: data) {
            node.traverseTree(new GetCategoryTreeResponse.TreeTraversalBlock() {
                @Override
                public boolean execute(GetCategoryTreeResponse parent, Object option) {
                    int level = ((Integer) option).intValue();
                    list.add(new TransformedNode(parent, Integer.valueOf(level)));
                    return true;
                }

                @Override
                public Object transformOption(GetCategoryTreeResponse parent, Object option) {
                    return Integer.valueOf(((Integer) option).intValue() + 1);
                }
            }, Integer.valueOf(0));
        }
        return list;
    }
}