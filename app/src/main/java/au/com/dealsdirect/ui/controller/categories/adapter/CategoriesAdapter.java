package au.com.dealsdirect.ui.controller.categories.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.ui.controller.categories.CategoriesMvpPresenter;
import au.com.dealsdirect.ui.controller.categories.listener.CategoryClickListener;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * jp Created by smartwave on 08/06/2017.
 */

public class CategoriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<GetCategoryTreeResponse> mData = new ArrayList<>();
    private CategoriesMvpPresenter mPresenter;
    private CategoryClickListener mCategoryAdapterClickListener;
    private int mLastPosition = -1;
    private int mLastSelectedCategory = 0;
    private ImageView mLastSelectedViewHolderImageView = null;

    public CategoriesAdapter(
            List<GetCategoryTreeResponse> data,
            CategoriesMvpPresenter presenter,
            CategoryClickListener categoryClickListener) {

        mData = data;
        mPresenter = presenter;
        mCategoryAdapterClickListener = categoryClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_sales_category, parent, false);
        CategoriesViewHolder vh = new CategoriesViewHolder(view, mPresenter);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        setAnimation(holder.itemView, position);

        if (!mData.isEmpty()) {
            if (!mData.get(position).getName().isEmpty()) {
                char first = mData.get(position).getName().charAt(0);
                ((CategoriesViewHolder) holder).categoryIndicator.setText(String.valueOf(first));
            }

            if (position==1){
                mCategoryAdapterClickListener.onCategoryClicked(position,mData.get(position));
                mLastSelectedViewHolderImageView = ((CategoriesViewHolder) holder).categoryTitleBackground;
                mLastSelectedViewHolderImageView.setBackgroundDrawable(
                        holder.itemView.getContext().getResources()
                                .getDrawable(R.drawable.bg_category_item_active));
            }

            ((CategoriesViewHolder) holder).categoryText.setText(mData.get(position).getName());
            ((CategoriesViewHolder) holder).itemView.setOnClickListener(view -> {

                if (mLastSelectedViewHolderImageView == null) {

                    if (position!=0){
                        mLastSelectedViewHolderImageView = ((CategoriesViewHolder) holder).categoryTitleBackground;
                        mLastSelectedViewHolderImageView.setBackgroundDrawable(
                                holder.itemView.getContext().getResources()
                                        .getDrawable(R.drawable.bg_category_item_active));
                    }
                } else {

                    if (position!=0){
                        mLastSelectedViewHolderImageView.setBackgroundDrawable(
                                holder.itemView.getContext().getResources()
                                        .getDrawable(R.drawable.bg_category_item_inactive));

                        mLastSelectedViewHolderImageView = ((CategoriesViewHolder) holder).categoryTitleBackground;

                        mLastSelectedViewHolderImageView.setBackgroundDrawable(
                                holder.itemView.getContext().getResources()
                                        .getDrawable(R.drawable.bg_category_item_active));
                    }else{

                        mLastSelectedViewHolderImageView.setBackgroundDrawable(
                                holder.itemView.getContext().getResources()
                                        .getDrawable(R.drawable.bg_category_item_inactive));

                    }

                }

                mCategoryAdapterClickListener.onCategoryClicked(
                        position,
                        mData.get(position));
            });

        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }


    @Override
    public int getItemCount() {
        if(mData!=null)
            return mData.size();
        return 0;
    }

    public void replaceData(List<GetCategoryTreeResponse> getCategoryTreeResponses) {
        mData = new ArrayList<>(getCategoryTreeResponses);
        notifyDataSetChanged();
    }

    public static class CategoriesViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.row_category_image)
        public ImageView categoryTitleBackground;

        @BindView(R.id.row_category_name)
        public TextView categoryText;

        @BindView(R.id.row_category_indicator)
        public TextView categoryIndicator;

        public CategoriesMvpPresenter mPresenter;

        public CategoriesViewHolder(View itemView, CategoriesMvpPresenter presenter) {
            super(itemView);
            mPresenter = presenter;
            ButterKnife.bind(this, itemView);

        }


    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > mLastPosition) {
            Animation animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            mLastPosition = position;
        }
    }

    public GetCategoryTreeResponse getItem(int position) {
        return mData.get(position);
    }

}
