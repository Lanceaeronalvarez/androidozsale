package au.com.dealsdirect.ui.controller.categories.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.ui.controller.categories.CategoriesMvpPresenter;
import au.com.dealsdirect.ui.controller.categories.listener.CategoryClickListener;
import au.com.dealsdirect.ui.controller.categories.listener.SubCategoryItemClickListener;
import au.com.dealsdirect.ui.controller.main.MainController;
import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.utils.AppLogger;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MTC on 2019-12-05.
 */
public class SaleCategoryAdapter extends RecyclerView.Adapter<SaleCategoryAdapter.SaleCategoryViewHolder> {

    private List<GetCategoryTreeResponse> mData = new ArrayList<>();
    private CategoriesMvpPresenter mPresenter;
    private CategoryClickListener mCategoryAdapterClickListener;
    private int mLastPosition = -1;
    private Context mContext;
    private int mSelectedIndex;
    private Map<String, List<GetCategoryTreeResponse>> mCategoryMap = new HashMap<>();
    private SubCategoryItemClickListener mSubCategoryItemClickListener;
    private SubCategoryItemsAdapter mSubCategoryItemsAdapter;
    private SubCategoriesAdapter mSubCategoryAdapter;
    private boolean mAnimateInsert = true;

    public SaleCategoryAdapter(Context context,
                               List<GetCategoryTreeResponse> data,
                               CategoriesMvpPresenter presenter,
                               CategoryClickListener categoryClickListener,
                               Map<String, List<GetCategoryTreeResponse>> categoryMap,
                               SubCategoryItemClickListener subCategoryItemClickListener) {

        mContext = context;
        mData = data;
        mPresenter = presenter;
        mCategoryAdapterClickListener = categoryClickListener;
        mCategoryMap = categoryMap;
        mSubCategoryItemClickListener = subCategoryItemClickListener;
    }

    @NonNull
    @Override
    public SaleCategoryAdapter.SaleCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_salecategory, parent, false);
        return new SaleCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SaleCategoryAdapter.SaleCategoryViewHolder holder, int position) {

        holder.subCategoryTitle.setText(mData.get(position).getName());
        String subCategoryTitle = "";
        StringBuilder builder = new StringBuilder();

        if (mData.get(position).getChildren() != null) {
            for (int i = 0; i < mData.get(position).getChildren().size(); i++) {
                String string = mData.get(position).getChildren().get(i).getName();
                String prefix = i == 0 ? "" : ", ";
                builder.append(prefix);
                builder.append(string);
            }
            subCategoryTitle = String.valueOf(builder);
            holder.saleCategorySubTitle.setText(subCategoryTitle);
        }

        holder.itemView.setOnClickListener(view -> {
            mSelectedIndex = position;
            if (holder.subCategoryImageButton.getDrawable().getConstantState() == mContext.getResources().getDrawable(R.drawable.ic_chevron_down).getConstantState()) {
                holder.subCategoryImageButton.setImageDrawable(mContext.getDrawable(R.drawable.ic_chevron_up));
                holder.subCategoryRecyclerView.setVisibility(View.VISIBLE);
                mCategoryAdapterClickListener.onCategoryClicked(position, mData.get(position));
            } else {
                holder.subCategoryImageButton.setImageDrawable(mContext.getDrawable(R.drawable.ic_chevron_down));
                holder.subCategoryRecyclerView.setVisibility(View.GONE);
            }
        });
    }

    public GetCategoryTreeResponse getItem(int position) {
        return mData.get(position);
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void replaceData(List<GetCategoryTreeResponse> getCategoryTreeResponses) {
        mData = new ArrayList<>(getCategoryTreeResponses);
        notifyDataSetChanged();
    }

    public static class SaleCategoryViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.viewholder_salecategory_container)
        RelativeLayout categoryContainer;

        @BindView(R.id.viewholder_salecategory_title)
        TextView subCategoryTitle;

        @BindView(R.id.viewholder_salecategory_subtitle)
        TextView saleCategorySubTitle;

        @BindView(R.id.viewholder_salecategory_border)
        View subCategoryBorder;

        @BindView(R.id.viewholder_salecategoryitems_border)
        View subCategoryItemsBorder;

        @BindView(R.id.viewholder_salecategory_image_button)
        ImageButton subCategoryImageButton;

        @BindView(R.id.viewholder_salecategory_items_recyclerview)
        public RecyclerView subCategoryRecyclerView;

        public CategoriesMvpPresenter mPresenter;

        public SaleCategoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public SaleCategoryViewHolder(View itemView, CategoriesMvpPresenter presenter) {
            super(itemView);
            mPresenter = presenter;
            ButterKnife.bind(this, itemView);

        }
    }

}
