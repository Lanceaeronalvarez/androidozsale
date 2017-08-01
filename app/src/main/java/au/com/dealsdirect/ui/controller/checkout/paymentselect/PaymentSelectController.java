package au.com.dealsdirect.ui.controller.checkout.paymentselect;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods.PaymentMethod;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.checkout.addpayment.AddPaymentController;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.ui.custom.RecyclerOnTouchListener;
import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.JsonUtils;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by smartwave on 30/06/2017.
 */

public class PaymentSelectController extends BaseController implements PaymentSelectMvpView {

    private static final String PAYMENT_METHODS = "PaymentMethods";
    private final static String IS_FROM_CART = "IsFromCart";

    @Inject
    PaymentSelectMvpPresenter<PaymentSelectMvpView> mPresenter;

    @BindView(R.id.payment_select_recyclerview)
    RecyclerView mRecyclerView;


    @BindView(R.id.partial_toolbar_arrow_title)
    TextView mPaymentSelectToolbarTitle;
    @BindView(R.id.partial_toolbar_filter_view)
    ImageView mPaymentSelectRightOption;

    PaymentSelectAdapter mAdapter;
    MainActivity mActivity;

    private ArrayList<PaymentMethod> mPaymentMethods = new ArrayList<>();
    private boolean isFromCart = false;


    public PaymentSelectController(String paymentMethodsJsonString, boolean isFromCart) {
        this(new BundleBuilder(new Bundle())
                .putString(PAYMENT_METHODS, paymentMethodsJsonString)
                .putBoolean(IS_FROM_CART, isFromCart)
                .build());
    }

    public PaymentSelectController(Bundle args) {
        super(args);
        mPaymentMethods = JsonUtils.convertStringToObject(args.getString(PAYMENT_METHODS), new TypeToken<ArrayList<PaymentMethod>>() {
        }.getType());
        if (mPaymentMethods == null) {
            mPaymentMethods = new ArrayList<>();
        }
        isFromCart = args.getBoolean(IS_FROM_CART);
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_payment_select, container, false);
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        mActivity = ((MainActivity) getActivity());
        setUp(view);
    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);
    }

    @Override
    public void showPaymentList(List<PaymentMethod> paymentMethods) {
        if (paymentMethods != null) {
            mPaymentMethods = new ArrayList<>(paymentMethods);
            mAdapter.replaceData(mPaymentMethods);
        } else {
            getRouter().pushController(RouterTransaction.with(new AddPaymentController(false))
                    .pushChangeHandler(new HorizontalChangeHandler())
                    .popChangeHandler(new HorizontalChangeHandler()));
        }
    }

    @Override
    public void showRemovePaymentMethodResult(PaymentMethod paymentMethod, boolean result, String message) {
        mActivity.setPaymentMethodSelected(null);

        if (result) {
            mPaymentMethods.remove(paymentMethod);
            mAdapter.notifyDataSetChanged();

            CustomAlertDialog.showCustomAlertDialog(mActivity, CustomAlertDialog.CustomDialogIconState.POSITIVE, "Payment method removed!");
//            DialogUtils.showYesDialog(mActivity, "Success", "Payment method removed!", "OK", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                }
//            });

        } else {
            CustomAlertDialog.showCustomAlertDialog(mActivity, CustomAlertDialog.CustomDialogIconState.NEGATIVE, "An error occured.");

//            DialogUtils.showYesDialog(mActivity, "Failed", "Please try again.", "OK", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                }
//            });
        }
    }

    @Override
    protected void setUp(View view) {

        if (!isFromCart) {
            showLoading();
            mPresenter.fetchUserPaymentMethods();
        }

        mPaymentSelectToolbarTitle.setText("Add Payment Method");
        if(mPresenter.isTablet()){
            mPaymentSelectRightOption.setPadding(5, 5, 5, 5);
        } else {
            mPaymentSelectRightOption.setPadding(20, 20, 20, 20);
        }
        mPaymentSelectRightOption.setImageDrawable(getApplicationContext().getDrawable(R.drawable.ic_add));

        mAdapter = new PaymentSelectAdapter(mActivity, mPaymentMethods, mPresenter, isFromCart);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnItemTouchListener(new RecyclerOnTouchListener(mActivity, new RecyclerOnTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                if (isFromCart) {
                    mActivity.setPaymentMethodSelected(mPaymentMethods.get(position));
                    mAdapter.notifyDataSetChanged();
                    mActivity.onBackPressed();
                } else {
                    mPresenter.removeUserPaymentMethod(mPaymentMethods.get(position));
                }
            }
        }));
    }

    @OnClick(R.id.partial_toolbar_arrow_view)
    public void onBackClick() {
        getActivity().onBackPressed();
    }

    @OnClick(R.id.partial_toolbar_filter_view)
    public void onAddPaymentMethod() {
        getRouter().pushController(RouterTransaction.with(new AddPaymentController(true))
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));
    }

}
