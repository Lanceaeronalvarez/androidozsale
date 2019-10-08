package au.com.dealsdirect.ui.custom;

import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import au.com.dealsdirect.ui.controller.orders.orders.OrdersRecyclerViewAdapter;

public class OrderItemDecorator extends RecyclerView.ItemDecoration {
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int position = parent.getChildAdapterPosition(view);
        int viewType = parent.getAdapter().getItemViewType(position);

        if(viewType == OrdersRecyclerViewAdapter.TITLE_VIEW_TYPE && position != 0) {
            outRect.top = 50;
        }
    }
}
