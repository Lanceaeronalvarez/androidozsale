package au.com.dealsdirect.ui.controller.vouchers.View;

import au.com.dealsdirect.ui.base.MvpPresenter;

/**
 * Created by Paul on 6/23/17.
 */

public interface ViewVouchersMvpPresenter<V extends ViewVouchersMvpView> extends MvpPresenter<V> {

    void loadMyVouchers();
}
