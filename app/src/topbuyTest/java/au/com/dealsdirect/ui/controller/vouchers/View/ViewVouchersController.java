package au.com.dealsdirect.ui.controller.vouchers.View;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.vouchers.GetUserVoucherResponse;
import au.com.dealsdirect.data.network.model.vouchers.GetVouchersResponse;
import au.com.dealsdirect.ui.base.SwipeableBaseToolBarController;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;

/**
 * Created by Paul on 6/23/17.
 */

public class ViewVouchersController extends SwipeableBaseToolBarController implements ViewVouchersMvpView {

    @Inject
    ViewVouchersMvpPresenter<ViewVouchersMvpView> mPresenter;

    @BindView(R.id.controller_text_vouchers_desc)
    TextView mVouchersDescText;

    @BindView(R.id.controller_text_used_voucher_indicator)
    TextView mUsedVoucherIndicatorText;

    @BindView(R.id.controller_recycler_view_used_vouchers)
    RecyclerView mUsedVouchersRecyclerView;

    @BindView(R.id.controller_recycler_view_unused_vouchers)
    RecyclerView mUnusedVouchersRecyclerView;

    @BindView(R.id.voucher_recycler_divider)
    View mDivider;

    @BindView(R.id.controller_vouchers_root_layout)
    RelativeLayout mRootLayout;

    @BindView(R.id.no_vouchers_placeholder)
    RelativeLayout mNoVouchersLayout;

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
        View view = super.inflateView(inflater, container);

        fillContent(inflater.inflate(R.layout.controller_view_vouchers, container, false));

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
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setupSwipingBehavior();
        setUp(view);
    }

    @Override
    protected void setUp(View view) {
        mToolbarTitle.setText(getActivity().getResources().getString(R.string.my_voucher));
        mUnusedVouchersAdapter = new ViewVouchersRecyclerViewAdapter(new ArrayList<>(), mActivity);
        mUsedVouchersAdapter = new ViewVouchersRecyclerViewAdapter
                (new ArrayList<>(), mActivity);

        LinearLayoutManager unusedVouchersLayoutManager
                = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

        LinearLayoutManager usedVouchersLayoutManager
                = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

        mUnusedVouchersRecyclerView.setAdapter(mUnusedVouchersAdapter);
        mUnusedVouchersRecyclerView.setLayoutManager(unusedVouchersLayoutManager);
        mUsedVouchersRecyclerView.setAdapter(mUsedVouchersAdapter);
        mUsedVouchersRecyclerView.setLayoutManager(usedVouchersLayoutManager);

        SnapHelper unusedVoucherHelper = new PagerSnapHelper();
        SnapHelper usedVoucherHelper = new PagerSnapHelper();

        unusedVoucherHelper.attachToRecyclerView(mUnusedVouchersRecyclerView);
        usedVoucherHelper.attachToRecyclerView(mUsedVouchersRecyclerView);

        mPresenter.loadMyVouchers();
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @Override
    public void updateVoucherList(Pair<List<GetUserVoucherResponse.Voucher>, GetVouchersResponse> pair) {
        if (pair.first != null && pair.first.size() != 0) {
            mRootLayout.setVisibility(View.VISIBLE);
            mNoVouchersLayout.setVisibility(View.GONE);
            mUnusedVouchersRecyclerView.getLayoutManager().scrollToPosition(0);

            List<GetUserVoucherResponse.Voucher> usedVouchers = new ArrayList<>();
            List<GetUserVoucherResponse.Voucher> currentVouchers = new ArrayList<>();

            for (int i = 0; i < pair.first.size(); i++) {

                String discountLeft = pair.first.get(i).getDiscountLeft();

                if (discountLeft.equalsIgnoreCase("Already Spent")) {
                    usedVouchers.add(pair.first.get(i));
                } else {
                    currentVouchers.add(pair.first.get(i));
                }
            }

            mUnusedVouchersAdapter.replace(currentVouchers);
            mUnusedVouchersRecyclerView.setVisibility(View.VISIBLE);


            if (!usedVouchers.isEmpty()) {
                mUsedVouchersAdapter.replace(usedVouchers);
                mDivider.setVisibility(View.VISIBLE);
                mUsedVoucherIndicatorText.setVisibility(View.VISIBLE);
                mUsedVouchersRecyclerView.setVisibility(View.VISIBLE);
            } else {
                mDivider.setVisibility(View.GONE);
                mUsedVoucherIndicatorText.setVisibility(View.GONE);
            }
        } else {
            mRootLayout.setVisibility(View.GONE);
            mNoVouchersLayout.setVisibility(View.VISIBLE);
        }

    }
}
