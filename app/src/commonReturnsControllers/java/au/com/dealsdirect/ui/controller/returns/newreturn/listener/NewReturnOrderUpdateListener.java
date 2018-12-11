package au.com.dealsdirect.ui.controller.returns.newreturn.listener;

import au.com.dealsdirect.ui.controller.returns.newreturn.viewholder.NewReturnOrderViewHolder;

/**
 * dp Created by Admin on 7/25/17.
 */

public interface NewReturnOrderUpdateListener {

    void onReturnValueUpdated(NewReturnOrderViewHolder holder, int position, String
            returnId, boolean isAdding, int productQuantityValue);

}
