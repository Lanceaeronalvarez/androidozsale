package au.com.dealsdirect.ui.sample;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.SampleRequest;
import au.com.dealsdirect.data.network.model.SampleResponse;
import au.com.dealsdirect.ui.base.BasePullToRefreshController;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;
import butterknife.OnClick;

/*
 * Created by Ayi on 05/06/2017.
 * Edited by Elaine on 07/31/2017 - Support for PullToRefresh Functionality
 */

public class SampleController extends BasePullToRefreshController implements SampleMvpView {

    // REQUIRED: Declare a TAG for all developed controllers
    public static final String TAG = "SampleController";

    // OPTIONAL: Keys for argument passing
    private static final String KEY_TEXT = "SampleController.KEY_TEXT";

    // REQUIRED: Must inject created Presenter
    @Inject
    SampleMvpPresenter<SampleMvpView> mPresenter;

    @BindView(R.id.controller_sample_text)
    TextView mTitleTextView;


    // REQUIRED: Create a new instance method for easier access and cleaner code
    public static SampleController newInstance(String arg) {

        return new SampleController(
                new BundleBuilder(new Bundle())
                        .putString(KEY_TEXT, arg)
                        .build());
    }

    // REQUIRED: Constructor
    public SampleController(Bundle args) {
        super(args);
    }

    // REQUIRED: inflate views here, follow this template when creating new controllers
    @NonNull
    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {

        // Assign super call in a view variable to inflate pull to refresh
        View view = super.inflateView(inflater, container);

        // Call fillContent and fillToolbar with corresponding layout files
        // NOTE: No need to fillToolbar if screen does not have a toolbar
        fillContent(inflater.inflate(R.layout.controller_sample, container, false));
        fillToolbar(inflater.inflate(R.layout.partial_toolbar_arrow, container, false));

        // Inject UI via butter knife
        getControllerComponent().inject(this);

        // Attach presenter
        mPresenter.onAttach(this);

        // Make sure to return view from super call
        return view;
    }

    @Override
    public void onViewBound(@NonNull View view) {
        super.onViewBound(view);

        // Always call setUp inside onViewBound after super call
        setUp(view);
    }

    @Override
    protected void setUp(View view) {

        // Setup views here and initial presenter calls
        //mPresenter.loadSample(new SampleRequest());

        mTitleTextView.setText(getArgs().getString(KEY_TEXT));
    }

    @Override
    public void onDestroyView(View view) {

        // Do not forget to call presenter detach onDestroyView
        // If you don't call detach, it's going to cause a leak
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    // Sample event for filled Content Layout
    @OnClick(R.id.controller_sample_button)
    void onButtonClick() {
        mTitleTextView.setText("BUTTON CLICKED");
    }

    // Sample event for filled Toolbar Layout
    @OnClick(R.id.partial_toolbar_left_view)
    void onBackClick() {
        getActivity().onBackPressed();
    }

    @Override
    public void showSample(SampleResponse response) {
        // Sample
    }


    // Override this method with super call
    // This will be called when push to refresh is triggered
    @Override
    public void onRefreshStart() {
        super.onRefreshStart();

        mPresenter.loadSample(new SampleRequest());
    }
}
