package au.com.dealsdirect.ui.controller.returns.newreturn;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.SampleResponse;
import au.com.dealsdirect.data.network.model.returns.returnorders.List;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;
import butterknife.OnClick;

/*
 * Created by Ayi on 05/06/2017.
 */

public class NewReturnController extends BaseController implements NewReturnMvpView {

    public static final String TAG = "NewReturnController";

    private static final String KEY_TEXT = "NewReturnController.KEY_TEXT";

    private static List mReturnItem;

    @BindView(R.id.partial_toolbar_arrow_title)
    TextView mNewReturnToolbarTitle;

    @BindView(R.id.partial_toolbar_filter_view)
    ImageButton mNewReturnToolbarRightOption;

    @Inject
    NewReturnMvpPresenter<NewReturnMvpView> mPresenter;

    public static NewReturnController newInstance(List returnItem) {

        mReturnItem = returnItem;
        return new NewReturnController(
                new BundleBuilder(new Bundle())
                        .build());
    }

    public NewReturnController(Bundle args) {
        super(args);
    }

    @NonNull
    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_new_return, container, false);

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
        mNewReturnToolbarTitle.setText("Create Return");
        mNewReturnToolbarRightOption.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onDestroyView(View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @Override
    public void showSample(SampleResponse response) {

    }

    @OnClick(R.id.partial_toolbar_arrow_view)
    void onBackClick(){
        if (getActivity()!=null)
            getActivity().onBackPressed();
    }

}
