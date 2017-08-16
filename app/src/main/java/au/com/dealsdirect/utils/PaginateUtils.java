package au.com.dealsdirect.utils;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemCreator;

import java.util.List;

import au.com.dealsdirect.R;

/**
 * Created by Ayi on 26/05/2017.
 */

public class PaginateUtils {

    public static final int DEFAULT_COUNT = 48;

    private static final int LOADING_TRIGGER_THRESHOLD = 1;

    private static final boolean ADD_LOADING_LIST_ITEM = true;

    public static Paginate init(RecyclerView recyclerView, Paginate.Callbacks callbacks) {
        return Paginate.with(recyclerView, callbacks)
                .setLoadingTriggerThreshold(PaginateUtils.LOADING_TRIGGER_THRESHOLD)
                .addLoadingListItem(PaginateUtils.ADD_LOADING_LIST_ITEM)
                .setLoadingListItemCreator(new DDLoadingListItemCreator())
                .build();
    }

    public static boolean hasLoadedAllItems(List<?> list) {
        return list.size() < DEFAULT_COUNT;
    }

    private static class DDLoadingListItemCreator implements LoadingListItemCreator {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.progress_dialog, parent, false);
            return new VH(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            // Bind custom loading row if needed
        }
    }

    private static class VH extends RecyclerView.ViewHolder {
        public VH(View itemView) {
            super(itemView);
        }
    }
}
