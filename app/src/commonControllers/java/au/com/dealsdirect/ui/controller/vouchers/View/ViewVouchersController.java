package au.com.dealsdirect.ui.controller.vouchers.View;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.vouchers.GetUserVoucherResponse;
import au.com.dealsdirect.data.network.model.vouchers.GetVouchersResponse;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;

/**
 * Created by Paul on 6/23/17.
 */

public class ViewVouchersController extends BaseController implements ViewVouchersMvpView {

    @Inject
    ViewVouchersMvpPresenter<ViewVouchersMvpView> mPresenter;

    @BindView(R.id.partial_toolbar_title)
    TextView mTitleText;

    @BindView(R.id.partial_toolbar_right_view)
    ImageView mFilterView;

    @BindView(R.id.partial_toolbar_left_view)
    View mArrowImage;

    @BindView(R.id.controller_view_voucher_desc_text)
    TextView mVouchersDescText;

    @BindView(R.id.controller_view_voucher_used_label)
    TextView mUsedVoucherIndicatorText;

    @BindView(R.id.controller_view_voucher_used_recyclerviewpager)
    RecyclerView mUsedVouchersRecyclerViewPager;

    @BindView(R.id.controller_view_voucher_unused_recyclerviewpager)
    RecyclerView mUnusedVouchersRecyclerViewPager;

    @BindView(R.id.controller_view_voucher_divider)
    View mDivider;

    @BindView(R.id.controller_view_voucher_layout)
    NestedScrollView mRootLayout;

    @BindView(R.id.no_vouchers_placeholder)
    LinearLayout mNoVouchersLayout;

    private ViewVouchersRecyclerViewAdapter mUnusedVouchersAdapter;

    private ViewVouchersRecyclerViewAdapter mUsedVouchersAdapter;

    public ViewVouchersController(Bundle arg) {
        super(arg);
    }

    public static ViewVouchersController newInstance() {
        return new ViewVouchersController(
                new BundleBuilder(new Bundle())
                        .build());
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_view_vouchers, container, false);

        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        return view;
    }

    @Override
    public void onRefreshStart() {
        super.onRefreshStart();
        mPresenter.loadMyVouchers();
    }

    @Override
    public void refreshContents() {
        super.refreshContents();
        mPresenter.loadMyVouchers();
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    @Override
    protected void setUp(View view) {
        mNoVouchersLayout.setVisibility(View.GONE);
        mTitleText.setText(getString(R.string.account_vouchers));
        mFilterView.setVisibility(View.INVISIBLE);
        mArrowImage.setVisibility(mPresenter.isTablet() ? View.INVISIBLE : View.VISIBLE);
        mArrowImage.setOnClickListener(action -> {
            mActivity.onBackPressed();
        });

        HashMap<String, GetUserVoucherResponse.Status> statusAssociatedString = getStatusAssociatedString();
        mUnusedVouchersAdapter = new ViewVouchersRecyclerViewAdapter(
                new ArrayList<>(), statusAssociatedString, mActivity);
        mUsedVouchersAdapter = new ViewVouchersRecyclerViewAdapter(
                new ArrayList<>(), statusAssociatedString, mActivity);

        LinearLayoutManager unusedVouchersLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager usedVouchersLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

        mUnusedVouchersRecyclerViewPager.setAdapter(mUnusedVouchersAdapter);
        mUnusedVouchersRecyclerViewPager.setLayoutManager(unusedVouchersLayoutManager);
        mUsedVouchersRecyclerViewPager.setAdapter(mUsedVouchersAdapter);
        mUsedVouchersRecyclerViewPager.setLayoutManager(usedVouchersLayoutManager);

        showLoading();
        mPresenter.loadMyVouchers();
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @Override
    public void updateVoucherList(Pair<List<GetUserVoucherResponse.Voucher>, GetVouchersResponse> pair) {
        hideLoading();
        HashMap<String, GetUserVoucherResponse.Status> statusAssociatedString = getStatusAssociatedString();
        if (pair.first != null && pair.first.size() != 0) {
            mRootLayout.setVisibility(View.VISIBLE);
            mNoVouchersLayout.setVisibility(View.GONE);
            mUnusedVouchersRecyclerViewPager.getLayoutManager().scrollToPosition(0);

            List<GetUserVoucherResponse.Voucher> usedVouchers = new ArrayList<>();
            List<GetUserVoucherResponse.Voucher> currentVouchers = new ArrayList<>();

            for (int i = 0; i < pair.first.size(); i++) {

                String statusString = pair.first.get(i).getStatus();
                GetUserVoucherResponse.Status status = statusAssociatedString.get(statusString);
                if (status == null) {
                    status = GetUserVoucherResponse.Status.NORMAL;
                }
                switch (status) {
                    case ALREADY_SPENT:
                    case EXPIRED:
                        usedVouchers.add(pair.first.get(i));
                        break;
                    default:
                        currentVouchers.add(pair.first.get(i));
                }
            }

            mUnusedVouchersAdapter.replace(currentVouchers);
            mUnusedVouchersRecyclerViewPager.setVisibility(View.VISIBLE);

            mVouchersDescText.setVisibility(View.VISIBLE);

            if (!usedVouchers.isEmpty()) {
                mUsedVouchersAdapter.replace(usedVouchers);
                mDivider.setVisibility(View.VISIBLE);
                mUsedVoucherIndicatorText.setVisibility(View.VISIBLE);
                mUsedVouchersRecyclerViewPager.setVisibility(View.VISIBLE);
            } else {
                mDivider.setVisibility(View.GONE);
                mUsedVoucherIndicatorText.setVisibility(View.GONE);
            }
        } else {
            mRootLayout.setVisibility(View.GONE);
            mNoVouchersLayout.setVisibility(View.VISIBLE);
        }

    }

    private HashMap<String, GetUserVoucherResponse.Status> getStatusAssociatedString() {
        HashMap<String, GetUserVoucherResponse.Status> statusAssociatedString = new HashMap<>();
        for (GetUserVoucherResponse.Status status : GetUserVoucherResponse.AllStatus) {
            statusAssociatedString.put(mPresenter.getVoucherStatusString(status), status);
        }
        return statusAssociatedString;
    }
}
