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

import com.google.gson.reflect.TypeToken;

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
import au.com.dealsdirect.utils.JsonUtils;
import butterknife.BindView;
import au.com.dealsdirect.data.network.model.vouchers.AddVoucherByKeyResponse;


/**
 * Created by Paul on 6/27/17.
 */

public class AddVouchersController extends BaseController implements AddVouchersMvpView,
        AddVouchersItemClickListener{
    private static final String VOUCHERS="Vouchers";

    private static final String testVouchersString = "[{\n" +
            "\t\t\t\t\"ID\": \"ba41e1d8-0a3d-4868-81ba-2f139f0827fa\",\n" +
            "\t\t\t\t\"Description\": \"$20.0000 (Ozsale.com.au)\"\n" +
            "\t\t\t}, {\n" +
            "\t\t\t\t\"ID\": \"7f6c29b4-4a94-4336-81f3-179fe040179d\",\n" +
            "\t\t\t\t\"Description\": \"$20.0000 (Ozsale.com.au)\"\n" +
            "\t\t\t}, {\n" +
            "\t\t\t\t\"ID\": \"e1475fb6-b29d-41e1-b802-1fdaad9817f2\",\n" +
            "\t\t\t\t\"Description\": \"$20.0000 (Ozsale.com.au)\"\n" +
            "\t\t\t}, {\n" +
            "\t\t\t\t\"ID\": \"6aa0efed-fe82-43bb-884c-448b1229aab8\",\n" +
            "\t\t\t\t\"Description\": \"$20.0000 (Ozsale.com.au)\"\n" +
            "\t\t\t}, {\n" +
            "\t\t\t\t\"ID\": \"7a1c12e2-e7b3-4a14-9b74-9e0c9a756e3e\",\n" +
            "\t\t\t\t\"Description\": \"$20.0000 (Ozsale.com.au)\"\n" +
            "\t\t\t}, {\n" +
            "\t\t\t\t\"ID\": \"36aefc78-f96d-497e-aa35-f79fb40e12e6\",\n" +
            "\t\t\t\t\"Description\": \"$20.0000 (Ozsale.com.au)\"\n" +
            "\t\t\t}, {\n" +
            "\t\t\t\t\"ID\": \"0bba976f-395e-4746-9b64-5773509f4465\",\n" +
            "\t\t\t\t\"Description\": \"$20.0000 (Ozsale.com.au)\"\n" +
            "\t\t\t}, {\n" +
            "\t\t\t\t\"ID\": \"7265376a-50b6-448a-857b-667c2a338919\",\n" +
            "\t\t\t\t\"Description\": \"$20.0000 (Ozsale.com.au)\"\n" +
            "\t\t\t}, {\n" +
            "\t\t\t\t\"ID\": \"8d7e79b9-1055-480e-b4b2-8e491992afd3\",\n" +
            "\t\t\t\t\"Description\": \"$20.0000 (Ozsale.com.au)\"\n" +
            "\t\t\t}, {\n" +
            "\t\t\t\t\"ID\": \"abdb13ea-54b9-4f87-9e6e-9549fc24f8b3\",\n" +
            "\t\t\t\t\"Description\": \"$20.0000 (Ozsale.com.au)\"\n" +
            "\t\t\t}, {\n" +
            "\t\t\t\t\"ID\": \"e27536b2-fe19-41e4-97df-b12692441547\",\n" +
            "\t\t\t\t\"Description\": \"$20.0000 (Ozsale.com.au)\"\n" +
            "\t\t\t}, {\n" +
            "\t\t\t\t\"ID\": \"8587ba3f-bdfd-4c7e-b2a8-b37a7be2ca2e\",\n" +
            "\t\t\t\t\"Description\": \"$20.0000 (Ozsale.com.au)\"\n" +
            "\t\t\t}, {\n" +
            "\t\t\t\t\"ID\": \"3fba8bb7-879a-487c-82b0-c3773cf83d13\",\n" +
            "\t\t\t\t\"Description\": \"$20.0000 (Ozsale.com.au)\"\n" +
            "\t\t\t}, {\n" +
            "\t\t\t\t\"ID\": \"04c0b80d-a9f2-413b-b995-e240b5ae1cbf\",\n" +
            "\t\t\t\t\"Description\": \"$20.0000 (Ozsale.com.au)\"\n" +
            "\t\t\t}, {\n" +
            "\t\t\t\t\"ID\": \"59498f7e-d914-4298-8807-94a406db2784\",\n" +
            "\t\t\t\t\"Description\": \"$20.0000 (Ozsale.com.au)\"\n" +
            "\t\t\t}, {\n" +
            "\t\t\t\t\"ID\": \"0fb6a831-76e5-41f9-aade-a48125f9c214\",\n" +
            "\t\t\t\t\"Description\": \"$20.0000 (Ozsale.com.au)\"\n" +
            "\t\t\t}, {\n" +
            "\t\t\t\t\"ID\": \"308050ca-ddce-451a-a74e-a49360a0cc0e\",\n" +
            "\t\t\t\t\"Description\": \"$20.0000 (Ozsale.com.au)\"\n" +
            "\t\t\t}]";

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

    @BindView(R.id.partial_checkout_vouchers_button_clear)
    Button mButtonClear;

    @BindView(R.id.partial_checkout_vouchers_button_apply)
    Button mButtonApply;

    View footerButtons;

    static List<String> voucherIds = new LinkedList<>();

    List<String> tempVoucherIds = new LinkedList<>();
    private ArrayList<Voucher> mVouchers = new ArrayList<>();

    HashMap<Integer, Boolean> voucherOptionIndicator = new HashMap<>();
    private String mTempVoucherKey;
    int listSize;

    private  AddVouchersRecycleViewAdapter mAdapter;

    public static AddVouchersController newInstance(String vouchersJsonString) {
        return new AddVouchersController(new BundleBuilder(new Bundle())
                .putString(VOUCHERS, testVouchersString)
                .build());
    }

    public AddVouchersController(Bundle args) {
        super(args);
        mVouchers = JsonUtils.convertStringToObject(args.getString(VOUCHERS,""), new TypeToken<ArrayList<Voucher>>(){}.getType());
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_add_vouchers,container,false);
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
        mTitleText.setText("Promo Code");
        mFilterView.setVisibility(View.INVISIBLE);
        mArrowImage.setOnClickListener(action -> {
            getActivity().onBackPressed();
        });

        mAdapter = new AddVouchersRecycleViewAdapter(mVouchers, this, getActivity());
        if (voucherIds.isEmpty()) {
            mButtonClear.setVisibility(View.GONE);
        } else {
            mButtonClear.setVisibility(View.VISIBLE);
            mButtonClear.setOnClickListener(view1 -> clearAppliedVouchers());
        }

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
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

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
