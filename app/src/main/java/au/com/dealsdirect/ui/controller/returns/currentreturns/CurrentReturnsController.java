package au.com.dealsdirect.ui.controller.returns.currentreturns;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.returns.createreturn.ReturnReceivedRequest;
import au.com.dealsdirect.data.network.model.returns.createreturn.ReturnReceivedSatisfactionValue;
import au.com.dealsdirect.data.network.model.returns.currentreturn.CurrentReturn;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.account.AccountController;
import au.com.dealsdirect.ui.controller.login.PopUpHostController;
import au.com.dealsdirect.ui.controller.returns.currentreturns.adapter.CurrentReturnAdapter;
import au.com.dealsdirect.ui.controller.returns.menu.ReturnsMenuHelper;
import au.com.dealsdirect.ui.controller.returns.returndetails.ReturnDetailsController;
import au.com.dealsdirect.ui.controller.returns.returnorders.ReturnOrdersController;
import au.com.dealsdirect.ui.controller.returns.returnsteps.ReturnTrackingClickListener;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.LoadingDialogType;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;
import butterknife.OnClick;

/*
 * Created by dp on 05/06/2017.
 */

public class CurrentReturnsController extends BaseController implements CurrentReturnsMvpView {

    public static final String TAG = "CurrentReturnsController";

    private List<CurrentReturn> mCurrentReturns;
    private CurrentReturnAdapter mCurrentReturnsAdapter;

    private final HashMap<String, Boolean> hasSetSatisfaction = new HashMap<>();

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
        View view = inflater.inflate(R.layout.controller_current_returns, container, false);

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

        if (previousController == null || previousController instanceof AccountController) {
            openInfo();
        }

        if (mCurrentReturns == null || mCurrentReturns.size() == 0) {
            showLoading(LoadingDialogType.DEFAULT);
        }
        mPresenter.loadCurrentReturns();
    }

    @Override
    public void showCurrentReturns(List<CurrentReturn> currentReturns) {
        if (currentReturns != null && currentReturns.size() != 0) {
            currentReturns = new ArrayList<>(currentReturns);

            mCurrentReturnsRequestButton.setVisibility(View.VISIBLE);
//            mCurrentReturnsRightOption.setVisibility(View.VISIBLE);
            mCurrentReturnsRightOption.setImageResource(R.drawable.ic_info_encircled);

            mPlaceholderLayout.setVisibility(View.GONE);
            mCurrentReturnsRecyclerView.setVisibility(View.VISIBLE);

            if (mCurrentReturnsAdapter == null) {
                mCurrentReturnsAdapter = new CurrentReturnAdapter(currentReturns, new CurrentReturnAdapter.CurrentReturnItemListener() {
                    @Override
                    public void itemSelected(CurrentReturn item) {
                        getRouter().pushController(RouterTransaction.with(
                                ReturnDetailsController.newInstance(item))
                                .pushChangeHandler(new FadeChangeHandler())
                                .popChangeHandler(new FadeChangeHandler()));

                    }

                    @Override
                    public void optionsOpened(CurrentReturn item, View anchor) {
                        final ReturnsMenuHelper.SelectOptionListener listener = () -> getRouter().pushController(RouterTransaction.with(
                                ReturnDetailsController.newInstance(item, true))
                                .pushChangeHandler(new FadeChangeHandler())
                                .popChangeHandler(new FadeChangeHandler()));
                        if (mPresenter.isTablet()) {
                            ReturnsMenuHelper.showPopupMenu(CurrentReturnsController.this, anchor, listener);
                        } else {
                            ReturnsMenuHelper.showCurrentReturnsDialog(CurrentReturnsController.this, listener);
                        }

                    }
                }, new ReturnTrackingClickListener() {
                    @Override
                    public void onReturnReceivedToggle(String returnId, boolean isReceived) {
                        ReturnReceivedRequest returnReceivedRequest = new ReturnReceivedRequest();
                        returnReceivedRequest.setReturnId(returnId);

                        Boolean hasSetSatisfaction = CurrentReturnsController.this.hasSetSatisfaction.get(returnId);
                        if (isReceived) {
                            if (hasSetSatisfaction == null) {
                                mPresenter.callGetReturnReceivedSatisfaction(returnReceivedRequest);
                            } else {
                                returnSatisfactionReceived(returnReceivedRequest, hasSetSatisfaction);
                            }
                        } else {
                            mPresenter.callSetReturnNotReceived(returnReceivedRequest);
                        }
                    }
                });
            } else {
                mCurrentReturnsAdapter.updateCurrentReturnsList(currentReturns);
            }
            mCurrentReturnsRecyclerView.setAdapter(mCurrentReturnsAdapter);

            scrollToNewReturn(mCurrentReturns, currentReturns);
            mCurrentReturns = currentReturns;
        } else {

            mPlaceholderLayout.setVisibility(View.VISIBLE);
            mCurrentReturnsRecyclerView.setVisibility(View.GONE);
            mCurrentReturnsRequestButton.setVisibility(View.VISIBLE);
            mCurrentReturnsRightOption.setVisibility(View.INVISIBLE);
        }
    }

    @OnClick(R.id.partial_toolbar_left_view)
    public void onBackClick() {
        mActivity.onBackPressed();
    }

    @OnClick(R.id.controller_current_returns_request_button)
    public void onRequestReturnClick() {
        requestNewReturn();
    }

    private void requestNewReturn() {
        getRouter().pushController(RouterTransaction.with(
                ReturnOrdersController.newInstance())
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));
    }

    private void updateToolbar() {
        mCurrentReturnsRightOption.setVisibility(View.INVISIBLE);
        mCurrentReturnsRequestButton.setVisibility(View.VISIBLE);
    }

    private void scrollToNewReturn(List<CurrentReturn> previousList, List<CurrentReturn> newList) {
        if (previousList == null || newList == null) {
            return;
        }

        int index = -1;
        for (int i = 0; i < newList.size(); i++) {
            CurrentReturn currentReturn = newList.get(i);
            if (!previousList.contains(currentReturn)) {
                index = i;
                break;
            }
        }

        if (index >= 0 && mCurrentReturnsRecyclerView != null) {
            mCurrentReturnsRecyclerView.scrollToPosition(index);
        }
    }

    @OnClick(R.id.partial_toolbar_right_view)
    public void openInfo() {
        final Router router = mActivity.getMainController().getPopUpHostRouter();
        final Bundle bundle = new BundleBuilder(new Bundle())
                .putSerializable(BundleKeys.KEY_POP_UP_HOST_DESTINATION, GateKeeper.Destination.CURRENT_RETURNS_INFO)
                .build();

        RouterTransaction routerTransaction = RouterTransaction
                .with(new PopUpHostController(bundle))
                .pushChangeHandler(new FadeChangeHandler())
                .popChangeHandler(new FadeChangeHandler());

        router.replaceTopController(routerTransaction);
    }

    @Override
    public void returnSatisfactionReceived(ReturnReceivedRequest request, boolean hasSetSatisfactionAlready) {
        if (hasSetSatisfactionAlready) {
            hasSetSatisfaction.put(request.getReturnId(), true);
            request.setSatisfaction(null);
            mPresenter.callSetReturnReceived(request);
        } else {
            hasSetSatisfaction.put(request.getReturnId(), false);
            mActivity.showReturnSatisfactionDialog(response -> {
                hasSetSatisfaction.put(request.getReturnId(), true);
                switch (response) {
                    case BottomSheetReturnSatisfactionDialog.POSITIVE_RESPONSE:
                        request.setSatisfaction(ReturnReceivedSatisfactionValue.GOOD.getValue());
                        break;
                    case BottomSheetReturnSatisfactionDialog.NEGATIVE_RESPONSE:
                        request.setSatisfaction(ReturnReceivedSatisfactionValue.BAD.getValue());
                        break;
                    default:
                        request.setSatisfaction(ReturnReceivedSatisfactionValue.NEUTRAL.getValue());
                        break;
                }
                mPresenter.callSetReturnReceived(request);
            });
        }
    }
}
