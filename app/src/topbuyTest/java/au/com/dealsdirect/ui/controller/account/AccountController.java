package au.com.dealsdirect.ui.controller.account;

import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.account.adapter.AccountItemAdapter;
import au.com.dealsdirect.ui.main.MainActivity;
import butterknife.BindView;

/**
 * Created by smartwave on 02/11/2017.
 */

public class AccountController extends BaseController implements AccountMvpView {

    public static final String TAG = "AccountController";
    private static final String KEY_TEXT = "AccountController.KEY_TEXT";
    AccountItemAdapter accountItemAdapter;


    @BindView(R.id.account_recycler_view)
    RecyclerView mAccountRecyclerView;

    @Inject
    AccountMvpPresenter<AccountMvpView> mPresenter;

    @Inject
    MainActivity mActivity;

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_account, container, false);

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

    }

    @Override
    public void showAccountItems(List<String> accountItems, int[] accountImages) {
        accountItemAdapter = new AccountItemAdapter(accountItems, accountImages, mActivity, mPresenter);
        mAccountRecyclerView.setAdapter(accountItemAdapter);
        mAccountRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity,LinearLayoutManager.VERTICAL,false));
        mAccountRecyclerView.setItemAnimator(new DefaultItemAnimator());
        accountItemAdapter.notifyDataSetChanged();
    }

    @Override
    public void showMyDetailsController() {

    }

    @Override
    public void showMyAddressesController() {

    }

    @Override
    public void showMyOrders() {

    }

    @Override
    public void showMyVouchers() {

    }

    @Override
    public void showMyReturns() {

    }

    @Override
    public void showMyPaymentsController() {

    }

    @Override
    public void showLanguage() {

    }

    @Override
    public void showLegalities(String key, String Title) {

    }

    @Override
    public void triggerLogin(String option) {

    }

    @Override
    public void triggerLogout() {

    }

    @Override
    public void initLoginDrawable() {

    }
}
