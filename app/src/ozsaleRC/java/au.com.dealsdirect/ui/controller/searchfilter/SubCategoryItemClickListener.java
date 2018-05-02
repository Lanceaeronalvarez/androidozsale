package au.com.dealsdirect.ui.controller.searchfilter;

import android.support.v7.widget.RecyclerView;

import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.ui.controller.searchfilter.adapter.SubCategoryItemsAdapter;

/**
 * Created by smartwave on 25/10/2017.
 */

public interface SubCategoryItemClickListener {
    void onSubCategoryItemClicked(SubCategoryItemsAdapter adapter,GetCategoryTreeResponse getCategoryTreeResponse);
}
