package au.com.dealsdirect.ui.sample;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.SampleResponse;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;
import butterknife.OnClick;

/*
 * Created by Ayi on 05/06/2017.
 */

public class SampleController extends BaseController implements SampleMvpView {

    public static final String TAG = "SampleController";

    private static final String KEY_TEXT = "SampleController.KEY_TEXT";

    @Inject
    SampleMvpPresenter<SampleMvpView> mPresenter;

    @BindView(R.id.controller_sample_text)
    TextView mTitleTextView;

    public static SampleController newInstance(String arg) {

        return new SampleController(
                new BundleBuilder(new Bundle())
                        .putString(KEY_TEXT, arg)
                        .build());
    }

    public SampleController(Bundle args) {
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

        mTitleTextView.setText(getArgs().getString(KEY_TEXT));
    }

    @Override
    public void onDestroyView(View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @OnClick(R.id.controller_sample_button)
    void onButtonClick() {
        mTitleTextView.setText("BUTTON CLICKED");
    }

    @Override
    public void showSample(SampleResponse response) {

    }
}
