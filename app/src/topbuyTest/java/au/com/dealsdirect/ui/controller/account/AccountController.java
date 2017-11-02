package au.com.dealsdirect.ui.controller.account;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.auth.AuthHandler;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;

/**
 * Created by smartwave on 02/11/2017.
 */

public class AccountController extends BaseController implements AccountMvpView {

    public static final String TAG = "AccountController";
    private static final String KEY_TEXT = "AccountController.KEY_TEXT";
    AccountItemAdapter accountItemAdapter;


    private int[] accountIcons = new int[]{
            R.drawable.ic_account_my_orders,
            R.drawable.ic_account_my_payments,
            R.drawable.ic_account_my_details,
            R.drawable.ic_account_my_addresses,
            R.drawable.ic_account_my_returns,
            R.drawable.ic_account_my_vouchers,
            R.drawable.ic_account_invite_a_friend,
            R.drawable.ic_account_language,
            R.drawable.ic_account_contact_us,
            R.drawable.ic_account_about_us,
            R.drawable.ic_account_privacy_policy,
            R.drawable.ic_account_terms_and_conditions,
            R.drawable.ic_account_tutorial,
            R.drawable.ic_account_logout
    };

    private ArrayList<String> accountItems = new ArrayList(Arrays.asList(
                    //"my cart",
                    "my orders",
                    "my payments",
                    "my details",
                    "my addresses",
                    "my returns",
                    "my vouchers",
                    "invite friends",
                    "language",
                    "contact us",
                    "about us",
                    "privacy policy",
                    "terms & conditions",
                    "tutorial"));


    @BindView(R.id.account_recycler_view)
    RecyclerView mAccountRecyclerView;

    @Inject
    AccountMvpPresenter<AccountMvpView> mPresenter;

    @Inject
    MainActivity mActivity;

    public static AccountController newInstance() {

        return new AccountController(
                new BundleBuilder(new Bundle())
                        .build());
    }

    public AccountController(Bundle args) {
        super(args);
    }


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
        // Setup views here
        assert (mActivity) != null;
        if(mPresenter.isAuthorized()){
            accountItems.add("logout");
        }
        mPresenter.loadAccountItems(accountItems,accountIcons);
    }

    @Override
    public void showAccountItems(List<String> accountItems, int[] accountImages) {
        accountItemAdapter = new AccountItemAdapter(accountItems, accountImages, mPresenter);
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
    public void showContactUs() {

    }

    @Override
    public void showTutorial() {

    }

    @Override
    public void showInviteAFriend() {

    }

    @Override
    public void showLegalities(String key, String Title) {

    }

    @Override
    public void triggerLogin(String option) {
        AccountMvpView mvpView = this;

        mActivity.showLoginController(getRouter(), new AuthHandler() {
            @Override
            public void success() {
                mPresenter.onAttach(mvpView);
                mPresenter.onAccountItemClick(option);
                mActivity.callGCMRegisterSubscriber();
            }

            @Override
            public void error() {
                mPresenter.onAttach(mvpView);
            }
        });
    }

    @Override
    public void triggerLogout() {

    }

    @Override
    public void initLoginDrawable() {

    }
}
