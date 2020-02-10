package au.com.dealsdirect.ui.controller.shops;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by MTC on 2020-01-27.
 */
public interface ShopsListener {

    void updateIndicatorPosition(int position, RecyclerView recyclerView);

    void updatePreviousIndicatorPosition(RecyclerView recyclerView);

}
