package au.com.dealsdirect.ui.controller.vouchers.Add;

import java.util.List;

import au.com.dealsdirect.data.network.model.vouchers.AddAndApplyVoucherByKeyRequest;
import au.com.dealsdirect.data.network.model.vouchers.ApplyVouchersRequest;
import au.com.dealsdirect.data.network.model.vouchers.ClearVouchersRequest;
import au.com.dealsdirect.ui.base.MvpPresenter;

/**
 * Created by Paul on 6/27/17.
 */

public interface AddVouchersMvpPresenter<V extends AddVouchersMvpView> extends MvpPresenter<V> {

    void applyVouchers(int imageSize, List<String> voucherIds);

    void clearVouchers(int imageSize);

    void addAndApplyVoucherByKey(int imageSize, String key);

    void loadMyVouchers();
}
