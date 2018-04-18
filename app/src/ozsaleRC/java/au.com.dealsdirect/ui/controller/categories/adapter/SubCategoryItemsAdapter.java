package au.com.dealsdirect.ui.controller.categories.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.ui.controller.categories.CategoriesMvpPresenter;
import au.com.dealsdirect.ui.controller.categories.listener.SubCategoryItemClickListener;
import au.com.dealsdirect.ui.main.MainActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * dp Created by Admin on 6/25/17.
 */

public class SubCategoryItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<GetCategoryTreeResponse> mData = new ArrayList<>();
    private CategoriesMvpPresenter mPresenter;
    private SubCategoryItemClickListener mCategoryAdapterClickListener;
    private int lastPosition = -1;
    private boolean mAnimateInsert = true;
    private boolean mIsResetSubCategories;

    private RecyclerView.ViewHolder mLastSelectedViewHolder = null;

    public SubCategoryItemsAdapter(
            List<GetCategoryTreeResponse> data,
            CategoriesMvpPresenter presenter,
            SubCategoryItemClickListener subCategoryItemClickListener,
            boolean animateInsert,
            boolean isResetSubCategories) {

        mData = data;
        mPresenter = presenter;
        mCategoryAdapterClickListener = subCategoryItemClickListener;
        mAnimateInsert = animateInsert;
        mIsResetSubCategories = isResetSubCategories;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_subcategory_item, parent, false);
        SubCategoryItemViewHolder vh = new SubCategoryItemViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        SubCategoryItemViewHolder vh = (SubCategoryItemViewHolder) holder;
        MainActivity activity = ((MainActivity) vh.itemView.getContext());
        setAnimation(holder.itemView, position);

        if (!mData.isEmpty()) {

            vh.itemView.setActivated(false);
            vh.subCategoryTitle.setText(mData.get(position).getName());

            vh.itemView.setOnClickListener(view -> {
                View subCategoryItem = activity.getMainController().getSelectedSubCategoryItem();
                if (subCategoryItem!=null){
                    subCategoryItem.setActivated(false);
                }

                if (mLastSelectedViewHolder == null) {
                    mLastSelectedViewHolder = vh;
                    activity.getMainController().setSelectedSubCategoryItem(vh.itemView);
                    vh.itemView.setActivated(true);
                } else {

                    mLastSelectedViewHolder.itemView.setActivated(false);
                    mLastSelectedViewHolder = vh;
                    activity.getMainController().setSelectedSubCategoryItem(vh.itemView);
                    mLastSelectedViewHolder.itemView.setActivated(true);
                }
                mCategoryAdapterClickListener.onSubCategoryItemClicked(
                        mData.get(position).getId(),
                        mData.get(position).getName(),
                        mData.get(position).getKey());
            });
        }
    }

    @Override
    public int getItemCount() {
        return mData!=null ? mData.size(): 0;
    }
    
    public void replaceData(List<GetCategoryTreeResponse> getCategoryTreeResponses) {
        mData = new ArrayList<>(getCategoryTreeResponses);
        notifyDataSetChanged();
    }

    static class SubCategoryItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.viewholder_subcategory_item_name)
        TextView subCategoryTitle;

        boolean isSelected = false;

        public SubCategoryItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

        public void toggle(){
            isSelected = !isSelected;
        }
    }


    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition && mAnimateInsert) {
            Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), R.anim.slide_to_bottom);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }


    public void addItem(GetCategoryTreeResponse getCategoryTreeResponse){
        mData.add(getCategoryTreeResponse);
        notifyDataSetChanged();
    }
}
