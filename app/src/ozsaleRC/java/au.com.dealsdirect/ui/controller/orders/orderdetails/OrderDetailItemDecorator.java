package au.com.dealsdirect.ui.controller.orders.orderdetails;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class OrderDetailItemDecorator extends RecyclerView.ItemDecoration {

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int position = parent.getChildAdapterPosition(view);
        int viewType = parent.getAdapter().getItemViewType(position);

        if(viewType == OrderDetailsRecyclerViewAdapter.VIEW_TYPE_SALE_NAME && position != 0) {
            outRect.top = 50;
        }
    }
}
