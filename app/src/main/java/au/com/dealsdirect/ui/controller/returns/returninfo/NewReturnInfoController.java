package au.com.dealsdirect.ui.controller.returns.returninfo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.base.BaseController;
import butterknife.OnClick;

public class NewReturnInfoController extends BaseController {

    public NewReturnInfoController(Bundle args) {
        super(args);
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);
    }

    @Override
    public void onDetach(View view) {
        mActivity.getMainController().showBottomNav();
        super.onDetach(view);
    }

    @Override
    protected void setUp(View view) {
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_new_return_info, container, false);

        getControllerComponent().inject(this);
        return view;
    }

    @OnClick(R.id.partial_new_return_info_continue_button)
    public void dismiss() {
        mActivity.onBackPressed();
    }
}
