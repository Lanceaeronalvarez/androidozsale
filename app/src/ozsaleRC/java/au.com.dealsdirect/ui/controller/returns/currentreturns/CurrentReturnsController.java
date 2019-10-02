package au.com.dealsdirect.ui.controller.returns.currentreturns;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.ControllerChangeHandler;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.returns.currentreturn.CurrentReturnResponseBody;
import au.com.dealsdirect.data.network.model.returns.currentreturn.CurrentReturns;
import au.com.dealsdirect.data.network.model.returns.returndetails.GetReturnDetailRequest;
import au.com.dealsdirect.data.network.model.returns.returndetails.GetReturnDetailsResponseBody;
import au.com.dealsdirect.ui.base.BasePullToRefreshController;
import au.com.dealsdirect.ui.controller.returns.currentreturns.adapter.CurrentReturnAdapter;
import au.com.dealsdirect.ui.controller.returns.currentreturns.listener.CurrentReturnClickListener;
import au.com.dealsdirect.ui.controller.returns.returndetails.ReturnDetailsController;
import au.com.dealsdirect.ui.controller.returns.returnorders.ReturnOrdersController;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;
import butterknife.OnClick;

/*
 * Created by dp on 05/06/2017.
 */

public class CurrentReturnsController extends BasePullToRefreshController
        implements CurrentReturnsMvpView {

    public static final String TAG = "CurrentReturnsController";
    private static final String KEY_TEXT = "CurrentReturnsController.KEY_TEXT";

    private CurrentReturnClickListener mCurrentReturnsListener;
    private HashMap<Integer, GetReturnDetailsResponseBody> returnItemsMap = new HashMap<>();
    private List<GetReturnDetailsResponseBody> returnDetailsResponseBodyList = new ArrayList<>();
    private List<CurrentReturns> mCurrentReturns;
    private int itemIterator = 0;
    private CurrentReturnAdapter mCurrentReturnsAdapter;

    @BindView(R.id.partial_toolbar_left_view)
    View mToolbarLeftView;

    @BindView(R.id.partial_toolbar_title)
    TextView mCurrentReturnsToolbarTitle;

    @BindView(R.id.partial_toolbar_right_view)
    ImageView mCurrentReturnsRightOption;

    @BindView(R.id.controller_current_returns_recycler_view)
    RecyclerView mCurrentReturnsRecyclerView;

    @BindView(R.id.no_returns_placeholder)
    RelativeLayout mPlaceholderLayout;

    @BindView(R.id.controller_current_returns_request_button)
    Button mCurrentReturnsRequestButton;

    @Inject
    CurrentReturnsMvpPresenter<CurrentReturnsMvpView> mPresenter;

    public static CurrentReturnsController newInstance() {

        return new CurrentReturnsController(
                new BundleBuilder(new Bundle())
                        .build());
    }

    public CurrentReturnsController(Bundle args) {
        super(args);
    }

    @NonNull
    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = super.inflateView(inflater, container, ToolBarType.ARROW);

        setToolBarVisible(getResource().getBoolean(R.bool.returns_toolbar_visibility));
        fillContent(inflater.inflate(R.layout.controller_current_returns, container, false));

        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        return view;
    }

    @Override
    public void onRefreshStart() {
        super.onRefreshStart();
        mPresenter.loadCurrentReturns();
    }

    @Override
    public void refreshContents() {
        super.refreshContents();
        mPresenter.loadCurrentReturns();
    }

    @Override
    public void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);

    }

    @Override
    protected void setUp(View view) {

        mCurrentReturnsRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));

        mToolbarLeftView.setVisibility(mPresenter.isTablet() ? View.INVISIBLE : View.VISIBLE);
        mCurrentReturnsToolbarTitle.setText(getString(R.string.account_returns));
        if (mPresenter.isTablet()) {
            mCurrentReturnsRightOption.setPadding(5, 5, 5, 5);
        } else {
            mCurrentReturnsRightOption.setPadding(20, 20, 20, 20);
        }
        mCurrentReturnsRightOption.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
        mCurrentReturnsRightOption.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onDestroyView(View view) {
        mPresenter.onDetach();
        mCurrentReturnsRecyclerView.setAdapter(null);
        mCurrentReturnsAdapter = null;
        super.onDestroyView(view);
    }

    @Override
    public void onViewDidAppear(Controller previousController) {
        super.onViewDidAppear(previousController);
        updateToolbar();

        if (mCurrentReturns == null || mCurrentReturns.size() == 0) {
            showLoading();
        }
        mPresenter.loadCurrentReturns();
    }

    @Override
    public void showCurrentReturns(CurrentReturnResponseBody currentReturnResponseBody) {
        List<CurrentReturns> currentReturns = currentReturnResponseBody
                .getCurrentReturnResponse().getCurrentReturns();

        if (currentReturns != null && currentReturns.size() != 0) {
            currentReturns = new ArrayList<>(currentReturns);

            mCurrentReturnsRequestButton.setVisibility(View.VISIBLE);
            mCurrentReturnsRightOption.setVisibility(View.INVISIBLE);

            mPlaceholderLayout.setVisibility(View.GONE);
            mCurrentReturnsRecyclerView.setVisibility(View.VISIBLE);

            if (mCurrentReturnsAdapter == null) {
                mCurrentReturnsAdapter = new CurrentReturnAdapter(mActivity, currentReturns, returnDetailsResponseBodyList, mPresenter);
            } else {
                mCurrentReturnsAdapter.updateCurrentReturnsList(currentReturns);
            }
            mCurrentReturnsRecyclerView.setAdapter(mCurrentReturnsAdapter);

            scrollToNewReturn(mCurrentReturns, currentReturns);
            mCurrentReturns = currentReturns;

            getCurrentReturnItems(mCurrentReturns);


        } else {

            mPlaceholderLayout.setVisibility(View.VISIBLE);
            mCurrentReturnsRecyclerView.setVisibility(View.GONE);
            mCurrentReturnsRequestButton.setVisibility(View.VISIBLE);
            mCurrentReturnsRightOption.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void showCurrentReturnDetails(GetReturnDetailsResponseBody getReturnDetailsResponseBody) {

        returnItemsMap.put(itemIterator, getReturnDetailsResponseBody);
        if (returnItemsMap.size() == mCurrentReturns.size()) {
            returnDetailsResponseBodyList.clear();

            for (int i = 0; i < returnItemsMap.size(); i++) {
                if (!returnItemsMap.isEmpty())
                    returnDetailsResponseBodyList.add(returnItemsMap.get(i));
            }
            mCurrentReturnsAdapter.updateReturnDetailsResponseBody(returnDetailsResponseBodyList);

        } else {
            if (!returnItemsMap.isEmpty())
                returnDetailsResponseBodyList.add(returnItemsMap.get(itemIterator));

            itemIterator = itemIterator + 1;
            if (!mCurrentReturns.isEmpty())
                mPresenter.loadReturnDetails(createReturnDetailsRequest(mCurrentReturns.get(itemIterator).getID()));
        }
    }

    public void getCurrentReturnItems(List<CurrentReturns> currentReturns) {
        if (!mCurrentReturns.isEmpty())
            mPresenter.loadReturnDetails(createReturnDetailsRequest(currentReturns.get(itemIterator).getID()));

    }

    @Override
    public void onCurrentReturnClickListener(
            int orderNumber,
            int position,
            String productName,
            String productRequestStatus,
            String productRAN,
            String returnRequestDateFormat,
            String isRequestApproved,
            String returnId) {

        getRouter().pushController(RouterTransaction.with(
                ReturnDetailsController.newInstance(
                        productName,
                        orderNumber,
                        returnId,
                        returnRequestDateFormat,
                        isRequestApproved,
                        productRequestStatus,
                        productRAN))
                .pushChangeHandler(new FadeChangeHandler())
                .popChangeHandler(new FadeChangeHandler()));

    }

    @OnClick(R.id.partial_toolbar_left_view)
    public void onBackClick() {
        mActivity.onBackPressed();
    }

    @OnClick(R.id.controller_current_returns_request_button)
    public void onRequestReturnClick() {
        requestNewReturn();
    }

    @OnClick(R.id.partial_toolbar_right_view)
    public void onToolbarRequestReturnClick() {
        requestNewReturn();
    }

    private void requestNewReturn() {
        getRouter().pushController(RouterTransaction.with(
                ReturnOrdersController.newInstance())
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));
    }

    public GetReturnDetailRequest createReturnDetailsRequest(String itemID) {
        return new GetReturnDetailRequest(itemID);
    }

    private void updateToolbar(){
        mCurrentReturnsRightOption.setVisibility(View.INVISIBLE);
        mCurrentReturnsRequestButton.setVisibility(View.VISIBLE);
    }

    private void scrollToNewReturn(List<CurrentReturns> previousList, List<CurrentReturns> newList) {
        if (previousList == null || newList == null) {
            return;
        }

        int index = -1;
        for(int i = 0; i < newList.size(); i++) {
            CurrentReturns currentReturn = newList.get(i);
            if (!previousList.contains(currentReturn)) {
                index = i;
                break;
            }
        }

        if (index >= 0 && mCurrentReturnsRecyclerView != null) {
            mCurrentReturnsRecyclerView.scrollToPosition(index);
        }
    }
}
