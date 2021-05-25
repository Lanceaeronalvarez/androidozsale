package au.com.dealsdirect.ui.controller.returns.returnspolicy;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.returns.returnspolicy.ReturnsPolicyRecyclerViewAdapter.FaqItem;
import au.com.dealsdirect.ui.controller.returns.returnspolicy.ReturnsPolicyRecyclerViewAdapter.Item;
import au.com.dealsdirect.ui.controller.returns.returnspolicy.ReturnsPolicyRecyclerViewAdapter.SubtitleItem;
import au.com.dealsdirect.ui.controller.returns.returnspolicy.ReturnsPolicyRecyclerViewAdapter.TopItem;
import au.com.dealsdirect.ui.controller.shops.BottomSheetInfoDialog;
import au.com.dealsdirect.utils.ActionConstants;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;
import butterknife.OnClick;

public class ReturnsPolicyViewController extends BaseController implements ReturnsPolicyMvpView {

    @Inject
    ReturnsPolicyMvpPresenter<ReturnsPolicyMvpView> mPresenter;

    @BindView(R.id.partial_toolbar_title)
    TextView mTitleTextView;

    @BindView(R.id.partial_toolbar_left_view)
    View mLeftToolbarButton;

    @BindView(R.id.controller_returns_policy_recyclerview)
    RecyclerView mRecyclerView;

    ReturnsPolicyRecyclerViewAdapter mAdapter;
    ReturnsPolicyTopRecyclerViewAdapter mTopAdapter;

    public static ReturnsPolicyViewController newInstance() {

        return new ReturnsPolicyViewController(
                new BundleBuilder(new Bundle())
                        .build());
    }

    public ReturnsPolicyViewController(Bundle args) {
        super(args);
    }


    @Override
    public void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    @Override
    protected void onAttach(@NonNull View view) {
        mPresenter.onAttach(this);
        super.onAttach(view);
    }

    @Override
    public void onDetach(View view) {
        mPresenter.onDetach();
        super.onDetach(view);
    }


    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_returns_policy, container, false);

        getControllerComponent().inject(this);

        mPresenter.onAttach(this);
        return view;
    }

    @Override
    protected void setUp(View view) {
        mTitleTextView.setText(R.string.account_returns_policy);
        mLeftToolbarButton.setVisibility(mPresenter.isTablet() ? View.INVISIBLE : View.VISIBLE);

        setupTopAdapter();
        setupAdapter();
        final LinearLayoutManager layoutManager = new LinearLayoutManager(
                mRecyclerView.getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void setupTopAdapter() {
        TypedArray stepTitlesIds = mActivity.getResources().obtainTypedArray(R.array.returns_policy_step_titles);
        TypedArray stepDescriptionsIds = mActivity.getResources().obtainTypedArray(R.array.returns_policy_step_descriptions);

        ArrayList<String> stepTitles = new ArrayList<>();
        ArrayList<String> stepDescriptions = new ArrayList<>();
        ArrayList<Integer> stepDrawableIds = new ArrayList<Integer>() {{
            add(R.drawable.returns_policy_step_1);
            add(R.drawable.returns_policy_step_2);
            add(R.drawable.returns_policy_step_3);
            add(R.drawable.returns_policy_step_4);
        }};

        for (int i = 0; i < stepTitlesIds.length() && i < stepDescriptionsIds.length() && i < stepDrawableIds.size(); i++) {
            stepTitles.add(mActivity.getString(stepTitlesIds.getResourceId(i, 0)));
            stepDescriptions.add(mActivity.getString(stepDescriptionsIds.getResourceId(i, 0)));
        }

        mTopAdapter = new ReturnsPolicyTopRecyclerViewAdapter(
                stepTitles,
                stepDescriptions,
                stepDrawableIds
        );

        stepTitlesIds.recycle();
        stepDescriptionsIds.recycle();
    }

    private void setupAdapter() {
        ArrayList<Item> items = new ArrayList<Item>() {{
            add(new SubtitleItem("RETURNS POLICY"));
            add(new TopItem(mTopAdapter, ReturnsPolicyViewController.this::gotoMyReturns));
            add(new SubtitleItem("FREQUENTLY ASKED QUESTIONS"));
        }};

        TypedArray faqItemTitleIds = mActivity.getResources().obtainTypedArray(R.array.returns_policy_faq_titles);
        TypedArray faqItemContentIds = mActivity.getResources().obtainTypedArray(R.array.returns_policy_faq_content);

        for (int i = 0; i < faqItemTitleIds.length() && i < faqItemContentIds.length(); i++) {
            final String title = mActivity.getString(faqItemTitleIds.getResourceId(i, 0));
            final String content = mActivity.getString(faqItemContentIds.getResourceId(i, 0));
            final FaqItem faqItem = new FaqItem(title, mActivity.getDrawable(R.drawable.ic_gray_chevron), () -> showFaq(title, content));
            items.add(faqItem);
        }

        mAdapter = new ReturnsPolicyRecyclerViewAdapter(items);

        faqItemTitleIds.recycle();
        faqItemContentIds.recycle();
    }

    private void gotoMyReturns() {
        mActivity.getAccountController().showMyReturns();
    }

    @OnClick(R.id.partial_toolbar_left_view)
    public void onBackClick() {
        getRouter().popCurrentController();
    }

    private void showFaq(String title, String content) {
        BottomSheetInfoDialog bottomSheetFragment = new BottomSheetInfoDialog();

        bottomSheetFragment.setTitle(title);
        bottomSheetFragment.setDescription(content);
        bottomSheetFragment.setupButton(mActivity.getString(R.string.go_to_my_returns), this::gotoMyReturns);
        bottomSheetFragment.setDismissOnButtonClick(true);

        bottomSheetFragment.show(mActivity.getSupportFragmentManager(), ActionConstants.ORDER_BOTTOM_DIALOG_TAG);
    }
}
