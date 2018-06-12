package au.com.dealsdirect.ui.controller.account;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;

/**
 * Created by smartwave on 11/06/2018.
 */

public class AccountsHostController extends BaseController implements AccountsHostMvpView {

    @Inject
    AccountsHostMvpPresenter<AccountsHostMvpView> mPresenter;

    @BindView(R.id.accounts_host_master_container)
    FrameLayout mMasterContainer;
    @BindView(R.id.accounts_host_detail_container)
    FrameLayout mDetailContainer;

    private Router mMasterRouter;
    private Router mDetailRouter;

    public static AccountsHostController newInstance() {

        return new AccountsHostController(
                new BundleBuilder(new Bundle())
                        .build());
    }

    public AccountsHostController(Bundle args) {
        super(args);
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_accounts_host,container,false);
        getControllerComponent().inject(this);
//        mPresenter.onAttach(this);
        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    @Override
    protected void setUp(View view) {
        mMasterRouter = getChildRouter(mMasterContainer);
        mDetailRouter = getChildRouter(mDetailContainer);

        mMasterRouter.setRoot(RouterTransaction.with(AccountController.newInstance()));
    }

    public Router getMasterRouter() {
        return mMasterRouter;
    }

    public Router getDetailRouter() {
        return mDetailRouter;
    }
}
