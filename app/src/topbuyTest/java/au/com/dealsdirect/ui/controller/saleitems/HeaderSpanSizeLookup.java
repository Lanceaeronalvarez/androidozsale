package au.com.dealsdirect.ui.controller.saleitems;

/**
 * Created by smartwave on 24/01/2017.
 */

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * GridLayoutManager.SpanSizeLookup implementation used to show a header in a RecyclerView when the
 * LayoutManager used is a GridLayoutManager.
 */
public class HeaderSpanSizeLookup extends GridLayoutManager.SpanSizeLookup {

    private final RecyclerView.Adapter adapter;
    private final GridLayoutManager layoutManager;

    public HeaderSpanSizeLookup(RecyclerView.Adapter adapter, GridLayoutManager layoutManager) {
        this.adapter = adapter;
        this.layoutManager = layoutManager;
    }

    @Override
    public int getSpanSize(int position) {
        return adapter.getItemViewType(position) == 0 ? layoutManager.getSpanCount() : 1;
    }
}
