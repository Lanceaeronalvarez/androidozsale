package au.com.dealsdirect.ui.controller.checkout.paymentselect;

import android.view.View;

/**
 * Created by pauldesilva on 5/24/18.
 */

public interface PaymentEventListener {
    void onItemRemoved(int position);

    void onItemPinned(int position);

    void onItemViewClicked(View v, boolean pinned);
}
