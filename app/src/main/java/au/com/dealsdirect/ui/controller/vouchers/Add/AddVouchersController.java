package au.com.dealsdirect.ui.controller.vouchers.Add;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.vouchers.AddAndApplyVoucherByKeyResponse;
import au.com.dealsdirect.data.network.model.vouchers.AddVoucherByKeyResponse;
import au.com.dealsdirect.data.network.model.vouchers.ApplyVouchersResponse;
import au.com.dealsdirect.data.network.model.vouchers.ClearVouchersResponse;
import au.com.dealsdirect.ui.base.BaseController;

/**
 * Created by Paul on 6/27/17.
 */

public class AddVouchersController extends BaseController implements AddVouchersMvpView {
    @Inject
    AddVouchersMvpPresenter<AddVouchersMvpView> mPresenter;

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_add_vouchers, container, false);

        getControllerComponent().inject(this);
        mPresenter.onAttach(this);

        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @Override
    public void showAddedVoucherItem(AddVoucherByKeyResponse.Response addVoucherResponse) {

    }

    @Override
    public void onVouchersApplied(ApplyVouchersResponse applyVouchersResponseBody) {

    }

    @Override
    public void onVouchersCleared(ClearVouchersResponse clearVouchersResponse) {

    }

    @Override
    public void onApplyVouchersError() {

    }

    @Override
    public void onAddAndAppliedVoucher(AddAndApplyVoucherByKeyResponse response) {

    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    protected void setUp(View view) {

    }

}
