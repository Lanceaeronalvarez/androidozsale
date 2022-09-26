package au.com.dealsdirect.ui.controller.vouchers.Add;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Value;
import au.com.dealsdirect.data.network.model.vouchers.AddAndApplyVoucherByKeyResponse;
import au.com.dealsdirect.data.network.model.vouchers.ApplyVouchersResponse;
import au.com.dealsdirect.data.network.model.vouchers.ClearVouchersResponse;
import au.com.dealsdirect.data.network.model.vouchers.RemoveVoucherByKeyResponse;
import au.com.dealsdirect.data.network.model.vouchers.Voucher;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutDetailsMapper;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import butterknife.BindView;

public class AddVouchersController extends BaseController implements AddVouchersMvpView {

    @Inject
    AddVouchersMvpPresenter<AddVouchersMvpView> mPresenter;

    @BindView(R.id.partial_toolbar_title)
    TextView mTitleText;

    @BindView(R.id.partial_toolbar_right_view)
    ImageView mFilterView;

    @BindView(R.id.partial_toolbar_left_view)
    View mArrowImage;

    @BindView(R.id.controller_button_add_voucher)
    Button mAddPromoCodeButton;

    @BindView(R.id.container_voucher_list)
    LinearLayout mVoucherListContainerLayout;

    @BindView(R.id.controller_recycler_view_promo_vouchers)
    RecyclerView voucherRecyclerView;

    @BindView(R.id.controller_edit_text_voucher)
    TextView mPromoCodeText;

    @BindView(R.id.partial_checkout_vouchers_button_clear)
    Button mButtonClear;

    @BindView(R.id.partial_checkout_vouchers_button_apply)
    Button mButtonApply;

    @BindView(R.id.no_vouchers_placeholder)
    LinearLayout mPlaceholderLayout;

    @BindView(R.id.controller_add_voucher_select_text)
    TextView mSelectTextView;

    @BindView(R.id.promo_code_container)
    RecyclerView promoCodeContainer;

    private final List<Voucher> vouchers = new ArrayList<>();
    private final Set<String> appliedVouchers = new HashSet<>();

    private final List<Value.PromoCode> appliedPromoCodes = new ArrayList<>();

    private String postcode = null;

    private CheckoutDetailsMapper mappedCheckoutDetails = null;
    private NewCartDetailsListener cartDetailsListener = null;

    private boolean isVouchersApplyButtonEnabled = true;
    private boolean isVouchersClearButtonEnabled = true;
    private boolean isPromoCodeAddButtonEnabled = true;
    private boolean isPromoCodeDeleteButtonEnabled = true;

    public AddVouchersController(Bundle args) {
        super(args);
    }

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
    protected void setUp(View view) {
        mTitleText.setText(getString(R.string.add_new_voucher));
        mFilterView.setVisibility(View.INVISIBLE);
        mArrowImage.setOnClickListener(action -> mActivity.onBackPressed());

        if (appliedVouchers.isEmpty()) {
            mButtonClear.setVisibility(View.GONE);
        } else {
            mButtonClear.setVisibility(View.VISIBLE);
            mButtonClear.setOnClickListener(view1 -> clearAppliedVouchers());
        }

        mButtonApply.setOnClickListener(view2 -> applyVouchers());

        mVoucherListContainerLayout.setVisibility(vouchers.isEmpty() ? View.GONE : View.VISIBLE);
        voucherRecyclerView.setVisibility(vouchers.isEmpty() ? View.GONE : View.VISIBLE);
        mSelectTextView.setVisibility(vouchers.isEmpty() ? View.GONE : View.VISIBLE);
        mPlaceholderLayout.setVisibility(vouchers.isEmpty() ? View.VISIBLE : View.GONE);
        mButtonApply.setVisibility(vouchers.isEmpty() ? View.GONE : View.VISIBLE);

        voucherRecyclerView.setAdapter(new AddVouchersRecyclerViewAdapter(
                vouchers,
                appliedVouchers,
                position -> {
                    final String voucherId = vouchers.get(position).getId();
                    final boolean isApplied = appliedVouchers.contains(voucherId);
                    if (isApplied) {
                        appliedVouchers.remove(voucherId);
                    } else {
                        appliedVouchers.add(voucherId);
                    }
                }));
        voucherRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));

        mAddPromoCodeButton.setOnClickListener(action -> applyPromoCode());

        promoCodeContainer.setLayoutManager(new LinearLayoutManager(promoCodeContainer.getContext(), RecyclerView.VERTICAL, false));
        AddVouchersPromoCodeRecyclerViewAdapter adapter = new AddVouchersPromoCodeRecyclerViewAdapter(
                appliedPromoCodes,
                getDeletePromoCodeButtonListener());
        promoCodeContainer.setAdapter(adapter);
    }


    @Override
    protected void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @Override
    public void onVouchersApplied(ApplyVouchersResponse applyVouchersResponseBody) {
        final String responseMessage = applyVouchersResponseBody.getD().getMessage();
        final boolean responseResult = applyVouchersResponseBody.getD().getResult();
        final boolean responseIsAuthenticated = applyVouchersResponseBody.getD().isAuthenticated();

        if (responseMessage.isEmpty() && responseResult && responseIsAuthenticated) {
            mappedCheckoutDetails = new CheckoutDetailsMapper(applyVouchersResponseBody.getD().value);
            informNewCartDetailsListener();

            CustomAlertDialog.showCustomAlertDialog(
                    mActivity,
                    CustomAlertDialog.CustomDialogIconState.POSITIVE,
                    "Vouchers Applied"
            );

            mActivity.onBackPressed();
        } else {
            setAllButtonsEnabled(true);
            CustomAlertDialog.showCustomAlertDialog(
                    mActivity,
                    CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                    responseMessage.isEmpty() ? mActivity.getString(R.string.unable_to_apply_voucher) : responseMessage
            );
        }
    }

    @Override
    public void onVouchersCleared(ClearVouchersResponse clearVouchersResponse) {
        if (clearVouchersResponse.getD().getResult()) {
            mappedCheckoutDetails = new CheckoutDetailsMapper(clearVouchersResponse.getD().getValue());
            informNewCartDetailsListener();
        }

        CustomAlertDialog.showCustomAlertDialog(
                mActivity,
                CustomAlertDialog.CustomDialogIconState.POSITIVE,
                "Cleared Vouchers"
        );

        mActivity.onBackPressed();

    }

    @Override
    public void onApplyVouchersError() {
        setAllButtonsEnabled(true);
        CustomAlertDialog.showCustomAlertDialog(
                mActivity,
                CustomAlertDialog.CustomDialogIconState.POSITIVE,
                mActivity.getString(R.string.unable_to_apply_voucher)
        );
    }

    @Override
    public void onAddAndAppliedVoucher(AddAndApplyVoucherByKeyResponse response) {
        if (response.getD().getResult()) {
            mappedCheckoutDetails = new CheckoutDetailsMapper(response.getD().getValue());
            informNewCartDetailsListener();

            CustomAlertDialog.showCustomAlertDialog(
                    mActivity,
                    CustomAlertDialog.CustomDialogIconState.POSITIVE,
                    mActivity.getString(R.string.promo_code_applied)
            );

            mActivity.onBackPressed();
        } else {
            setAllButtonsEnabled(true);
            CustomAlertDialog.showCustomAlertDialog(
                    mActivity,
                    CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                    response.getD().getMessage());
        }
    }

    @Override
    public void onRemoveVoucherByKey(RemoveVoucherByKeyResponse response) {
        setAllButtonsEnabled(true);
        if (response.d.getResult()) {
            mappedCheckoutDetails = new CheckoutDetailsMapper(response.getD().getValue());
            informNewCartDetailsListener();
        } else {
            final String message = response.d.getMessage();
            CustomAlertDialog.showCustomAlertDialog(
                    mActivity,
                    CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                    message
            );
        }
    }

    private void setAllButtonsEnabled(boolean enabled) {
        isVouchersApplyButtonEnabled = enabled;
        isVouchersClearButtonEnabled = enabled;
        isPromoCodeAddButtonEnabled = enabled;
        isPromoCodeDeleteButtonEnabled = enabled;
    }

    private void applyPromoCode() {
        if (!isPromoCodeAddButtonEnabled) {
            return;
        }
        if (!mPromoCodeText.getText().toString().isEmpty()) {
            mPresenter.addAndApplyVoucherByKey(postcode, 100, mPromoCodeText.getText().toString());
            mPromoCodeText.clearFocus();
            hideKeyboard();
            setAllButtonsEnabled(false);
        } else {
            CustomAlertDialog
                    .showCustomAlertDialog(
                            mActivity,
                            CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                            mActivity.getString(R.string.please_input_promo_code));
        }
    }

    private AddVouchersPromoCodeRecyclerViewAdapter.PromoCodeDeleteButtonListener getDeletePromoCodeButtonListener() {
        return position -> {
            if (!isPromoCodeDeleteButtonEnabled) {
                return;
            }
            final String key = appliedPromoCodes.get(position).getCode();
            appliedPromoCodes.remove(position);
            if (promoCodeContainer.getAdapter() != null) {
                promoCodeContainer.getAdapter().notifyItemRemoved(position);
            }
            mPresenter.removeVoucherByKey(postcode, 100, key);
            setAllButtonsEnabled(false);
        };
    }

    private void applyVouchers() {
        if (!isVouchersApplyButtonEnabled) {
            return;
        }
        if (!appliedVouchers.isEmpty()) {
            mPresenter.applyVouchers(postcode, 100, new ArrayList<>(appliedVouchers));
            setAllButtonsEnabled(false);
        } else {
            CustomAlertDialog.showCustomAlertDialog(
                    mActivity,
                    CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                    mActivity.getResources().getString(R.string.no_voucher_selected));
        }
    }

    private void clearAppliedVouchers() {
        if (!isVouchersClearButtonEnabled) {
            return;
        }
        mPresenter.clearVouchers(postcode, 100);
        setAllButtonsEnabled(false);
    }

    public List<Voucher> getVouchers() {
        return new ArrayList<>(vouchers);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setVouchers(List<Voucher> vouchers) {
        this.vouchers.clear();
        this.vouchers.addAll(vouchers);
        resetAppliedVouchers();
        if (voucherRecyclerView != null && voucherRecyclerView.getAdapter() != null) {
            voucherRecyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    private void resetAppliedVouchers() {
        appliedVouchers.clear();
        for (Voucher voucher : vouchers) {
            if (voucher.isApplied()) {
                appliedVouchers.add(voucher.getId());
            }
        }
    }

    public List<Value.PromoCode> getAppliedPromoCodes() {
        return new ArrayList<>(appliedPromoCodes);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setAppliedPromoCodes(List<Value.PromoCode> appliedPromoCodes) {
        this.appliedPromoCodes.clear();
        this.appliedPromoCodes.addAll(appliedPromoCodes);
        if (promoCodeContainer != null && promoCodeContainer.getAdapter() != null) {
            promoCodeContainer.getAdapter().notifyDataSetChanged();
        }
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    private void informNewCartDetailsListener() {
        if (cartDetailsListener != null) {
            cartDetailsListener.onNewCartDetailsReceived(mappedCheckoutDetails);
        }
    }

    public void setCartDetailsListener(NewCartDetailsListener cartDetailsListener) {
        this.cartDetailsListener = cartDetailsListener;
    }

    public interface NewCartDetailsListener {
        void onNewCartDetailsReceived(CheckoutDetailsMapper mappedValues);
    }
}
