package au.com.dealsdirect.ui.controller.categories.adapter;

import android.content.Context;
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

    public SubCategoryItemsAdapter(
            List<GetCategoryTreeResponse> data,
            CategoriesMvpPresenter presenter,
            SubCategoryItemClickListener subCategoryItemClickListener) {

        mData = data;
        mPresenter = presenter;
        mCategoryAdapterClickListener = subCategoryItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_subcategory_item, parent, false);
        SubCategoryItemsAdapter.SubCategoryItemViewHolder vh
                = new SubCategoryItemsAdapter.SubCategoryItemViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        setAnimation(holder.itemView, position);
        Context context = holder.itemView.getContext();

        if (!mData.isEmpty()) {

            if (!mData.get(position).getName().isEmpty()) {

                ((SubCategoryItemsAdapter.SubCategoryItemViewHolder) holder)
                        .subCategoryTitle.setText(mData.get(position).getName());

                String chosenKey =((MainActivity)context).getMainController().getChosenCategoryItemKey();

                if (mData.get(position).getKey().equals(chosenKey)){

                    ((SubCategoryItemsAdapter.SubCategoryItemViewHolder) holder)
                            .subCategoryTitle.setTextColor(context.getResources().getColor(R.color.category_text_active));
                }
            }

            ((SubCategoryItemViewHolder) holder).subCategoryTitle.setOnClickListener(view -> {

                ((SubCategoryItemViewHolder) holder).subCategoryTitle.setEnabled(true);
                mCategoryAdapterClickListener.onSubCategoryItemClicked(
                        mData.get(position).getId(),
                        mData.get(position).getName(),
                        mData.get(position).getKey());
            });
        }
    }

    @Override
    public int getItemCount() {
        if(mData!=null)
            return mData.size();
        return 0;
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
//        Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.slide_to_top);
//        holder.itemView.startAnimation(animation);
    }

    public void replaceData(List<GetCategoryTreeResponse> getCategoryTreeResponses) {
        mData = new ArrayList<>(getCategoryTreeResponses);
        notifyDataSetChanged();
    }

    static class SubCategoryItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.viewholder_subcategory_item_name)
        TextView subCategoryTitle;

        public SubCategoryItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }


    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
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
