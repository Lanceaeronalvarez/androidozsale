package au.com.dealsdirect.ui.controller.categories.adapter;

import android.content.Context;
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
import au.com.dealsdirect.utils.StringUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * jp Created by smartwave on 08/06/2017.
 */

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder> {

    private static final String CATEGORY_HOME = "Home";
    private static final String CATEGORY_WOMEN = "Women";
    private static final String CATEGORY_MEN = "Men";
    private static final String CATEGORY_KIDS_TOYS = "Kids & Toys";
    private static final String CATEGORY_BEAUTY = "Beauty";
    private static final String CATEGORY_SPORTS = "Sports";
    private static final String CATEGORY_TECH = "Tech";

    private List<GetCategoryTreeResponse> mData = new ArrayList<>();
    private CategoriesMvpPresenter mPresenter;
    private CategoryClickListener mCategoryAdapterClickListener;
    private int mLastPosition = -1;
    private Context mContext;
    private int mSelectedIndex;

    public CategoriesAdapter(Context context,
                             List<GetCategoryTreeResponse> data,
                             CategoriesMvpPresenter presenter,
                             CategoryClickListener categoryClickListener) {

        mContext = context;
        mData = data;
        mPresenter = presenter;
        mCategoryAdapterClickListener = categoryClickListener;
    }

    @Override
    public CategoriesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_sales_category, parent, false);
        CategoriesViewHolder vh = new CategoriesViewHolder(view, mPresenter);
        return vh;
    }

    @Override
    public void onBindViewHolder(CategoriesViewHolder holder, int position) {
        setAnimation(holder.itemView, position);

//        Sets Category Image might be used in the future
//        if (!mData.isEmpty() && !mData.get(position).getName().isEmpty()) {
//            setCategoryImage(holder, mData.get(position), mSelectedIndex == position);
//        }

        int backgroundColor = mSelectedIndex == position ? R.color.colorAccent : R.color.white;
        int textColor = mSelectedIndex == position ? R.color.white : R.color.colorAccent;

        holder.itemView.setBackgroundColor(mContext.getResources().getColor(backgroundColor));
        holder.categoryText.setTextColor(mContext.getResources().getColor(textColor));
        holder.categoryText.setText(mData.get(position).getName());

        holder.itemView.setOnClickListener(view -> {
            mSelectedIndex = position;
            mCategoryAdapterClickListener.onCategoryClicked(position, mData.get(position));
        });

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
        return mData != null ? mData.size() : 0;
    }

    public void replaceData(List<GetCategoryTreeResponse> getCategoryTreeResponses) {
        mData = new ArrayList<>(getCategoryTreeResponses);
        notifyDataSetChanged();
    }

    public static class CategoriesViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.row_category_image_indicator)
        public ImageView categoryImageIndicator;

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

    public void setCategoryImage(CategoriesViewHolder holder, GetCategoryTreeResponse item, boolean isItemSelected) {
        holder.categoryImageIndicator.setVisibility(View.VISIBLE);
        holder.categoryIndicator.setVisibility(View.GONE);
        Context context = holder.itemView.getContext();
        int categoryDrawable = 0;

        switch (item.getName()) {
            case CATEGORY_HOME:
                categoryDrawable = isItemSelected ? R.drawable.ic_home_active : R.drawable.ic_home;
                break;
            case CATEGORY_WOMEN:
                categoryDrawable = isItemSelected ? R.drawable.ic_women_active : R.drawable.ic_women;
                break;
            case CATEGORY_KIDS_TOYS:
                categoryDrawable = isItemSelected ? R.drawable.ic_kids_active : R.drawable.ic_kids;
                break;
            case CATEGORY_MEN:
                categoryDrawable = isItemSelected ? R.drawable.ic_men_active : R.drawable.ic_men;
                break;
            case CATEGORY_BEAUTY:
                categoryDrawable = isItemSelected ? R.drawable.ic_beauty_active : R.drawable.ic_beauty;
                break;
            case CATEGORY_SPORTS:
                categoryDrawable = isItemSelected ? R.drawable.ic_sports_active : R.drawable.ic_sports;
                break;
            case CATEGORY_TECH:
                categoryDrawable = isItemSelected ? R.drawable.ic_tech_active : R.drawable.ic_tech;
                break;
        }

        if(categoryDrawable != 0) {
            holder.categoryImageIndicator.setImageDrawable(context.getDrawable(categoryDrawable));
        } else {
            holder.categoryImageIndicator.setVisibility(View.GONE);
            holder.categoryIndicator.setVisibility(View.VISIBLE);
            holder.categoryIndicator.setText(StringUtils.getCategoryInitials(item));
        }
    }
}
