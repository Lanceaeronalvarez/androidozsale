package au.com.dealsdirect.ui.controller.returns.currentreturns.listener;

import au.com.dealsdirect.ui.controller.returns.currentreturns.viewholder.CurrentReturnViewHolder;

/**
 * dp Created by Admin on 6/29/17.
 */

public interface CurrentReturnClickListener {

    void onCurrentReturnClickListener(
            int orderNumber,
            CurrentReturnViewHolder holder,
            int position,
            String productRequestStatus,
            String productRAN,
            String returnRequestDateFormat,
            String isRequestApproved,
            String returnId);

}
