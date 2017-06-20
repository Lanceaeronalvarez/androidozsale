package au.com.dealsdirect.ui.controller.contact;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.sample.SampleController;
import au.com.dealsdirect.utils.BundleBuilder;

/**
 * dp Created by Admin on 6/6/17.
 */

public class ViewContactsController extends BaseController implements ViewContactsMvpView {

    public static final String TAG = "ContactController";

    private static final String KEY_TEXT = "ContactController.KEY_TEXT";

    @Inject
    ViewContactsMvpPresenter<ViewContactsMvpView> mPresenter;

    public static SampleController newInstance() {

        return new SampleController(
                new BundleBuilder(new Bundle())
                        .build());
    }

    public ViewContactsController(Bundle args) {
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
}
