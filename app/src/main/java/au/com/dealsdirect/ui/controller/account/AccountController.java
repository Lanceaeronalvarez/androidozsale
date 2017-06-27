package au.com.dealsdirect.ui.controller.account;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;

import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.auth.AuthHandler;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.account.adapter.AccountItemAdapter;
import au.com.dealsdirect.ui.controller.account.listener.AccountItemClickListener;
import au.com.dealsdirect.ui.controller.address.viewaddress.ViewAddressController;
import au.com.dealsdirect.ui.controller.contact.viewcontacts.ViewContactsController;
import au.com.dealsdirect.ui.controller.details.DetailsController;
import au.com.dealsdirect.ui.controller.language.LanguageController;
import au.com.dealsdirect.ui.controller.orders.orders.OrdersController;
import au.com.dealsdirect.ui.controller.vouchers.View.ViewViewVouchersController;
import au.com.dealsdirect.ui.main.MainMvpView;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;

/**
 * dp Created by Admin on 6/6/17.
 */

public class AccountController extends BaseController implements AccountMvpView {

    public static final String TAG = "AccountController";
    private static final String KEY_TEXT = "AccountController.KEY_TEXT";

    private AccountItemClickListener accountItemClickListener;

    @BindView(R.id.partial_toolbar_title_view)
    TextView mTitleTextView;

    @BindView(R.id.account_recycler_view)
    RecyclerView mAccountRecyclerView;

    @Inject
    AccountMvpPresenter<AccountMvpView> mPresenter;

    public static AccountController newInstance() {

        return new AccountController(
                new BundleBuilder(new Bundle())
                        .build());
    }

    public AccountController(Bundle args) {
        super(args);
    }

    @NonNull
    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_account, container, false);

        getControllerComponent().inject(this);

        mPresenter.onAttach(this);

        return view;
    }

    @Override
    public void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);

    }

    @Override
    protected void setUp(View view) {
        // Setup views here

        mPresenter.loadAccountItems();
        mTitleTextView.setText("My Account");
    }

    @Override
    public void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @Override
    protected void onSaveViewState(@NonNull View view, @NonNull Bundle outState) {
        super.onSaveViewState(view, outState);

    }

    @Override
    public void showAccountItems(List<String> accountItems) {
        AccountItemAdapter accountItemAdapter
                = new AccountItemAdapter(accountItems, getActivity(), mPresenter);
        mAccountRecyclerView.setAdapter(accountItemAdapter);
        mAccountRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAccountRecyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    @Override
    public void showMyDetailsController() {
        getRouter().pushController(RouterTransaction.with(DetailsController.newInstance())
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));
    }

    @Override
    public void showMyAddressesController() {
        getRouter().pushController(RouterTransaction.with(new ViewAddressController())
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));
    }

    @Override
    public void showMyOrders() {
        getRouter().pushController(RouterTransaction.with(new OrdersController())
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));
    }

    @Override
    public void showMyVouchers() {
        getRouter().pushController(RouterTransaction.with(ViewViewVouchersController.newInstance())
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));
    }

    @Override
    public void showMyReturns() {

    }

    @Override
    public void showViewContactUsController() {
        getRouter().pushController(RouterTransaction.with(ViewContactsController.newInstance())
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));
    }

    @Override
    public void showLanguage() {
        getRouter().pushController(RouterTransaction.with(LanguageController.newInstance())
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));

    }

    @Override
    public void triggerLogin(String option) {
//
//        switch (option) {
//
//            case "My Details":
//                break;
//            case "My Addresses":
//                break;
//            case "My Orders":
//                break;
//            case "My Vouchers":
//                break;
//            case "My Returns":
//                break;
//            case "Contact Us":
//                if (new Auth().isAuthorized()) {
//                    showViewContactUsController();
//                } else {
//                    new Auth().invokeLogin(getRouter(), new AuthHandler() {
//                        @Override
//                        public void success() {
//                            showViewContactUsController();
//                        }
//
//                        @Override
//                        public void error() {
//
//                        }
//                    });
//                }
//                break;
//            case "Language":
//                break;
//
//   	    default:
//                    break;
//            }

        ((MainMvpView)getActivity()).showLoginController(getRouter(),new AuthHandler() {
            @Override
            public void success() {
                mPresenter.onAccountItemClick(option);
            }

            @Override
            public void error() {

            }
        });
    }

}

         

