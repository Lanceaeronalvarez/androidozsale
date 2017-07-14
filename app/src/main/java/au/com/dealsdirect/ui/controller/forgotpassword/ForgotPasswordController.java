package au.com.dealsdirect.ui.controller.forgotpassword;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.SampleResponse;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.utils.BundleBuilder;

/*
 * Created by Ayi on 05/06/2017.
 */

public class ForgotPasswordController extends BaseController implements ForgotPasswordMvpView {

    public static final String TAG = "ForgotPasswordController";

    private static final String KEY_TEXT = "ForgotPassword.KEY_TEXT";

    @Inject
    ForgotPasswordMvpPresenter<ForgotPasswordMvpView> mPresenter;

    public static ForgotPasswordController newInstance(String arg) {

        return new ForgotPasswordController(
                new BundleBuilder(new Bundle())
                        .putString(KEY_TEXT, arg)
                        .build());
    }

    public ForgotPasswordController(Bundle args) {
        super(args);
    }

    @NonNull
    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_sample, container, false);

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
        //mPresenter.loadSample(new SampleRequest());

    }

    @Override
    public void onDestroyView(View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @Override
    public void showSample(SampleResponse response) {

    }

}
