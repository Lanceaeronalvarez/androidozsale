package au.com.dealsdirect.ui.controller.vouchers.Add;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.vouchers.AddAndApplyVoucherByKeyResponse;
import au.com.dealsdirect.data.network.model.vouchers.ApplyVouchersResponse;
import au.com.dealsdirect.data.network.model.vouchers.ClearVouchersResponse;
import au.com.dealsdirect.data.network.model.vouchers.GetUserVoucherResponse;
import au.com.dealsdirect.data.network.model.vouchers.GetVouchersResponse;
import au.com.dealsdirect.data.network.model.vouchers.Voucher;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;
import au.com.dealsdirect.data.network.model.vouchers.AddVoucherByKeyResponse;


/**
 * Created by Paul on 6/27/17.
 */

public class AddVouchersController extends BaseController implements AddVouchersMvpView,
        AddVouchersItemClickListener{
    @Inject
    AddVouchersMvpPresenter<AddVouchersMvpView> mPresenter;

    @BindView(R.id.partial_toolbar_arrow_title)
    TextView mTitleText;

    @BindView(R.id.partial_toolbar_filter_view)
    ImageView mFilterView;

    @BindView(R.id.partial_toolbar_arrow_view)
    ImageView mArrowImage;

    @BindView(R.id.controller_button_add_voucher)
    Button mAddVoucherButton;

    @BindView(R.id.controller_recycler_view_promo_vouchers)
    RecyclerView mRecyclerView;

    @BindView(R.id.controller_edit_text_voucher)
    TextView mPromoCodeText;

    @BindView(R.id.controller_view_add_promo_place_holder)
    LinearLayout vouchersPlaceHolder;

    View footerButtons;

    static List<String> voucherIds = new LinkedList<>();

    List<String> tempVoucherIds = new LinkedList<>();
    private ArrayList<Voucher> mVouchers = new ArrayList<>();

    HashMap<Integer, Boolean> voucherOptionIndicator = new HashMap<>();
    private String mTempVoucherKey;
    int listSize;

    private  AddVouchersRecycleViewAdapter mAdapter;

    public static AddVouchersController newInstance() {
        return new AddVouchersController(
                new BundleBuilder(
                new Bundle()).build());
    }

    public AddVouchersController(Bundle args) {
        super(args);
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        return null;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    @Override
    protected void setUp(View view) {
        mTitleText.setText("Promo Code");
        mFilterView.setVisibility(View.INVISIBLE);
        mArrowImage.setOnClickListener(action -> {
            getActivity().onBackPressed();
        });

        mAdapter = new AddVouchersRecycleViewAdapter(mVouchers, this, getActivity());
        Button mButtonClear =
                (Button) footerButtons.findViewById(R.id.partial_checkout_vouchers_button_clear);

        if (voucherIds.isEmpty()) {
            mButtonClear.setVisibility(View.GONE);
        } else {
            mButtonClear.setVisibility(View.VISIBLE);
            mButtonClear.setOnClickListener(view1 -> clearAppliedVouchers());
        }

        Button mButtonApply =
                (Button) footerButtons.findViewById(R.id.partial_checkout_vouchers_button_apply);

        mButtonApply.setOnClickListener(view2 -> {
            if (voucherIds.size() != 0 && tempVoucherIds.size() != 0) {
                mPresenter.applyVouchers(100, voucherIds);

            } else {
                //TODO: put dialog here
//                CustomAlertDialog.showCustomAlertDialog(
//                        mActivity,
//                        CustomAlertDialog.CustomDialogIconState.NEGATIVE,
//                        mActivity.getString("no voucher selected"));

            }
        });


        mAdapter = new AddVouchersRecycleViewAdapter(mVouchers, this, getActivity());

        mRecyclerView = (RecyclerView)
                view.findViewById(R.id.controller_recycler_view_promo_vouchers);

        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        SnapHelper helper = new PagerSnapHelper();
        helper.attachToRecyclerView(mRecyclerView);

        if (mVouchers.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            vouchersPlaceHolder.setVisibility(View.VISIBLE);

        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            vouchersPlaceHolder.setVisibility(View.GONE);
        }

        mAddVoucherButton.setOnClickListener(action -> {
            if (!mPromoCodeText.getText().toString().isEmpty()) {

                mPresenter.addAndApplyVoucherByKey(100, mPromoCodeText.getText().toString());

                mTempVoucherKey = mPromoCodeText.getText().toString();

                mPromoCodeText.clearFocus();
                hideKeyboard();

            } else {

                //TODO:CustomerAlertDialog
//                CustomAlertDialog
//                        .showCustomAlertDialog(
//                                mActivity,
//                                CustomAlertDialog.CustomDialogIconState.NEGATIVE,
//                                "Please input a promo code");
            }
        });
    }


    @Override
    protected void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @Override
    public void onVouchersApplied(ApplyVouchersResponse applyVouchersResponseBody) {

//        GDebug.log(this.getClass().getSimpleName(), "on vouchers applied");
        String responseMessage = applyVouchersResponseBody.getValue().getMessage();
        boolean responseResult = applyVouchersResponseBody.getValue().getResult();
        boolean responseIsAuthenticated = applyVouchersResponseBody.getValue().isAuthenticated();

        if (responseMessage.isEmpty() && responseResult && responseIsAuthenticated) {
//            GDebug.log(this.getClass().getSimpleName(),
//                    "on vouchers applied and response is not empty");

            String successResponse = "voucher applied";
            if (voucherIds.size() > 1) {
                successResponse = "vouchers applied";
            }

//            CustomAlertDialog.showCustomAlertDialog(
//                    mActivity,
//                    CustomAlertDialog.CustomDialogIconState.POSITIVE,
//                    successResponse
//            );


            getActivity().onBackPressed();


        } else {

//            GDebug.log(this.getClass().getSimpleName(),
//                    "on vouchers applied and response is empty");


            if (responseMessage.isEmpty()) {
//                CustomAlertDialog.showCustomAlertDialog(
//                        mActivity,
//                        CustomAlertDialog.CustomDialogIconState.NEGATIVE,
//                        mActivity.getString(R.string.unable_to_apply_voucher)
//                );

            } else {
//                CustomAlertDialog.showCustomAlertDialog(
//                        mActivity,
//                        CustomAlertDialog.CustomDialogIconState.NEGATIVE,
//                        responseMessage
//                );
            }

//            mActivity.getSupportFragmentManager().popBackStack();
            voucherIds.clear();
            tempVoucherIds.clear();
        }
    }

    public void showAddedVoucherItem(AddVoucherByKeyResponse.Response addVoucherResponse) {

    }

    @Override
    public void onVouchersCleared(ClearVouchersResponse clearVouchersResponse) {
        String responseMessage = "cleared voucher";
        if (voucherIds.size() > 1) {
            responseMessage = "cleared vouchers";
        }

//        CustomAlertDialog.showCustomAlertDialog(
//                mActivity,
//                CustomAlertDialog.CustomDialogIconState.POSITIVE,
//                responseMessage
//        );

        voucherIds.clear();
        tempVoucherIds.clear();
        getActivity().onBackPressed();

    }

    @Override
    public void onApplyVouchersError() {
        voucherIds.clear();
        tempVoucherIds.clear();
        getActivity().onBackPressed();
    }

    @Override
    public void onAddAndAppliedVoucher(AddAndApplyVoucherByKeyResponse response) {
//        GDebug.log(ViewMyVouchersPresenter.class.getName(), "onAddAndAppliedVoucher");
        if (response.getValue().getResult()) {
//            CustomAlertDialog.showCustomAlertDialog(
//                    mActivity,
//                    CustomAlertDialog.CustomDialogIconState.POSITIVE,
//                    mActivity.getString(R.string.promo_code_applied)
//            );

            voucherIds.add(mTempVoucherKey);
            tempVoucherIds.add(mTempVoucherKey);
            getActivity().onBackPressed();
        } else {
//
//            CustomAlertDialog.showCustomAlertDialog(
//                    mActivity,
//                    CustomAlertDialog.CustomDialogIconState.NEGATIVE,
//                    response.getD().getMessage());
        }
    }


    @Override
    public void updateVoucherList(Pair<List<GetUserVoucherResponse.Voucher>, GetVouchersResponse> pair) {
//        GDebug.log(MyVouchersFragment.class.getName(), "updateVoucherList2 called");

        if (pair.first == null) {
            listSize = 0;

        } else {
            listSize = pair.first.size();
            RecyclerView.LayoutManager linearLayoutManager = mRecyclerView.getLayoutManager();
            linearLayoutManager.scrollToPosition(0);

            setVouchersHashMap();
        }
    }

    @Override
    public void onVoucherItemClicked(String voucherId, String voucherState, LinearLayout holder, int position) {
        if (voucherOptionIndicator.get(position) != null) {

            boolean isClicked = voucherOptionIndicator.get(position);

            if (isClicked) {
                voucherIds.remove(voucherId);
                tempVoucherIds.remove(voucherId);
//                GDebug.log("vouchers", " ID = " + voucherId);

//                switch (voucherState) {
//                    case "red":
//
//                        holder.setBackgroundResource(R.drawable.voucher_container_red);
//                        break;
//                    case "blue":
//
//                        holder.setBackgroundResource(R.drawable.voucher_container_blue);
//                        break;
//                    case "yellow":
//
//                        holder.setBackgroundResource(R.drawable.voucher_container_yellow);
//                        break;
//                    case "green":
//
//                        holder.setBackgroundResource(R.drawable.voucher_container_green);
//                        break;
//                    case "violet":
//
//                        holder.setBackgroundResource(R.drawable.voucher_container_violet);
//                        break;
//                    case "aqua":
//
//                        holder.setBackgroundResource(R.drawable.voucher_container_aqua);
//                        break;
//                    case "orange":
//
//                        holder.setBackgroundResource(R.drawable.voucher_container_orange);
//                        break;
//                    case "darkblue":
//
//                        holder.setBackgroundResource(R.drawable.voucher_container_darkblue);
//                        break;
//                    default:
//
//                        holder.setBackgroundResource(R.drawable.voucher_container_darkblue);
//                        break;
//                }
//
                voucherOptionIndicator.put(position, false);

            } else {

//                GDebug.log(this.getClass().getSimpleName(), " ID = " + voucherId);
                voucherIds.add(voucherId);
                tempVoucherIds.add(voucherId);

//                switch (voucherState) {
//                    case "red":
//
//                        holder.setBackgroundResource(R.drawable.voucher_container_red_active);
//                        break;
//                    case "blue":
//
//                        holder.setBackgroundResource(R.drawable.voucher_container_blue_active);
//                        break;
//                    case "yellow":
//
//                        holder.setBackgroundResource(R.drawable.voucher_container_yellow_active);
//                        break;
//                    case "green":
//
//                        holder.setBackgroundResource(R.drawable.voucher_container_green_active);
//                        break;
//                    case "violet":
//
//                        holder.setBackgroundResource(R.drawable.voucher_container_violet_active);
//                        break;
//                    case "aqua":
//
//                        holder.setBackgroundResource(R.drawable.voucher_container_aqua_active);
//                        break;
//                    case "orange":
//
//                        holder.setBackgroundResource(R.drawable.voucher_container_orange_active);
//                        break;
//                    case "darkblue":
//
//                        holder.setBackgroundResource(R.drawable.voucher_container_darkblue_active);
//                        break;
//                    default:
//
//                        holder.setBackgroundResource(R.drawable.voucher_container_darkblue_active);
//                        break;
//                }
//
                voucherOptionIndicator.put(position, true);
            }
        }
//        GDebug.log("vouchers", "log the size of the listener list = " + voucherIds.size());
    }

    public void setVouchersHashMap() {
        for (int i = 0; i <= listSize; i++) {
            voucherOptionIndicator.put(i, false);
        }
    }

    private void clearAppliedVouchers() {
        mPresenter.clearVouchers(100);

    }
}
