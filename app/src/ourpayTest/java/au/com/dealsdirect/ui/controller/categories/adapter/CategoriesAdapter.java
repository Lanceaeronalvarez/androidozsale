package au.com.dealsdirect.ui.controller.categories.adapter;

import android.annotation.TargetApi;
import android.os.Build;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.ui.controller.categories.CategoriesMvpView;
//import com.example.myfiltercategoriesmodule.R;

/**
 * dp Created by Admin on 10/26/16.
 */

public class CategoriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private String fragmentCategoryIdentifier;
    private final CategoriesMvpView mCategoriesMvpView;

    private long mLastClickTime = System.currentTimeMillis();
    private static final long CLICK_TIME_INTERVAL = 300;

    private List<GetCategoryTreeResponse> mCategoryOption;
    TextView rowTextView;
    View row;


    public CategoriesAdapter(List<GetCategoryTreeResponse> categoryList, String fragmentIdentifier, CategoriesMvpView categoriesMvpView) {

        mCategoriesMvpView = categoriesMvpView;
        mCategoryOption = categoryList;
        fragmentCategoryIdentifier = fragmentIdentifier;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup container, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        row = inflater.inflate(R.layout.category_row_item, container, false);

        rowTextView = (TextView)row.findViewById(R.id.category_text);

        return new CategoriesViewHolder(row);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {

        CategoriesViewHolder vh = (CategoriesViewHolder) viewHolder;

        vh.categoryText.setText(mCategoryOption.get(position).getName().toLowerCase());

        setTextViewTransitionName(
                vh.categoryText,
                vh.categoryText.getText().toString()
                +"_transition_"+fragmentCategoryIdentifier);

//        GDebug.log("transitioning", "category text transition = "+vh.categoryText.getText().toString());

        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                long now = System.currentTimeMillis();
                if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
                    return;
                }
                mLastClickTime = now;

                mCategoriesMvpView.onCategoryClicked
                        (vh,
                         position,
                         mCategoryOption.get(position).getName().toLowerCase(),mCategoryOption.get(position).getKey());
            }
        });
    }

    @Override
    public int getItemCount() {
//        GDebug.log("msize", Integer.toString(mSize));
        if(mCategoryOption == null){
            mCategoryOption = new ArrayList<>();
        }
        return mCategoryOption.size();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setTextViewTransitionName(TextView textView, String tag){
        textView.setTransitionName(tag);
    }

    public static class CategoriesViewHolder extends RecyclerView.ViewHolder {

        public TextView categoryText;

        public CategoriesViewHolder(View itemView) {
            super(itemView);
            categoryText = (TextView) itemView.findViewById(R.id.category_text);

        }
    }
}
