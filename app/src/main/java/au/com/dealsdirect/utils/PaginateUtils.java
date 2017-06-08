package au.com.dealsdirect.utils;


import android.support.v7.widget.RecyclerView;

import com.paginate.Paginate;

import java.util.List;

/**
 * Created by Ayi on 26/05/2017.
 */

public class PaginateUtils {
    public static final int DEFAULT_COUNT = 48;


    private static final int LOADING_TRIGGER_THRESHOLD = 2;

    private static final boolean ADD_LOADING_LIST_ITEM = true;

    public static Paginate init(RecyclerView recyclerView, Paginate.Callbacks callbacks) {
        return Paginate.with(recyclerView, callbacks)
                .setLoadingTriggerThreshold(PaginateUtils.LOADING_TRIGGER_THRESHOLD)
                .addLoadingListItem(PaginateUtils.ADD_LOADING_LIST_ITEM)
                .build();
    }

    public static boolean hasLoadedAllItems(List<?> list){
        return list.size() < DEFAULT_COUNT;
    }

}
